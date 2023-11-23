package advancedGeometry.circleCover;

import math.ZMath;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2023/11/23
 * @time 14:13
 */
class CircleCoverProblem extends AbstractDoubleProblem {
    private int circleNum;
    private double radius;
    private List<WB_Point> ptsToCover;

    protected CircleCoverProblem(List<WB_Point> pts, WB_AABB aabb, int circleNum, double radius) {
        this.circleNum = circleNum;
        this.radius = radius;
        this.ptsToCover = pts;
        double minx = aabb.getMinX();
        double maxx = aabb.getMaxX();
        double miny = aabb.getMinY();
        double maxy = aabb.getMaxY();

        this.numberOfObjectives(1);//定义目标的数量
        this.numberOfConstraints(1);//定义约束的数量
        this.name("CircleCoverProblem");

        //定义变量的最小值最大值
        List<Double> lowerLimit = new ArrayList<>();
        List<Double> upperLimit = new ArrayList<>();
        for (int i = 0; i < circleNum; i++) {
            lowerLimit.add(minx);
            lowerLimit.add(miny);
            upperLimit.add(maxx);
            upperLimit.add(maxy);
        }

        //设置变量的取值范围
        this.variableBounds(lowerLimit, upperLimit);
    }


    //设置目标，用于评价解，默认求目标的最小值
    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
//        // 覆盖
//        int coverCount = 0;
//        outer:
//        for (WB_Point p : ptsToCover) {
//            inner:
//            for (int i = 0; i < circleNum; i++) {
//                double x = (Double) solution.variables().get(i * 2);
//                double y = (Double) solution.variables().get(i * 2 + 1);
//
//                if (ZMath.distanceSq2D(x, y, p.xd(), p.yd()) <= radius * radius) {
//                    coverCount++;
//                    break inner;
//                }
//            }
//        }
//        solution.objectives()[0] = -coverCount;

        // 方差
        List<Double> distSqAll = new ArrayList<>();
        for (int i = 0; i < circleNum - 1; i++) {
            double x1 = (Double) solution.variables().get(i * 2);
            double y1 = (Double) solution.variables().get(i * 2 + 1);
            for (int j = i + 1; j < circleNum; j++) {
                double x2 = (Double) solution.variables().get(j * 2);
                double y2 = (Double) solution.variables().get(j * 2 + 1);

                double distSq = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
                distSqAll.add(distSq);
            }
        }

        double[] data = new double[distSqAll.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = distSqAll.get(i);
        }
        double variance = ZMath.variance(data);
        solution.objectives()[0] = variance;


        this.evaluateConstraints(solution);
        return solution;
    }

    //设置约束式，用于评价解，默认约束式≥0时，不违背约束条件
    //需注意，在构建算法时设置OverallConstraintViolationDegreeComparator，约束式评价才起作用
    public void evaluateConstraints(DoubleSolution solution) {
        // 覆盖
        int coverCount = 0;
        outer:
        for (WB_Point p : ptsToCover) {
            inner:
            for (int i = 0; i < circleNum; i++) {
                double x = (Double) solution.variables().get(i * 2);
                double y = (Double) solution.variables().get(i * 2 + 1);

                if (ZMath.distanceSq2D(x, y, p.xd(), p.yd()) <= radius * radius) {
                    coverCount++;
                    break inner;
                }
            }
        }

        double[] constraint = new double[this.numberOfConstraints()];
        constraint[0] = (double) coverCount / ptsToCover.size() - 1;

        IntStream.range(0, this.numberOfConstraints())
                .forEach(i -> solution.constraints()[i] = constraint[i]);
    }
}
