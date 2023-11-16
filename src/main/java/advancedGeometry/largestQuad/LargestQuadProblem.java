package advancedGeometry.largestQuad;

import basicGeometry.ZFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * description
 *
 * @author zbz_lennovo
 * @project jmetal-learn
 * @date 2023/11/16
 * @time 11:50
 */
public class LargestQuadProblem extends AbstractDoubleProblem {
    private Polygon boundary;

    public LargestQuadProblem(Polygon boundary, WB_AABB aabb) {
        this.boundary = boundary;
        double minx = aabb.getMinX();
        double maxx = aabb.getMaxX();
        double miny = aabb.getMinY();
        double maxy = aabb.getMaxY();

        this.numberOfObjectives(1);//定义目标的数量
        this.numberOfConstraints(1);//定义约束的数量
        this.name("LargestQuadProblem");

        //定义变量的最小值
        List<Double> lowerLimit = new ArrayList<>(Arrays.asList(minx, miny, minx, miny, minx, miny, minx, miny));
        //定义变量的最大值
        List<Double> upperLimit = new ArrayList<>(Arrays.asList(maxx, maxy, maxx, maxy, maxx, maxy, maxx, maxy));
        //设置变量的取值范围
        this.variableBounds(lowerLimit, upperLimit);
    }


    //设置目标，用于评价解，默认求目标的最小值
    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
        double x1 = (Double) solution.variables().get(0);
        double y1 = (Double) solution.variables().get(1);
        double x2 = (Double) solution.variables().get(2);
        double y2 = (Double) solution.variables().get(3);
        double x3 = (Double) solution.variables().get(4);
        double y3 = (Double) solution.variables().get(5);
        double x4 = (Double) solution.variables().get(6);
        double y4 = (Double) solution.variables().get(7);
        solution.objectives()[0] = -x2 * y1 + x1 * y2 - x3 * y2 + x2 * y3 - x4 * y3 + x3 * y4 - x1 * y4 + x4 * y1;

        this.evaluateConstraints(solution);
        return solution;
    }

    //设置约束式，用于评价解，默认约束式≥0时，不违背约束条件
    //需注意，在构建算法时设置OverallConstraintViolationDegreeComparator，约束式评价才起作用
    public void evaluateConstraints(DoubleSolution solution) {
        double[] constraint = new double[this.numberOfConstraints()];
        double x1 = (Double) solution.variables().get(0);
        double y1 = (Double) solution.variables().get(1);
        double x2 = (Double) solution.variables().get(2);
        double y2 = (Double) solution.variables().get(3);
        double x3 = (Double) solution.variables().get(4);
        double y3 = (Double) solution.variables().get(5);
        double x4 = (Double) solution.variables().get(6);
        double y4 = (Double) solution.variables().get(7);
        boolean contain = boundary.contains(
                ZFactory.jtsgf.createPolygon(
                        new Coordinate[]{
                                new Coordinate(x1, y1),
                                new Coordinate(x2, y2),
                                new Coordinate(x3, y3),
                                new Coordinate(x4, y4),
                                new Coordinate(x1, y1),
                        }
                )
        );

        constraint[0] = contain ? 1.0D : -1.0D;

        IntStream.range(0, this.numberOfConstraints())
                .forEach(i -> solution.constraints()[i] = constraint[i]);
    }
}
