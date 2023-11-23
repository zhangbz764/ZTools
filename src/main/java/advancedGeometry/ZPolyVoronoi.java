package advancedGeometry;

import basicGeometry.ZFactory;
import math.ZGeoMath;
import org.locationtech.jts.geom.Polygon;
import transform.ZTransform;
import wblut.geom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2023/11/23
 * @time 16:26
 */
public class ZPolyVoronoi {
    private int step = 10;
    private List<WB_Polygon> voronoiResultsWB;
    private List<List<WB_Polygon>> originalVoroWB;

    /* ------------- constructor ------------- */

    public ZPolyVoronoi(List<? extends WB_PolyLine> polys, WB_Polygon boundary) {
        create(polys, boundary);
    }

    public ZPolyVoronoi(List<? extends WB_PolyLine> polys, WB_Polygon boundary, int density) {
        this.step = density;
        create(polys, boundary);
    }

//    public ZPolyVoronoi(List<? extends Geometry> geomerties, Polygon boundary) {
//        create(geomerties, boundary);
//    }

    /* ------------- member function ------------- */
//    private void create(List<? extends Geometry> geomerties, Polygon boundary) {
//        this.voronoiResultsJTS = new ArrayList<>();
//        this.originalVoroJTS = new ArrayList<>();
//        List<List<Coordinate>> originalGens = new ArrayList<>();
//
//        // create divided points
//        List<Coordinate> allGenerators = new ArrayList<>();
//        for (int i = 0; i < geomerties.size(); i++) {
//            Geometry geo = geomerties.get(i);
//            String type = geo.getGeometryType();
//            if (type.equals("Point")) {
//                Point pt = (Point) geo;
//                int num = 1;
//                allGenerators.add(pt.getCoordinate());
//                originalGens.add(new ArrayList<>(Collections.singletonList(pt.getCoordinate())));
//                originalVoroJTS.add(new ArrayList<>());
//            } else if (type.equals("LineString")) {
//                LineString ls = (LineString) geo;
//                List<Coordinate> divPts = ZGeoMath.dividePolyLineByStep(ls, step);
//                int num = divPts.size();
//                allGenerators.addAll(divPts);
//                originalGens.add(divPts);
//                originalVoroJTS.add(new ArrayList<>());
//            } else if (type.equals("Polygon")) {
//                Polygon pl = (Polygon) geo;
//                List<Coordinate> divPts = ZGeoMath.dividePolygonEdgeByStep(pl, step);
//                int num = divPts.size();
//                allGenerators.addAll(divPts);
//                originalGens.add(divPts);
//                originalVoroJTS.add(new ArrayList<>());
//            } else {
//                throw new IllegalArgumentException("Can't handle the geometry type of " + type + ".");
//            }
//        }
//
//        // generate voronoi
//        VoronoiDiagramBuilder voronoiDiagramBuilder = new VoronoiDiagramBuilder();
//        voronoiDiagramBuilder.setSites(allGenerators);
//        voronoiDiagramBuilder.setClipEnvelope(boundary.getEnvelopeInternal());
//        Geometry diagram = voronoiDiagramBuilder.getDiagram(ZFactory.jtsgf);
//
//        if (diagram.getGeometryType().equals("GeometryCollection")) {
//            outer:
//            for (int i = 0; i < diagram.getNumGeometries(); i++) {
//                Polygon cell = (Polygon) diagram.getGeometryN(i);
//                inner:
//                for (int j = 0; j < originalGens.size(); j++) {
//                    if (originalGens.get(j).contains(cell.getUserData())) {
//                        originalVoroJTS.get(j).add(cell);
//                        break inner;
//                    }
//                }
//            }
//        } else {
//            throw new RuntimeException("Fail to get voronoi.");
//        }
//
//        // union cells of the same pl
//        for (List<Polygon> voroEach : originalVoroJTS) {
//            Geometry union = voroEach.get(0);
//            if (voroEach.size() > 1) {
//                for (int i = 1; i < voroEach.size(); i++) {
//                    union = union.union(voroEach.get(i));
//                }
//            }
//            String type = union.getGeometryType();
//            if (type.equals("Polygon")) {
//                voronoiResultsJTS.add((Polygon) union);
//            } else if (type.equals("MultiPolygon")) {
//                for (int i = 0; i < union.getNumGeometries(); i++) {
//                    voronoiResultsJTS.add((Polygon) union.getGeometryN(i));
//                }
//            } else {
//                throw new RuntimeException("Fail to union.");
//            }
//        }
//    }

    private void create(List<? extends WB_PolyLine> polys, WB_Polygon boundary) {
        this.voronoiResultsWB = new ArrayList<>();
        this.originalVoroWB = new ArrayList<>();
        List<List<WB_Point>> originalGens = new ArrayList<>();

        // create divided points
        List<WB_Point> allGenerators = new ArrayList<>();
        for (int i = 0; i < polys.size(); i++) {
            WB_PolyLine pl = polys.get(i);
            List<WB_Point> divPts = ZGeoMath.dividePolyLineByStep(pl, step);

            allGenerators.addAll(divPts);
            originalGens.add(divPts);
            originalVoroWB.add(new ArrayList<>());
        }


        // generate voronoi
        WB_Voronoi2D voronoi = WB_VoronoiCreator.getClippedVoronoi2D(allGenerators, boundary);
        List<WB_VoronoiCell2D> allCells = voronoi.getCells();

        outer:
        for (WB_VoronoiCell2D cell2D : allCells) {
            inner:
            for (int i = 0; i < originalGens.size(); i++) {
                if (originalGens.get(i).contains(cell2D.getGenerator())) {
                    originalVoroWB.get(i).add(cell2D.getPolygon());
                    break inner;
                }
            }
        }

        // union cells of the same pl
        for (List<WB_Polygon> voroEach : originalVoroWB) {
            List<WB_Polygon> union = new ArrayList<>();
            union.add(voroEach.get(0));

            if (voroEach.size() > 1) {
                for (int i = 1; i < voroEach.size(); i++) {
                    union = ZFactory.wbgf.unionPolygons2D(voroEach.get(i), union);
                }
            }
            voronoiResultsWB.addAll(union);
        }
    }


    /* ------------- setter & getter ------------- */

    public List<WB_Polygon> getVoronoiResultsWB() {
        return voronoiResultsWB;
    }

    public List<Polygon> getVoronoiResultsJTS() {
        List<Polygon> list = new ArrayList<>();
        voronoiResultsWB.forEach(v -> list.add(ZTransform.WB_PolygonToPolygon(v)));
        return list;
    }

    public List<List<WB_Polygon>> getOriginalVoroWB() {
        return originalVoroWB;
    }

    public List<List<Polygon>> getOriginalVoroJTS() {
        List<List<Polygon>> list = new ArrayList<>();
        for (List<WB_Polygon> l : originalVoroWB) {
            List<Polygon> jtsl = new ArrayList<>();
            l.forEach(v -> jtsl.add(ZTransform.WB_PolygonToPolygon(v)));
            list.add(jtsl);
        }
        return list;
    }

    /* ------------- draw ------------- */
}
