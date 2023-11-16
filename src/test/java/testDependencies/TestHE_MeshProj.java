package testDependencies;

import guo_cam.CameraController;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HEC_Archimedes;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2023/10/7
 * @time 17:09
 */
public class TestHE_MeshProj extends PApplet {
    public static void main(String[] args) {
        PApplet.main("testDependencies.TestHE_MeshProj");
    }

    /* ------------- settings ------------- */

    private WB_Polygon poly0;
    private WB_Polygon poly1;
    private WB_Polygon poly2;
    private WB_Polygon poly3;
    private WB_Polygon poly4;
    private HE_Mesh mesh;

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Render render;
    private CameraController gcam;
    private WB_Polygon orthoPoly;
    private WB_Polygon planarPoly;

    public void setup() {
        render = new WB_Render(this);
        gcam = new CameraController(this);


        HEC_Archimedes archimedes = new HEC_Archimedes();
        this.mesh = archimedes.create();

        this.orthoPoly = mesh.getFaceWithIndex(2).getOrthoPolygon();
        this.planarPoly = mesh.getFaceWithIndex(2).getPlanarPolygon();
    }

    private void createMesh() {
        WB_Point[] pts0 = new WB_Point[5];
        pts0[0] = new WB_Point(200, 400);
        pts0[1] = new WB_Point(700, 100);
        pts0[2] = new WB_Point(800, 400);
        pts0[3] = new WB_Point(200, 400);
        pts0[4] = new WB_Point(200, 400);
        poly0 = new WB_Polygon(pts0);

        WB_Point[] pts1 = new WB_Point[5];
        pts1[0] = new WB_Point(100, 100);
        pts1[1] = new WB_Point(700, 100);
        pts1[2] = new WB_Point(800, 400);
        pts1[3] = new WB_Point(200, 400);
        pts1[4] = new WB_Point(100, 100);
        poly1 = new WB_Polygon(pts1);

        WB_Point[] pts2 = new WB_Point[5];
        pts2[0] = new WB_Point(100, 100);
        pts2[1] = new WB_Point(100, -100);
        pts2[2] = new WB_Point(700, -100);
        pts2[3] = new WB_Point(700, 100);
        pts2[4] = new WB_Point(100, 100);
        poly2 = new WB_Polygon(pts2);

        WB_Point[] pts3 = new WB_Point[6];
        pts3[0] = new WB_Point(700, 100);
        pts3[1] = new WB_Point(700, -100);
        pts3[2] = new WB_Point(1000, -100);
        pts3[3] = new WB_Point(1200, 0);
        pts3[4] = new WB_Point(1000, 100);
        pts3[5] = new WB_Point(700, 100);
        poly3 = new WB_Polygon(pts3);

        WB_Point[] pts4 = new WB_Point[6];
        pts4[0] = new WB_Point(800, 400);
        pts4[1] = new WB_Point(700, 100);
        pts4[2] = new WB_Point(1000, 100);
        pts4[3] = new WB_Point(1200, 0);
        pts4[4] = new WB_Point(1200, 400);
        pts4[5] = new WB_Point(800, 400);
        poly4 = new WB_Polygon(pts4);

        // create mesh
        WB_Polygon[] polys = new WB_Polygon[]{
                poly1, poly2, poly3, poly4
        };
        for (WB_Polygon p : polys) {
            System.out.println("origin  " + p.getNormal());
        }
        this.mesh = new HEC_FromPolygons(polys).create();


    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(1000);

        noFill();
        for (HE_Face f : mesh.getFaces()) {
            render.drawFace(f);
        }


        fill(255, 0, 0);
        render.drawPolygonEdges(orthoPoly);
        fill(0, 255, 0);
        render.drawPolygonEdges(planarPoly);
    }

}
