package testUtils;

import basicGeometry.ZFactory;
import math.ZKMeans;
import math.ZMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import processing.core.PApplet;
import render.JtsRender;
import render.ZRender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project city_site_matching
 * @date 2021/11/10
 * @time 19:41
 */
public class Test11KMeans extends PApplet {

    public static void main(String[] args) {
        PApplet.main("testUtils.Test11KMeans");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    private double[][] samples;
    private int[][] clusters;
    private double[][] centroids;

    private Geometry[] clusterConvexHull;

    private JtsRender jtsRender;

    private int k = 8;
    private int sampleNum = 100;

    private List<ZRender.ZColor> colors;
    private int[] colorsIDs;
    private final Random random = new Random();

    /* ------------- setup ------------- */

    public void setup() {
        this.jtsRender = new JtsRender(this);

        this.samples = new double[sampleNum][];
        for (int i = 0; i < sampleNum; i++) {
            samples[i] = new double[]{
                    random(100, 900),
                    random(100, 900)
            };
        }

        ZKMeans kmeans = new ZKMeans(samples, k, 0.0001);
        kmeans.cluster();
        this.clusters = kmeans.getClusters();
        this.centroids = kmeans.getCentroid();

        System.out.println("final clusters:  " + Arrays.deepToString(clusters));
        System.out.println("final centroids:  " + Arrays.deepToString(centroids));

        if (samples[0].length > 1) {
            this.clusterConvexHull = new Geometry[k];
            for (int i = 0; i < k; i++) {
                Point[] pts = new Point[clusters[i].length];
                for (int j = 0; j < clusters[i].length; j++) {
                    int id = clusters[i][j];
                    Point p = ZFactory.jtsgf.createPoint(new Coordinate(samples[id][0], samples[id][1]));
                    pts[j] = p;
                }
                clusterConvexHull[i] = ZFactory.jtsgf.createMultiPoint(pts).convexHull();
            }
        } else {
            this.clusterConvexHull = new Geometry[0];
        }


        this.colors = new ArrayList<>();
        this.colorsIDs = ZMath.randomIntegers(0, ZRender.ZColor.values().length - 1, clusters.length);
        colors.addAll(Arrays.asList(ZRender.ZColor.values()));
        System.out.println(Arrays.toString(colorsIDs));
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        noFill();
        for (Geometry g : clusterConvexHull) {
            jtsRender.drawGeometry(g);
        }


        for (int i = 0; i < clusters.length; i++) {
            noStroke();
            fill(colors.get(colorsIDs[i]).getColor());
            for (int j = 0; j < clusters[i].length; j++) {
                ellipse((float) samples[clusters[i][j]][0], (float) samples[clusters[i][j]][1], 10, 10);
            }
            stroke(0);
            ellipse((float) centroids[0][0], (float) centroids[0][1], 20, 20);
        }
    }

//    public void draw() {
//        background(255);
//        noFill();
//        for (Geometry g : clusterConvexHull) {
//            jtsRender.drawGeometry(g);
//        }
//
//        for (int i = 0; i < clusters.length; i++) {
//            noStroke();
//            fill(i * 25, 0, 0);
//            for (int j = 0; j < clusters[i].length; j++) {
//                ellipse((float) samples[clusters[i][j]][0], 500, 10, 10);
//            }
//            stroke(0);
//
//            if (centroids[i].length > 0) {
//                ellipse((float) centroids[i][0], 500, 20, 20);
//            }
//        }
//
//
//        noStroke();
//        fill(255, 0, 0);
//        for (int i = 0; i < clusters[0].length; i++) {
//            ellipse((float) samples[clusters[0][i]][0], 500, 10, 10);
//        }
//        stroke(0);
//        ellipse((float) centroids[0][0], 500, 20, 20);
//
//        noStroke();
//        fill(0, 255, 0);
//        for (int i = 0; i < clusters[1].length; i++) {
//            ellipse((float) samples[clusters[1][i]][0], 500, 10, 10);
//        }
//        stroke(0);
//        ellipse((float) centroids[1][0], 500, 20, 20);
//
//        noStroke();
//        fill(0, 0, 255);
//        for (int i = 0; i < clusters[2].length; i++) {
//            ellipse((float) samples[clusters[2][i]][0], 500, 10, 10);
//        }
//        stroke(0);
//        ellipse((float) centroids[2][0], 500, 20, 20);
//
//        noStroke();
//        fill(0, 255, 255);
//        for (int i = 0; i < clusters[3].length; i++) {
//            ellipse((float) samples[clusters[3][i]][0], 500, 10, 10);
//        }
//        stroke(0);
//        ellipse((float) centroids[3][0], 500, 20, 20);
//
//        noStroke();
//        fill(255, 0, 255);
//        for (int i = 0; i < clusters[4].length; i++) {
//            ellipse((float) samples[clusters[4][i]][0], 500, 10, 10);
//        }
//        stroke(0);
//        ellipse((float) centroids[4][0], 500, 20, 20);
//    }

    public void keyPressed() {
        if (key == 's') {
            String className = getClass().getSimpleName();
            save("./src/test/resources/exampleImgs/" + className + ".jpg");
        }
    }
}
