package render;

import java.util.Arrays;

/**
 * create a heatmap color marks
 *
 * @author ZHANG Baizhou zhangbz
 * @project Ztools
 * @date 2022/9/27
 * @time 10:14
 */
public class ZHeatMap {
    private double dataMin;
    private double dataMax;
    private double k;

    private double[][] colorMarks;
    private int[][] colorMinMax;
    private int sectionNum;
    private double markStep;

    /* ------------- constructor ------------- */

    public ZHeatMap(ZColorPalette palette, double _dataMin, double _dataMax) {
        this.dataMin = _dataMin;
        this.dataMax = _dataMax;
        this.k = 1 / (dataMax - dataMin);

        this.colorMarks = palette.getMarks();
        setColorMinMax();

        this.sectionNum = colorMarks.length - 1;
        this.markStep = 1d / sectionNum;
    }

    public ZHeatMap(int[][] customMarks, double _dataMin, double _dataMax) {
        this.dataMin = _dataMin;
        this.dataMax = _dataMax;
        this.k = 1 / (dataMax - dataMin);

        // set up custom marks
        if (customMarks.length < 1) {
            throw new IllegalArgumentException("You need to input at least 2 color marks in RGB mode!");
        } else {
            boolean flag = false;
            marks:
            for (int[] marks : customMarks) {
                if (marks.length != 3) {
                    flag = true;
                    break marks;
                } else {
                    colors:
                    for (int color : marks) {
                        if (color < 0 || color > 255) {
                            flag = true;
                            break marks;
                        }
                    }
                }
            }
            if (flag) {
                throw new IllegalArgumentException("Each color mark has to be R,G,B between 0 to 255!");
            } else {
                this.colorMarks = new double[customMarks.length][];
                for (int i = 0; i < customMarks.length; i++) {
                    colorMarks[i] = new double[]{
                            1.0 * customMarks[i][0] / 255,
                            1.0 * customMarks[i][1] / 255,
                            1.0 * customMarks[i][2] / 255
                    };
                }
            }
            setColorMinMax();
        }

        this.sectionNum = colorMarks.length - 1;
        this.markStep = 1d / sectionNum;
    }

    /* ------------- member function ------------- */

    public int[] getColorRGB(double inputData) {
        double dataMapped = k * (inputData - dataMin);

        int R, G, B;
        if (inputData <= dataMin) {
            R = colorMinMax[0][0];
            G = colorMinMax[0][1];
            B = colorMinMax[0][2];
        } else if (inputData >= dataMax) {
            R = colorMinMax[1][0];
            G = colorMinMax[1][1];
            B = colorMinMax[1][2];
        } else {
            if (sectionNum > 1) {
                double pos = dataMapped / markStep;
                double fractional = pos % 1;
                int currSec = (int) Math.floor(pos);
                R = (int) (255 * (fractional * (colorMarks[currSec + 1][0] - colorMarks[currSec][0]) + colorMarks[currSec][0]));
                G = (int) (255 * (fractional * (colorMarks[currSec + 1][1] - colorMarks[currSec][1]) + colorMarks[currSec][1]));
                B = (int) (255 * (fractional * (colorMarks[currSec + 1][2] - colorMarks[currSec][2]) + colorMarks[currSec][2]));
            } else {
                R = (int) (255 * (dataMapped * (colorMarks[1][0] - colorMarks[0][0]) + colorMarks[0][0]));
                G = (int) (255 * (dataMapped * (colorMarks[1][1] - colorMarks[0][1]) + colorMarks[0][1]));
                B = (int) (255 * (dataMapped * (colorMarks[1][2] - colorMarks[0][2]) + colorMarks[0][2]));
            }
        }

        return new int[]{R, G, B};
    }

    public String getColorHEX() {
        return "";
    }

    /* ------------- setter & getter ------------- */

    private void setColorMinMax() {
        this.colorMinMax = new int[][]{
                {(int) (colorMarks[0][0] * 255), (int) (colorMarks[0][1] * 255), (int) (colorMarks[0][2] * 255)},
                {(int) (colorMarks[colorMarks.length - 1][0] * 255), (int) (colorMarks[colorMarks.length - 1][1] * 255), (int) (colorMarks[colorMarks.length - 1][2] * 255)}
        };

        System.out.println("colorMinMax: " + Arrays.deepToString(colorMinMax));
    }

    public ZHeatMap setReverse() {
        double[][] originalMarks = colorMarks.clone();
        this.colorMarks = new double[originalMarks.length][];
        for (int i = 0; i < originalMarks.length; i++) {
            colorMarks[i] = originalMarks[originalMarks.length - 1 - i];
        }
        setColorMinMax();
        return this;
    }


}

