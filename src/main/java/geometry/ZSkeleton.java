package geometry;

import math.ZGeoMath;
import org.twak.camp.*;
import org.twak.utils.collections.Loop;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Vertex;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/30
 * @time 11:12
 * @description compute straight skeleton using "campskeleton"
 * and extract some useful geometries
 * only support 2D polygons counter-clockwise (face up)
 */
public class ZSkeleton {
    // input
    private WB_Polygon polygon;
    private double capHeight = 0;
    private double generalMachine = Math.PI / 4;

    private Skeleton skel;

    // output
    private List<ZLine> allEdges;
    private List<ZLine> topEdges;
    private List<ZLine> sideEdges;
    private List<ZLine> bottomEdges;

    private List<ZLine> ridges;
    private List<ZPoint> ridgePoints;

    /* ------------- constructors ------------- */

    public ZSkeleton() {

    }

    public ZSkeleton(WB_Polygon polygon) {
        // input polygon needs to be upside
        this.polygon = ZGeoMath.faceUp(polygon);
        initSkeleton();
        // extract bottom, side, top edges and ridges, ridgePoint
        extractEdges2D();
    }

    public ZSkeleton(WB_Polygon polygon, double capHeight) {
        // input polygon needs to be face upside
        this.polygon = ZGeoMath.faceUp(polygon);
        setCapHeight(capHeight);
        initSkeleton();
        // extract bottom, side, top edges and ridges, ridgePoint
        extractEdges2D();
    }

    public ZSkeleton(WB_Polygon polygon, boolean if3d) {
        // input polygon needs to be upside
        this.polygon = ZGeoMath.faceUp(polygon);
        initSkeleton();

        // extract bottom, side, top edges and ridges, ridgePoint
        if (if3d) {
            extractEdges3D();
        } else {
            extractEdges2D();
        }
    }

    public ZSkeleton(WB_Polygon polygon, double capHeight, boolean if3d) {
        // input polygon needs to be face upside
        this.polygon = ZGeoMath.faceUp(polygon);
        setCapHeight(capHeight);
        initSkeleton();

        // extract bottom, side, top edges and ridges, ridgePoint
        if (if3d) {
            extractEdges3D();
        } else {
            extractEdges2D();
        }
    }

    /* ------------- set & get ------------- */

    public void setCapHeight(double capHeight) {
        this.capHeight = capHeight;
    }

    public void setGeneralMachine(double generalMachine) {
        this.generalMachine = generalMachine;
    }

    public List<ZLine> getAllEdges() {
        return this.allEdges;
    }

    public List<ZLine> getTopEdges() {
        return this.topEdges;
    }

    public List<ZLine> getSideEdges() {
        return this.sideEdges;
    }

    public List<ZLine> getBottomEdges() {
        return this.bottomEdges;
    }

    public List<ZLine> getRidges() {
        return this.ridges;
    }

    public List<ZPoint> getRidgePoints() {
        return this.ridgePoints;
    }

    /* ------------- initialize ------------- */

    /**
     * @return void
     * @description initialize straight skeleton by settings
     */
    public void initSkeleton() {
        this.allEdges = new ArrayList<>();
        this.topEdges = new ArrayList<>();
        this.sideEdges = new ArrayList<>();
        this.bottomEdges = new ArrayList<>();

        this.ridgePoints = new ArrayList<>();
        this.ridges = new ArrayList<>();
        // add corners
        List<Corner> corners = new ArrayList<>();
        for (int i = 0; i < polygon.getNumberOfPoints(); i++) {
            corners.add(new Corner(polygon.getPoint(i).xd(), polygon.getPoint(i).yd()));
        }

        // set angles
        Machine speed = new Machine(generalMachine);

        // create loop
        Loop<Edge> loop = new Loop<>();
        for (int j = 0; j < corners.size() - 1; j++) {
            Edge edge = new Edge(corners.get(j), corners.get((j + 1) % (corners.size() - 1)));
            edge.machine = speed;
            loop.append(edge);
        }

        // add cap or not
        if (this.capHeight == 0) {
            this.skel = new Skeleton(loop.singleton(), true);
        } else {
            this.skel = new Skeleton(loop.singleton(), capHeight);
        }
        skel.skeleton();
    }

    /**
     * @return void
     * @description (3D mode)extract bottom, side, top edges and ridges, ridgePoint
     */
    private void extractEdges3D() {
        // extract all edges
        for (Output.SharedEdge se : skel.output.edges.map.values()) {
            allEdges.add(new ZLine(new ZPoint(se.start.getX(), se.start.getY(), se.start.getZ()), new ZPoint(se.end.getX(), se.end.getY(), se.end.getZ())));
        }

        // convert skeleton faces to HE_Mesh
        List<WB_Polygon> faces = new ArrayList<>();
        Set<Output.Face> nonRepeatFace = new HashSet<>();
        for (Output.Face face : skel.output.faces.values()) {
            if (!nonRepeatFace.contains(face) && face.points.size() == 1) {
                List<WB_Point> polyPoints = new ArrayList<>();
                for (Loop<Point3d> loop : face.points) {
                    for (Point3d p : loop) {
                        polyPoints.add(new WB_Point(p.getX(), p.getY(), p.getZ()));
                    }
                }
                faces.add(new WB_Polygon(polyPoints));
            }
            nonRepeatFace.add(face);
        }
        HE_Mesh mesh = new HEC_FromPolygons(faces).create();

        // find ridge in skeleton
        List<HE_Vertex> curr_vertices = new ArrayList<>();
        for (int i = 0; i < mesh.getNumberOfVertices(); i++) {
            if (!mesh.getVertexWithIndex(i).isBoundary()) {
                curr_vertices.add(mesh.getVertexWithIndex(i));
                ridgePoints.add(new ZPoint(mesh.getVertexWithIndex(i)));
                for (HE_Vertex vertex : mesh.getVertexWithIndex(i).getNeighborVertices()) {
                    if (!vertex.isBoundary() && !curr_vertices.contains(vertex)) {
                        ridges.add(new ZLine(new ZPoint(mesh.getVertexWithIndex(i)), new ZPoint(vertex)));
                    }
                }
            }
        }

        // find top, side, bottom edges
        Set<Output.SharedEdge> nonRepeatShared = new HashSet<>();
        for (Output.Face face : skel.output.faces.values()) {
            for (Loop<Output.SharedEdge> egdeLoop : face.edges) {
                for (Output.SharedEdge edge : egdeLoop) {
                    ZPoint start = new ZPoint(edge.start.getX(), edge.start.getY(), edge.start.getZ());
                    ZPoint end = new ZPoint(edge.end.getX(), edge.end.getY(), edge.end.getZ());
                    if (face.isSide(edge) && !nonRepeatShared.contains(edge)) {
                        sideEdges.add(new ZLine(start, end));
                    } else if (face.isBottom(edge) && !nonRepeatShared.contains(edge)) {
                        bottomEdges.add(new ZLine(start, end));
                    } else if (face.isTop(edge) && !nonRepeatShared.contains(edge)) {
                        topEdges.add(new ZLine(start, end));
                    }
                    nonRepeatShared.add(edge);
                }
            }
        }
    }

    /**
     * @return void
     * @description (2D mode)extract bottom, side, top edges and ridges, ridgePoint
     */
    private void extractEdges2D() {
        // extract all edges
        for (Output.SharedEdge se : skel.output.edges.map.values()) {
            allEdges.add(new ZLine(new ZPoint(se.start.getX(), se.start.getY()), new ZPoint(se.end.getX(), se.end.getY())));
        }

        // convert skeleton faces to HE_Mesh
        List<WB_Polygon> faces = new ArrayList<>();
        Set<Output.Face> nonRepeatFace = new HashSet<>();
        for (Output.Face face : skel.output.faces.values()) {
            if (!nonRepeatFace.contains(face) && face.points.size() == 1) {
                List<WB_Point> polyPoints = new ArrayList<>();
                for (Loop<Point3d> loop : face.points) {
                    for (Point3d p : loop) {
                        polyPoints.add(new WB_Point(p.getX(), p.getY()));
                    }
                }
                faces.add(new WB_Polygon(polyPoints));
            }
            nonRepeatFace.add(face);
        }
        HE_Mesh mesh = new HEC_FromPolygons(faces).create();

        // find ridge in skeleton
        List<HE_Vertex> curr_vertices = new ArrayList<>();
        for (int i = 0; i < mesh.getNumberOfVertices(); i++) {
            if (!mesh.getVertexWithIndex(i).isBoundary()) {
                curr_vertices.add(mesh.getVertexWithIndex(i));
                ridgePoints.add(new ZPoint(mesh.getVertexWithIndex(i)));
                for (HE_Vertex vertex : mesh.getVertexWithIndex(i).getNeighborVertices()) {
                    if (!vertex.isBoundary() && !curr_vertices.contains(vertex)) {
                        ridges.add(new ZLine(new ZPoint(mesh.getVertexWithIndex(i)), new ZPoint(vertex)));
                    }
                }
            }
        }

        // find top, side, bottom edges
        Set<Output.SharedEdge> nonRepeatShared = new HashSet<>();
        for (Output.Face face : skel.output.faces.values()) {
            for (Loop<Output.SharedEdge> egdeLoop : face.edges) {
                for (Output.SharedEdge edge : egdeLoop) {
                    ZPoint start = new ZPoint(edge.start.getX(), edge.start.getY());
                    ZPoint end = new ZPoint(edge.end.getX(), edge.end.getY());
                    if (face.isSide(edge) && !nonRepeatShared.contains(edge)) {
                        sideEdges.add(new ZLine(start, end));
                    } else if (face.isBottom(edge) && !nonRepeatShared.contains(edge)) {
                        bottomEdges.add(new ZLine(start, end));
                    } else if (face.isTop(edge) && !nonRepeatShared.contains(edge)) {
                        topEdges.add(new ZLine(start, end));
                    }
                    nonRepeatShared.add(edge);
                }
            }
        }
    }


    /*-------- print & draw --------*/

    public void printInfo() {
        System.out.println("allEdges: " + allEdges.size() +
                "\n" + "topEdges: " + topEdges.size() +
                "\n" + "sideEdges: " + sideEdges.size() +
                "\n" + "bottomEdges: " + bottomEdges.size() +
                "\n" + "ridgePoints: " + ridgePoints.size() +
                "\n" + "ridges: " + ridges.size());
    }

    public void display(PApplet app) {
        app.pushStyle();
        app.stroke(57, 137, 203);
        for (ZLine line : allEdges) {
            line.display(app, 1);
        }
        app.stroke(137, 57, 50);
        for (ZLine ridge : ridges) {
            ridge.display(app, 5);
        }
        app.popStyle();
    }
}
