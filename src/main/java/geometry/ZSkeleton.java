package geometry;

import math.ZGeoMath;
import org.twak.camp.*;
import org.twak.utils.collections.Loop;
import org.twak.utils.collections.LoopL;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Vertex;

import javax.vecmath.Point3d;
import java.util.*;

/**
 * compute straight skeleton using campskeleton by twak
 * and extract some useful geometries, support 2D polygon with holes
 * <p>
 * polygon shell must be counter clockwise
 * polygon should be valid (first point coincides with last point)
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/30
 * @time 11:12
 */
public class ZSkeleton {
    // input
    private WB_Polygon polygon;
    private double capHeight = 0;
    private double offsetDist = 0;
    private double generalMachine = Math.PI / 4;

    private Skeleton skeleton;

    // output
    private List<ZLine> allEdges;
    private List<ZLine> topEdges;
    private List<ZLine> sideEdges;
    private List<ZLine> bottomEdges;

    private List<ZLine> ridges;
    private List<ZLine> extendedRidges;
    private List<ZPoint> ridgePoints;

    private List<WB_Polygon> allFacePolys;
    private HE_Mesh skeletonMesh;

    /* ------------- constructors ------------- */

    public ZSkeleton() {

    }

    public ZSkeleton(WB_Polygon polygon) {
        this.polygon = ZGeoMath.polygonFaceUp(ZTransform.validateWB_Polygon(polygon));

        initSkeleton();
        // extract bottom, side, top edges and ridges, ridgePoint
        extractEdges2D();
    }

    public ZSkeleton(WB_Polygon polygon, double offsetDist) {
        // input polygon needs to be face upside
        this.polygon = ZGeoMath.polygonFaceUp(ZTransform.validateWB_Polygon(polygon));

        setOffsetDist(offsetDist);
        initSkeleton();
        // extract bottom, side, top edges and ridges, ridgePoint
        extractEdges2D();
    }

    public ZSkeleton(WB_Polygon polygon, boolean if3d) {
        // input polygon needs to be upside
        this.polygon = ZGeoMath.polygonFaceUp(ZTransform.validateWB_Polygon(polygon));

        initSkeleton();
        // extract bottom, side, top edges and ridges, ridgePoint
        if (if3d) {
            extractEdges3D();
        } else {
            extractEdges2D();
        }
    }

    public ZSkeleton(WB_Polygon polygon, double offsetDist, boolean if3d) {
        // input polygon needs to be face upside
        this.polygon = ZGeoMath.polygonFaceUp(ZTransform.validateWB_Polygon(polygon));

        setOffsetDist(capHeight);
        initSkeleton();

        // extract bottom, side, top edges and ridges, ridgePoint
        if (if3d) {
            extractEdges3D();
        } else {
            extractEdges2D();
        }
    }

    /* ------------- set & get ------------- */

    public void setOffsetDist(double offsetDist) {
        this.offsetDist = offsetDist;
    }

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

    public List<ZLine> getExtendedRidges() {
        return this.extendedRidges;
    }

    public List<ZPoint> getRidgePoints() {
        return this.ridgePoints;
    }

    public List<WB_Polygon> getAllFacePolys() {
        return allFacePolys;
    }

    public HE_Mesh getSkeletonMesh() {
        return skeletonMesh;
    }

    /* ------------- initialize ------------- */

    /**
     * initialize straight skeleton
     *
     * @param
     * @return void
     */
    private void initSkeleton() {
        this.allEdges = new ArrayList<>();
        this.topEdges = new ArrayList<>();
        this.sideEdges = new ArrayList<>();
        this.bottomEdges = new ArrayList<>();

        this.ridgePoints = new ArrayList<>();
        this.ridges = new ArrayList<>();
        this.extendedRidges = new ArrayList<>();

        // set angles
        Machine speed = new Machine(generalMachine);

        if (this.polygon.getNumberOfHoles() == 0) {
            // add corners
            List<Corner> corners = new ArrayList<>();
            for (int i = 0; i < polygon.getNumberOfPoints(); i++) {
                corners.add(new Corner(polygon.getPoint(i).xd(), polygon.getPoint(i).yd()));
            }
            // create loop
            Loop<Edge> loop = new Loop<>();
            for (int j = 0; j < corners.size() - 1; j++) {
                Edge edge = new Edge(corners.get(j), corners.get((j + 1) % (corners.size() - 1)));
                edge.machine = speed;
                loop.append(edge);
            }
            // add cap or not
            if (this.offsetDist == 0) {
                this.skeleton = new Skeleton(loop.singleton(), true);
            } else {
                if (this.generalMachine == Math.PI / 4) {
                    this.skeleton = new Skeleton(loop.singleton(), offsetDist);
                } else {
                    this.skeleton = new Skeleton(loop.singleton(), offsetDist * Math.tan(generalMachine));
                }
            }
        } else {
            // holes should be clockwise
            LoopL<Edge> loopL = new LoopL<>();
            final int[] npc = polygon.getNumberOfPointsPerContour();
            int index = 0; // count
            for (int i = 0; i < polygon.getNumberOfContours(); i++) {
                List<Corner> corners = new ArrayList<>();
                for (int j = 0; j < npc[i]; j++) {
                    corners.add(new Corner(polygon.getPoint(index).xd(), polygon.getPoint(index).yd()));
                    index = index + 1;
                }
                Loop<Edge> loop = new Loop<>();
                for (int j = 0; j < corners.size() - 1; j++) {
                    Edge edge = new Edge(corners.get(j), corners.get((j + 1) % (corners.size() - 1)));
                    edge.machine = new Machine(Math.PI / 4);
                    loop.append(edge);
                }
                loopL.add(loop);
            }
            // add cap or not
            if (this.offsetDist == 0) {
                this.skeleton = new Skeleton(loopL, true);
            } else {
                if (this.generalMachine == Math.PI / 4) {
                    this.skeleton = new Skeleton(loopL, offsetDist);
                } else {
                    this.skeleton = new Skeleton(loopL, offsetDist * Math.tan(generalMachine));
                }
            }
        }
        skeleton.skeleton();
    }

    /**
     * extract edges in 3D mode
     *
     * @param
     * @return void
     */
    private void extractEdges3D() {
        // extract all edges
        for (Output.SharedEdge se : skeleton.output.edges.map.values()) {
            allEdges.add(new ZLine(new ZPoint(se.start.getX(), se.start.getY(), se.start.getZ()), new ZPoint(se.end.getX(), se.end.getY(), se.end.getZ())));
        }

        // convert skeleton faces to HE_Mesh, find ridges from mesh
        this.allFacePolys = new ArrayList<>();
        Set<Output.Face> nonRepeatFace = new HashSet<>();
        for (Output.Face face : skeleton.output.faces.values()) {
            if (!nonRepeatFace.contains(face) && face.points.size() == 1) {
                List<WB_Point> polyPoints = new ArrayList<>();
                for (Loop<Point3d> loop : face.points) {
                    for (Point3d p : loop) {
                        polyPoints.add(new WB_Point(p.getX(), p.getY(), p.getZ()));
                    }
                }
                allFacePolys.add(new WB_Polygon(polyPoints));
            }
            nonRepeatFace.add(face);
        }
        findRidgesFromMesh(allFacePolys);

        // find top, side, bottom edges
        Set<Output.SharedEdge> nonRepeatShared = new HashSet<>();
        for (Output.Face face : skeleton.output.faces.values()) {
            for (Loop<Output.SharedEdge> egdeLoop : face.edges) {
                for (Output.SharedEdge edge : egdeLoop) {
                    ZPoint start = new ZPoint(edge.start.getX(), edge.start.getY(), edge.start.getZ());
                    ZPoint end = new ZPoint(edge.end.getX(), edge.end.getY(), edge.start.getZ());
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
        topEdges.addAll(ridges);
    }

    /**
     * extract edges in 2D mode
     *
     * @param
     * @return void
     */
    private void extractEdges2D() {
        // extract all edges
        for (Output.SharedEdge se : skeleton.output.edges.map.values()) {
            allEdges.add(new ZLine(new ZPoint(se.start.getX(), se.start.getY()), new ZPoint(se.end.getX(), se.end.getY())));
        }

        // convert skeleton faces to HE_Mesh, find ridges from mesh
        this.allFacePolys = new ArrayList<>();
        Set<Output.Face> nonRepeatFace = new HashSet<>();
        for (Output.Face face : skeleton.output.faces.values()) {
            if (!nonRepeatFace.contains(face) && face.points.size() == 1) {
                List<WB_Point> polyPoints = new ArrayList<>();
                for (Loop<Point3d> loop : face.points) {
                    for (Point3d p : loop) {
                        polyPoints.add(new WB_Point(p.getX(), p.getY()));
                    }
                }
                allFacePolys.add(new WB_Polygon(polyPoints));
            }
            nonRepeatFace.add(face);
        }
        findRidgesFromMesh(allFacePolys);

        // find top, side, bottom edges
        Set<Output.SharedEdge> nonRepeatShared = new HashSet<>();
        for (Output.Face face : skeleton.output.faces.values()) {
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
        topEdges.addAll(ridges);
    }

    /**
     * extract ridges by converting faces to mesh
     *
     * @param faces all polygons to create mesh
     * @return void
     */
    private void findRidgesFromMesh(List<WB_Polygon> faces) {
        this.skeletonMesh = new HEC_FromPolygons(faces).create();

        // find ridge in skeleton
        List<HE_Vertex> curr_vertices = new ArrayList<>();
        for (int i = 0; i < skeletonMesh.getNumberOfVertices(); i++) {
            if (!skeletonMesh.getVertexWithIndex(i).isBoundary()) {
                curr_vertices.add(skeletonMesh.getVertexWithIndex(i));
                ridgePoints.add(new ZPoint(skeletonMesh.getVertexWithIndex(i)));

                List<ZPoint> verticesFromRidgeEnd = new ArrayList<>();
                for (HE_Vertex vertex : skeletonMesh.getVertexWithIndex(i).getNeighborVertices()) {
                    if (!vertex.isBoundary() && !curr_vertices.contains(vertex)) {
                        ridges.add(new ZLine(new ZPoint(skeletonMesh.getVertexWithIndex(i)), new ZPoint(vertex)));
                    }
                    if (vertex.isBoundary()) {
                        verticesFromRidgeEnd.add(new ZPoint(vertex));
                    }
                }
                if (verticesFromRidgeEnd.size() == 2) {
                    ZPoint center = verticesFromRidgeEnd.get(0).centerWith(verticesFromRidgeEnd.get(1));
                    extendedRidges.add(new ZLine(new ZPoint(skeletonMesh.getVertexWithIndex(i)), center));
                }
            }
        }
    }


    /*-------- print & draw --------*/

    @Override
    public String toString() {
        return "ZSkeleton{" +
                "polygon=" + polygon +
                ", capHeight=" + capHeight +
                ", offsetDist=" + offsetDist +
                ", generalMachine=" + generalMachine +
                ", allEdges=" + allEdges.size() +
                ", topEdges=" + topEdges.size() +
                ", sideEdges=" + sideEdges.size() +
                ", bottomEdges=" + bottomEdges.size() +
                ", ridges=" + ridges.size() +
                ", extendedRidges=" + extendedRidges.size() +
                '}';
    }

    public void display(PApplet app) {
        app.pushStyle();
        displayAllEdges(app);
        displayTopEdges(app);
        displayExtendedRidges(app);
        app.popStyle();
    }

    public void displayAllEdges(PApplet app) {
        app.stroke(0, 0, 200);
        for (ZLine line : allEdges) {
            line.display(app, 1);
        }
    }

    public void displayTopEdges(PApplet app) {
        app.stroke(137, 57, 50);
        for (ZLine top : topEdges) {
            top.display(app, 5);
        }
    }

    public void displayExtendedRidges(PApplet app) {
        app.stroke(104, 210, 120);
        for (ZLine extendRidge : extendedRidges) {
            extendRidge.display(app, 5);
        }
    }
}
