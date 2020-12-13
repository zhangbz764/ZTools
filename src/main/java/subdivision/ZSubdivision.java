package subdivision;

import math.ZMath;
import processing.core.PApplet;
import render.JtsRender;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

import java.util.List;

/**
 * interface of subdivision
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/1
 * @time 23:02
 */
public abstract class ZSubdivision {
    private final WB_Polygon originPolygon;
    private List<WB_Polygon> allSubPolygons;

    private int[][] randomColor;

    /* ------------- constructor ------------- */

    public ZSubdivision(WB_Polygon originPolygon) {
        this.originPolygon = originPolygon;
    }

    /**
     * main method to perform subdivision
     *
     * @param
     * @return void
     */
    public abstract void performDivide();

    /* ------------- setter & getter ------------- */

    public void setRandomColor() {
        this.randomColor = new int[allSubPolygons.size()][];
        for (int i = 0; i < randomColor.length; i++) {
            randomColor[i] = new int[]{(int) ZMath.random(0, 255), (int) ZMath.random(0, 255), (int) ZMath.random(0, 255)};
        }
    }

    public void setAllSubPolygons(List<WB_Polygon> allSubPolygons) {
        this.allSubPolygons = allSubPolygons;
    }

    public abstract void setCellConstraint(double constraint);

    public WB_Polygon getOriginPolygon() {
        return originPolygon;
    }

    public List<WB_Polygon> getAllSubPolygons() {
        return allSubPolygons;
    }

    public HE_Mesh getMesh() {
        return new HEC_FromPolygons(allSubPolygons).create();
    }

    /* ------------- draw ------------- */

    public abstract void display(PApplet app, WB_Render render);

    public void displaySubPolygonsc(WB_Render render) {
        for (WB_Polygon poly : allSubPolygons) {
            render.drawPolygonEdges2D(poly);
        }
    }

    public void displayWithColor(PApplet app, WB_Render render) {
        for (int i = 0; i < allSubPolygons.size(); i++) {
            app.fill(randomColor[i][0], randomColor[i][1], randomColor[i][2]);
            render.drawPolygonEdges2D(allSubPolygons.get(i));
        }
    }
}
