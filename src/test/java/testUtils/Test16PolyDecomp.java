package testUtils;

import advancedGeometry.rectCover.ZRectCover;
import advancedGeometry.rectCover.ZRectCover2;
import guo_cam.CameraController;
import igeo.ICurve;
import igeo.IG;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.ZRender;
import transform.ZTransform;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

/**
 * test polygon decomposition and rect cover
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/22
 * @time 12:43
 */
public class Test16PolyDecomp extends PApplet {
    public static void main(String[] args) {
        PApplet.main("testUtils.Test16PolyDecomp");
    }

    private WB_Polygon boundary;
    private WB_Render render;
    private ZRender zRender;

    // concave + rays
    private ZRectCover zrc;
    private ZRectCover2 zrc2;

    private CameraController gcam;

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    public void setup() {
        this.gcam = new CameraController(this);
        this.render = new WB_Render(this);
        this.zRender = new ZRender(this);
        setBoundary();

        long t1 = System.currentTimeMillis();

        // rays on concave
        this.zrc = new ZRectCover(boundary, 3);
        long t2 = System.currentTimeMillis();

        // jMetal
        this.zrc2 = new ZRectCover2(boundary, 3);
        long t3 = System.currentTimeMillis();

        System.out.println("rays: " + (t2 - t1) + "ms");
        System.out.println("jMetal: " + (t3 - t2) + "ms");
    }

    /* ------------- function ------------- */

    private void setBoundary() {
        String path = "src/test/resources/test_rect_cover.3dm";

        IG.init();
        IG.open(path);
        ICurve[] polyLines = IG.layer("boundary").curves();
        this.boundary = (WB_Polygon) ZTransform.ICurveToWB(polyLines[0]);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        // zrc
        strokeWeight(3);
        render.drawPolygonEdges(boundary);

        pushStyle();
        strokeWeight(0.5f);
        stroke(150);
        for (LineString l : zrc.getRayExtends()) {
            zRender.drawGeometry(l);
        }
        strokeWeight(2f);
        stroke(255, 0, 0);
        for (Polygon rect : zrc.getBestRects()) {
            zRender.drawGeometry(rect);
        }
        popStyle();

        // zrc2
        translate(1000, 0, 0);
        strokeWeight(1);
        render.drawPolygonEdges(boundary);

        pushStyle();
        strokeWeight(2f);
        stroke(255, 0, 0);
        for (Polygon rect : zrc2.getBestRects()) {
            zRender.drawGeometry(rect);
        }
        popStyle();
    }

    public void keyPressed() {
        if (key == 'r') {
            setBoundary();
            this.zrc = new ZRectCover(boundary, 3);
        }

        if (key == 's') {
            String className = getClass().getSimpleName();
            save("./src/test/resources/exampleImgs/" + className + ".jpg");
        }

    }
}