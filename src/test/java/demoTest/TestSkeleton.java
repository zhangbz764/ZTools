package demoTest;

import Guo_Cam.CameraController;
import geometry.ZSkeleton;
import igeo.ICurve;
import igeo.IG;
import processing.core.PApplet;
import render.ZRender;
import transform.ZTransform;
import wblut.geom.WB_GeometryFactory3D;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 测试ZSkeleton
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/31
 * @time 11:27
 */
public class TestSkeleton extends PApplet {

    public void settings() {
        size(1800, 1000, P3D);
    }

    WB_Point[] pts;
    List<WB_Polygon> inputBoundary;
    List<ZSkeleton> skeletons;

    WB_Render render;
    CameraController gcam;
    WB_GeometryFactory3D wbgf = new WB_GeometryFactory3D();

    public void setup() {
        gcam = new CameraController(this);
        render = new WB_Render(this);

        String path = Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("./test_convex_hull.3dm")
        ).getPath();

        IG.init();
        IG.open(path);

        // load boundary polygon
        ICurve[] boundary = IG.layer("test").curves();
        inputBoundary = new ArrayList<>();
        for (ICurve iCurve : boundary) {
            inputBoundary.add((WB_Polygon) ZTransform.ICurveToWB(iCurve));
        }

        skeletons = new ArrayList<>();
        for (WB_Polygon p : inputBoundary) {
            ZSkeleton s = new ZSkeleton(p, 50, true);
            skeletons.add(s);
        }
    }

    public void draw() {
        background(255);
        ZRender.drawAxis3D(this);
        noFill();

        gcam.begin3d();
        fill(0);

        for (ZSkeleton s : skeletons) {
            s.display(this);
        }
    }
}
