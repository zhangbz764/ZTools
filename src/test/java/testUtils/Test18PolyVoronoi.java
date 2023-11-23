package testUtils;

import advancedGeometry.ZPolyVoronoi;
import guo_cam.CameraController;
import igeo.ICurve;
import igeo.IG;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2023/11/23
 * @time 16:36
 */
public class Test18PolyVoronoi extends PApplet {
    public static void main(String[] args) {
        PApplet.main("testUtils.Test18PolyVoronoi");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */
    private int[] colors;
    private List<WB_PolyLine> pls;
    private final WB_Polygon boundary = new WB_Polygon(
            new WB_Point[]{
                    new WB_Point(0, 0),
                    new WB_Point(400, 0),
                    new WB_Point(400, 400),
                    new WB_Point(0, 400),
                    new WB_Point(0, 0),
            }
    );

    private List<List<WB_Polygon>> originalVoro;
    private List<WB_Polygon> voronoiResults;

    private CameraController gcam;
    private WB_Render render;

    public void setup() {
        this.gcam = new CameraController(this);
        this.render = new WB_Render(this);

        loadPolys();
        this.colors = new int[pls.size()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = (int) random(0, 100);
        }

        ZPolyVoronoi polyVoronoi = new ZPolyVoronoi(pls, boundary);

        this.voronoiResults = polyVoronoi.getVoronoiResultsWB();
        this.originalVoro = polyVoronoi.getOriginalVoroWB();
    }

    private void loadPolys() {
        String path = ".\\src\\test\\resources\\test_poly_voronoi.3dm";
        IG.init();
        IG.open(path);

        ICurve[] _boundary = IG.layer("polys").curves();
        this.pls = new ArrayList<>();

        for (ICurve c : _boundary) {
            WB_Geometry2D geo = ZTransform.ICurveToWB(c);
            if (geo instanceof WB_PolyLine) {
                pls.add((WB_PolyLine) geo);
            } else if (geo instanceof WB_Segment) {
                pls.add(ZTransform.WB_SegmentToWB_PolyLine((WB_Segment) geo));
            }
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        colorMode(HSB, 100);
        background(0, 0, 100);

        stroke(0);
        noFill();
        render.drawPolygonEdges(boundary);

        stroke(0, 100, 100);
        render.drawPolylineEdges(pls);

        stroke(0);
        render.drawPolygonEdges(voronoiResults);

        translate(500, 0);
        for (int i = 0; i < originalVoro.size(); i++) {
            fill(colors[i], 100, 100);
            render.drawPolygonEdges(originalVoro.get(i));
        }
    }

    public void keyPressed() {
        if (key == 's') {
            String className = getClass().getSimpleName();
            save("./src/test/resources/exampleImgs/" + className + ".jpg");
        }
    }

}
