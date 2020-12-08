package subdivision;

import geometry.ZGeoFactory;
import math.ZMath;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 不断计算OBB，沿OBB长边中线剖分，直到剖分至面积下限
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/6
 * @time 15:15
 */
public class ZSD_OBB implements ZSubdivision {
    private final WB_Polygon originPolygon;
    private List<WB_Polygon> allSubPolygons;
    private List<Polygon> allSubJtsPolygons;

    private double minArea = 8000;

    private int[][] randomColor;

    /* ------------- constructor ------------- */

    public ZSD_OBB(WB_Polygon originPolygon) {
        this.originPolygon = originPolygon;
        performDivide();
    }

    @Override
    public void performDivide() {
        this.allSubJtsPolygons = new ArrayList<>();
        Polygon origin = ZTransform.WB_PolygonToJtsPolygon(originPolygon);
        divide(origin);

        this.allSubPolygons = new ArrayList<>();
        for (Polygon p : allSubJtsPolygons) {
            this.allSubPolygons.add(ZTransform.jtsPolygonToWB_Polygon(p));
        }

        this.randomColor = new int[allSubPolygons.size()][];
        for (int i = 0; i < randomColor.length; i++) {
            randomColor[i] = new int[]{(int) ZMath.random(0, 255), (int) ZMath.random(0, 255), (int) ZMath.random(0, 255)};
        }
    }

    private void divide(Polygon input) {
        // get OBB
        Geometry currOBB = MinimumDiameter.getMinimumRectangle(input);
        if (currOBB instanceof Polygon) {
            assert currOBB.getNumPoints() == 5 : "not a valid rectangle";

            // find longer edge, create divide line
            double[] length = new double[2];
            length[0] = currOBB.getCoordinates()[0].distance(currOBB.getCoordinates()[1]);
            length[1] = currOBB.getCoordinates()[1].distance(currOBB.getCoordinates()[2]);
            int max = ZMath.getMaxIndex(length);

            Coordinate[] centerPoints = new Coordinate[2];
            centerPoints[0] = new Coordinate(
                    (currOBB.getCoordinates()[max].x + currOBB.getCoordinates()[max + 1].x) * 0.5,
                    (currOBB.getCoordinates()[max].y + currOBB.getCoordinates()[max + 1].y) * 0.5,
                    (currOBB.getCoordinates()[max].z + currOBB.getCoordinates()[max + 1].z) * 0.5
            );
            centerPoints[1] = new Coordinate(
                    (currOBB.getCoordinates()[max + 2].x + currOBB.getCoordinates()[max + 3].x) * 0.5,
                    (currOBB.getCoordinates()[max + 2].y + currOBB.getCoordinates()[max + 3].y) * 0.5,
                    (currOBB.getCoordinates()[max + 2].z + currOBB.getCoordinates()[max + 3].z) * 0.5
            );

            Coordinate[] dividePoints = new Coordinate[2];
            dividePoints[0] = new Coordinate(
                    centerPoints[0].x + (centerPoints[0].x - centerPoints[1].x),
                    centerPoints[0].y + (centerPoints[0].y - centerPoints[1].y),
                    centerPoints[0].z + (centerPoints[0].z - centerPoints[1].z)
            );
            dividePoints[1] = new Coordinate(
                    centerPoints[1].x + centerPoints[1].x - centerPoints[0].x,
                    centerPoints[1].y + centerPoints[1].y - centerPoints[0].y,
                    centerPoints[1].z + centerPoints[1].z - centerPoints[0].z
            );
            LineString divideLine = ZGeoFactory.jtsgf.createLineString(dividePoints);

            // use Polygonizer to divide
            Polygonizer pr = new Polygonizer();
            Geometry allGeometry = divideLine;
            for (LineString lineString : ZTransform.PolygonToLineString(input)) {
                allGeometry = allGeometry.union(lineString);
            }
            pr.add(allGeometry);
            Collection<Polygon> dividedPolys = pr.getPolygons();

//            allSubJtsPolygons.addAll(dividedPolys);
//            System.out.println("poly num total : " + allSubJtsPolygons.size());

            for (Polygon p : dividedPolys) {
                allSubJtsPolygons.add(p);
                System.out.println("poly num total : " + allSubJtsPolygons.size());
                if (p.getArea() > minArea) {
                    allSubJtsPolygons.remove(p);
                    Geometry newInput = p.buffer(-3);
                    divide((Polygon) newInput);
//                    divide(p);
                }
            }
        }
    }

    /* ------------- setter & getter ------------- */

    @Override
    public WB_Polygon getOriginPolygon() {
        return originPolygon;
    }

    @Override
    public List<WB_Polygon> getAllSubPolygons() {
        return allSubPolygons;
    }

    @Override
    public HE_Mesh getMesh() {
        return new HEC_FromPolygons(allSubPolygons).create();
    }

    /* ------------- draw ------------- */

    @Override
    public void display(PApplet app, WB_Render render, JtsRender jtsRender) {
        app.pushStyle();
        app.noFill();
        app.stroke(0);
        for (int i = 0; i < allSubPolygons.size(); i++) {
            app.fill(randomColor[i][0], randomColor[i][1], randomColor[i][2]);
            render.drawPolygonEdges2D(allSubPolygons.get(i));
        }
        app.popStyle();
    }
}
