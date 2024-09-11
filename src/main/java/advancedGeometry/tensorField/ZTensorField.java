package advancedGeometry.tensorField;

import basicGeometry.ZFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.math.Vector2D;
import org.locationtech.jts.operation.distance.DistanceOp;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/9/10
 * @time 20:59
 */
public class ZTensorField {
    //private double magnitude = 50;
    private Polygon range;

    private List<Disturbance> disturbances;
    private List<LineString> disturbLS;

    /* ------------- constructor ------------- */

    public ZTensorField() {
        this.disturbances = new ArrayList<>();
        this.disturbLS = new ArrayList<>();
    }

    /* ------------- member function ------------- */

    /**
     * description
     *
     * @param d
     * @return void
     */
    public void addDisturbance(Disturbance d) {
        this.disturbLS.addAll(d.toLineString());
    }

    public LineString getLineStringAlongField(Coordinate pos, Vector2D dir, double step) {
        if (range == null) {
            // TODO: 2024/9/11 13:41 by zhangbz no boundary
        }

        ZTensorCross tensor = getTensorAtPoint(pos);
        Vector2D dir1 = tensor.getClosestVec(dir);
        Vector2D dir2 = tensor.getOppositeVec(dir1);

        // forward
        List<Coordinate> forwardCoords = new ArrayList<>();
        forwardCoords.add(new Coordinate(pos));

        Vector2D currVecFW = new Vector2D(pos);
        Vector2D currDirFW = dir1;
        Vector2D nextVecFW  = currVecFW.add(currDirFW.multiply(step));

        while (range.contains(ZFactory.jtsgf.createPoint(nextVecFW.toCoordinate()))) {
            // TODO: 2024/9/11 13:41 by zhangbz loop forever?
            forwardCoords.add(nextVecFW.toCoordinate()); // add Coordinate to list
            ZTensorCross currTensor = getTensorAtPoint(nextVecFW.toCoordinate()); // calculate tensor
            currDirFW = currTensor.getClosestVec(currDirFW); // update direction
            currVecFW = nextVecFW; // update base point
            nextVecFW  = currVecFW.add(currDirFW.multiply(step));
        }

        // backward
        List<Coordinate> backwardCoords = new ArrayList<>();

        Vector2D currVecBW = new Vector2D(pos);
        Vector2D currDirBW = dir2;
        Vector2D nextVecBW  = currVecBW.add(currDirBW.multiply(step));

        while (range.contains(ZFactory.jtsgf.createPoint(nextVecBW.toCoordinate()))) {
            // TODO: 2024/9/11 13:41 by zhangbz loop forever?
            backwardCoords.add(nextVecBW.toCoordinate()); // add Coordinate to list
            ZTensorCross currTensor = getTensorAtPoint(nextVecBW.toCoordinate()); // calculate tensor
            currDirBW = currTensor.getClosestVec(currDirBW); // update direction
            currVecBW = nextVecBW; // update base point
            nextVecBW  = currVecBW.add(currDirBW.multiply(step));
        }

        // merge coordinate
        List<Coordinate> resultCoords = new ArrayList<>();
        resultCoords.addAll(backwardCoords.reversed());
        resultCoords.addAll(forwardCoords);

        return ZFactory.createLineStringFromList(resultCoords);
    }

    /**
     * description
     *
     * @param coord
     * @return advancedGeometry.tensorField.ZTensor
     */
    public ZTensorCross getTensorAtPoint(Coordinate coord) {
        if (!disturbLS.isEmpty()) {
            Point pt = ZFactory.jtsgf.createPoint(coord);
            double totalWeight = 0;
            Vector2D fieldVector = new Vector2D(0, 0);

            // 对每一条边线，计算对该点的影响
            for (LineString line : disturbLS) {
                Vector2D lineVector = calculateInfluenceFromLine(pt, line);
                double distance = DistanceOp.distance(pt, line); // 点到线段的最短距离

                if (distance > 0) {
                    double weight = 1.0 / Math.pow(distance, 2); // 影响力与距离的平方成反比
                    totalWeight += weight;
                    fieldVector = fieldVector.add(lineVector.multiply(weight));
                }
            }

            // 归一化总场向量
            if (totalWeight > 0) {
                fieldVector = fieldVector.multiply(1 / totalWeight).normalize();
            }

            return new ZTensorCross(coord, fieldVector);
        } else {
            return new ZTensorCross();
        }
    }

    /**
     * Calculate the influence vector of a boundary line on a given point
     *
     * @param point
     * @param seg
     * @return org.locationtech.jts.math.Vector2D
     */
    private Vector2D calculateInfluenceFromLine(Point point, LineString seg) {
        // 计算线段的方向向量
        double dx = seg.getCoordinateN(1).getX() - seg.getCoordinateN(0).getX();
        double dy = seg.getCoordinateN(1).getY() - seg.getCoordinateN(0).getY();

        // 计算垂直方向（法线方向）的向量
        Vector2D normalVector = new Vector2D(-dy, dx);

        // 计算到线段的最短点（投影点）
        Coordinate[] nearestPoints = DistanceOp.nearestPoints(point, seg);
        Coordinate closestPoint = nearestPoints[0];

        // 计算该点到线段的距离向量（从最近点指向该点的向量）
        double toPointX = point.getX() - closestPoint.getX();
        double toPointY = point.getY() - closestPoint.getY();

        // 计算角度来决定是垂直还是平行影响，根据需要调整
        double influenceAngle = Math.atan2(normalVector.getY(), normalVector.getX());

        return new Vector2D(Math.cos(influenceAngle), Math.sin(influenceAngle));
    }



    /* ------------- setter & getter ------------- */

    public void setRange(Polygon range) {
        this.range = range;
    }

    public Polygon getRange() {
        return range;
    }

    public void setDisturbances(List<Disturbance> disturbances) {
        this.disturbances = disturbances;
        for (Disturbance disturbance : disturbances) {
            addDisturbance(disturbance);
        }
    }

    public List<Disturbance> getDisturbances() {
        return disturbances;
    }

    /* ------------- draw ------------- */
}
