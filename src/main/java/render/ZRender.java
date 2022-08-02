package render;

import advancedGeometry.ZSkeleton;
import basicGeometry.*;
import processing.core.PApplet;
import processing.core.PConstants;
import wblut.geom.WB_Polygon;

/**
 * render tools in Processing
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/9/29
 * @time 15:41
 */
public class ZRender {

    public final static ZPoint origin = new ZPoint(0, 0, 0);

    /*-------- basic --------*/

    /**
     * draw ZLine as a line
     *
     * @param app PApplet
     * @param l   ZLine
     */
    public static void drawZLine2D(PApplet app, ZLine l) {
        app.line(l.getPt0().xf(), l.getPt0().yf(), l.getPt1().xf(), l.getPt1().yf());
    }

    /**
     * draw ZLine as a line
     *
     * @param app PApplet
     * @param l   ZLine
     */
    public static void drawZLine3D(PApplet app, ZLine l) {
        app.line(l.getPt0().xf(), l.getPt0().yf(), l.getPt1().xf(), l.getPt1().yf(), l.getPt1().zf(), l.getPt1().zf());
    }

    /**
     * draw ZPoint as a circle
     *
     * @param app PApplet
     * @param p   ZPoint
     * @param r   radius
     */
    public static void drawZPoint2D(PApplet app, ZPoint p, float r) {
        app.ellipse(p.xf(), p.yf(), r, r);
    }

    /**
     * draw ZPoint as a box in 3D
     *
     * @param app PApplet
     * @param p   ZPoint
     * @param w   width of the box
     */
    public static void drawZPoint3D(PApplet app, ZPoint p, float w) {
        app.pushMatrix();
        app.translate(p.xf(), p.yf(), p.zf());
        app.box(w);
        app.popMatrix();
    }

    /**
     * draw ZPoint as vector (set base point)
     *
     * @param app    PApplet
     * @param vec    vector ZPoint to draw
     * @param base   base point of the vector
     * @param vecCap vector cap radius
     */
    public static void drawZPointAsVec2D(PApplet app, ZPoint vec, ZPoint base, float vecCap) {
        ZPoint dest = base.add(vec);
        app.pushStyle();
        app.noFill();
        app.stroke(255, 0, 0);
        app.line(base.xf(), base.yf(), base.zf(), dest.xf(), dest.yf(), dest.zf());
        app.ellipse(dest.xf(), dest.yf(), vecCap, vecCap);
        app.popStyle();
    }

    /**
     * draw ZPoint as vector (set base point)
     *
     * @param app    PApplet
     * @param vec    vector ZPoint to draw
     * @param base   base point of the vector
     * @param scale  scale ratio
     * @param vecCap vector cap radius
     */
    public static void drawZPointAsVec2D(PApplet app, ZPoint vec, ZPoint base, double scale, float vecCap) {
        ZPoint dest = base.add(vec.scaleTo(scale));
        app.line(base.xf(), base.yf(), base.zf(), dest.xf(), dest.yf(), dest.zf());
        if (vecCap > 0) {
            app.ellipse(dest.xf(), dest.yf(), vecCap, vecCap);
        }
    }

    /**
     * draw ZPoint as vector in 3D (set base point)
     *
     * @param app    PApplet
     * @param vec    vector ZPoint to draw
     * @param base   base point of the vector
     * @param vecCap vector cap radius
     */
    public static void drawZPointAsVec3D(PApplet app, ZPoint vec, ZPoint base, float vecCap) {
        ZPoint dest = base.add(vec);
        app.line(base.xf(), base.yf(), base.zf(), dest.xf(), dest.yf(), dest.zf());
        if (vecCap > 0) {
            app.pushMatrix();
            app.translate(dest.xf(), dest.yf(), dest.zf());
            app.box(vecCap);
            app.popMatrix();
        }
    }

    /**
     * draw ZPoint as vector in 3D (set base point)
     *
     * @param app    PApplet
     * @param vec    vector ZPoint to draw
     * @param base   base point of the vector
     * @param scale  scale ratio
     * @param vecCap vector cap radius
     */
    public static void drawZPointAsVec3D(PApplet app, ZPoint vec, ZPoint base, double scale, float vecCap) {
        ZPoint dest = base.add(vec.scaleTo(scale));
        app.line(base.xf(), base.yf(), base.zf(), dest.xf(), dest.yf(), dest.zf());
        if (vecCap > 0) {
            app.pushMatrix();
            app.translate(dest.xf(), dest.yf(), dest.zf());
            app.box(vecCap);
            app.popMatrix();
        }
    }

    /*-------- advanced --------*/

    /**
     * draw ZSkeleton, all edges, ridges and ridge points
     *
     * @param app      PApplet
     * @param skeleton ZSkeleton
     */
    public static void drawSkeleton2D(PApplet app, ZSkeleton skeleton) {
        app.pushStyle();

        app.noFill();

        app.strokeWeight(1);
        app.stroke(150);
        for (ZLine l : skeleton.getSideEdges()) {
            drawZLine2D(app, l);
        }

        app.strokeWeight(3);
        app.stroke(190, 60, 45);
        for (ZLine l : skeleton.getRidges()) {
            drawZLine2D(app, l);
        }

        app.strokeWeight(3);
        app.stroke(0);
        for (ZLine l : skeleton.getBottomEdges()) {
            drawZLine2D(app, l);
        }

        app.noStroke();
        app.fill(255, 79, 76, 150);
        for (ZPoint p : skeleton.getRidgePoints()) {
            drawZPoint2D(app, p, 2.5f);
        }

        app.popStyle();
    }

    /**
     * draw ZGraph
     *
     * @param app   PApplet
     * @param graph ZGraph
     */
    public static void drawZGraph2D(PApplet app, ZGraph graph) {
        for (ZEdge edge : graph.getEdges()) {
            drawZLine2D(app, edge);
        }
        for (ZNode node : graph.getNodes()) {
            drawZPoint2D(app, node, 3);
        }
    }

    /*-------- axis --------*/

    /**
     * 2D axis (default length)
     *
     * @param app PApplet
     */
    public static void drawAxis2D(PApplet app) {
        app.pushStyle();
        app.strokeWeight(3);
        app.stroke(255, 0, 0);
        app.line(0, 0, 0, 10, 0, 0);
        app.stroke(0, 255, 0);
        app.line(0, 0, 0, 0, 10, 0);
        app.popStyle();
    }

    /**
     * 2D axis (input length)
     *
     * @param app    PApplet
     * @param length axis length
     */
    public static void drawAxis2D(PApplet app, float length) {
        app.pushStyle();
        app.strokeWeight(3);
        app.stroke(255, 0, 0);
        app.line(0, 0, 0, length, 0, 0);
        app.stroke(0, 255, 0);
        app.line(0, 0, 0, 0, length, 0);
        app.popStyle();
    }

    /**
     * 3D axis (default length)
     *
     * @param app PApplet
     */
    public static void drawAxis3D(PApplet app) {
        app.pushStyle();
        app.strokeWeight(3);
        app.stroke(255, 0, 0);
        app.line(0, 0, 0, 10, 0, 0);
        app.stroke(0, 255, 0);
        app.line(0, 0, 0, 0, 10, 0);
        app.stroke(0, 0, 255);
        app.line(0, 0, 0, 0, 0, 10);
        app.popStyle();
    }

    /**
     * 3D axis (input length)
     *
     * @param app    PApplet
     * @param length axis length
     */
    public static void drawAxis3D(PApplet app, float length) {
        app.pushStyle();
        app.strokeWeight(3);
        app.stroke(255, 0, 0);
        app.line(0, 0, 0, length, 0, 0);
        app.stroke(0, 255, 0);
        app.line(0, 0, 0, 0, length, 0);
        app.stroke(0, 0, 255);
        app.line(0, 0, 0, 0, 0, length);
        app.popStyle();
    }

    /**
     * draw WB_Polygon with holes
     *
     * @param polygon input polygon
     * @param app     PApplet
     */
    public static void drawWB_PolygonWithHoles(WB_Polygon polygon, PApplet app) {
        if (polygon.getNumberOfHoles() > 0) {
            int[] npc = polygon.getNumberOfPointsPerContour();
            app.beginShape();
            for (int i = 0; i < npc[0]; i++) {
                app.vertex(polygon.getPoint(i).xf(), polygon.getPoint(i).yf(), polygon.getPoint(i).zf());
            }
            int index = npc[0];
            for (int i = 0; i < polygon.getNumberOfHoles(); i++) {
                app.beginContour();
                for (int j = 0; j < npc[i + 1]; j++) {
                    app.vertex(polygon.getPoint(index).xf(), polygon.getPoint(index).yf(), polygon.getPoint(index).zf());
                    index++;
                }
                app.endContour();
                index++;
            }
            app.endShape(PConstants.CLOSE);
        }
    }
}
