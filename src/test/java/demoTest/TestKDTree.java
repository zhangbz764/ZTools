package demoTest;

import guo_cam.CameraController;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project Ztools
 * @date 2022/7/30
 * @time 18:45
 */
public class TestKDTree extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private CameraController gcam;
    private WB_Render render;

    private WB_RandomPoint source;
    private List<WB_Point> points;

    private WB_KDTreeInteger<WB_Point> tree;
    private List<WB_AABB> leafs;

    private WB_Point test;
    private WB_Coord near;

    private boolean drawAABB = false;

    public void setup() {
        this.source = new WB_RandomOnSphere().setRadius(250);
        this.gcam = new CameraController(this);
        this.render = new WB_Render(this);

        this.tree = new WB_KDTreeInteger<WB_Point>(8);
        this.points = new ArrayList<WB_Point>();
        for (int i = 0; i < 500; i++) {
            points.add(source.nextPoint());
            tree.add(points.get(i), i);
        }
        this.leafs = tree.getLeafRegions();

        this.test = new WB_Point(350, 0);

        WB_KDTreeInteger.WB_KDEntryInteger<WB_Point> entry = tree.getNearestNeighbor(test);
        System.out.println(entry.d2);
        System.out.println(entry.value);
        System.out.println(entry.coord);
        this.near = entry.coord;
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        noFill();
        stroke(0);
        if (drawAABB) {
            for (WB_AABB aabb : leafs) {
                render.drawAABB(aabb);
            }
        }

        stroke(255, 0, 0);
        render.drawPoint(points, 5);

        fill(255,0,0);
        render.drawPoint(test,10);
        render.drawPoint(near,10);
    }

    public void keyPressed() {
        if (key == 'q') {
            drawAABB = !drawAABB;
        }
        if (key == 'w') {

        }
    }


}
