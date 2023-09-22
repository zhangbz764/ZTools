package testUtils;

import basicGeometry.ZLine;
import basicGeometry.ZPoint;
import math.ZGeoMath;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.math.Vector2D;
import processing.core.PApplet;
import render.JtsRender;
import render.ZRender;
import transform.ZTransform;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

/**
 * test extend and trim
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/29
 * @time 15:34
 */
public class Test2ExtendTrim extends PApplet {
    public static void main(String[] args) {
        PApplet.main("testUtils.Test2ExtendTrim");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P2D);
    }

    /* ------------- setup ------------- */

    private WB_Polygon poly;

    // ZGeoMath: extend
    private ZPoint origin1 = new ZPoint(100, 900);
    private ZLine extend;
    private ZPoint origin2 = new ZPoint(500, 500);
    private ZLine extend2;

    // utils
    private WB_Render render;
    private JtsRender jtsRender = new JtsRender(this);

    public void setup() {
        this.render = new WB_Render(this);

        WB_Point[] pts1 = new WB_Point[6];
        pts1[0] = new WB_Point(100, 100);
        pts1[1] = new WB_Point(700, 100);
        pts1[2] = new WB_Point(800, 400);
        pts1[3] = new WB_Point(500, 800);
        pts1[4] = new WB_Point(100, 600);
        pts1[5] = new WB_Point(100, 100);
        this.poly = new WB_Polygon(pts1);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        noFill();
        strokeWeight(1);
        stroke(0);
        render.drawPolyLine2D(poly);

        fill(255, 0, 0);
        ZRender.drawZPoint2D(this, origin1, 10);
        fill(0, 255, 0);
        ZRender.drawZPoint2D(this, origin2, 10);
        strokeWeight(3);
        stroke(255, 0, 0);
        ZLine seg1 = new ZLine(origin1, new ZPoint(mouseX, mouseY));

        LineString e = ZGeoMath.extendSegmentToPolygon(
                new Vector2D[]{
                        new Vector2D(seg1.getPt0().toJtsCoordinate()),
                        new Vector2D(seg1.getDirectionNor().toJtsCoordinate()),
                },
                ZTransform.WB_PolygonToPolygon(poly)
        );
        if (e != null) {
            jtsRender.drawGeometry(e);
        }

        this.extend = ZGeoMath.extendSegmentToPolygon(seg1.toLinePD(), poly);
//        if (extend != null) {
//            extend.display(this);
//        }
        stroke(0, 255, 0);
        ZLine seg2 = new ZLine(origin2, new ZPoint(mouseX, mouseY));
        this.extend2 = ZGeoMath.extendSegmentToPolygonBothSides(seg2.toLinePD(), poly);
        if (extend2 != null) {
            extend2.display(this);
        }
    }


    public void keyPressed() {
        if (key == 's') {
            String className = getClass().getSimpleName();
            save("./src/test/resources/exampleImgs/" + className + ".jpg");
        }
    }
}
