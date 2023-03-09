package demoTest;

import advancedGeometry.ZAABBTree;
import guo_cam.CameraController;
import processing.core.PApplet;
import wblut.geom.WB_AABB;
import wblut.hemesh.HEC_Beethoven;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

import java.util.List;

/**
 * test ZAABBTree
 *
 * @author ZHANG Baizhou zhangbz
 * @project Ztools
 * @date 2022/7/30
 * @time 15:40
 */
public class TestZAABBTree extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    private WB_Render render;
    private CameraController gcam;

    private HE_Mesh btMesh;
    private ZAABBTree zaabbTree;
    private List<List<WB_AABB>> aabbEachDepth;
    int count = 0;
    private List<WB_AABB> aabbLeaves;

    private boolean draw = true;

    /* ------------- setup ------------- */

    public void setup() {
        this.beginContour();
        this.render = new WB_Render(this);
        this.gcam = new CameraController(this);

        this.btMesh = new HE_Mesh(new HEC_Beethoven().setScale(10).setZAxis(0, 1, 0).setZAngle(PI));

        this.zaabbTree = new ZAABBTree(btMesh, 10);
        this.aabbEachDepth = zaabbTree.getAllAABBs();
        this.aabbLeaves = zaabbTree.getLeafAABBs();

        System.out.println("leaf AABB num: " + zaabbTree.getLeafAABBNum());
        System.out.println("current depth: " + count + "    aabb num: " + aabbEachDepth.get(count).size());
    }


    public void keyPressed() {
        if (key == 'q') {
            count = (count + 1) % (zaabbTree.getDepth() + 1);
            System.out.println("current depth: " + count + "    aabb num: " + aabbEachDepth.get(count).size());
        }
        if (key == 'w') {
            draw = !draw;
            if (draw) {
                System.out.println("current depth: " + count + "    aabb num: " + aabbEachDepth.get(count).size());
            } else {
                System.out.println("leaf AABB num: " + zaabbTree.getLeafAABBNum());
            }
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        noFill();
        stroke(255, 0, 0);
        render.drawEdges(btMesh);
        fill(200);
        noStroke();
        render.drawFaces(btMesh);

        stroke(0, 0, 255);
        noFill();
        if (draw) {
            // draw each layer
            for (WB_AABB aabb : aabbEachDepth.get(count)) {
                render.drawAABB(aabb);
            }
        } else {
            // draw leaves
            for (WB_AABB aabb : aabbLeaves) {
                render.drawAABB(aabb);
            }
        }

    }

}
