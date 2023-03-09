package testFunc;

import guo_cam.CameraController;
import processing.core.PApplet;
import wblut.geom.WB_Plane;
import wblut.hemesh.*;
import wblut.processing.WB_Render;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project shopping_mall
 * @date 2021/12/14
 * @time 10:32
 */
public class TestHE_MeshSlice extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private HE_Mesh mesh;
    private WB_Render render;
    private WB_Plane P;
    private HEM_Slice modifier1;
    private HEM_SliceSurface modifier2;

    private CameraController gcam;

    public void setup() {
        this.gcam = new CameraController(this);
        this.render = new WB_Render(this);

        createMesh();
        System.out.println(mesh.getSelectionNames());
        System.out.println(mesh.getNumberOfFaces());
        this.modifier1 = new HEM_Slice();

        this.P = new WB_Plane(0, 0, 20, 1, 1, 1);
        modifier1.setPlane(P);// Cut plane
        //you can also pass directly as origin and normal:  modifier.setPlane(0,0,-200,0,0,1)
        modifier1.setOffset(0); // shift cut plane along normal
        modifier1.setReverse(true);
        mesh.modify(modifier1);
        System.out.println(mesh.getSelectionNames());
        System.out.println(mesh.getNumberOfFaces());

//        createMesh();
//        System.out.println(mesh.getSelectionNames());
//        System.out.println(mesh.getNumberOfFaces());
//        this.modifier2 = new HEM_SliceSurface();
//
//        this.P = new WB_Plane(0, 0, 20, 1, 1, 1);
//        modifier2.setPlane(P);// Cut plane
//        //you can also pass directly as origin and normal:  modifier.setPlane(0,0,-200,0,0,1)
//        modifier2.setOffset(0);// shift cut plane along normal
//        mesh.modify(modifier2);
//        System.out.println(mesh.getSelectionNames());
//        System.out.println(mesh.getNumberOfFaces());
    }

    private void createMesh() {
        HEC_Box creator = new HEC_Box();
        creator.setSize(100, 100, 500);
        creator.setWidthSegments(10).setHeightSegments(10).setDepthSegments(30);
        this.mesh = new HE_Mesh(creator);
        HET_Diagnosis.validate(mesh);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        fill(255);
        noStroke();
        render.drawFaces(mesh);
        fill(255, 0, 0);
        render.drawFaces(mesh.getSelection("caps"));
        fill(0, 0, 255);
        render.drawFaces(mesh.getSelection("cuts"));
        noFill();
        stroke(0);
        render.drawEdges(mesh);
        stroke(0, 255, 0);
        render.drawEdges(mesh.getSelection("edges"));
        stroke(255, 0, 0);
        render.drawPlane(P, 300);
    }

}
