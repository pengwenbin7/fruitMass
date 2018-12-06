package cn.xl910829.KiwiMass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.io.*;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    private static final String TAG = "KiwiMass: main";
    private static int REQUEST_TAKE = 1;
    private static int REQUEST_PICK = 2;

    private Button buttonExplain, buttonBreed, buttonTake, buttonPick;
    private ImageView imageView1;
    private Mat src;
    private int kiwiId;

    private SharedPreferences mPre;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    // Initiation program data
    private void init() {
        mPre = getPreferences(Activity.MODE_PRIVATE);
        boolean correct = mPre.getBoolean("correct", false);
        // Build json file
        if (!correct) {
            String json = XlUtils.buildJson();
            try {
                OutputStream out = openFileOutput("formula.json", Activity.MODE_PRIVATE);
                out.write(json.getBytes());
                out.close();
            } catch (Exception e) {
                Log.i(TAG, "Error write json to file");
            }
            SharedPreferences.Editor editor = mPre.edit();
            editor.putBoolean("correct", true);
            editor.apply();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
        buttonExplain = (Button) findViewById(R.id.button_explain);
        buttonBreed = (Button) findViewById(R.id.button_breed);
        buttonTake = (Button) findViewById(R.id.button_take);
        buttonPick = (Button) findViewById(R.id.button_pick);
        imageView1 = (ImageView) findViewById(R.id.imageViewIcon);

        buttonExplain.setOnClickListener(new ExplainListener());
        buttonBreed.setOnClickListener(new BreedListener());
        buttonPick.setOnClickListener(new PickListener());
        buttonTake.setOnClickListener(new TakeListener());
    }

    // Load OpenCV Manager
    @Override
    protected void onResume() {
        Log.i(TAG, "Call onResume");
        super.onResume();
        Log.i(TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback)) {
            Log.i(TAG, "can not connect to OpenCV Manager");
        }
    }

    // Process resolved picture
    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        if (result == Activity.RESULT_OK && data != null) {
            if (request == REQUEST_TAKE) {

                Bundle extra = data.getExtras();
                Bitmap bitmap = (Bitmap) extra.get("data");
                src = new Mat();
                Utils.bitmapToMat(bitmap, src);
            }

            else if (request == REQUEST_PICK) {
                Uri uri = data.getData();
                String filePath = XlUtils.UriToPath(uri, getApplicationContext());
                src = Highgui.imread(filePath);
            }

            Intent resultIntent = new Intent(this, ResultActivity.class);
            float ratio1 = mPre.getFloat("ratio1", 0);
            float ratio0 = mPre.getFloat("ratio0", 0);
            float[] ratio = {ratio1, ratio0};
            resultIntent.putExtra("ratio", ratio);
            double[] info = ImageProcess.getInfo(src, (double) ratio1, (double) ratio0);
            resultIntent.putExtra("info", info);
            startActivity(resultIntent);
        }

    }

    // Call camera take picture
    private class TakeListener implements View.OnClickListener {
        private Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        @Override
        public void onClick(View v) {
            kiwiId = mPre.getInt("kiwiId", -1);
            if (kiwiId != -1) {
                if (takeIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeIntent, REQUEST_TAKE);
                }
            } else {
                Toast.makeText(getApplicationContext(), "先选择品种", Toast.LENGTH_SHORT).show();

            }
        }
    }

    // Pick image from local storage
    private class PickListener implements View.OnClickListener {
        private Intent pickIntent = new Intent(Intent.ACTION_PICK);
        @Override
        public void onClick(View v) {
            kiwiId = mPre.getInt("kiwiId", -1);
            if (kiwiId != -1) {
                pickIntent.setType("image/*");
                if (pickIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(pickIntent, REQUEST_PICK);
                }
            } else {
                Toast.makeText(getApplicationContext(), "先选择品种", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // View help document
    private class ExplainListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent explainIntent = new Intent(MainActivity.this, ExplainActivity.class);
            if (explainIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(explainIntent);
            }
        }
    }

    // Choice breed
    private class BreedListener implements View.OnClickListener {
        Intent breedIntent = new Intent(MainActivity.this, BreedActivity.class);
        @Override
        public void onClick(View v) {
            startActivity(breedIntent);
        }
    }
}
