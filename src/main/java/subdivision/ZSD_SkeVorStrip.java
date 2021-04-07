package subdivision;

import geometry.*;
import math.ZGeoMath;
import math.ZGraphMath;
import org.locationtech.jts.geom.Polygon;
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

    public ZSD_SkeVorStrip(Polygon originPolygon) {
        super(originPolygon);
    }

    List<ZEdge> longestChain;
    ZGraph graph;

    @Override
    public void performDivide() {
        if (depth == 0) {
            this.skeleton = new ZSkeleton(super.getOriginPolygon());
        } else {
            this.skeleton = new ZSkeleton(super.getOriginPolygon(), depth);
        }

        List<ZLine> topSegments = skeleton.getRidges();
        topSegments.addAll(skeleton.getExtendedRidges());

//        polyLine = ZFactory.createWB_PolyLine(topSegments);
        ZGraph tempGraph = ZFactory.createZGraphFromSegments(topSegments);
        graph = tempGraph;
        System.out.println("graph : nodes num  " + tempGraph.getNodesNum());
        longestChain = ZGraphMath.longestChain(tempGraph);
        polyLine = ZFactory.createWB_PolyLine(longestChain);

        // polyLine maybe null because segments might not be nose to tail
        if (polyLine != null) {
            voronoiGenerator = ZGeoMath.splitPolyLineByStep(polyLine, span);
//            voronoiGenerator = ZGeoMath.splitWB_PolyLineEachEdgeByThreshold(polyLine, span + 5, span - 5);
            if (voronoiGenerator.size() > 2 && depth == 0) {
                voronoiGenerator.remove(0);
                voronoiGenerator.remove(voronoiGenerator.size() - 1);
            }
            System.out.println("voronoiGenerator: " + voronoiGenerator.size());
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
        } else {
            System.out.println("polyLine == null");
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

        app.pushMatrix();
        app.translate(0,0,250);
        graph.display(app);
        app.translate(0, 0, 250);
        for (ZEdge e : longestChain) {
            e.display(app);
        }
        app.popMatrix();
    }
}
