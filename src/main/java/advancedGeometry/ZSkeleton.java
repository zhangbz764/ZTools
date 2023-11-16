package advancedGeometry;

import basicGeometry.ZFactory;
import math.ZGeoMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.twak.camp.*;
import org.twak.utils.collections.Loop;
import org.twak.utils.collections.LoopL;
import transform.ZTransform;
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
 * compute straight skeleton using campskeleton by twak
 * and extract some geometries, support 2D polygon with holes
 * polygon shell must be counterclockwise
 * polygon should be valid (first point coincides with last point)
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/30
 * @time 11:12
 */
public class ZSkeleton {
    // input
    private Polygon polygon;
    private double capHeight = 0;
    private double offsetDist = 0;
    private double generalMachine = Math.PI / 3;

    private Skeleton skeleton;

    // output
    private List<LineString> allEdges;
    private List<LineString> topEdges;
    private List<LineString> sideEdges;
    private List<LineString> bottomEdges;

    private List<LineString> ridges;
    private List<LineString> extendedRidges;
    private List<Coordinate> ridgePoints;

    private List<Polygon> allFacePolys;
    private HE_Mesh skeletonMesh;

    /* ------------- constructors ------------- */

    public ZSkeleton() {

    }

    public ZSkeleton(Polygon polygon) {
        // input polygon needs to be face up (norm)
        this.polygon = (Polygon) polygon.norm().reverse();

        initSkeleton();
        // extract bottom, side, top edges and ridges, ridgePoint
        extractEdges2D();
    }

    public ZSkeleton(Polygon polygon, double offsetDist, boolean if3d) {
        // input polygon needs to be face up (norm)
        this.polygon = (Polygon) polygon.norm().reverse();

        setOffsetDist(offsetDist);
        initSkeleton();
        // extract bottom, side, top edges and ridges, ridgePoint
        if (if3d) {
            extractEdges3D();
        } else {
            extractEdges2D();
        }
    }

    public ZSkeleton(WB_Polygon polygon) {
        // input polygon needs to be face up (norm)
        WB_Polygon validate = ZGeoMath.polygonFaceUp(ZTransform.validateWB_Polygon(polygon));
        this.polygon = ZTransform.WB_PolygonToPolygon(validate);

        initSkeleton();
        // extract bottom, side, top edges and ridges, ridgePoint
        extractEdges2D();
    }

    public ZSkeleton(WB_Polygon polygon, double offsetDist, boolean if3d) {
        // input polygon needs to be face up (norm)
        WB_Polygon validate = ZGeoMath.polygonFaceUp(ZTransform.validateWB_Polygon(polygon));
        this.polygon = ZTransform.WB_PolygonToPolygon(validate);

        setOffsetDist(offsetDist);
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

    public Skeleton getSkeleton() {
        return skeleton;
    }

    public List<LineString> getAllEdges() {
        return this.allEdges;
    }

    public List<LineString> getTopEdges() {
        return this.topEdges;
    }

    public List<LineString> getSideEdges() {
        return this.sideEdges;
    }

    public List<LineString> getBottomEdges() {
        return this.bottomEdges;
    }

    public List<LineString> getRidges() {
        return this.ridges;
    }

    public List<LineString> getExtendedRidges() {
        return this.extendedRidges;
    }

    public List<Coordinate> getRidgePoints() {
        return this.ridgePoints;
    }

    public List<Polygon> getAllFacePolys() {
        return allFacePolys;
    }

    public List<WB_Polygon> getAllFacePolysWB() {
        List<WB_Polygon> result = new ArrayList<>();
        for (Polygon p : allFacePolys) {
            result.add(ZTransform.PolygonToWB_Polygon(p));
        }
        return result;
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
    public void initSkeleton() {
        this.allEdges = new ArrayList<>();
        this.topEdges = new ArrayList<>();
        this.sideEdges = new ArrayList<>();
        this.bottomEdges = new ArrayList<>();

        this.ridgePoints = new ArrayList<>();
        this.ridges = new ArrayList<>();
        this.extendedRidges = new ArrayList<>();

        // set angles
        Machine speed = new Machine(generalMachine);

        if (this.polygon.getNumInteriorRing() > 0) {
            LoopL<Edge> loopL = new LoopL<>();

            // exterior
            LineString exterior = polygon.getExteriorRing();
            List<Corner> corners_E = new ArrayList<>();
            for (int i = 0; i < exterior.getNumPoints(); i++) {
                corners_E.add(new Corner(exterior.getCoordinateN(i).getX(), exterior.getCoordinateN(i).getY()));
            }
            Loop<Edge> loop_E = new Loop<>();
            for (int j = 0; j < corners_E.size() - 1; j++) {
                Edge edge = new Edge(corners_E.get(j), corners_E.get((j + 1) % (corners_E.size() - 1)));
                edge.machine = speed;
                loop_E.append(edge);
            }
            loopL.add(loop_E);

            // interior holes should be clockwise
            for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
                LineString interior = polygon.getInteriorRingN(i);
                List<Corner> corners_I = new ArrayList<>();
                for (int j = 0; j < interior.getNumPoints(); j++) {
                    corners_I.add(new Corner(interior.getCoordinateN(j).getX(), interior.getCoordinateN(j).getY()));
                }
                Loop<Edge> loop_I = new Loop<>();
                for (int j = 0; j < corners_I.size() - 1; j++) {
                    Edge edge = new Edge(corners_I.get(j), corners_I.get((j + 1) % (corners_I.size() - 1)));
                    edge.machine = speed;
                    loop_I.append(edge);
                }
                loopL.add(loop_I);
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
        } else {
            // add corners
            List<Corner> corners = new ArrayList<>();
            for (int i = 0; i < polygon.getCoordinates().length; i++) {
                corners.add(new Corner(polygon.getCoordinates()[i].getX(), polygon.getCoordinates()[i].getY()));
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
            allEdges.add(ZFactory.jtsgf.createLineString(
                    new Coordinate[]{
                            new Coordinate(se.start.getX(), se.start.getY(), se.start.getZ()), new Coordinate(se.end.getX(), se.end.getY(), se.end.getZ())
                    }
            ));
        }

        // convert skeleton faces to HE_Mesh, find ridges from mesh
        this.allFacePolys = new ArrayList<>();
        Set<Output.Face> nonRepeatFace = new HashSet<>();
        for (Output.Face face : skeleton.output.faces.values()) {
            if (!nonRepeatFace.contains(face) && face.points.size() == 1) {
                List<Coordinate> polyCoords = new ArrayList<>();
                for (Loop<Point3d> loop : face.points) {
                    for (Point3d p : loop) {
                        polyCoords.add(new Coordinate(p.getX(), p.getY(), p.getZ()));
                    }
                }
                polyCoords.add(polyCoords.get(0));
                allFacePolys.add(ZFactory.createPolygonFromList(polyCoords));
            }
            nonRepeatFace.add(face);
        }
        findRidgesFromMesh(allFacePolys);

        // find top, side, bottom edges
        Set<Output.SharedEdge> nonRepeatShared = new HashSet<>();
        for (Output.Face face : skeleton.output.faces.values()) {
            for (Loop<Output.SharedEdge> egdeLoop : face.edges) {
                for (Output.SharedEdge edge : egdeLoop) {
                    Coordinate start = new Coordinate(edge.start.getX(), edge.start.getY(), edge.start.getZ());
                    Coordinate end = new Coordinate(edge.end.getX(), edge.end.getY(), edge.start.getZ());
                    if (face.isSide(edge) && !nonRepeatShared.contains(edge)) {
                        sideEdges.add(ZFactory.jtsgf.createLineString(new Coordinate[]{start, end}));
                    } else if (face.isBottom(edge) && !nonRepeatShared.contains(edge)) {
                        bottomEdges.add(ZFactory.jtsgf.createLineString(new Coordinate[]{start, end}));
                    } else if (face.isTop(edge) && !nonRepeatShared.contains(edge)) {
                        topEdges.add(ZFactory.jtsgf.createLineString(new Coordinate[]{start, end}));
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
            allEdges.add(ZFactory.jtsgf.createLineString(
                    new Coordinate[]{
                            new Coordinate(se.start.getX(), se.start.getY()), new Coordinate(se.end.getX(), se.end.getY())
                    }
            ));
        }

        // convert skeleton faces to HE_Mesh, find ridges from mesh
        this.allFacePolys = new ArrayList<>();
        Set<Output.Face> nonRepeatFace = new HashSet<>();
        for (Output.Face face : skeleton.output.faces.values()) {
            if (!nonRepeatFace.contains(face) && face.points.size() == 1) {
                List<Coordinate> polyCoords = new ArrayList<>();
                for (Loop<Point3d> loop : face.points) {
                    for (Point3d p : loop) {
                        polyCoords.add(new Coordinate(p.getX(), p.getY()));
                    }
                }
                polyCoords.add(polyCoords.get(0));
                allFacePolys.add(ZFactory.createPolygonFromList(polyCoords));
            }
            nonRepeatFace.add(face);
        }
        findRidgesFromMesh(allFacePolys);

        // find top, side, bottom edges
        Set<Output.SharedEdge> nonRepeatShared = new HashSet<>();
        for (Output.Face face : skeleton.output.faces.values()) {
            for (Loop<Output.SharedEdge> egdeLoop : face.edges) {
                for (Output.SharedEdge edge : egdeLoop) {
                    Coordinate start = new Coordinate(edge.start.getX(), edge.start.getY());
                    Coordinate end = new Coordinate(edge.end.getX(), edge.end.getY());
                    if (face.isSide(edge) && !nonRepeatShared.contains(edge)) {
                        sideEdges.add(ZFactory.jtsgf.createLineString(new Coordinate[]{start, end}));
                    } else if (face.isBottom(edge) && !nonRepeatShared.contains(edge)) {
                        bottomEdges.add(ZFactory.jtsgf.createLineString(new Coordinate[]{start, end}));
                    } else if (face.isTop(edge) && !nonRepeatShared.contains(edge)) {
                        topEdges.add(ZFactory.jtsgf.createLineString(new Coordinate[]{start, end}));
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
    private void findRidgesFromMesh(List<Polygon> faces) {
        List<WB_Polygon> facesTemp = new ArrayList<>();
        for (Polygon f : faces) {
            facesTemp.add(ZTransform.PolygonToWB_Polygon(f));
        }
        this.skeletonMesh = new HEC_FromPolygons(facesTemp).create();

        // find ridge in skeleton
        List<HE_Vertex> curr_vertices = new ArrayList<>();
        for (int i = 0; i < skeletonMesh.getNumberOfVertices(); i++) {
            HE_Vertex v = skeletonMesh.getVertexWithIndex(i);
            if (!v.isBoundary()) {
                curr_vertices.add(v);
                ridgePoints.add(new Coordinate(v.xd(), v.yd(), v.zd()));

                List<HE_Vertex> verticesFromRidgeEnd = new ArrayList<>();
                for (HE_Vertex vertex : v.getNeighborVertices()) {
                    if (!vertex.isBoundary() && !curr_vertices.contains(vertex)) {
                        ridges.add(ZFactory.jtsgf.createLineString(new Coordinate[]{
                                ZTransform.WB_CoordToCoordinate(v),
                                ZTransform.WB_CoordToCoordinate(vertex)
                        }));
                    }
                    if (vertex.isBoundary()) {
                        verticesFromRidgeEnd.add(vertex);
                    }
                }

                if (verticesFromRidgeEnd.size() == 2 && v.getNeighborVertices().size() == 3) {
                    HE_Vertex v0 = verticesFromRidgeEnd.get(0);
                    HE_Vertex v1 = verticesFromRidgeEnd.get(1);

                    Coordinate center = new Coordinate((v0.xd() + v1.xd()) * 0.5, (v0.yd() + v1.yd()) * 0.5, (v0.zd() + v1.zd()) * 0.5);
                    extendedRidges.add(ZFactory.jtsgf.createLineString(new Coordinate[]{ZTransform.WB_CoordToCoordinate(v), center}));
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

}
