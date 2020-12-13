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
public class ZSD_OBB extends ZSubdivision {
    private List<Polygon> allSubJtsPolygons;

    private double minArea = 300;

    /* ------------- constructor ------------- */

    public ZSD_OBB(WB_Polygon originPolygon) {
        super(originPolygon);
    }

    @Override
    public void performDivide() {
        this.allSubJtsPolygons = new ArrayList<>();
        Polygon origin = ZTransform.WB_PolygonToJtsPolygon(super.getOriginPolygon());
        divide(origin);

        List<WB_Polygon> allSubPolygons = new ArrayList<>();
        for (Polygon p : allSubJtsPolygons) {
            allSubPolygons.add(ZTransform.jtsPolygonToWB_Polygon(p));
        }
        super.setAllSubPolygons(allSubPolygons);
        super.setRandomColor();

        System.out.println("poly num total : " + allSubJtsPolygons.size());
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


            for (Polygon p : dividedPolys) {
                allSubJtsPolygons.add(p);
                if (p.getArea() > minArea) {
                    allSubJtsPolygons.remove(p);
//                    Geometry newInput = p.buffer(-3);
//                    divide((Polygon) newInput);
                    divide(p);
                }
            }
        }
    }

    /* ------------- setter & getter ------------- */

    @Override
    public void setCellConstraint(double minArea) {
        this.minArea = minArea;
    }

    /* ------------- draw ------------- */

    public void display(PApplet app, WB_Render render) {
        app.pushStyle();
        app.noFill();
        app.stroke(0);
        super.displayWithColor(app, render);
        app.popStyle();
    }
}
