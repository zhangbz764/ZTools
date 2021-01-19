package render;

import processing.core.PApplet;
import processing.core.PConstants;
import wblut.geom.WB_Polygon;

/**
 * 基本显示工具
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/9/29
 * @time 15:41
 */
public class ZRender {

    /*-------- 坐标轴 --------*/

    /**
     * 绘制2D坐标轴（默认长度）
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
     * 绘制2D坐标轴（输入长度）
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
     * 绘制3D坐标轴（默认长度）
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
     * 绘制3D坐标轴（输入长度）
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
     * 绘制带洞多边形WB_Polygon
     *
     * @param polygon input polygon
     * @param app     PApplet
     * @return void
     */
    public static void drawWB_PolygonWithHoles(WB_Polygon polygon, PApplet app) {
        if (polygon.getNumberOfHoles() > 0) {
            int[] npc = polygon.getNumberOfPointsPerContour();
            app.beginShape();
            for (int i = 0; i < npc[0] - 1; i++) {
                app.vertex(polygon.getPoint(i).xf(), polygon.getPoint(i).yf(), polygon.getPoint(i).zf());
            }
            int index = npc[0];
            for (int i = 0; i < polygon.getNumberOfHoles(); i++) {
                app.beginContour();
                for (int j = 0; j < npc[i + 1] - 1; j++) {
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
