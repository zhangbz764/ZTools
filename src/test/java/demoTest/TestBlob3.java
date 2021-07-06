package demoTest;

import basicGeometry.ZPoint;
import processing.core.PApplet;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/6/18
 * @time 14:32
 */
public class TestBlob3 extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Polygon boundary;
    private ZPoint blobCenter;
    private ZPoint[] blobNodes;
    private int nodeNum = 50;
    private double initR = 10;

    private WB_Render render;

    public void setup() {
        this.render = new WB_Render(this);

        WB_Coord[] coords = new WB_Coord[6];
        coords[0] = new WB_Point(300, 300);
        coords[1] = new WB_Point(600, 400);
        coords[2] = new WB_Point(800, 500);
        coords[3] = new WB_Point(600, 700);
        coords[4] = new WB_Point(400, 600);
        coords[5] = coords[0];

        this.boundary = new WB_Polygon(coords);

        this.blobCenter = new ZPoint(500, 500);
        this.blobNodes = new ZPoint[nodeNum];

        double theta = Math.PI * 2 / nodeNum;
        for (int i = 0; i < nodeNum; i++) {
            blobNodes[i] = blobCenter.add(new ZPoint(initR * Math.cos(i * theta), initR * Math.sin(i * theta)));
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        render.drawPolygonEdges2D(boundary);

        blobCenter.displayAsPoint(this, 2);
        for (ZPoint p : blobNodes) {
            p.displayAsPoint(this, 1);
        }
    }

    public void update() {
        // reset center

        // force between nodes
//        for(){
//
//        }
    }

    private void setBlobCenter() {
        double cx = 0;
        double cy = 0;
        for (int i = 0; i < blobNodes.length; i++) {
            int j = i + 1;
            if (j >= blobNodes.length) {
                j = 0;
            }
            cx += (
                    blobNodes[i].xd() + blobNodes[j].xd())
                    *
                    (blobNodes[i].xd() * blobNodes[j].yd() - blobNodes[j].xd() * blobNodes[i].yd()
                    );
            cy += (
                    blobNodes[i].yd() + blobNodes[j].yd())
                    *
                    (blobNodes[i].xd() * blobNodes[j].yd() - blobNodes[j].xd() * blobNodes[i].yd()
                    );
        }
        blobCenter.set(cx, cy);
        System.out.println(blobCenter);
    }

    public void keyPressed() {
        if (key == 'u') {
            setBlobCenter();
        }
    }
}
