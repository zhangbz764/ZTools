package advancedGeometry.subdivision;

import advancedGeometry.ZSkeleton;
import basicGeometry.*;
import math.ZGeoMath;
import math.ZGraphMath;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * set subdivide width (span)
 * find the straight skeleton of original polygon
 * generate points on the ridge of skeleton
 * perform advancedGeometry.subdivision along the direction of OBB shorter edge
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/2
 * @time 10:31
 */
public class ZSD_SkeOBBStrip extends ZSubdivision {
    private ZSkeleton skeleton;
    private List<ZPoint> generator;

    private double span = 50;

    ZGraph graph;

    /* ------------- constructor ------------- */

    public ZSD_SkeOBBStrip(WB_Polygon originPolygon) {
        super(originPolygon);
    }

    public ZSD_SkeOBBStrip(Polygon originPolygon) {
        super(originPolygon);
    }

    /* ------------- member function ------------- */

    @Override
    public void performDivide() {
        this.skeleton = new ZSkeleton(super.getOriginPolygon());
        List<ZLine> topSegments = skeleton.getRidges();
        topSegments.addAll(skeleton.getExtendedRidges());

        ZGraph tempGraph = ZFactory.createZGraphFromSegments(topSegments);
        graph = tempGraph;
        List<ZEdge> longestChain = ZGraphMath.longestChain(tempGraph);
        WB_PolyLine polyLine = ZFactory.createWB_PolyLine(longestChain);

        if (polyLine != null) {
            ZPoint dir = ZGeoMath.miniRectDir(super.getOriginPolygon());

            // create divide line by extending
            List<ZLine> divideLine = new ArrayList<>();
            generator = ZGeoMath.splitPolyLineByStep(polyLine, span);
            if (generator.size() > 2) {
                generator.remove(0);
                generator.remove(generator.size() - 1);
            }
            for (ZPoint p : generator) {
                ZLine dl = ZGeoMath.extendSegmentToPolygonBothSides(
                        new ZPoint[]{p, dir}, super.getOriginPolygon()
                );
                if (dl != null) {
                    divideLine.add(dl.extendTwoSidesSlightly(0.01));
                }
            }

            // polygonize
            Polygonizer pr = new Polygonizer();
            Geometry nodedLineStrings = ZTransform.WB_PolyLineToJtsLineString(super.getOriginPolygon());
            for (ZLine l : divideLine) {
                nodedLineStrings = nodedLineStrings.union(l.toJtsLineString());
            }
            pr.add(nodedLineStrings);
            Collection<Polygon> allPolys = pr.getPolygons();
            List<WB_Polygon> polys = new ArrayList<>();
            for (Polygon p : allPolys) {
                polys.add(ZTransform.jtsPolygonToWB_Polygon(p));
            }
            super.setAllSubPolygons(polys);
        } else {
            System.out.println("polyLine == null");
        }
    }

    /* ------------- setter & getter ------------- */

    @Override
    public void setCellConstraint(double constraint) {

    }

    public void setSpan(double span) {
        this.span = span;
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

        app.noStroke();
        app.fill(255, 0, 0);
        for (ZPoint p : generator) {
            p.displayAsPoint(app, 5);
        }

        app.popStyle();

        app.pushMatrix();
        app.translate(0, 0, 250);
        graph.display(app);
        app.popMatrix();
    }
}
