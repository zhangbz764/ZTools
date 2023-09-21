package testUtils;

import advancedGeometry.rectCover.ZRectCover;
import basicGeometry.ZLine;
import igeo.ICurve;
import igeo.IG;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;
import render.ZRender;
import transform.ZTransform;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * test polygon decomposition and rect cover
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/22
 * @time 12:43
 */
public class TestPolyDecomp extends PApplet {
    private WB_Polygon boundary;
    private WB_Render render;
    private ZRender zRender;

    // concave + rays
    private ZRectCover zrc;

    // OBBTree
    private List<Polygon> obbTree;
    private Polygon[] halves;

    // HE_Mesh WB_PolygonDecomposer
    private List<WB_Polygon> decomposePolys;

    private Collection<Polygon> splitPolyResults;
    private List<Polygon> splitOBBResults;

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    public void setup() {
        render = new WB_Render(this);
        zRender = new ZRender(this);
        setBoundary();

        long start = System.currentTimeMillis();

        // test OBBTree
//        this.obbTree = ZGeoMath.performOBBTree(ZTransform.WB_PolygonToJtsPolygon(boundary), 1);
//        this.halves = ZGeoMath.halvingOBB(obbTree.get(0));

        // test rays on concave
        this.zrc = new ZRectCover(boundary, 3);

        // WB_PolygonDecomposer
//        System.out.println(boundary.getNormal());
//        this.decomposePolys = WB_PolygonDecomposer.decomposePolygon2D(ZGeoMath.reversePolygon(boundary));
//        System.out.println("WB_PolygonDecomposer num: " + decomposePolys.size());

        long end = System.currentTimeMillis();
        System.out.println("---> time used: " + (end - start) + "ms");
    }

    /* ------------- function ------------- */

    private void setBoundary() {
        String path = Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("./test_rect_cover.3dm")
        ).getPath();

//        String inputPath = "./src/test/resources/test_rect_cover.3dm";

        IG.init();
        IG.open(path);
        ICurve[] polyLines = IG.layer("boundary").curves();
        this.boundary = (WB_Polygon) ZTransform.ICurveToWB(polyLines[0]);

//        WB_Point[] bdpts = new WB_Point[7];
//        bdpts[0] = new WB_Point(600, 100);
//        bdpts[1] = new WB_Point(600, 600);
//        bdpts[2] = new WB_Point(150, 650);
//        bdpts[3] = new WB_Point(100, 870);
//        bdpts[4] = new WB_Point(900, 900);
////        bdpts[5] = new WB_Point(820, 500);
//        bdpts[5] = new WB_Point(860, 120);
//        bdpts[6] = bdpts[0];
//        this.boundary = new WB_Polygon(bdpts);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        strokeWeight(3);
        render.drawPolygonEdges(boundary);

//        pushStyle();
//        stroke(0, 255, 0);
//        strokeWeight(5);
//        for (WB_Polygon p : decomposePolys) {
//            render.drawPolygonEdges(p);
//        }
//        popStyle();

//        if (obbTree != null) {
//            for (Polygon p : obbTree) {
//                jtsRender.drawGeometry(p);
//            }
//        }
//        jtsRender.drawGeometry(MinimumDiameter.getMinimumRectangle(ZTransform.WB_PolygonToJtsPolygon(boundary)));
//        if (halves != null) {
//            for (Polygon p : halves) {
//                jtsRender.drawGeometry(p);
//            }
//        }

        pushStyle();
        strokeWeight(0.5f);
        stroke(150);
        for (LineString l : zrc.getRayExtends()) {
            zRender.drawGeometry(l);
        }
//        strokeWeight(1.5f);
//        stroke(255, 0, 0);
//        for (List<ZLine> lineList : zrc.getGrid()) {
//            for (ZLine l : lineList) {
//                l.display(this, 1.5f);
//            }
//        }
        popStyle();
    }

    public void keyPressed() {
        if (key == 'r') {
            setBoundary();
            this.zrc = new ZRectCover(boundary, 3);
        }
    }
}