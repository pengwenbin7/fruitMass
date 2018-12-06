package cn.xl910829.KiwiMass;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xl on 11/6/14.
 */
public class XlUtils {
    private final static String TAG = "KiwiMass: XlUtils";

    public static String UriToPath(Uri uri, Context context) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;
    }

    public static String buildJson() {
        JSONObject root = new JSONObject();
        JSONObject node = new JSONObject();
        try {
            node.put("name", "海优0");
            node.put("ration1", 0.02248);
            node.put("ration0", 1.842);
            root.put("0", node);

            node = new JSONObject();
            node.put("name", "海优1");
            node.put("ration1", 0.02969);
            node.put("ration0", -21.88);
            root.put("1", node);

            node = new JSONObject();
            node.put("name", "海优2");
            node.put("ration1", 0.02925);
            node.put("ration0", -21.89);
            root.put("2", node);

            node = new JSONObject();
            node.put("name", "海优3");
            node.put("ration1", 0.03027);
            node.put("ration0", -19.2);
            root.put("3", node);

            node = new JSONObject();
            node.put("name", "海优4");
            node.put("ration1", 0.02903);
            node.put("ration0", -17.24);
            root.put("4", node);

            node = new JSONObject();
            node.put("name", "海优5");
            node.put("ration1", 0.02933);
            node.put("ration0", -18.17);
            root.put("5", node);

            node = new JSONObject();
            node.put("name", "徐香0");
            node.put("ration1", 0.02039);
            node.put("ration0", 0.294);
            root.put("6", node);

            node = new JSONObject();
            node.put("name", "徐香1");
            node.put("ration1", 0.01995);
            node.put("ration0", 10.92);
            root.put("7", node);

            node = new JSONObject();
            node.put("name", "徐香2");
            node.put("ration1", 0.0214);
            node.put("ration0", 14.57);
            root.put("8", node);

            node = new JSONObject();
            node.put("name", "徐香3");
            node.put("ration1", 0.02385);
            node.put("ration0", -1.749);
            root.put("9", node);

            node = new JSONObject();
            node.put("name", "徐香4");
            node.put("ration1", 0.01998);
            node.put("ration0", 5.433);
            root.put("10", node);

            node = new JSONObject();
            node.put("name", "徐香5");
            node.put("ration1", 0.0238);
            node.put("ration0", 5.12);
            root.put("11", node);
        } catch (Exception e) {
            Log.i(TAG, "Fail create formula json");
        }
        return root.toString();
    }
}

class JsonParser {
    private final static String TAG = "XlUtils: JsonParser";
    private int[] id;
    private String[] name;
    private double[] ratio1;
    private double[] ratio0;
    private int length;

    private JsonParser() {}
    static JsonParser parse(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.i(TAG, "Error convert string to json object");
        }
        return parse(jsonObject);
    }
    static JsonParser parse(JSONObject json) {
        JsonParser jp = new JsonParser();
        JSONObject node;

        int len = json.length();
        int[] idR = new int[len];
        String[] nameR = new String[len];
        double[] ratio1R = new double[len];
        double[] ratio0R = new double[len];

        for (int i = 0; i < len; i++) {
            try {
                node = json.getJSONObject(Integer.toString(i));
                idR[i] = i;
                nameR[i] = node.getString("name");
                ratio1R[i] = node.getDouble("ration1");
                ratio0R[i] = node.getDouble("ration0");
            } catch (JSONException e) {
                Log.i(TAG, "Error arse json object");
            }
        }
        jp.setId(idR);
        jp.setName(nameR);
        jp.setRatio1(ratio1R);
        jp.setRatio0(ratio0R);
        jp.setLength(len);
        return jp;
    }

    int[] getId() {
        return id;
    }

    String[] getName() {
        return name;
    }

    double[] getRatio1() {
        return ratio1;
    }

    double[] getRatio0() {
        return ratio0;
    }

    int getLength() {
        return length;
    }

    private void setId(int[] id) {
        this.id = id;
    }

    private void setName(String[] name) {
        this.name = name;
    }

    private void setRatio1(double[] ratio1) {
        this.ratio1 = ratio1;
    }

    private void setRatio0(double[] ratio0) {
        this.ratio0 = ratio0;
    }

    private void setLength(int length) {
        this.length = length;
    }
}