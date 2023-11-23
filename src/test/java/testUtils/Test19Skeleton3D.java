package testUtils;

import advancedGeometry.ZSkeleton;
import guo_cam.CameraController;
import igeo.ICurve;
import igeo.IG;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.ZRender;
import transform.ZTransform;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * test ZSkeleton with holes
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/5
 * @time 14:33
 */
public class Test19Skeleton3D extends PApplet {

    public static void main(String[] args) {
        PApplet.main("testUtils.Test19Skeleton3D");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private List<WB_Polygon> boundaries = new ArrayList<>();
    private List<ZSkeleton> skeletons;

    private Polygon polygon;
    private WB_Polygon wb_polygon;
    private GeometryFactory gf = new GeometryFactory();

    private ZSkeleton skeleton;

    // utils
    private CameraController gcam;
    private ZRender zRender;
    private WB_Render render;

    public void setup() {
        this.gcam = new CameraController(this);
        this.zRender = new ZRender(this);
        this.render = new WB_Render(this);

        load();
        this.skeletons = new ArrayList<>();
        for (WB_Polygon b : boundaries) {
            skeletons.add(new ZSkeleton(b, 0, true));
        }
    }

    public void load() {
        // import
        String path = ".\\src\\test\\resources\\test_skeletons_3d.3dm";
        IG.init();
        IG.open(path);

        ICurve[] _boundary = IG.layer("boundary").curves();
        boundaries = new ArrayList<>();

        for (ICurve c : _boundary) {
            boundaries.add((WB_Polygon) ZTransform.ICurveToWB(c));
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        noFill();
        render.drawPolygonEdges(boundaries);

        for (ZSkeleton s : skeletons) {
            zRender.drawSkeleton3D(s);
        }

//        fill(0);
//        textSize(15);
//        for (int i = 0; i < wb_polygon.getNumberOfShellPoints(); i++) {
//            text(i, wb_polygon.getPoint(i).xf(), wb_polygon.getPoint(i).yf());
//        }
//        int[] npc = wb_polygon.getNumberOfPointsPerContour();
//        int index = wb_polygon.getNumberOfShellPoints();
//        for (int i = 0; i < wb_polygon.getNumberOfHoles(); i++) {
//            for (int j = 0; j < npc[i + 1]; j++) {
//                text(j, wb_polygon.getPoint(j + index).xf(), wb_polygon.getPoint(j + index).yf());
//            }
//            index += npc[i + 1];
//        }
//        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
//            LineString curr = polygon.getInteriorRingN(i);
//            for (int j = 0; j < curr.getNumPoints(); j++) {
//                text(j, (float) curr.getCoordinates()[j].x, (float) curr.getCoordinates()[j].y);
//            }
//        }
//
//        translate(1000, 0, 0);
//        fill(200);
//        zRender.drawGeometry(polygon);
//        fill(0);
//        textSize(15);
//        for (int i = 0; i < polygon.getExteriorRing().getNumPoints(); i++) {
//            text(i, (float) polygon.getCoordinates()[i].x, (float) polygon.getCoordinates()[i].y);
//        }
//        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
//            LineString curr = polygon.getInteriorRingN(i);
//            for (int j = 0; j < curr.getNumPoints(); j++) {
//                text(j, (float) curr.getCoordinates()[j].x, (float) curr.getCoordinates()[j].y);
//            }
//        }
//
//        zRender.drawSkeleton2D(skeleton);
    }

    private double mach = Math.PI / 4;


    public void keyPressed() {
        if (key == 's') {
            String className = getClass().getSimpleName();
            save("./src/test/resources/exampleImgs/" + className + ".jpg");
        }
    }

}
