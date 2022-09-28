package demoTest;

import guo_cam.CameraController;
import math.ZMath;
import processing.core.PApplet;
import processing.core.PImage;
import render.ZColorPalette;
import render.ZHeatMap;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project Ztools
 * @date 2022/9/27
 * @time 11:00
 */
public class TestZHeatMap extends PApplet {
    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private CameraController gcam;

    private int testMapNum = 7;
    private ZHeatMap[] heatMaps;
    private final int[] data = ZMath.createIntegerSeries(0, 601);

    private PImage[] heatBars;
    private int barW = 600;
    private int barH = 50;

    public void setup() {
        this.gcam = new CameraController(this);
        gcam.top();

        this.heatMaps = new ZHeatMap[testMapNum];
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
        heatMaps[1] = new ZHeatMap(ZColorPalette.Grays, 0, 600);
        heatMaps[2] = new ZHeatMap(ZColorPalette.Greens, 0, 600);
        heatMaps[3] = new ZHeatMap(ZColorPalette.Viridis, 0, 600);
        heatMaps[4] = new ZHeatMap(ZColorPalette.Magma, 0, 600);
        heatMaps[5] = new ZHeatMap(ZColorPalette.Coolwarm, 0, 600);
        heatMaps[6] = new ZHeatMap(ZColorPalette.Plasma, 0, 600);

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
