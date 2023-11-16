package advancedGeometry.largestRectangle;

import basicGeometry.ZFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.math.Vector2D;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

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
public class LargestRectProblem extends AbstractDoubleProblem {
    private Polygon boundary;

    public LargestRectProblem(Polygon boundary, WB_AABB aabb) {
        this.boundary = boundary;

        this.numberOfObjectives(1);//定义目标的数量
        this.numberOfConstraints(1);//定义约束的数量
        this.name("LargestRectProblem");

        //定义变量的最小值
        List<Double> lowerLimit = new ArrayList<>(Arrays.asList(aabb.getMinX(), aabb.getMinY(), 0.0D, 0.0D, -Math.PI));
        //定义变量的最大值
        List<Double> upperLimit = new ArrayList<>(Arrays.asList(aabb.getMaxX(), aabb.getMaxY(), aabb.getWidth(), aabb.getHeight(), Math.PI));
        //设置变量的取值范围
        this.variableBounds(lowerLimit, upperLimit);
    }


    //设置目标，用于评价解，默认求目标的最小值
    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
        double w = (Double) solution.variables().get(2);
        double h = (Double) solution.variables().get(3);

        solution.objectives()[0] = -w * h;

        this.evaluateConstraints(solution);
        return solution;
    }

    //设置约束式，用于评价解，默认约束式≥0时，不违背约束条件
    //需注意，在构建算法时设置OverallConstraintViolationDegreeComparator，约束式评价才起作用
    public void evaluateConstraints(DoubleSolution solution) {
        double[] constraint = new double[this.numberOfConstraints()];
        double x = (Double) solution.variables().get(0);
        double y = (Double) solution.variables().get(1);
        double w = (Double) solution.variables().get(2);
        double h = (Double) solution.variables().get(3);
        double a = (Double) solution.variables().get(4);

        // create rectangle
        Polygon rect = ZFactory.createPolygonFromXYWHA(x, y, w, h, a);

        boolean contain = boundary.contains(rect);
        constraint[0] = contain ? 1.0D : -1.0D;

        IntStream.range(0, this.numberOfConstraints())
                .forEach(i -> solution.constraints()[i] = constraint[i]);
    }
}
