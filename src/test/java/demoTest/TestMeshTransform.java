package demoTest;

import guo_cam.CameraController;
import igeo.IG;
import igeo.IMesh;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project Ztools
 * @date 2022/8/12
 * @time 16:56
 */
public class TestMeshTransform extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Render render;
    private CameraController gcam;

    private HE_Mesh mesh;

    public void setup() {
        this.render = new WB_Render(this);
        this.gcam = new CameraController(this);

        // import
        String path = ".\\src\\test\\resources\\test_mesh_trans.3dm";
        IG.init();
        IG.open(path);

        IMesh[] meshes = IG.layer("mesh").meshes();
        this.mesh = ZTransform.IMeshToHE_Mesh(meshes[0]);

        System.out.println(mesh.getNumberOfFaces());
        System.out.println(mesh.getNumberOfVertices());
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(300);

        // draw mesh
        strokeWeight(1);
        stroke(0);
        render.drawEdges(mesh);
        fill(200, 150);
        noStroke();
        render.drawFaces(mesh);
    }

}
