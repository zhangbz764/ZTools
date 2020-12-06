package subdivision;

import geometry.ZGeoFactory;
import geometry.ZLine;
import geometry.ZPoint;
import geometry.ZSkeleton;
import math.ZGeoMath;
import processing.core.PApplet;
import render.JtsRender;
import wblut.geom.*;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/1
 * @time 23:01
 * @description 找到originPolygon直骨架脊线，在脊线上均匀布点，voronoi剖分
 */
public class ZSD_Stripe implements ZSD {
    private final WB_Polygon originPolygon;
    private List<WB_Polygon> allSubPolygons;

    private ZSkeleton skeleton;
    private List<ZPoint> voronoiGenerator;

    private double span = 50;

    /* ------------- constructor ------------- */

    public ZSD_Stripe(WB_Polygon originPolygon) {
        this.originPolygon = originPolygon;
        performDivide();
    }

    @Override
    public void performDivide() {
        this.skeleton = new ZSkeleton(originPolygon);

        List<ZLine> ridgeSegments = skeleton.getRidges();
        ridgeSegments.addAll(skeleton.getExtendedRidges());

        WB_PolyLine polyLine = ZGeoFactory.createWB_PolyLine(ridgeSegments);

        // polyLine maybe null because segments might not be nose to tail
        if (polyLine != null) {
            voronoiGenerator = ZGeoMath.splitWB_PolyLineEdgeByStep(polyLine, span);
            if (voronoiGenerator.size() > 1) {
                voronoiGenerator.remove(voronoiGenerator.size() - 1);
                voronoiGenerator.remove(0);
            } else if (voronoiGenerator.size() == 1) {
                voronoiGenerator.remove(0);
            }
            // generate voronoi
            List<WB_Point> points = new ArrayList<>();
            for (ZPoint p : voronoiGenerator) {
                points.add(p.toWB_Point());
            }
            WB_Voronoi2D voronoi = WB_VoronoiCreator.getClippedVoronoi2D(points, originPolygon);

            this.allSubPolygons = new ArrayList<>();
            for (WB_VoronoiCell2D cell : voronoi.getCells()) {
                allSubPolygons.add(cell.getPolygon());
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

        app.stroke(0);
        app.strokeWeight(3);
        app.fill(200);
        for (WB_Polygon poly : allSubPolygons) {
            render.drawPolygonEdges2D(poly);
        }

        skeleton.displayRidges(app);
        skeleton.displayExtendedRidges(app);

        app.noStroke();
        app.fill(255, 0, 0);
        for (ZPoint p : voronoiGenerator) {
            p.displayAsPoint(app, 10);
        }

        app.popStyle();
    }
}
