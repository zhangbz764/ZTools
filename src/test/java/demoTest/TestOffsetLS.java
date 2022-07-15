package demoTest;

import basicGeometry.ZFactory;
import guo_cam.CameraController;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import render.JtsRender;

/**
 * test LineString offset
 *
 * @author ZHANG Baizhou zhangbz
 * @project shopping_mall
 * @date 2022/6/18
 * @time 16:06
 */
public class TestOffsetLS extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private JtsRender jtsRender;
    private CameraController gcam;

    private LineString ls;
    private LineString offsetLS;
    private double dist = 50;

    public void setup() {
        this.gcam = new CameraController(this);
        gcam.top();
        this.jtsRender = new JtsRender(this);

        Coordinate[] coords = new Coordinate[7];
        coords[0] = new Coordinate(-100, 300);
        coords[1] = new Coordinate(-200, 200);
        coords[2] = new Coordinate(-200, -200);
        coords[3] = new Coordinate(-100, -300);
        coords[4] = new Coordinate(100, -300);
        coords[5] = new Coordinate(200, -200);
        coords[6] = new Coordinate(200, 300);

        this.ls = ZFactory.jtsgf.createLineString(coords);

        this.offsetLS = ZFactory.createOffsetLineString(ls, dist);
        System.out.println("offset pts num: " + offsetLS.getNumPoints());
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        strokeWeight(2);
        stroke(0);
        jtsRender.drawGeometry(ls);
        stroke(255, 0, 0);
        jtsRender.drawGeometry(offsetLS);

        for (Coordinate c : offsetLS.getCoordinates()) {
            ellipse((float) c.getX(), (float) c.getY(), 5, 5);
        }
    }


    public void keyPressed() {
        if (key == 'q') {
            dist += 10;
            this.offsetLS = ZFactory.createOffsetLineString(ls, dist);
            System.out.println("offset pts num: " + offsetLS.getNumPoints());
        }
        if (key == 'w') {
            dist -= 10;
            this.offsetLS = ZFactory.createOffsetLineString(ls, dist);
            System.out.println("offset pts num: " + offsetLS.getNumPoints());
        }
    }

}
