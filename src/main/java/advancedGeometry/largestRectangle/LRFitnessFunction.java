package advancedGeometry.largestRectangle;

import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import net.sourceforge.jswarm_pso.FitnessFunction;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import transform.ZTransform;
import wblut.geom.WB_Polygon;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/29
 * @time 17:34
 */
public class LRFitnessFunction extends FitnessFunction {
    public static final int X = 0;
    public static final int Y = 1;
    public static final int W = 2;
    public static final int H = 3;
    public static final int A = 4;

    private Polygon boundary;
    private double r;

    public LRFitnessFunction(WB_Polygon boundary) {
        this.boundary = ZTransform.WB_PolygonToJtsPolygon(boundary);
    }

    @Override
    public double evaluate(double[] position) {
        double x = position[0];
        double y = position[1];
        double w = position[2];
        double h = position[3];
        double a = position[4];
        ZPoint base = new ZPoint(x, y);
        ZPoint dir1 = new ZPoint(Math.cos(a), Math.sin(a));
        ZPoint dir2 = dir1.rotate2D(Math.PI * 0.5);
        ZPoint base2 = base.add(dir1.scaleTo(w));

        Coordinate[] coords = new Coordinate[5];
        coords[0] = base.toJtsCoordinate();
        coords[1] = base2.toJtsCoordinate();
        coords[2] = (base2.add(dir2.scaleTo(h))).toJtsCoordinate();
        coords[3] = (base.add(dir2.scaleTo(h))).toJtsCoordinate();
        coords[4] = coords[0];

        Polygon rect = ZFactory.jtsgf.createPolygon(coords);

        return this.boundary.contains(rect) ? h * w : 0.0D;
    }
}
