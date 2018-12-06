package cn.xl910829.KiwiMass;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by xl on 11/6/14.
 */
public class BreedActivity extends Activity {

    private int kiwiId;
    private static final String TAG = "BreedActivity";

    private TextView textView;
    private ListView listView1;
    private SharedPreferences mPre;
    private JsonParser parser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breed);

        textView = (TextView) findViewById(R.id.textViewBreed);
        listView1 = (ListView) findViewById(R.id.listViewBreed);

        mPre = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        kiwiId = mPre.getInt("kiwiId", -1);

        // Read file get json parser
        String json = null;
        try {
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(openFileInput("formula.json")));
            json = in.readLine();
        } catch (Exception e) {
            Log.i(TAG, "Error read json file");
        }
        parser = JsonParser.parse(json);

        // Parse parser get parameters fill list view
        final String[] kiwis = parser.getName();
        String currentBreed = kiwiId == -1? "未选择": kiwis[kiwiId];
        textView.setText("当前品种: " + currentBreed);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, kiwis);
        listView1.setAdapter(adapter);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView.setText("当前品种: " + kiwis[(int)id]);
                // Save choice
                SharedPreferences.Editor editor = mPre.edit();
                editor.putInt("kiwiId", (int)id);
                editor.putFloat("ratio1", (float)parser.getRatio1()[(int)id]);
                editor.putFloat("ratio0", (float)parser.getRatio0()[(int)id]);
                editor.apply();
            }
        });
    }
}