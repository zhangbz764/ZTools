package render;

/**
 * some color palette for heatmap color mark
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2022/9/27
 * @time 15:45
 */
public enum ZHeatMapPalette {

    /*-------- single color --------*/
    Grays(
            new double[][]{
                    {0, 0, 0},
                    {1, 1, 1}
            },
            "Grays"
    ),

    Greens(
            new double[][]{
                    {0.96862745098039216, 0.9882352941176471, 0.96078431372549022},
                    {0.0, 0.26666666666666666, 0.10588235294117647}
            },
            "Greens"
    ),

    Blues(
            new double[][]{
                    {0.96862745098039216, 0.98431372549019602, 1.0},
                    {0.03137254901960784, 0.18823529411764706, 0.41960784313725491}
            },
            "Blues"
    ),

    /*-------- multiple colors --------*/

    Plasma(
            new double[][]{
                    {0.050383, 0.029803, 0.527975},
                    {0.940015, 0.975158, 0.131326}
            },
            "Plasma"
    ),

    Magma(
            new double[][]{
                    {0.001462, 0.000466, 0.013866},
                    {0.316654, 0.071690, 0.485380},
                    {0.716387, 0.214982, 0.475290},
                    {0.986700, 0.535582, 0.382210},
                    {0.987053, 0.991438, 0.749504}
            },
            "Magma"
    ),

    Viridis(
            new double[][]{
                    {0.267004, 0.004874, 0.329415},
                    {0.127568, 0.566949, 0.550556},
                    {0.993248, 0.906157, 0.143936}
            },
            "Viridis"
    ),

    HSB(
            new double[][]{
                    {1, 0, 0},
                    {1, 0.7, 0},
                    {1, 1, 0},
                    {0, 1, 0},
                    {0, 1, 1},
                    {0, 0, 1},
                    {0.66667, 0, 1},
            },
            "HSB"
    ),

    /*-------- diverging color map --------*/

    CoolWarm(
            new double[][]{
                    {0.230, 0.299, 0.754},
                    {0.865, 0.865, 0.865},
                    {0.706, 0.016, 0.150}
            },
            "CoolWarm"
    ),
    ;

    /*-------- constructor --------*/

    private double[][] colorMarks;
    private String name;

    private ZHeatMapPalette(double[][] colorMarks, String name) {
        this.colorMarks = colorMarks;
        this.name = name;
    }

    public double[][] getMarks() {
        return colorMarks;
    }

    public String getName() {
        return name;
    }
}
