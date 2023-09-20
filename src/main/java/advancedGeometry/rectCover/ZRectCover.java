package advancedGeometry.rectCover;

import basicGeometry.ZFactory;
import math.ZGeoMath;
import math.ZMath;
import math.ZPermuCombi;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.math.Vector2D;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import transform.ZTransform;
import wblut.geom.WB_Polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * given number of rectangles
 * cover a polygon with tightest rectangles
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/30
 * @time 13:09
 */
public class ZRectCover {
    private Polygon boundary;

    // rays
    private int rayDensity = 100;
    private List<LineString> rayExtends;

    // rectangles and nets
    private int rectNum = 3;
    private List<Polygon> bestRects;

    /* ------------- constructor ------------- */

    public ZRectCover(WB_Polygon boundary, int rectNum) {
        setBoundary(ZTransform.WB_PolygonToPolygon(boundary));
        setRectNum(rectNum);
        init();
    }

    public ZRectCover(Polygon boundary, int rectNum) {
        setBoundary(boundary);
        setRectNum(rectNum);
        init();
    }

    public ZRectCover(WB_Polygon boundary, int rectNum, int rayDensity) {
        setBoundary(ZTransform.WB_PolygonToPolygon(boundary));
        setRectNum(rectNum);
        setRayDensity(rayDensity);
        init();
    }

    /* ------------- member function ------------- */

    public void init() {
        if (this.rectNum > 1) {
            initRays();
            coverRectangles();
        } else if (this.rectNum < 1) {
            throw new IllegalArgumentException("at least 1 covering rectangle");
        } else {
            this.rayExtends = new ArrayList<>();
            Geometry obb = MinimumDiameter.getMinimumRectangle(boundary);
            this.bestRects = new ArrayList<>();
            bestRects.add((Polygon) obb);
        }
    }

    /**
     * generate rays inside the boundary
     *
     * @return void
     */
    private void initRays() {
        // generate rays
        Vector2D[] dirs = new Vector2D[this.rayDensity];
        double step = (Math.PI * 2) / dirs.length;
        for (int i = 0; i < dirs.length; i++) {
            dirs[i] = new Vector2D(Math.cos(step * i), Math.sin(step * i));
        }

        // TODO: 2021/4/30 no concave?
        // find concave points
        List<Coordinate> concave = ZGeoMath.getConcavePoints(boundary);

        // rays extends to boundary
        this.rayExtends = new ArrayList<>();
        for (Coordinate c : concave) {
            for (Vector2D dir : dirs) {
                Vector2D[] ray = new Vector2D[]{Vector2D.create(c), dir};
                LineString extendRay = ZGeoMath.extendSegmentToPolygon(ray, boundary);
                if (extendRay != null) {
                    rayExtends.add(ZFactory.jtsgf.createLineString(new Coordinate[]{
                            c, extendRay.getCoordinateN(1)
                    }));
                }
            }
        }
//        System.out.println("total rays num : " + rayExtends.size());
    }

    /**
     * find tightest covering rectangle by the giving number
     *
     * @return void
     */
    private void coverRectangles() {
        // generate combinations of rays
        ZPermuCombi pc = new ZPermuCombi();
        int[] indices = ZMath.createIntegerSeries(0, rayExtends.size());
        pc.combination(indices, rectNum - 1, 0, 0);
        List<List<Integer>> rayGroups = pc.getCombinationResults();
//        System.out.println("total ray (group) num : " + rayGroups.size());

        // polygonizer by rays
        List<Collection<Polygon>> allSplitPolygons = new ArrayList<>();
        Polygonizer pr;
        int invalidCount = 0;
        for (List<Integer> list : rayGroups) {
            pr = new Polygonizer();
            Geometry nodedLineStrings = ZTransform.PolygonToLineString(boundary).get(0);
            for (int index : list) {
                try {
                    nodedLineStrings = nodedLineStrings.union(ZFactory.createExtendedLineString(rayExtends.get(index), 1));
                } catch (TopologyException e) {
                    invalidCount++;
                }
            }
            pr.add(nodedLineStrings);
            Collection<Polygon> allPolys = pr.getPolygons();
            allSplitPolygons.add(allPolys);
        }
//        System.out.println("invalid during polygonizer: " + invalidCount);

        // filter by rect number
        List<Integer> validI = new ArrayList<>();
        for (int i = 0; i < allSplitPolygons.size(); i++) {
            if (allSplitPolygons.get(i).size() == rectNum) {
                validI.add(i);
            }
        }
//        System.out.println("valid ray (group) num: " + validI.size());

        // keep the most rectangular results
        double[] ratioResult = new double[validI.size()];
        for (int i = 0; i < validI.size(); i++) {
            double ratioSum = 0;
            if (allSplitPolygons.get(validI.get(i)).size() == rectNum) {
                for (Polygon p : allSplitPolygons.get(validI.get(i))) {
                    if (p.getArea() < 5) {
                        ratioSum = 0;
                        break;
                    } else {
                        Geometry obb = MinimumDiameter.getMinimumRectangle(p);
                        ratioSum = ratioSum + (p.getArea() / obb.getArea());
                    }
                }
            }
            ratioResult[i] = ratioSum;
        }
        int max = ZMath.getMaxIndex(ratioResult);
//        System.out.println("max  " + "[" + max + "]" + "  " + ratioResult[max]);

        // generate rectangles
        this.bestRects = new ArrayList<>();
        for (Polygon p : allSplitPolygons.get(validI.get(max))) {
            Polygon obb = (Polygon) MinimumDiameter.getMinimumRectangle(p);
            bestRects.add(obb);
        }
    }

    /* ------------- setter & getter ------------- */

    public void setRectNum(int rectNum) {
        this.rectNum = rectNum;
    }

    public void setRayDensity(int rayDensity) {
        this.rayDensity = rayDensity;
    }

    public void setBoundary(Polygon boundary) {
        this.boundary = boundary;
    }

    public List<LineString> getRayExtends() {
        return rayExtends;
    }

    public List<Polygon> getBestRects() {
        return bestRects;
    }

    /* ------------- draw ------------- */

}
