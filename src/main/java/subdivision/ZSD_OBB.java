package subdivision;

import geometry.ZGeoFactory;
import math.ZMath;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/6
 * @time 15:15
 * @description 不断计算OBB，沿OBB长边中线剖分，直到剖分至面积下限
 */
public class ZSD_OBB implements ZSD {
    private final WB_Polygon originPolygon;
    private List<WB_Polygon> allSubPolygons;

    private Geometry firstOBB;
    private LineString divideLine;
    private double minArea = 200;

    /* ------------- constructor ------------- */

    public ZSD_OBB(WB_Polygon originPolygon) {
        this.originPolygon = originPolygon;
        performDivide();
    }

    @Override
    public void performDivide() {
        Polygon origin = ZTransform.WB_PolygonToJtsPolygon(originPolygon);
        Geometry currOBB = MinimumDiameter.getMinimumRectangle(origin);
        this.firstOBB = currOBB;
        if (currOBB instanceof Polygon) {
            assert currOBB.getNumPoints() == 5 : "not a valid rectangle";

            // find longer edge, create divide line
            double[] length = new double[2];
            length[0] = currOBB.getCoordinates()[0].distance(currOBB.getCoordinates()[1]);
            length[1] = currOBB.getCoordinates()[1].distance(currOBB.getCoordinates()[2]);
            int max = ZMath.getMaxIndex(length);

            Coordinate[] divideLinePoints = new Coordinate[2];
            divideLinePoints[0] = new Coordinate(
                    (currOBB.getCoordinates()[max].x + currOBB.getCoordinates()[max + 1].x) * 0.5,
                    (currOBB.getCoordinates()[max].y + currOBB.getCoordinates()[max + 1].y) * 0.5,
                    (currOBB.getCoordinates()[max].z + currOBB.getCoordinates()[max + 1].z) * 0.5
            );
            divideLinePoints[1] = new Coordinate(
                    (currOBB.getCoordinates()[max + 2].x + currOBB.getCoordinates()[max + 3].x) * 0.5,
                    (currOBB.getCoordinates()[max + 2].y + currOBB.getCoordinates()[max + 3].y) * 0.5,
                    (currOBB.getCoordinates()[max + 2].z + currOBB.getCoordinates()[max + 3].z) * 0.5
            );
            LineString divideLine = ZGeoFactory.jtsgf.createLineString(divideLinePoints);
            this.divideLine = divideLine;

            // use Polygonizer to divide
            Polygonizer pr = new Polygonizer();
            Geometry allGeometry = origin;
            allGeometry = allGeometry.union(divideLine);
            pr.add(allGeometry);
            Collection<Polygon> allPolys = pr.getPolygons();
            System.out.println("poly num: " + allPolys.size());

            // convert to WB_Polygon
            this.allSubPolygons = new ArrayList<>();
            for (Polygon p : allPolys) {
                this.allSubPolygons.add(ZTransform.jtsPolygonToWB_Polygon(p));
            }
        }
    }

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

    @Override
    public void display(PApplet app, WB_Render render, JtsRender jtsRender) {
        app.pushStyle();
        app.noFill();
        app.stroke(0);
        jtsRender.drawGeometry(firstOBB);
        jtsRender.drawGeometry(divideLine);

        for (int i = 0; i < allSubPolygons.size(); i++) {
            app.fill(i * 50);
            render.drawPolygonEdges2D(allSubPolygons.get(i));
        }
        app.popStyle();
    }
}
