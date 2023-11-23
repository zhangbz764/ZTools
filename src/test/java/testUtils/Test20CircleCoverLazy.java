package testUtils;

import advancedGeometry.circleCover.ZCircleCover;
import guo_cam.CameraController;
import math.ZGeoMath;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2023/11/23
 * @time 21:03
 */
public class Test20CircleCoverLazy extends PApplet {
    public static void main(String[] args) {
        PApplet.main("testUtils.Test20CircleCoverLazy");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private List<WB_Point> pts;
    private List<WB_Point> centersPtCover;
    private List<WB_Point> centersPolyCover;

    private WB_Polygon polygon;

    private CameraController gcam;
    private WB_Render render;

    public void setup() {
        this.gcam = new CameraController(this);
        this.render = new WB_Render(this);

        // points cover
        this.pts = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            pts.add(new WB_Point(random(300, 700), random(0, 400)));
        }

        ZCircleCover circleCover = new ZCircleCover(pts, 5, 120);
        circleCover.init();
        this.centersPtCover = circleCover.getCircleCenters();

        // polygon cover
        this.polygon = new WB_Polygon(new WB_Point[]{
                new WB_Point(0, 0),
                new WB_Point(50, 0),
                new WB_Point(50, 100),
                new WB_Point(150, 200),
                new WB_Point(150, 400),
                new WB_Point(120, 380),
                new WB_Point(70, 250),
                new WB_Point(0, 80),
                new WB_Point(0, 0),
        });
        List<WB_Point> divPts = ZGeoMath.dividePolyLineByStep(polygon, 5);
        System.out.println(divPts.size());

        ZCircleCover circleCover2 = new ZCircleCover(divPts, 5, 50);
        circleCover2.init();
        this.centersPolyCover = circleCover2.getCircleCenters();
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        noFill();

        stroke(0);
        for (int i = 0; i < pts.size(); i++) {
            ellipse(pts.get(i).xf(), pts.get(i).yf(), 5, 5);
        }
        render.drawPolygonEdges(polygon);

        stroke(255, 0, 0);
        for (int i = 0; i < centersPtCover.size(); i++) {
            ellipse(centersPtCover.get(i).xf(), centersPtCover.get(i).yf(), 240, 240);
        }

        stroke(0, 0, 255);
        for (int i = 0; i < centersPolyCover.size(); i++) {
            ellipse(centersPolyCover.get(i).xf(), centersPolyCover.get(i).yf(), 100, 100);
        }
    }

    public void keyPressed() {
        if (key == 's') {
            String className = getClass().getSimpleName();
            save("./src/test/resources/exampleImgs/" + className + ".jpg");
        }
    }
}
