package cn.xl910829.KiwiMass;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xl on 11/7/14.
 */
public class ImageProcess {

    private final static String TAG = "ImageProcess";

    public static void mian(String[] args) {

    }
    public static double[] getInfo(Mat src, double ratio1, double ratio0) {
        double[] info = {0, 0};
        double area = kiwiArea(src);
        double mass = area * ratio1 + ratio0;
        info[0] = area;
        info[1] = mass;
        return info;
    }
    // Get kiwi ral area from input image
    public static double kiwiArea(Mat src) {
        double kiwiRealArea = 0;

        // Gaussian Blue
        Imgproc.GaussianBlur(src, src, new Size(5, 5), 0, 0);
        // Convert RGB image to 3-channel hsv image
        Mat hsv = src.clone();
        Imgproc.cvtColor(src, hsv, Imgproc.COLOR_RGB2HSV);
        List<Mat> hsvSplit = new ArrayList<Mat>(3);
        Core.split(hsv, hsvSplit);
        // Morphology close operation
        Mat kernel = Mat.ones(15, 15, CvType.CV_8U);
        Mat h = hsvSplit.get(0);
        Mat v = hsvSplit.get(2);
        Imgproc.morphologyEx(h, h, Imgproc.MORPH_CLOSE, kernel);
        Imgproc.morphologyEx(v, v, Imgproc.MORPH_CLOSE, kernel);
        // Convert to binary image
        Imgproc.threshold(h, h, 0, 255, Imgproc.THRESH_OTSU);
        Imgproc.threshold(v, v, 0, 255, Imgproc.THRESH_OTSU);
        h = smallerAreaMat(h);
        Core.bitwise_not(v, v);
        // Get pix area
        List<Double> pixKiwiAreaArray = bwArea(h);
        List<Double> pixKiwiAndCircleAreaArray = bwArea(v);
        listInverse(pixKiwiAreaArray);
        listInverse(pixKiwiAndCircleAreaArray);
        double pixKiwiArea = pixKiwiAreaArray.get(0);
        double pixKiwiAndCircleArea =
                pixKiwiAndCircleAreaArray.get(0) + pixKiwiAndCircleAreaArray.get(1);
        // Get ratio -- k
        double ratio =
                (pixKiwiAndCircleArea - pixKiwiArea) / (Math.PI * 20 * 20);
        // Get kiwi real area
        kiwiRealArea = pixKiwiArea / ratio;
        return kiwiRealArea;
    }

    // Calculate binary image area
    public static List<Double> bwArea(Mat bw) {
        List<Double> areas = new ArrayList<Double>();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = bw.clone();
        Imgproc.findContours(bw, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (Mat mat : contours) {
            areas.add(Imgproc.contourArea(mat));
        }
        return areas;
    }

    // Inverse Double list
    public static void listInverse(List<Double> list) {
        int len = list.size();
        Double[] array = list.toArray(new Double[len]);
        list.clear();
        Arrays.sort(array);
        for (int i = 0; i < len; i++){
            list.add(array[len - 1 -i]);
        }
    }

    // Parse h-channel image return smaller pix area one
    // 在处理过程中，有时h通道图需要2值化，有时不需要，根据先验知识——空白区域面积较大,决定是否取反
    private static Mat smallerAreaMat(Mat bw) {
        double tmp1 = 0;
        double tmp2 = 0;
        List<Double> areas1 = bwArea(bw);
        for (Double d: areas1) {
            tmp1 += d;
        }
        Mat bw_not = bw.clone();
        Core.bitwise_not(bw, bw_not);
        List<Double> areas2 = bwArea(bw_not);
        for (Double d: areas2) {
            tmp2 += d;
        }
        return tmp1 < tmp2? bw: bw_not;
    }
}
