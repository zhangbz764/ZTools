package testFunc;

import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import guo_cam.CameraController;
import igeo.ICurve;
import igeo.IG;
import math.ZGeoMath;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.ConformingDelaunayTriangulationBuilder;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project Ztools
 * @date 2022/8/10
 * @time 16:48
 */
public class TestJtsTri extends PApplet {
    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private JtsRender jtsRender;
    private CameraController gcam;

    private Polygon boundary;
    private LineString[] terrains;
    private double[] terrainH;
    private Polygon cutBoundary;

    private List<Point> delaunayGen;
    private Geometry tri1;
    private Geometry tri2;
    private Geometry tri2Edges;

    public void setup() {
        this.jtsRender = new JtsRender(this);
        this.gcam = new CameraController(this);

        load();

        this.delaunayGen = new ArrayList<>();
        for (int i = 0; i < terrains.length; i++) {
            LineString contour = terrains[i];
            List<ZPoint> split = ZGeoMath.splitPolyLineByThreshold(contour, 20, 10, 1);
            for (ZPoint p : split) {
                Point pt = p.toJtsPoint();
                delaunayGen.add(p.toJtsPoint());
            }
        }
        System.out.println(delaunayGen.size());

        Point[] gen = delaunayGen.toArray(new Point[0]);
        MultiPoint multiPoint = ZFactory.jtsgf.createMultiPoint(gen);

        DelaunayTriangulationBuilder builder1 = new DelaunayTriangulationBuilder();
        builder1.setSites(multiPoint);
        tri1 = builder1.getTriangles(ZFactory.jtsgf);


        ConformingDelaunayTriangulationBuilder builder2 = new ConformingDelaunayTriangulationBuilder();
        builder2.setSites(multiPoint);
        Point[] cons = new Point[cutBoundary.getNumPoints()];
        for (int i = 0; i < cutBoundary.getNumPoints(); ++i) {
            cons[i] = ZFactory.jtsgf.createPoint(cutBoundary.getCoordinates()[i]);
        }
        MultiPoint conMP = ZFactory.jtsgf.createMultiPoint(cons);
        builder2.setConstraints(conMP);
        tri2 = builder2.getTriangles(ZFactory.jtsgf);

        tri2Edges = builder2.getEdges(ZFactory.jtsgf);


    }

    public void load() {
        // import
        String path = ".\\src\\test\\resources\\test_triangulation.3dm";
        IG.init();
        IG.open(path);

        ICurve[] _boundary = IG.layer("boundary").curves();
        this.boundary = (Polygon) ZTransform.ICurveToJts(_boundary[0]);

        ICurve[] _terrains = IG.layer("terrain").curves();
        this.terrains = new LineString[_terrains.length];
        this.terrainH = new double[_terrains.length];
        for (int i = 0; i < _terrains.length; i++) {
            this.terrainH[i] = Double.parseDouble(_terrains[i].attribute.name);
            this.terrains[i] = (LineString) ZTransform.ICurveToLineString(_terrains[i]);
        }

        ICurve[] _cutboundary = IG.layer("cutBoundary").curves();
        this.cutBoundary = (Polygon) ZTransform.ICurveToJts(_cutboundary[0]);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        stroke(255, 0, 0);
        strokeWeight(3);
        jtsRender.drawGeometry(boundary);
        stroke(0);
        strokeWeight(1);
        for (LineString ls : terrains) {
            jtsRender.drawGeometry(ls);
        }
        stroke(0, 0, 255);
        jtsRender.drawGeometry(cutBoundary);

        stroke(0);
        for (Point p : delaunayGen) {
            ellipse((float) p.getX(), (float) p.getY(), 3, 3);
        }

        translate(200, 0);
        strokeWeight(2);
        stroke(0, 0, 255);
        jtsRender.drawGeometry(cutBoundary);
        strokeWeight(1);
        stroke(185, 79, 242);
        if (draw) {
            jtsRender.drawGeometry(tri1);
        } else {
            jtsRender.drawGeometry(tri2);
        }

    }

    private boolean draw;

    public void keyPressed() {
        if (key == 'q') {
            draw = !draw;
        }
    }
}
