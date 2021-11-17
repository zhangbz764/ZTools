package demoTest;

import basicGeometry.ZFactory;
import math.ZKMeans;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import processing.core.PApplet;
import render.JtsRender;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project city_site_matching
 * @date 2021/11/10
 * @time 19:41
 */
public class Test11KMeans extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    private double[][] samples;
    private int[][] clusters;
    private double[][] centroids;

    private Geometry[] clusterConvexHull;

    private JtsRender jtsRender;

    /* ------------- setup ------------- */

    public void setup() {
        this.jtsRender = new JtsRender(this);
        int k = 5;

        initSamples();
        ZKMeans kmeans = new ZKMeans(samples, k, 5);
        kmeans.init();
        this.clusters = kmeans.getClusters();
        this.centroids = kmeans.getCentroid();

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
    }

    private void initSamples() {
        this.samples = new double[200][];
        for (int i = 0; i < 200; i++) {
            samples[i] = new double[]{
                    random(100, 900),
                    random(100, 900)
            };
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        noFill();
        for (Geometry g : clusterConvexHull) {
            jtsRender.drawGeometry(g);
        }

        noStroke();
        fill(255, 0, 0);
        for (int i = 0; i < clusters[0].length; i++) {
            ellipse((float) samples[clusters[0][i]][0], (float) samples[clusters[0][i]][1], 10, 10);
        }
        stroke(0);
        ellipse((float) centroids[0][0], (float) centroids[0][1], 20, 20);

        noStroke();
        fill(0, 255, 0);
        for (int i = 0; i < clusters[1].length; i++) {
            ellipse((float) samples[clusters[1][i]][0], (float) samples[clusters[1][i]][1], 10, 10);
        }
        stroke(0);
        ellipse((float) centroids[1][0], (float) centroids[1][1], 20, 20);

        noStroke();
        fill(0, 0, 255);
        for (int i = 0; i < clusters[2].length; i++) {
            ellipse((float) samples[clusters[2][i]][0], (float) samples[clusters[2][i]][1], 10, 10);
        }
        stroke(0);
        ellipse((float) centroids[2][0], (float) centroids[2][1], 20, 20);

        noStroke();
        fill(0, 255, 255);
        for (int i = 0; i < clusters[3].length; i++) {
            ellipse((float) samples[clusters[3][i]][0], (float) samples[clusters[3][i]][1], 10, 10);
        }
        stroke(0);
        ellipse((float) centroids[3][0], (float) centroids[3][1], 20, 20);

        noStroke();
        fill(255, 0, 255);
        for (int i = 0; i < clusters[4].length; i++) {
            ellipse((float) samples[clusters[4][i]][0], (float) samples[clusters[4][i]][1], 10, 10);
        }
        stroke(0);
        ellipse((float) centroids[4][0], (float) centroids[4][1], 20, 20);
    }

}
