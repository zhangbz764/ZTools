package advancedGeometry.largestRectangle;

import net.sourceforge.jswarm_pso.FitnessFunction;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/29
 * @time 17:34
 */
public class LRFitnessFunction extends FitnessFunction {
    private static final GeometryFactory jtsgf = new GeometryFactory();
    public static final int X = 0;
    public static final int Y = 1;
    public static final int W = 2;
    public static final int H = 3;
    public static final int A = 4;

    private Polygon boundary;
    private double r;

    public LRFitnessFunction(WB_Polygon boundary) {
        this.boundary = WB_PolygonToPolygon(boundary);
    }

    @Override
    public double evaluate(double[] position) {
        double x = position[0];
        double y = position[1];
        double w = position[2];
        double h = position[3];
        double a = position[4];
        WB_Point base = new WB_Point(x, y);
        WB_Vector dir1 = new WB_Vector(Math.cos(a), Math.sin(a));
        double angle = Math.PI * 0.5;
        WB_Vector dir2 = dir1.rotateAboutOrigin2D(angle);
        WB_Point base2 = base.add(dir1.scale(w));

        Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(base.xd(), base.yd());
        coords[1] = new Coordinate(base2.xd(), base2.yd());
        WB_Vector scaled1 = base2.add(dir2.scale(h));
        coords[2] = new Coordinate(scaled1.xd(), scaled1.yd());
        WB_Vector scaled2 = base.add(dir2.scale(h));
        coords[3] = new Coordinate(scaled2.xd(), scaled2.yd());
        coords[4] = coords[0];

        Polygon rect = jtsgf.createPolygon(coords);

        return this.boundary.contains(rect) ? h * w : 0.0D;
    }

    /**
     * WB_Polygon -> Polygon (holes supported)
     *
     * @param wbp input WB_Polygon
     * @return org.locationtech.jts.geom.Polygon
     */
    private Polygon WB_PolygonToPolygon(final WB_Polygon wbp) {
        if (wbp.getNumberOfHoles() == 0) {
            if (wbp.getPoint(0).equals(wbp.getPoint(wbp.getNumberOfPoints() - 1))) {
                Coordinate[] coords = new Coordinate[wbp.getNumberOfPoints()];
                for (int i = 0; i < wbp.getNumberOfPoints(); i++) {
                    coords[i] = new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd());
                }
                return jtsgf.createPolygon(coords);
            } else {
                Coordinate[] coords = new Coordinate[wbp.getNumberOfPoints() + 1];
                for (int i = 0; i < wbp.getNumberOfPoints(); i++) {
                    coords[i] = new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd());
                }
                coords[wbp.getNumberOfPoints()] = coords[0];
                return jtsgf.createPolygon(coords);
            }
        } else {
            // exterior
            List<Coordinate> exteriorCoords = new ArrayList<>();
            for (int i = 0; i < wbp.getNumberOfShellPoints(); i++) {
                exteriorCoords.add(new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd()));
            }
            if (!exteriorCoords.get(0).equals3D(exteriorCoords.get(exteriorCoords.size() - 1))) {
                System.out.println("here");
                exteriorCoords.add(exteriorCoords.get(0));
            }
            LinearRing exteriorLinearRing = jtsgf.createLinearRing(exteriorCoords.toArray(new Coordinate[0]));

            // interior
            final int[] npc = wbp.getNumberOfPointsPerContour();
            int index = npc[0];
            LinearRing[] interiorLinearRings = new LinearRing[wbp.getNumberOfHoles()];
            for (int i = 0; i < wbp.getNumberOfHoles(); i++) {
                List<Coordinate> contour = new ArrayList<>();
                for (int j = 0; j < npc[i + 1]; j++) {
                    contour.add(new Coordinate(wbp.getPoint(index).xd(), wbp.getPoint(index).yd(), wbp.getPoint(index).zd()));
                    index++;
                }
                if (!contour.get(0).equals3D(contour.get(contour.size() - 1))) {
                    contour.add(contour.get(0));
                }
                interiorLinearRings[i] = jtsgf.createLinearRing(contour.toArray(new Coordinate[0]));
            }

            return jtsgf.createPolygon(exteriorLinearRing, interiorLinearRings);
        }
    }
}
