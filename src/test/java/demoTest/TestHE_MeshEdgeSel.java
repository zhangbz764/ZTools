package demoTest;

import guo_cam.CameraController;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.hemesh.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * test select edges between each pair of polygon faces
 *
 * @author ZHANG Baizhou zhangbz
 * @project shopping_mall
 * @date 2022/4/4
 * @time 13:31
 */
public class TestHE_MeshEdgeSel extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Polygon p0, p1, p2, p3, p4, p5, p6;
    private HE_Mesh mesh;

    private List<List<HE_Halfedge>> coEdges;

    private WB_Render render;
    private CameraController gcam;

    public void setup() {
        this.render = new WB_Render(this);
        this.gcam = new CameraController(this);
        gcam.top();

        createMesh2();
    }

    private void createMesh2() {
        p0 = new WB_Polygon(
                new WB_Point[]{
                        new WB_Point(0, 0),
                        new WB_Point(100, 0),
                        new WB_Point(100, 100),
                        new WB_Point(200, 200),
                        new WB_Point(0, 200),
                        new WB_Point(0, 0)
                }
        );
        p1 = new WB_Polygon(
                new WB_Point[]{
                        new WB_Point(100, 0),
                        new WB_Point(200, 0),
                        new WB_Point(200, 100),
                        new WB_Point(200, 200),
                        new WB_Point(100, 100),
                        new WB_Point(100, 0)
                }
        );
        p2 = new WB_Polygon(
                new WB_Point[]{
                        new WB_Point(200, 0),
                        new WB_Point(300, 0),
                        new WB_Point(300, 200),
                        new WB_Point(200, 200),
                        new WB_Point(200, 100),
                        new WB_Point(200, 0)
                }
        );
        p3 = new WB_Polygon(
                new WB_Point[]{
                        new WB_Point(300, 0),
                        new WB_Point(600, 0),
                        new WB_Point(450, 100),
                        new WB_Point(400, 200),
                        new WB_Point(300, 200),
                        new WB_Point(300, 0)
                }
        );
        p4 = new WB_Polygon(
                new WB_Point[]{
                        new WB_Point(400, 200),
                        new WB_Point(450, 100),
                        new WB_Point(600, 0),
                        new WB_Point(600, 600),
                        new WB_Point(500, 500),
                        new WB_Point(400, 400),
                        new WB_Point(400, 200)
                }
        );
        p5 = new WB_Polygon(
                new WB_Point[]{
                        new WB_Point(400, 400),
                        new WB_Point(500, 500),
                        new WB_Point(600, 600),
                        new WB_Point(-200, 600),
                        new WB_Point(-100, 500),
                        new WB_Point(400, 400)
                }
        );
        p6 = new WB_Polygon(
                new WB_Point[]{
                        new WB_Point(0, 0),
                        new WB_Point(0, 200),
                        new WB_Point(-100, 500),
                        new WB_Point(-200, 600),
                        new WB_Point(0, 0)
                }
        );

        this.mesh = new HEC_FromPolygons(new WB_Polygon[]{p0, p1, p2, p3, p4, p5, p6}).create();
        System.out.println("vertex num  " + mesh.getNumberOfVertices());
        System.out.println("haleedge num  " + mesh.getNumberOfHalfedges());
        System.out.println("edge num  " + mesh.getNumberOfEdges());
        for (HE_Face f : mesh.getFaces()) {
            System.out.println(f.getFaceEdges().size());
            System.out.println(f.getFaceEdges());
        }

        coEdges = new ArrayList<>();

        List<Integer> visited = new ArrayList<>();
        for (int i = 0; i < mesh.getFaces().size(); i++) {
            HE_Face sel = mesh.getFaceWithIndex(i);
            List<HE_Face> nei = sel.getNeighborFaces();

            for (int j = 0; j < nei.size(); j++) {
                HE_Face f = nei.get(j);
                if (!visited.contains(f.getInternalLabel())) {
                    HE_Halfedge e0 = sel.getHalfedge(f);
                    HE_Halfedge curr = e0.getNextInFace();

                    List<HE_Halfedge> co = new ArrayList<>();
                    co.add(e0);
                    int count = 0;
                    while (curr != e0) {
                        System.out.println("loop " + count + " curr  " + curr);
                        System.out.println("f.getFaceEdges()  " + f.getFaceEdges());
                        if (f.getFaceEdges().contains(curr.getPair()) || f.getFaceEdges().contains(curr)) {
                            co.add(curr);
                        }
                        curr = curr.getNextInFace();
                        count++;
                    }
                    coEdges.add(co);
                }
            }
            visited.add(i);
        }

        System.out.println(visited);

        for (List<HE_Halfedge> cos : coEdges) {
            System.out.println(cos.size());
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        noFill();
        stroke(0);
        strokeWeight(1);
        render.drawPolygonEdges2D(p0);
        render.drawPolygonEdges2D(p1);
        render.drawPolygonEdges2D(p2);
        render.drawPolygonEdges2D(p3);
        render.drawPolygonEdges2D(p4);
        render.drawPolygonEdges2D(p5);
        render.drawPolygonEdges2D(p6);
        for (HE_Vertex v : mesh.getVertices()) {
            render.drawVertex(v, 4);
        }

        stroke(255, 0, 0);
        strokeWeight(3);
        for (List<HE_Halfedge> list : coEdges) {
            for (HE_Halfedge he : list) {
                render.drawEdge(he);
            }
        }

//        stroke(0);
//        strokeWeight(1);
//        fill(220);
//        for (HE_Face f : nei) {
//            render.drawPolygonEdges2D(f.getPolygon());
//        }

        // text index
        fill(0);
        for (int j = 0; j < mesh.getFaces().size(); j++) {
            HE_Face f = mesh.getFaceWithIndex(j);
            text(f.getInternalLabel(), f.getFaceCenter().xf(), f.getFaceCenter().yf());
        }
    }

}
