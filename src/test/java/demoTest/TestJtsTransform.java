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
 * description
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
    private LineString transform;

    private CameraController gcam;
    private JtsRender jtsRender;

    public void setup() {
        this.gcam = new CameraController(this);
        this.jtsRender = new JtsRender(this);
        gcam.top();

        Coordinate[] coords = new Coordinate[]{
                new Coordinate(100, 0),
                new Coordinate(200, 200),
                new Coordinate(300, 200),
                new Coordinate(500, 500),
                new Coordinate(700, 500)
        };
        this.original = ZFactory.jtsgf.createLineString(coords);
        ZJtsTransform trans = new ZJtsTransform();
        trans.addTranslate2D(new ZPoint(200, 0)).addRotateAboutPoint2D(Math.PI * 0.5, new ZPoint(200, 200));
        this.transform = (LineString) trans.applyToGeometry2D(original);

        System.out.println(original);
        System.out.println(transform);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(200);

        jtsRender.drawGeometry(original);
        jtsRender.drawGeometry(transform);
    }

}
