package render;

public enum ZColorPalette {

    /*-------- single color --------*/
    Grays {
        public double[][] getMarks() {
            return new double[][]{
                    {0, 0, 0},
                    {1, 1, 1}
            };
        }
    },
    Greens {
        public double[][] getMarks() {
            return new double[][]{
                    {0.96862745098039216, 0.9882352941176471, 0.96078431372549022},
                    {0.0, 0.26666666666666666, 0.10588235294117647}
            };
        }
    },
    Blues {
        public double[][] getMarks() {
            return new double[][]{
                    {0.96862745098039216, 0.98431372549019602, 1.0},
                    {0.03137254901960784, 0.18823529411764706, 0.41960784313725491}
            };
        }
    },

    /*-------- multiple colors --------*/
    Plasma {
        public double[][] getMarks() {
            return new double[][]{
                    {0.050383, 0.029803, 0.527975},
                    {0.940015, 0.975158, 0.131326}
            };
        }
    },
    Magma {
        public double[][] getMarks() {
            return new double[][]{
                    {0.001462, 0.000466, 0.013866},
                    {0.316654, 0.071690, 0.485380},
                    {0.716387, 0.214982, 0.475290},
                    {0.986700, 0.535582, 0.382210},
                    {0.987053, 0.991438, 0.749504}
            };
        }
    },
    Viridis {
        public double[][] getMarks() {
            return new double[][]{
                    {0.267004, 0.004874, 0.329415},
                    {0.127568, 0.566949, 0.550556},
                    {0.993248, 0.906157, 0.143936}
            };
        }
    },
    /*-------- diverging color map --------*/
    Coolwarm {
        public double[][] getMarks() {
            return new double[][]{
                    {0.230, 0.299, 0.754},
                    {0.865, 0.865, 0.865},
                    {0.706, 0.016, 0.150}
            };
        }
    },

    ;

    /*-------- abstract methods --------*/
    public abstract double[][] getMarks();
}
