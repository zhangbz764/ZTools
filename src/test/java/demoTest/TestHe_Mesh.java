package demoTest;

import Guo_Cam.CameraController;
import geometry.ZGeoFactory;
import math.ZGeoMath;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Vertex;
import wblut.processing.WB_Render;

import java.util.List;

/**
 * 测试hemesh的mesh以及union
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/11/7
 * @time 20:00
 */
public class TestHe_Mesh extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    WB_Polygon poly0;
    WB_Polygon poly1;
    WB_Polygon poly2;
    WB_Polygon poly3;
    WB_Polygon poly4;

    WB_Polygon buffer;

    HE_Mesh mesh;

    WB_Render render;
    CameraController gcam;
    WB_GeometryFactory wbgf = new WB_GeometryFactory();

    /* ------------- setup ------------- */

    /**
     * set up 5 polygons
     *
     * @param
     * @return void
     */
    private void setPolys() {
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
        System.out.println(poly0.getSimplePolygon().getNumberOfPoints());

        // hemesh中buffer之后的多边形少一个点
        // jts的buffer功能使多边形反面
        buffer = ZGeoMath.polygonFaceUp(
                ZTransform.validateWB_Polygon(
                        ZGeoFactory.wbgf.createBufferedPolygonsStraight2D(poly1, 50).get(0)
                )
        );
    }

    public void setup() {
        gcam = new CameraController(this);
        render = new WB_Render(this);
        setPolys();

        WB_Polygon[] polys = new WB_Polygon[]{
                poly1, poly2, poly3, poly4
        };
//        WB_Polygon[] polys = new WB_Polygon[]{
//                ZGeoMath.reversePolygon(poly1),
//                ZGeoMath.reversePolygon(poly2),
//                ZGeoMath.reversePolygon(poly3),
//                ZGeoMath.reversePolygon(poly4)
//        };
        for (WB_Polygon p : polys) {
            System.out.println("origin  " + p.getNormal());
        }
        mesh = new HEC_FromPolygons(polys).create();

        System.out.println(mesh.getVertices().size());
        System.out.println(mesh.getAllBoundaryVertices().size());
        for (HE_Face f : mesh.getFaces()) {
            System.out.println("area  " + f.getFaceArea());
        }
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
        Polygon test = ZTransform.WB_PolygonToJtsPolygon(p1);
        test = (Polygon) test.union(ZTransform.WB_PolygonToJtsPolygon(p2));
        WB_Polygon testWB = ZTransform.jtsPolygonToWB_Polygon(test);

        List<WB_Polygon> union = wbgf.unionPolygons2D(p1, p2);
        println("union:" + union.size());

        List<WB_Polygon> newPolygons = mesh.getPolygonList();
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

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        box(20, 20, 20); // origin
        strokeWeight(1);

        // draw mesh
        for (int i = 0; i < mesh.getEdges().size(); i++) {
            render.drawEdge(mesh.getEdges().get(i));
        }

        render.drawPolygonEdges2D(buffer);

        // text index
        fill(0);
        for (int j = 0; j < mesh.getFaces().size(); j++) {
            text(j, mesh.getFaceWithIndex(j).getFaceCenter().xf(), mesh.getFaceWithIndex(j).getFaceCenter().yf());
        }
        for (int i = 0; i < poly1.getNumberOfPoints(); i++) {
            text(i, poly1.getPoint(i).xf(), poly1.getPoint(i).yf());
        }
        for (int i = 0; i < buffer.getNumberOfPoints(); i++) {
            text(i, buffer.getPoint(i).xf(), buffer.getPoint(i).yf());
        }

        noFill();
        for (int i = 0; i < mesh.getAllBoundaryVertices().size(); i++) {
            ellipse(mesh.getAllBoundaryVertices().get(i).xf(),
                    mesh.getAllBoundaryVertices().get(i).yf(),
                    10, 10);
        }
        for (HE_Vertex v : mesh.getFaces().get(0).getFaceVertices()) {
            ellipse(v.xf(), v.yf(), 20, 20);
        }
    }

    public void keyPressed() {
        if (key == '1') {
            mesh.getFaces().get(0).getFaceVertices().get(0).set(mouseX, mouseY);
        } else {
            optimize();
        }
    }

}
