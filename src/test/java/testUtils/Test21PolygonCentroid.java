package testUtils;

import basicGeometry.ZFactory;
import guo_cam.CameraController;
import math.ZGeoMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/8/27
 * @time 14:37
 */
public class Test21PolygonCentroid extends PApplet {
    public static void main(String[] args) {
        PApplet.main(Test21PolygonCentroid.class.getName());
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */


    private JtsRender jtsRender;
    private CameraController gcam;

    private Polygon randomPoly;
    private WB_Point geoCen;
    private Coordinate jtsCen;
    private WB_Point hemeshCen;

    public void setup() {

        this.jtsRender = new JtsRender(this);
        this.gcam = new CameraController(this);

        init();
    }

    private void init() {
//        this.randomPoly = ZFactory.createRandomPolygon(4, 100, 0.99);
//        this.randomPoly = ZFactory.jtsgf.createPolygon(new Coordinate[]{
//                new Coordinate(0, 0),
//                new Coordinate(0, 100),
//                new Coordinate(150, 100),
//                new Coordinate(150, 15),
//                new Coordinate(65, 80),
//                new Coordinate(0, 0),
//        });
        this.randomPoly = ZFactory.jtsgf.createPolygon(new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(65, 80),
                new Coordinate(150, 15),
                new Coordinate(150, 100),
                new Coordinate(0, 100),
                new Coordinate(0, 0),
        });

        this.jtsCen = randomPoly.getCentroid().getCoordinate();

        WB_Polygon wbPolygon = ZTransform.PolygonToWB_Polygon(randomPoly);
        this.geoCen = ZGeoMath.polygonGeoCentroid(wbPolygon);
        this.hemeshCen = wbPolygon.getCenter();
    }

    @Override
    public void keyPressed() {
        if (key == '1') {
            init();
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        noFill();
        stroke(0);
        jtsRender.drawGeometry(randomPoly);

        fill(0, 0, 255);
        ellipse((float) jtsCen.getX(), (float) jtsCen.getY(), 3, 3);
        text("jts centroid", (float) jtsCen.getX() + 3, (float) jtsCen.getY());

        noStroke();
        fill(255, 0, 0);
        ellipse(geoCen.xf(), geoCen.yf(), 3, 3);
        text("wb geo centroid", geoCen.xf() + 3, geoCen.yf() + 12);


        fill(255, 0, 255);
        ellipse(hemeshCen.xf(), hemeshCen.yf(), 3, 3);
        text("wb center", hemeshCen.xf() + 3, hemeshCen.yf());
    }

}
