package testDependencies;

import advancedGeometry.ZAABBTree;
import basicGeometry.ZPoint;
import guo_cam.CameraController;
import guo_cam.Vec_Guo;
import processing.core.PApplet;
import render.ZRender;
import wblut.geom.*;
import wblut.hemesh.HEC_Beethoven;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

import java.util.Arrays;
import java.util.List;

/**
 * test Guo_Cam pick 3D from screen
 *
 * @author ZHANG Baizhou zhangbz
 * @project Ztools
 * @date 2022/8/2
 * @time 15:19
 */
public class TestGuoCamPick extends PApplet {

    public static void main(String[] args) {
        PApplet.main("testDependencies.TestGuoCamPick");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private CameraController gcam;
    private WB_Render render;

    private WB_Polygon polygon;
    private HE_Mesh mesh;
    private ZAABBTree zaabbTree;
    private WB_AABBTree aabbTree;
    private ZPoint baseFromScreen = new ZPoint(0, 0, 0);
    private ZPoint vecFromScreen = new ZPoint(1, 0, 0);
    private WB_Ray ray;
    private boolean intersectPolyAABB = false;
    private boolean intersectMeshAABB = false;

    public void setup() {
        this.gcam = new CameraController(this);
        this.render = new WB_Render(this);
        gcam.begin3d();

        WB_Point[] pts = new WB_Point[]{
                new WB_Point(600, 100, 200),
                new WB_Point(700, 100, 200),
                new WB_Point(700, 200, 200),
                new WB_Point(800, 300, 200),
                new WB_Point(700, 400, 200),
                new WB_Point(600, 100, 200)
        };
        this.polygon = new WB_Polygon(pts);
        this.mesh = new HE_Mesh(new HEC_Beethoven().setScale(10).setZAxis(0, 1, 0).setZAngle(PI));
        this.zaabbTree = new ZAABBTree(mesh, 30);
        this.aabbTree = zaabbTree.getOriginalAABBTree();
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(500);

        // polygon
        if (intersectPolyAABB) {
            stroke(0);
            fill(0, 255, 0);
        } else {
            stroke(0);
            fill(200);
        }
        render.drawPolygonEdges(polygon);
        noFill();
        render.drawAABB(polygon.getAABB());

        // mesh
        if (intersectMeshAABB) {
            stroke(0);
            render.drawEdges(mesh);
            fill(0, 255, 0);
            noStroke();
            render.drawFaces(mesh);
        } else {
            stroke(0);
            render.drawEdges(mesh);
            fill(200);
            noStroke();
            render.drawFaces(mesh);
        }
        stroke(255, 0, 0);
        noFill();
        for (WB_AABB aabb : zaabbTree.getLeafAABBs()) {
            render.drawAABB(aabb);
        }


        stroke(255, 0, 0);
        fill(255, 0, 0);
        ZRender.drawZPointAsVec3D(this, vecFromScreen, baseFromScreen, 50, 3);
    }

    public void mouseClicked() {
        Vec_Guo[] pick = gcam.pick3d(mouseX, mouseY);
        System.out.println(Arrays.toString(pick));
        this.baseFromScreen = new ZPoint(pick[0].x, pick[0].y, pick[0].z);
        this.vecFromScreen = new ZPoint(pick[1].x, pick[1].y, pick[1].z);

        this.ray = new WB_Ray(baseFromScreen.toWB_Point(), vecFromScreen.toWB_Point());
        this.intersectPolyAABB = WB_GeometryOp.checkIntersection3D(ray, polygon.getAABB());
        List<WB_AABBTree.WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(ray, aabbTree);
        this.intersectMeshAABB = nodes.size() > 0;
    }
}
