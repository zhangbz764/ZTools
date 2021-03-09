package subdivision;

import geometry.ZGeoFactory;
import geometry.ZLine;
import geometry.ZPoint;
import geometry.ZSkeleton;
import math.ZGeoMath;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * 找到originPolygon直骨架脊线，在脊线上均匀布点，voronoi剖分，单条形式
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/1
 * @time 23:01
 */
public class ZSD_SkeVorStrip extends ZSubdivision {
    private ZSkeleton skeleton;
    private List<ZPoint> voronoiGenerator;

    private double span = 50; // 宽度
    private double depth = 0; // 深度

    private WB_PolyLine polyLine;

    /* ------------- constructor ------------- */

    public ZSD_SkeVorStrip(WB_Polygon originPolygon) {
        super(originPolygon);
    }

    @Override
    public void performDivide() {
        if (depth == 0) {
            this.skeleton = new ZSkeleton(super.getOriginPolygon());
        } else {
            this.skeleton = new ZSkeleton(super.getOriginPolygon(), depth);
        }

        List<ZLine> topSegments = skeleton.getTopEdges();

        topSegments.addAll(skeleton.getExtendedRidges());

        polyLine = ZGeoFactory.createWB_PolyLine(topSegments);

        // polyLine maybe null because segments might not be nose to tail
        if (polyLine != null) {
            voronoiGenerator = ZGeoMath.splitWB_PolyLineEachEdgeByThreshold(polyLine, span + 10, span - 10);
//            if (voronoiGenerator.size() > 1) {
//                voronoiGenerator.remove(voronoiGenerator.size() - 1);
//                voronoiGenerator.remove(0);
//            } else if (voronoiGenerator.size() == 1) {
//                voronoiGenerator.remove(0);
//            }
            if (voronoiGenerator.size() > 2 && depth == 0) {
                voronoiGenerator.remove(0);
                voronoiGenerator.remove(voronoiGenerator.size() - 1);
            }
            // generate voronoi
            List<WB_Point> points = new ArrayList<>();
            for (ZPoint p : voronoiGenerator) {
                points.add(p.toWB_Point());
            }
            WB_Voronoi2D voronoi = WB_VoronoiCreator.getClippedVoronoi2D(points, super.getOriginPolygon());

            List<WB_Polygon> allSubPolygons = new ArrayList<>();
            for (WB_VoronoiCell2D cell : voronoi.getCells()) {
                allSubPolygons.add(cell.getPolygon());
            }
            super.setAllSubPolygons(allSubPolygons);
        }
    }

    /* ------------- setter & getter ------------- */

    @Override
    public void setCellConstraint(double span) {
        this.span = span;
    }

    public void setSpan(double span) {
        this.span = span;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    /* ------------- draw ------------- */

    @Override
    public void display(PApplet app, WB_Render render) {
        app.pushStyle();

        app.stroke(0);
        app.strokeWeight(3);
        app.fill(200);
        super.displaySubPolygons(render);

        skeleton.display(app);
//        skeleton.displayTopEdges(app);
//        skeleton.displayExtendedRidges(app);

        app.noStroke();
        app.fill(255, 0, 0);
        for (ZPoint p : voronoiGenerator) {
            p.displayAsPoint(app, 10);
        }

        app.fill(0);
        app.textSize(15);
        for (int i = 0; i < polyLine.getNumberOfPoints(); i++) {
            app.text(i, polyLine.getPoint(i).xf(), polyLine.getPoint(i).yf(), polyLine.getPoint(i).zf());
        }
        app.popStyle();
    }
}