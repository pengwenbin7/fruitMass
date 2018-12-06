package cn.xl910829.KiwiMass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.awt.font.TextAttribute;
import java.math.BigDecimal;

/**
 * Created by xl on 11/6/14.
 */
public class ResultActivity extends Activity {

    private double[] info;
    private float[] ratio;
    private TextView textView1, textView2, textView3, textView4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        // textView1 = (TextView) findViewById(R.id.textViewResult1);
        textView2 = (TextView) findViewById(R.id.textViewResult2);
        textView3 = (TextView) findViewById(R.id.textViewResult3);
        textView4 = (TextView) findViewById(R.id.textViewResult4);

        Intent parenIntent = getIntent();
        info = parenIntent.getDoubleArrayExtra("info");
        ratio = parenIntent.getFloatArrayExtra("ratio");
        BigDecimal bigDecimal = new BigDecimal(info[1]);
        bigDecimal = bigDecimal.setScale(1, BigDecimal.ROUND_UP);
        double mass = bigDecimal.doubleValue();

        textView2.append(Html.fromHtml("(mm<small><sup>2</small></sup>):"));
        textView2.append(" " + info[0]);
        textView3.setText("重量(g): " + mass);
        textView4.append("B = " + ratio[0] + " x A + " + ratio[1]);

    }
}