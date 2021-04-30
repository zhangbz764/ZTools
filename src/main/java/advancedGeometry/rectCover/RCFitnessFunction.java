package advancedGeometry.rectCover;

import basicGeometry.ZPoint;
import basicGeometry.ZFactory;
import net.sourceforge.jswarm_pso.FitnessFunction;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import transform.ZTransform;
import wblut.geom.WB_Polygon;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/29
 * @time 17:12
 */
public class RCFitnessFunction extends FitnessFunction {
    private WB_Polygon boundary;
    private int rectNum;

    public RCFitnessFunction(int rectNum, WB_Polygon boundary) {
        this.rectNum = rectNum;
        this.boundary = boundary;
    }

    @Override
    public double evaluate(double[] position) {
        Polygon[] rects = new Polygon[rectNum];
        for (int i = 0; i < rectNum; i++) {
            double x = position[i * 5];
            double y = position[i * 5 + 1];
            double w = position[i * 5 + 2];
            double h = position[i * 5 + 3];
            double a = position[i * 5 + 4];
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

            rects[i] = ZFactory.jtsgf.createPolygon(coords);
        }
        Geometry union = rects[0];
        if (rectNum > 1) {
            for (int i = 1; i < rectNum; i++) {
                union = union.union(rects[i]);
            }
        }

        double area = 0;
        for (int i = 0; i < rectNum; i++) {
            area = area + (position[i * 5 + 2] * position[i * 5 + 3]);
        }

        return union.contains(ZTransform.WB_PolygonToJtsPolygon(boundary)) ? area : 0.0D;
    }
}
