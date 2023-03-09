package testMyself;

import advancedGeometry.ZSkeleton;
import guo_cam.CameraController;
import math.ZMath;
import org.locationtech.jts.geom.*;
import org.twak.camp.Machine;
import org.twak.camp.Output;
import org.twak.camp.Skeleton;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

/**
 * test ZSkeleton with holes
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/5
 * @time 14:33
 */
public class Test3Skeleton extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private Polygon polygon;
    private WB_Polygon wb_polygon;
    private GeometryFactory gf = new GeometryFactory();

    private ZSkeleton skeleton;

    // utils
    private CameraController gcam;
    private JtsRender jtsRender;
    private WB_Render render;

    public void setup() {
        this.gcam = new CameraController(this);
        this.jtsRender = new JtsRender(this);
        this.render = new WB_Render(this);

        createPolygon();

        this.skeleton = new ZSkeleton(wb_polygon, 0, true);
    }

    public void createPolygon() {
        Coordinate[] outer = new Coordinate[6]; // counter clockwise
        outer[0] = new Coordinate(100, 100, 0);
        outer[1] = new Coordinate(700, 100, 0);
        outer[2] = new Coordinate(800, 400, 0);
        outer[3] = new Coordinate(500, 800, 0);
        outer[4] = new Coordinate(100, 600, 0);
        outer[5] = new Coordinate(100, 100, 0);
        LinearRing outerL = gf.createLinearRing(outer);

        Coordinate[] inner0 = new Coordinate[5]; // clockwise
        inner0[0] = new Coordinate(250, 200, 0);
        inner0[1] = new Coordinate(250, 400, 0);
        inner0[2] = new Coordinate(450, 400, 0);
        inner0[3] = new Coordinate(450, 200, 0);
        inner0[4] = new Coordinate(250, 200, 0);
        LinearRing innerL1 = gf.createLinearRing(inner0);

        Coordinate[] inner1 = new Coordinate[5]; // clockwise
        inner1[0] = new Coordinate(500, 500, 0);
        inner1[1] = new Coordinate(400, 600, 0);
        inner1[2] = new Coordinate(500, 700, 0);
        inner1[3] = new Coordinate(600, 500, 0);
        inner1[4] = new Coordinate(500, 500, 0);
        LinearRing innerL2 = gf.createLinearRing(inner1);

        LinearRing[] innerL = new LinearRing[2];
        innerL[0] = innerL1;
        innerL[1] = innerL2;

        this.polygon = gf.createPolygon(outerL, innerL);
        println("polygon.getNumInteriorRing() " + polygon.getNumInteriorRing());
        println("polygon.isSimple() " + polygon.isSimple());

        this.wb_polygon = ZTransform.PolygonToWB_Polygon(polygon);
        println("wb_polygon.isSimple() " + wb_polygon.isSimple());
        println("wb_polygon.getNormal() " + wb_polygon.getNormal());
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        fill(128);
        render.drawPolygonEdges2D(wb_polygon);
        fill(0);
        textSize(15);
        for (int i = 0; i < wb_polygon.getNumberOfShellPoints(); i++) {
            text(i, wb_polygon.getPoint(i).xf(), wb_polygon.getPoint(i).yf());
        }
        int[] npc = wb_polygon.getNumberOfPointsPerContour();
        int index = wb_polygon.getNumberOfShellPoints();
        for (int i = 0; i < wb_polygon.getNumberOfHoles(); i++) {
            for (int j = 0; j < npc[i + 1]; j++) {
                text(j, wb_polygon.getPoint(j + index).xf(), wb_polygon.getPoint(j + index).yf());
            }
            index += npc[i + 1];
        }
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            LineString curr = polygon.getInteriorRingN(i);
            for (int j = 0; j < curr.getNumPoints(); j++) {
                text(j, (float) curr.getCoordinates()[j].x, (float) curr.getCoordinates()[j].y);
            }
        }

        translate(1000, 0, 0);
        fill(200);
        jtsRender.drawGeometry(polygon);
        fill(0);
        textSize(15);
        for (int i = 0; i < polygon.getExteriorRing().getNumPoints(); i++) {
            text(i, (float) polygon.getCoordinates()[i].x, (float) polygon.getCoordinates()[i].y);
        }
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            LineString curr = polygon.getInteriorRingN(i);
            for (int j = 0; j < curr.getNumPoints(); j++) {
                text(j, (float) curr.getCoordinates()[j].x, (float) curr.getCoordinates()[j].y);
            }
        }

        skeleton.display(this);
    }

    private double mach = Math.PI / 4;

    public void mouseClicked() {
        Skeleton skel = this.skeleton.getSkeleton();
        for (Output.Face f : skel.output.faces.values()) {
            mach += ZMath.random(0, Math.PI / 16);
            f.edge.machine = new Machine(mach);
        }
    }

}
