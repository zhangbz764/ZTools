package testUtils;

import guo_cam.CameraController;
import math.ZMath;
import processing.core.PApplet;
import processing.core.PImage;
import render.ZHeatMapPalette;
import render.ZHeatMap;

import java.util.Arrays;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project Ztools
 * @date 2022/9/27
 * @time 11:00
 */
public class Test15ZHeatMap extends PApplet {

    public static void main(String[] args) {
        PApplet.main("testUtils.Test15ZHeatMap");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private CameraController gcam;

    private int testMapNum = 8;
    private ZHeatMap[] heatMaps;
    private final int[] data = ZMath.createIntegerSeries(0, 601);

    private PImage[] heatBars;
    private int barW = 600;
    private int barH = 50;

    public void setup() {
        this.gcam = new CameraController(this);
        gcam.top();

        this.heatMaps = new ZHeatMap[testMapNum];

        // 从哪个颜色变到哪个颜色
        // 可以写多个
        int[][] custom = new int[][]{
                {255, 0, 0},
                {0, 255, 0},
                {0, 0, 255},
                {0, 0, 0},
                {0, 0, 255},
                {0, 255, 0},
                {255, 0, 0},
        };
        heatMaps[0] = new ZHeatMap(custom, 0, 600);

        // 也可以直接调用调色盘预设的颜色
        heatMaps[1] = new ZHeatMap(ZHeatMapPalette.Grays, 0, 600);
        heatMaps[2] = new ZHeatMap(ZHeatMapPalette.Greens, 0, 600);
        heatMaps[3] = new ZHeatMap(ZHeatMapPalette.Viridis, 0, 600);
        heatMaps[4] = new ZHeatMap(ZHeatMapPalette.Magma, 0, 600);
        heatMaps[5] = new ZHeatMap(ZHeatMapPalette.CoolWarm, 0, 600);
        heatMaps[6] = new ZHeatMap(ZHeatMapPalette.Plasma, 0, 600);
        heatMaps[7] = new ZHeatMap(ZHeatMapPalette.HSB, 0, 600);

        System.out.println("数值 435.7 在 heatMaps[0] 里对应的颜色值为" + Arrays.toString(heatMaps[0].getColorRGB(435.7)));

        // create bars to display
        this.heatBars = new PImage[testMapNum];
        for (int i = 0; i < testMapNum; i++) {
            heatBars[i] = createImage(barW, barH, RGB);
        }

        for (int i = 0; i < testMapNum; i++) {
            heatBars[i].loadPixels();
            for (int j = 0; j < barW; j++) {
                int datum = data[j];
                int[] rgb = heatMaps[i].getColorRGB(datum);
                for (int k = 0; k < barH; k++) {
                    heatBars[i].pixels[j + k * barW] = color(rgb[0], rgb[1], rgb[2]);
                }
            }
            heatBars[i].updatePixels();
        }

    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        for (int i = 0; i < testMapNum; i++) {
            image(heatBars[i], 0, 0);
            translate(0, 200);
        }
    }

}
