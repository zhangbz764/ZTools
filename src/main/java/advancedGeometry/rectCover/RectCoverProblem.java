package advancedGeometry.rectCover;

import basicGeometry.ZFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import wblut.geom.WB_AABB;

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
public class RectCoverProblem extends AbstractDoubleProblem {
    private final Polygon polygon;
    private final int rectNum;

    public RectCoverProblem(Polygon polygon, WB_AABB aabb, int rectNum) {
        this.polygon = polygon;
        this.rectNum = rectNum;

        this.numberOfObjectives(1);//定义目标的数量
        this.numberOfConstraints(1);//定义约束的数量
        this.name("RectCoverProblem");

        List<Double> lowerLimit = new ArrayList<>();
        List<Double> upperLimit = new ArrayList<>();

        for (int i = 0; i < rectNum; i++) {
            lowerLimit.addAll(Arrays.asList(aabb.getMinX() - aabb.getWidth() * 0.5, aabb.getMinY() - aabb.getHeight() * 0.5, 0.0D, 0.0D, -Math.PI));
            upperLimit.addAll(Arrays.asList(aabb.getMaxX() + aabb.getWidth() * 0.5, aabb.getMaxY() + aabb.getHeight() * 0.5, aabb.getWidth() * 2, aabb.getHeight() * 2, -Math.PI));
        }

        //设置变量的取值范围
        this.variableBounds(lowerLimit, upperLimit);
    }

    //设置目标，用于评价解，默认求目标的最小值
    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
        double totalArea = 0;
        for (int i = 0; i < rectNum; i++) {
            double w = (Double) solution.variables().get(i * 5 + 2);
            double h = (Double) solution.variables().get(i * 5 + 3);

            totalArea += w * h;
        }

        solution.objectives()[0] = totalArea / polygon.getArea();

        this.evaluateConstraints(solution);
        return solution;
    }


    //设置约束式，用于评价解，默认约束式≥0时，不违背约束条件
    //需注意，在构建算法时设置OverallConstraintViolationDegreeComparator，约束式评价才起作用
    public void evaluateConstraints(DoubleSolution solution) {
        double[] constraint = new double[this.numberOfConstraints()];

        Polygon[] covers = new Polygon[rectNum];
        for (int i = 0; i < rectNum; i++) {
            double x = (Double) solution.variables().get(0);
            double y = (Double) solution.variables().get(1);
            double w = (Double) solution.variables().get(2);
            double h = (Double) solution.variables().get(3);
            double a = (Double) solution.variables().get(4);

            // create rectangle
            Polygon rect = ZFactory.createPolygonFromXYWHA(x, y, w, h, a);
            covers[i] = rect;
        }

        // union
        Geometry union = covers[0];
        if (rectNum > 1) {
            for (int i = 1; i < rectNum; i++) {
                union = union.union(covers[i]);
            }
        }

        boolean contain = union.contains(polygon);
        constraint[0] = contain ? 1.0D : -1.0D;

        IntStream.range(0, this.numberOfConstraints())
                .forEach(i -> solution.constraints()[i] = constraint[i]);
    }
}
