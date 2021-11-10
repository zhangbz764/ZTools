package demoTest;

import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import guo_cam.CameraController;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZJtsTransform;

/**
 * test transform for jts Geometry
 *
 * @author ZHANG Baizhou zhangbz
 * @project city_site_matching
 * @date 2021/10/21
 * @time 12:27
 */
public class TestJtsTransform extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private LineString original;
    private LineString reflect;
    private LineString multi;

    private CameraController gcam;
    private JtsRender jtsRender;

    public void setup() {
        this.gcam = new CameraController(this);
        this.jtsRender = new JtsRender(this);
        gcam.top();

        // create original Geometry
        Coordinate[] coords = new Coordinate[]{
                new Coordinate(100, 0),
                new Coordinate(200, 200),
                new Coordinate(300, 200),
                new Coordinate(500, 500),
                new Coordinate(700, 500)
        };
        this.original = ZFactory.jtsgf.createLineString(coords);

        // mirror transform
        ZJtsTransform transReflect = new ZJtsTransform();
        transReflect.addReflect2D(new ZPoint(0, 0), new ZPoint(-100, 100));
        this.reflect = (LineString) transReflect.applyToGeometry2D(original);

        // multiple transform
        ZJtsTransform trans = new ZJtsTransform();
        trans.addTranslate2D(new ZPoint(200, 0)).addScale2D(2)
                .addRotateAboutPoint2D(Math.PI * 0.5, new ZPoint(200, 200))
                .addReflect2D(new ZPoint(0, 0), new ZPoint(0, 100))
        ;
        this.multi = (LineString) trans.applyToGeometry2D(original);

        System.out.println(original);
        System.out.println(reflect);
        System.out.println(multi);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(200);

        strokeWeight(2);

        stroke(0);
        jtsRender.drawGeometry(original);
        stroke(255, 0, 0);
        jtsRender.drawGeometry(reflect);


        stroke(255, 255, 0);
        jtsRender.drawGeometry(multi);
    }

}
