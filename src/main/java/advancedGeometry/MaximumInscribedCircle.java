package advancedGeometry;

import org.locationtech.jts.algorithm.locate.IndexedPointInAreaLocator;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.distance.IndexedFacetDistance;

import java.util.PriorityQueue;

/**
 * description
 *
 * @author Martin Davis
 * @project jts 1.18.1
 * @date 2021/10/14
 * @time 18:08
 */
public class MaximumInscribedCircle {
    private Geometry inputGeom;
    private double tolerance;
    private GeometryFactory factory;
    private IndexedPointInAreaLocator ptLocater;
    private IndexedFacetDistance indexedDistance;
    private MaximumInscribedCircle.Cell centerCell = null;
    private Coordinate centerPt = null;
    private Coordinate radiusPt;
    private Point centerPoint;
    private Point radiusPoint;

    public static Point getCenter(Geometry polygonal, double tolerance) {
        MaximumInscribedCircle mic = new MaximumInscribedCircle(polygonal, tolerance);
        return mic.getCenter();
    }

    public static LineString getRadiusLine(Geometry polygonal, double tolerance) {
        MaximumInscribedCircle mic = new MaximumInscribedCircle(polygonal, tolerance);
        return mic.getRadiusLine();
    }

    public MaximumInscribedCircle(Geometry polygonal, double tolerance) {
        if (tolerance <= 0.0D) {
            throw new IllegalArgumentException("Tolerance must be positive");
        } else if (!(polygonal instanceof Polygon) && !(polygonal instanceof MultiPolygon)) {
            throw new IllegalArgumentException("Input geometry must be a Polygon or MultiPolygon");
        } else if (polygonal.isEmpty()) {
            throw new IllegalArgumentException("Empty input geometry is not supported");
        } else {
            this.inputGeom = polygonal;
            this.factory = polygonal.getFactory();
            this.tolerance = tolerance;
            this.ptLocater = new IndexedPointInAreaLocator(polygonal);
            this.indexedDistance = new IndexedFacetDistance(polygonal.getBoundary());
        }
    }

    public Point getCenter() {
        this.compute();
        return this.centerPoint;
    }

    public Point getRadiusPoint() {
        this.compute();
        return this.radiusPoint;
    }

    public LineString getRadiusLine() {
        this.compute();
        LineString radiusLine = this.factory.createLineString(new Coordinate[]{this.centerPt.copy(), this.radiusPt.copy()});
        return radiusLine;
    }

    private double distanceToBoundary(Point p) {
        double dist = this.indexedDistance.distance(p);
        boolean isOutide = 2 == this.ptLocater.locate(p.getCoordinate());
        return isOutide ? -dist : dist;
    }

    private double distanceToBoundary(double x, double y) {
        Coordinate coord = new Coordinate(x, y);
        Point pt = this.factory.createPoint(coord);
        return this.distanceToBoundary(pt);
    }

    private void compute() {
        if (this.centerCell == null) {
            PriorityQueue<Cell> cellQueue = new PriorityQueue();
            this.createInitialGrid(this.inputGeom.getEnvelopeInternal(), cellQueue);
            MaximumInscribedCircle.Cell farthestCell = this.createCentroidCell(this.inputGeom);

            while(!cellQueue.isEmpty()) {
                MaximumInscribedCircle.Cell cell = (MaximumInscribedCircle.Cell)cellQueue.remove();
                if (cell.getDistance() > farthestCell.getDistance()) {
                    farthestCell = cell;
                }

                double potentialIncrease = cell.getMaxDistance() - farthestCell.getDistance();
                if (potentialIncrease > this.tolerance) {
                    double h2 = cell.getHSide() / 2.0D;
                    cellQueue.add(this.createCell(cell.getX() - h2, cell.getY() - h2, h2));
                    cellQueue.add(this.createCell(cell.getX() + h2, cell.getY() - h2, h2));
                    cellQueue.add(this.createCell(cell.getX() - h2, cell.getY() + h2, h2));
                    cellQueue.add(this.createCell(cell.getX() + h2, cell.getY() + h2, h2));
                }
            }

            this.centerCell = farthestCell;
            this.centerPt = new Coordinate(this.centerCell.getX(), this.centerCell.getY());
            this.centerPoint = this.factory.createPoint(this.centerPt);
            Coordinate[] nearestPts = this.indexedDistance.nearestPoints(this.centerPoint);
            this.radiusPt = nearestPts[0].copy();
            this.radiusPoint = this.factory.createPoint(this.radiusPt);
        }
    }

    private void createInitialGrid(Envelope env, PriorityQueue<MaximumInscribedCircle.Cell> cellQueue) {
        double minX = env.getMinX();
        double maxX = env.getMaxX();
        double minY = env.getMinY();
        double maxY = env.getMaxY();
        double width = env.getWidth();
        double height = env.getHeight();
        double cellSize = Math.min(width, height);
        double hSide = cellSize / 2.0D;

        for(double x = minX; x < maxX; x += cellSize) {
            for(double y = minY; y < maxY; y += cellSize) {
                cellQueue.add(this.createCell(x + hSide, y + hSide, hSide));
            }
        }

    }

    private MaximumInscribedCircle.Cell createCell(double x, double y, double hSide) {
        return new MaximumInscribedCircle.Cell(x, y, hSide, this.distanceToBoundary(x, y));
    }

    private MaximumInscribedCircle.Cell createCentroidCell(Geometry geom) {
        Point p = geom.getCentroid();
        return new MaximumInscribedCircle.Cell(p.getX(), p.getY(), 0.0D, this.distanceToBoundary(p));
    }

    private static class Cell implements Comparable<MaximumInscribedCircle.Cell> {
        private static final double SQRT2 = 1.4142135623730951D;
        private double x;
        private double y;
        private double hSide;
        private double distance;
        private double maxDist;

        Cell(double x, double y, double hSide, double distanceToBoundary) {
            this.x = x;
            this.y = y;
            this.hSide = hSide;
            this.distance = distanceToBoundary;
            this.maxDist = this.distance + hSide * 1.4142135623730951D;
        }

        public Envelope getEnvelope() {
            return new Envelope(this.x - this.hSide, this.x + this.hSide, this.y - this.hSide, this.y + this.hSide);
        }

        public double getMaxDistance() {
            return this.maxDist;
        }

        public double getDistance() {
            return this.distance;
        }

        public double getHSide() {
            return this.hSide;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public int compareTo(MaximumInscribedCircle.Cell o) {
            return (int)(o.maxDist - this.maxDist);
        }
    }
}
