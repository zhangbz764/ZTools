package subdivision;

import math.ZMath;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Geometry;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;
import wblut.hemesh.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * interface of subdivision
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/1
 * @time 23:02
 */
public abstract class ZSubdivision {
    public boolean randomMode = false;
    public double randomThreshold = 1;

    private final WB_Polygon originPolygon;
    private List<WB_Polygon> allSubPolygons;
    List<WB_Polygon> redundantPolygons = new ArrayList<>();
    private HE_Mesh subdivideMesh;
    private WB_Vector[][] allFaceVertexVectors;

    private int[][] randomColor;

    /* ------------- constructor ------------- */

    public ZSubdivision(WB_Polygon originPolygon) {
        this.originPolygon = originPolygon;
    }

    /**
     * main method to perform subdivision
     *
     * @param
     * @return void
     */
    public abstract void performDivide();

    public void initializeShapeVector() {
        allFaceVertexVectors = new WB_Vector[subdivideMesh.getNumberOfFaces()][];
        for (int i = 0; i < allFaceVertexVectors.length; i++) {
            allFaceVertexVectors[i] = new WB_Vector[subdivideMesh.getFaceWithIndex(i).getFaceVertices().size()];
            for (int j = 0; j < allFaceVertexVectors[i].length; j++) {
                allFaceVertexVectors[i][j] = new WB_Vector(ZMath.random(-0.3, 0.3), ZMath.random(-0.3, 0.3));
            }
        }
    }

    /**
     * main method to update square shape
     *
     * @param
     * @return void
     */
    public void updateSiteShape() {
        for (int i = 0; i < subdivideMesh.getNumberOfFaces(); i++) {
            HE_Face f = subdivideMesh.getFaceWithIndex(i);
            Geometry cellOBB = MinimumDiameter.getMinimumRectangle(ZTransform.WB_PolygonToJtsPolygon(f.getPolygon()));
            double ratio = f.getFaceArea() / cellOBB.getArea();
            if (ratio < 0.7) {
                for (int j = 0; j < f.getFaceVertices().size(); j++) {
                    HE_Vertex v = f.getFaceVertices().get(j);
                    if (!v.isBoundary()) {
                        HE_Vertex originV = new HE_Vertex(v.xd(), v.yd(), v.zd());

                        v.set(
                                v.xd() + allFaceVertexVectors[i][j].xd(),
                                v.yd() + allFaceVertexVectors[i][j].yd()
                        );
                        Geometry newCellOBB = MinimumDiameter.getMinimumRectangle(ZTransform.WB_PolygonToJtsPolygon(f.getPolygon()));
                        double newRatio = f.getFaceArea() / newCellOBB.getArea();

                        if (newRatio <= ratio) {
                            v.set(originV);
                            allFaceVertexVectors[i][j] = new WB_Vector(ZMath.random(-0.1, 0.1), ZMath.random(-0.3, 0.3));
                        }
                    }
                }
            }
        }


//        for (HE_Face f : subdivideMesh.getFaces()) {
//            Geometry cellOBB = MinimumDiameter.getMinimumRectangle(ZTransform.WB_PolygonToJtsPolygon(f.getPolygon()));
//            double ratio = f.getFaceArea() / cellOBB.getArea();
//            if (ratio < 0.7) {
//                for (HE_Vertex v : f.getFaceVertices()) {
//                    if (!v.isBoundary()) {
//                        HE_Vertex originV = new HE_Vertex(v.xd(), v.yd(), v.zd());
//                        v.set(v.xd() + ZMath.random(-0.3, 0.3), v.yd() + ZMath.random(-0.3, 0.3));
//                        double newRatio = f.getFaceArea() / cellOBB.getArea();
//                        if (newRatio <= ratio) {
//                            v.set(originV);
//                        }
//                    }
//                }
//            }
//        }

    }

    private class VoronoiFace {
        private double ratio;
        private HE_Face face;

        private VoronoiFace(HE_Face voronoiCell) {
            Geometry cellOBB = MinimumDiameter.getMinimumRectangle(ZTransform.WB_PolygonToJtsPolygon(voronoiCell.getPolygon()));
            this.ratio = voronoiCell.getFaceArea() / cellOBB.getArea();
        }

        private void updateToSquare() {
            if (ratio < 0.7) {
                for (HE_Vertex v : face.getFaceVertices()) {

                }
            }
        }

        private void setByVertex() {

        }

        private double getRatio() {
            return ratio;
        }
    }

    /* ------------- setter & getter ------------- */

    public void setRandomColor() {
        this.randomColor = new int[allSubPolygons.size()][];
        for (int i = 0; i < randomColor.length; i++) {
            randomColor[i] = new int[]{(int) ZMath.random(0, 255), (int) ZMath.random(0, 255), (int) ZMath.random(0, 255)};
        }
    }

    protected void setAllSubPolygons(List<WB_Polygon> allSubPolygons) {
        this.allSubPolygons = allSubPolygons;
        this.subdivideMesh = new HEC_FromPolygons(allSubPolygons).create();
    }

    public abstract void setCellConstraint(double constraint);

    public WB_Polygon getOriginPolygon() {
        return originPolygon;
    }

    public List<WB_Polygon> getAllSubPolygons() {
        return allSubPolygons;
    }

    public List<WB_Polygon> getRedundantPolygons() {
        return redundantPolygons;
    }

    public HE_Mesh getMesh() {
        return subdivideMesh;
    }

    /* ------------- draw ------------- */

    public abstract void display(PApplet app, WB_Render render);

    public void displaySubPolygons(WB_Render render) {
        for (WB_Polygon poly : allSubPolygons) {
            render.drawPolygonEdges2D(poly);
        }
    }

    public void displayWithColor(PApplet app, WB_Render render) {
        for (int i = 0; i < allSubPolygons.size(); i++) {
            app.fill(randomColor[i][0], randomColor[i][1], randomColor[i][2]);
            render.drawPolygonEdges2D(allSubPolygons.get(i));
        }
    }

    public void displayMesh(WB_Render render) {
        for (HE_Halfedge e : subdivideMesh.getEdges()) {
            render.drawEdge(e);
        }
    }
}
