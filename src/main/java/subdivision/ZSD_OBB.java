package subdivision;

import geometry.ZFactory;
import math.ZMath;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 不断计算OBB，沿OBB长边中线剖分，直到剖分至面积下限
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/6
 * @time 15:15
 */
public class ZSD_OBB extends ZSubdivision {
    private List<Polygon> allSubJtsPolygons;

    private int subdivideTimes = 1;

    /* ------------- constructor ------------- */

    public ZSD_OBB(WB_Polygon originPolygon) {
        super(originPolygon);
    }

    @Override
    public void performDivide() {
        this.allSubJtsPolygons = new ArrayList<>();
        Polygon origin = ZTransform.WB_PolygonToJtsPolygon(super.getOriginPolygon());
        allSubJtsPolygons.add(origin);

        // subdivide by times
        for (int i = 0; i < subdivideTimes; i++) {
            allSubJtsPolygons = divide(allSubJtsPolygons);
        }

        // convert to WB_Polygon
        List<WB_Polygon> allSubPolygons = new ArrayList<>();
        for (Polygon p : allSubJtsPolygons) {
            allSubPolygons.add(ZTransform.jtsPolygonToWB_Polygon(p));
        }
        super.setAllSubPolygons(allSubPolygons);
        super.setRandomColor();
    }

    /* ------------- member function ------------- */

    /**
     * main divide function
     *
     * @param input input list of polygon to get subdivided
     * @return java.util.List<org.locationtech.jts.geom.Polygon>
     */
    private List<Polygon> divide(List<Polygon> input) {
        List<Polygon> result = new ArrayList<>();
        for (Polygon p : input) {
            // get OBB
            Geometry currOBB = MinimumDiameter.getMinimumRectangle(p);
            if (currOBB instanceof Polygon) {
                assert currOBB.getNumPoints() == 5 : "not a valid rectangle";

                // find longer edge, create divide line
                double[] length = new double[2];
                length[0] = currOBB.getCoordinates()[0].distance(currOBB.getCoordinates()[1]);
                length[1] = currOBB.getCoordinates()[1].distance(currOBB.getCoordinates()[2]);
                int max = ZMath.getMaxIndex(length);

                Coordinate[] centerPoints = new Coordinate[2];
                centerPoints[0] = new Coordinate(
                        (currOBB.getCoordinates()[max].x + currOBB.getCoordinates()[max + 1].x) * 0.5,
                        (currOBB.getCoordinates()[max].y + currOBB.getCoordinates()[max + 1].y) * 0.5,
                        (currOBB.getCoordinates()[max].z + currOBB.getCoordinates()[max + 1].z) * 0.5
                );
                centerPoints[1] = new Coordinate(
                        (currOBB.getCoordinates()[max + 2].x + currOBB.getCoordinates()[max + 3].x) * 0.5,
                        (currOBB.getCoordinates()[max + 2].y + currOBB.getCoordinates()[max + 3].y) * 0.5,
                        (currOBB.getCoordinates()[max + 2].z + currOBB.getCoordinates()[max + 3].z) * 0.5
                );

                Coordinate[] dividePoints = new Coordinate[2];
                dividePoints[0] = new Coordinate(
                        centerPoints[0].x + (centerPoints[0].x - centerPoints[1].x),
                        centerPoints[0].y + (centerPoints[0].y - centerPoints[1].y),
                        centerPoints[0].z + (centerPoints[0].z - centerPoints[1].z)
                );
                dividePoints[1] = new Coordinate(
                        centerPoints[1].x + centerPoints[1].x - centerPoints[0].x,
                        centerPoints[1].y + centerPoints[1].y - centerPoints[0].y,
                        centerPoints[1].z + centerPoints[1].z - centerPoints[0].z
                );
                LineString divideLine = ZFactory.jtsgf.createLineString(dividePoints);

                // use Polygonizer to divide
                Polygonizer pr = new Polygonizer();
                Geometry allGeometry = divideLine;
                for (LineString lineString : ZTransform.PolygonToLineString(p)) {
                    allGeometry = allGeometry.union(lineString);
                }
                pr.add(allGeometry);
                Collection<Polygon> dividedPolys = pr.getPolygons();

                result.addAll(dividedPolys);
            }
        }
        return result;
    }

    /* ------------- setter & getter ------------- */

    @Override
    public void setCellConstraint(double subdivideTimes) {
        this.subdivideTimes = (int) subdivideTimes;
    }

    /* ------------- draw ------------- */

    public void display(PApplet app, WB_Render render) {
        app.pushStyle();
        super.displayWithColor(app, render);
        app.popStyle();
    }
}
