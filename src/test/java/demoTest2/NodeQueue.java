package demoTest2;

import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import guo_cam.CameraController;
import math.ZGeoMath;
import math.ZMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import render.JtsRender;
import render.ZRender;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project shopping_mall
 * @date 2022/4/1
 * @time 13:36
 */
public class NodeQueue extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private LineString ls;
    private Node p1;
    private Node p2;
    private Node p3;

    private JtsRender jtsRender;
    private CameraController gcam;

    public void setup() {
        this.jtsRender = new JtsRender(this);
        this.gcam = new CameraController(this);
        gcam.top();


        createLS();
        this.p1 = new Node(new ZPoint(210, 100), 20, ls);
        this.p2 = new Node(new ZPoint(270, 100), 20, ls);
        this.p3 = new Node(new ZPoint(299, 100), 20, ls);
    }

    private void createLS() {
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(100, 0),
                new Coordinate(200, 100),
                new Coordinate(300, 100)
        };
        this.ls = ZFactory.jtsgf.createLineString(coordinates);
    }

    private void updatePos() {
        p1.moveAlongEdge(ls, null);
        p2.moveAlongEdge(ls, p1);
        p3.moveAlongEdge(ls, p2);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        jtsRender.drawGeometry(ls);
        ZRender.drawZPoint2D(this, p1.pos, 5);
        ellipse(p1.pos.xf(), p1.pos.yf(), (float) p1.r * 2, (float) p1.r * 2);
        ZRender.drawZPoint2D(this, p2.pos, 5);
        ellipse(p2.pos.xf(), p2.pos.yf(), (float) p2.r * 2, (float) p2.r * 2);
        ZRender.drawZPoint2D(this, p3.pos, 5);
        ellipse(p3.pos.xf(), p3.pos.yf(), (float) p3.r * 2, (float) p3.r * 2);
        updatePos();
    }

    public void keyPressed() {
        if (key == 'u') {
            updatePos();
        }
    }

    /* ------------- inner class ------------- */

    static class Node {
        private ZPoint pos;
        private double r;

        private ZPoint targetCenter;
        private double halfLength;

        private double forceBase = 2;

        public Node(ZPoint pos, double r, LineString ls) {
            this.pos = pos;
            this.r = r;

            double l = ls.getLength();
            this.halfLength = l * 0.5;
            this.targetCenter = ZGeoMath.pointFromStart(ls, 0.5 * l);
        }

        public double calForce(LineString ls, Node next) {
            if (next == null || this.pos.distance(next.pos) > r * 2) {
                double distFromStart = ZGeoMath.distFromStart(ls, pos);
                double distToCenter = halfLength - distFromStart;
                double map = ZMath.mapToRegion(Math.abs(distToCenter), halfLength, 0, 2, 0);
                if (map < 0.2) {
                    map = 0.2;
                }
                if (Math.abs(distToCenter) < 1) {
                    map = 0;
                }
                if (distToCenter >= 0) {// close to start
                    return map * map * forceBase;
                } else {// close to end
                    return map * map * forceBase * -1;
                }
            } else {
                return 0;
            }
        }

        public void moveAlongEdge(LineString ls, Node next) {
            double force = calForce(ls, next);
            if (force >= 0) {//forward
                pos = ZGeoMath.pointOnEdgeByDist(pos, ls, force)[0];
            } else {// backward
                pos = ZGeoMath.pointOnEdgeByDist(pos, ls, -force)[1];
            }
        }

        public void updateRadiusRule(Node other) {

        }
    }
}
