package render;

import advancedGeometry.ZSkeleton;
import basicGeometry.ZLine;
import basicGeometry.ZPoint;
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

    /*-------- skeleton --------*/

    public static void drawZLine2D(PApplet app, ZLine l) {
        app.line(l.getPt0().xf(), l.getPt0().yf(), l.getPt1().xf(), l.getPt1().yf());
    }

    public static void drawZPoint(PApplet app, ZPoint p, float r) {
        app.ellipse(p.xf(), p.yf(), r, r);
    }

    public static void drawSkeleton(PApplet app, ZSkeleton skeleton) {
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
            drawZPoint(app, p, 2.5f);
        }

        app.popStyle();
    }


    /*-------- axis --------*/

    /**
     * 2D axis (default length)
     *
     * @param app PApplet
     * @return void
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
     * @return void
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
     * @return void
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
     * @return void
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
     * @return void
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
