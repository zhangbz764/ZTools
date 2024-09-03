package advancedGeometry.subdivision.lb_sub;

import basicGeometry.ZFactory;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * TODO
 *
 * @author libiao
 * @version 1.0
 * @date 2024/4/17 19:17
 * Inst. AAA, S-ARCH, Southeast University
 */
public class Subdivision05 extends RangeDivision {
    int resultNumTemp; // 剖分数量
    double[] elongations; // 每个子剖分的延伸率（obb长宽比）

    /**
     * 构造函数
     *
     * @param range      - 待剖分的基地
     * @param areaRatios - 即将被剖分的地块面积比
     */
    public Subdivision05(Polygon range, double[] areaRatios) {
        super(range, areaRatios);
        this.setGAParas();
    }

    public List<DoubleSolution> getResults() {
//        List<DoubleSolution> doubleSolutions = this.getResultsFromSMPSOModel();
        List<DoubleSolution> doubleSolutions = this.getResultsFromNSGAIIModel();

        return doubleSolutions;
    }

    private void setGAParas() {
        // 需要改写****  xy ranges >>>>>
        Geometry envelope = this.range.getEnvelope();
        double minx = envelope.getEnvelopeInternal().getMinX();
        double miny = envelope.getEnvelopeInternal().getMinY();
        double maxx = envelope.getEnvelopeInternal().getMaxX();
        double maxy = envelope.getEnvelopeInternal().getMaxY();
        //定义变量的最小值
        List<Double> lowerLimit = new ArrayList<>(Arrays.asList(minx, miny));
        //定义变量的最大值
        List<Double> upperLimit = new ArrayList<>(Arrays.asList(maxx, maxy));
        // <<<<<<<<

        setGAParameters("T_division", 1, 3, lowerLimit, upperLimit);
    }

    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
        double x = solution.variables().get(0);
        double y = solution.variables().get(1);

        // 三条剖分线一定超出轮廓
        // TODO: 2024/4/17 9:28 by zhangbz 用来polygonizer的3条LineString目前比较猥琐
        LineString ls1 = ZFactory.jtsgf.createLineString(new Coordinate[]{
                new Coordinate(x, y),
                new Coordinate(x + vDir.getX(), y + vDir.getY()),
        });
        LineString ls2 = ZFactory.jtsgf.createLineString(new Coordinate[]{
                new Coordinate(x, y),
                new Coordinate(x - vDir.getX(), y - vDir.getY()),
        });
        LineString ls3 = ZFactory.jtsgf.createLineString(new Coordinate[]{
                new Coordinate(x, y),
                new Coordinate(x - uDir.getX(), y - uDir.getY()),
        });

        // polygonizer
        Polygonizer pr = new Polygonizer();
        Geometry nodedLineStrings = this.range.getExteriorRing();
        nodedLineStrings = nodedLineStrings.union(ls1);
        nodedLineStrings = nodedLineStrings.union(ls2);
        nodedLineStrings = nodedLineStrings.union(ls3);
        pr.add(nodedLineStrings);

        Object[] prObjs = pr.getPolygons().toArray();
        Polygon[] polygons = new Polygon[prObjs.length];
        for (int i = 0; i < prObjs.length; i++) {
            polygons[i] = (Polygon) prObjs[i];
        }

        // 记录剖分数量以及每个地块的延伸率(obb长宽比，0-1之间)
        resultNumTemp = polygons.length;
        elongations = new double[resultNumTemp];
        for (int i = 0; i < resultNumTemp; i++) {
            Geometry subOBB = MinimumDiameter.getMinimumRectangle(polygons[i]);
            Coordinate c0 = subOBB.getCoordinates()[0];
            Coordinate c1 = subOBB.getCoordinates()[1];
            Coordinate c2 = subOBB.getCoordinates()[2];
            double l = c0.distance(c1);
            double w = c1.distance(c2);
            elongations[i] = l >= w ? w / l : l / w;
        }


//        System.out.println("polygons  ---> " + polygons.length);

        // 设定单优化目标：平方和
//        double a0 = resultNumTemp > 0 ? Math.abs(plotsArea[0] - polygons[0].getArea()) : Integer.MAX_VALUE;
//        double a1 = resultNumTemp > 1 ? Math.abs(plotsArea[1] - polygons[1].getArea()) : Integer.MAX_VALUE;
//        double a2 = resultNumTemp > 2 ? Math.abs(plotsArea[2] - polygons[2].getArea()) : Integer.MAX_VALUE;

        double a0 = resultNumTemp > 0 ? Math.pow(plotsArea[0] - polygons[0].getArea(), 2) : Integer.MAX_VALUE;
        double a1 = resultNumTemp > 1 ? Math.pow(plotsArea[1] - polygons[1].getArea(), 2) : Integer.MAX_VALUE;
        double a2 = resultNumTemp > 2 ? Math.pow(plotsArea[2] - polygons[2].getArea(), 2) : Integer.MAX_VALUE;

        solution.objectives()[0] = a0 + a1 + a2;

        this.evaluateConstraints(solution);
        return solution;
    }

    @Override
    public void evaluateConstraints(DoubleSolution solution) {
        double[] constraint = new double[this.numberOfConstraints()];
        double x = solution.variables().get(0);
        double y = solution.variables().get(1);

        // 约束1：点在轮廓内
        boolean contain = range.contains(
                ZFactory.jtsgf.createPoint(new Coordinate(x, y))
        );
        constraint[0] = 1;

        // 约束2：3个剖分结果
        constraint[1] = 1;

        // 约束3: obb长宽比
        double eloCons = 0;
        for (double elo : elongations) {
            eloCons += Math.max(0, elo - 0.5);
        }
        constraint[2] = 1;

        IntStream.range(0, this.numberOfConstraints()).forEach(i -> {
            solution.constraints()[i] = constraint[i];
        });
    }

}
