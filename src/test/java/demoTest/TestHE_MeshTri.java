package demoTest;

import basicGeometry.ZPoint;
import guo_cam.CameraController;
import igeo.ICurve;
import igeo.IG;
import math.ZGeoMath;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project Ztools
 * @date 2022/8/9
 * @time 15:38
 */
public class TestHE_MeshTri extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Render render;
    private CameraController gcam;

    private WB_Polygon boundary;
    private WB_PolyLine[] terrains;
    private double[] terrainH;

    private List<WB_Point> delaunayGen;

    private int[] tri1;
    private int[] tri2;

    public void setup() {
        this.render = new WB_Render(this);
        this.gcam = new CameraController(this);

        load();

        this.delaunayGen = new ArrayList<>();
        for (int i = 0; i < terrains.length; i++) {
            WB_PolyLine contour = terrains[i];
            List<ZPoint> split = ZGeoMath.splitPolyLineByThreshold(contour, 15, 10);
            for (ZPoint p : split) {
                WB_Point pt = p.toWB_Point();
                pt.setZ(terrainH[i]);
                System.out.println(pt);
                delaunayGen.add(pt);
            }
        }

        WB_Triangulation2D triangulation = WB_Triangulate.triangulate2D(delaunayGen);
        WB_AlphaTriangulation2D alphaTriangulation = WB_Triangulate.alphaTriangulate2D(delaunayGen, 10);


        this.tri1 = triangulation.getTriangles();
        this.tri2 = alphaTriangulation.getTriangles();
        System.out.println(tri1);
        System.out.println(tri2);

    }

    public void load() {
        // import
        String path = ".\\src\\test\\resources\\test_triangulation.3dm";
        IG.init();
        IG.open(path);

        ICurve[] _boundary = IG.layer("boundary").curves();
        this.boundary = (WB_Polygon) ZTransform.ICurveToWB(_boundary[0]);

        ICurve[] _terrains = IG.layer("terrain").curves();
        this.terrains = new WB_PolyLine[_terrains.length];
        this.terrainH = new double[_terrains.length];
        for (int i = 0; i < _terrains.length; i++) {
            this.terrainH[i] = Double.parseDouble(_terrains[i].attribute.name);
            this.terrains[i] = ZTransform.ICurveToWB_PolyLine(_terrains[i]);
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        for (int i = 0; i < tri1.length - 2; i += 3) {
            line(
                    delaunayGen.get(tri1[i]).xf(),
                    delaunayGen.get(tri1[i]).yf(),
                    delaunayGen.get(tri1[i + 1]).xf(),
                    delaunayGen.get(tri1[i + 1]).yf()
            );
            line(
                    delaunayGen.get(tri1[i]).xf(),
                    delaunayGen.get(tri1[i]).yf(),
                    delaunayGen.get(tri1[i + 2]).xf(),
                    delaunayGen.get(tri1[i + 2]).yf()
            );
            line(
                    delaunayGen.get(tri1[i + 1]).xf(),
                    delaunayGen.get(tri1[i + 1]).yf(),
                    delaunayGen.get(tri1[i + 2]).xf(),
                    delaunayGen.get(tri1[i + 2]).yf()
            );
        }
        translate(400, 0, 0);
        for (int i = 0; i < tri2.length - 2; i += 3) {
            line(
                    delaunayGen.get(tri2[i]).xf(),
                    delaunayGen.get(tri2[i]).yf(),
                    delaunayGen.get(tri2[i + 1]).xf(),
                    delaunayGen.get(tri2[i + 1]).yf()
            );
            line(
                    delaunayGen.get(tri2[i]).xf(),
                    delaunayGen.get(tri2[i]).yf(),
                    delaunayGen.get(tri2[i + 2]).xf(),
                    delaunayGen.get(tri2[i + 2]).yf()
            );
            line(
                    delaunayGen.get(tri2[i + 1]).xf(),
                    delaunayGen.get(tri2[i + 1]).yf(),
                    delaunayGen.get(tri2[i + 2]).xf(),
                    delaunayGen.get(tri2[i + 2]).yf()
            );
        }
    }

}
