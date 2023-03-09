package testFunc;

import basicGeometry.ZFactory;
import guo_cam.CameraController;
import math.ZGeoMath;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * test hemesh mesh and union，buffer
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/11/7
 * @time 20:00
 */
public class TestHE_Mesh extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    private WB_Polygon poly0;
    private WB_Polygon poly1;
    private WB_Polygon poly2;
    private WB_Polygon poly3;
    private WB_Polygon poly4;
    private WB_Polygon buffer;
    private HE_Mesh mesh;

    private HE_Mesh mesh2;
    private HE_Halfedge co;

    private WB_Render render;
    private CameraController gcam;
    private WB_GeometryFactory wbgf = new WB_GeometryFactory();

    /* ------------- setup ------------- */

    public void setup() {
        this.gcam = new CameraController(this);
        this.render = new WB_Render(this);

        createMesh();
        createMesh2();

        System.out.println("v: " + mesh.getVertices().size());
        System.out.println("bv: " + mesh.getAllBoundaryVertices().size());
        for (HE_Face f : mesh.getFaces()) {
            System.out.println("area  " + f.getFaceArea());
        }
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

        // after buffer , the polygon is invalid
        // also face down
        this.buffer = ZGeoMath.polygonFaceUp(
                ZTransform.validateWB_Polygon(
                        ZFactory.wbgf.createBufferedPolygonsStraight2D(poly1, 50).get(0)
                )
        );

        // create mesh
        WB_Polygon[] polys = new WB_Polygon[]{
                poly1, poly2, poly3, poly4
        };
        for (WB_Polygon p : polys) {
            System.out.println("origin  " + p.getNormal());
        }
        this.mesh = new HEC_FromPolygons(polys).create();
    }

    private void createMesh2() {
        WB_Polygon p0 = new WB_Polygon(
                new WB_Point[]{
                        new WB_Point(0, 0),
                        new WB_Point(100, 0),
                        new WB_Point(100, 50),
                        new WB_Point(100, 100),
                        new WB_Point(100, 150),
                        new WB_Point(100, 200),
                        new WB_Point(0, 200),
                        new WB_Point(0, 0)
                }
        );
        WB_Polygon p1 = new WB_Polygon(
                new WB_Point[]{
                        new WB_Point(100, 0),
                        new WB_Point(200, 0),
                        new WB_Point(200, 30),
                        new WB_Point(100, 30),
                        new WB_Point(100, 0)
                }
        );
        WB_Polygon p2 = new WB_Polygon(
                new WB_Point[]{
                        new WB_Point(100, 30),
                        new WB_Point(200, 30),
                        new WB_Point(200, 130),
                        new WB_Point(100, 130),
                        new WB_Point(100, 100),
                        new WB_Point(100, 50),
                        new WB_Point(100, 30)
                }
        );
        WB_Polygon p3 = new WB_Polygon(
                new WB_Point[]{
                        new WB_Point(100, 130),
                        new WB_Point(200, 130),
                        new WB_Point(200, 200),
                        new WB_Point(100, 200),
                        new WB_Point(100, 150),
                        new WB_Point(100, 130)
                }
        );
        this.mesh2 = new HEC_FromPolygons(new WB_Polygon[]{p0, p1, p2, p3}).create();
        HE_Face f0 = mesh2.getFaceWithIndex(0);
        HE_Face f2 = mesh2.getFaceWithIndex(2);
        this.co =  f2.getHalfedge(f0);
    }

    /**
     * union 2 neighbor faces, update mesh
     *
     * @param
     * @return void
     */
    private void optimize() {
        WB_Polygon p1 = mesh.getFaces().get(0).getPolygon();
        WB_Polygon p2 = mesh.getFaces().get(0).getNeighborFaces().get(0).getPolygon();

        System.out.println("mesh face1  " + p1.getNormal());
        System.out.println("mesh face2  " + p2.getNormal());

        HE_Face f1 = mesh.getFaces().get(0);
        HE_Face f2 = mesh.getFaces().get(0).getNeighborFaces().get(0);

        mesh.remove(f1);
        mesh.remove(f2);
        Polygon test = ZTransform.WB_PolygonToPolygon(p1);
        test = (Polygon) test.union(ZTransform.WB_PolygonToPolygon(p2));
        WB_Polygon testWB = ZTransform.PolygonToWB_Polygon(test);

        List<WB_Polygon> union = wbgf.unionPolygons2D(p1, p2);
        println("union:" + union.size());

        List<WB_Polygon> newPolygons = new ArrayList<>();
        System.out.println("faces num: " + mesh.getFaces().size());
        for (int i = 0; i < mesh.getFaces().size(); i++) {
            newPolygons.add(mesh.getFaceWithIndex(i).getPolygon());
        }

        System.out.println("union  " + testWB.getNormal());
        if (testWB.getNormal().equals(p1.getNormal())) {
            newPolygons.add(testWB);
        } else {
            newPolygons.add(ZGeoMath.reversePolygon(testWB));
        }

        mesh = new HEC_FromPolygons(newPolygons).create();
        for (HE_Face f : mesh.getFaces()) {
            System.out.println("area  " + f.getFaceArea());
        }
    }

    private void optimize2() {
        List<WB_Polygon> all = new ArrayList<>();
        all.add(poly1);
        all.add(poly2);
        all.add(poly3);
        all.add(poly4);
        List<WB_Polygon> select = new ArrayList<>();
        select.add(poly1);
        select.add(poly2);
        all.removeAll(select);


        List<WB_Polygon> union = wbgf.unionPolygons2D(poly1, poly2);
        println("union:" + union.size());

        all.addAll(union);
        mesh = new HEC_FromPolygons(all).create();
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        strokeWeight(1);
        stroke(0);
        // draw mesh
//        for (int i = 0; i < mesh.getEdges().size(); i++) {
//            render.drawEdge(mesh.getEdges().get(i));
//        }
        // draw mesh2
        for (int i = 0; i < mesh2.getEdges().size(); i++) {
            render.drawEdge(mesh2.getEdges().get(i));
        }
        // text index
        fill(0);
        for (int j = 0; j < mesh2.getFaces().size(); j++) {
            text(j, mesh2.getFaceWithIndex(j).getFaceCenter().xf(), mesh2.getFaceWithIndex(j).getFaceCenter().yf());
        }
        strokeWeight(3);
        stroke(255,0,0);
        render.drawEdge(co);

//        render.drawPolygonEdges2D(buffer);
//
//        // text index
//        fill(0);
//        for (int j = 0; j < mesh.getFaces().size(); j++) {
//            text(j, mesh.getFaceWithIndex(j).getFaceCenter().xf(), mesh.getFaceWithIndex(j).getFaceCenter().yf());
//        }
//        for (int i = 0; i < poly1.getNumberOfPoints(); i++) {
//            text(i, poly1.getPoint(i).xf(), poly1.getPoint(i).yf());
//        }
//        for (int i = 0; i < buffer.getNumberOfPoints(); i++) {
//            text(i, buffer.getPoint(i).xf(), buffer.getPoint(i).yf());
//        }
//
//        noFill();
//        stroke(255, 0, 0);
//        for (int i = 0; i < mesh.getAllBoundaryVertices().size(); i++) {
//            ellipse(mesh.getAllBoundaryVertices().get(i).xf(),
//                    mesh.getAllBoundaryVertices().get(i).yf(),
//                    10, 10);
//        }
//        stroke(0, 0, 255);
//        for (HE_Vertex v : mesh.getFaces().get(0).getFaceVertices()) {
//            ellipse(v.xf(), v.yf(), 20, 20);
//        }
    }

    public void keyPressed() {
        if (key == '1') {
            mesh.getFaces().get(0).getFaceVertices().get(0).set(mouseX, mouseY);
        }
        if (key == 'o') {
            optimize();
        }
        if (key == 'p') {
            optimize2();
        }
    }
}
