package advancedGeometry.subdivision.lb_sub;

import basicGeometry.ZFactory;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.math.Vector2D;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.OverallConstraintViolationDegreeComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/4/16
 * @time 22:40
 */
public class FixedRatioSub_T {

    private Polygon[] subResult;

    /* ------------- constructor ------------- */

    public FixedRatioSub_T(Polygon polygon, double ratio0, double ratio1, double ratio2) {
        // 将输入多边形旋转到obb方向，缩小xy取值范围
        Polygon obb = (Polygon) MinimumDiameter.getMinimumRectangle(polygon);

        Vector2D vec01 = new Vector2D(obb.getCoordinates()[0], obb.getCoordinates()[1]);
        Vector2D xAxis = new Vector2D(1, 0);

        double angle = vec01.angleTo(xAxis);

        AffineTransformation transformation = new AffineTransformation();
        transformation.rotate(angle);
        Polygon transformed = (Polygon) transformation.transform(polygon);
        Polygon transformedOBB = (Polygon) transformation.transform(obb);

        // 进行优化
        double[] result = initOpti(transformed, transformedOBB, ratio0, ratio1, ratio2);

        // 将结果旋转回原始方向
        Point resultP = ZFactory.jtsgf.createPoint(new Coordinate(result[0], result[1]));
        transformation = new AffineTransformation();
        transformation.rotate(-angle);
        Point resultPTransBack = (Point) transformation.transform(resultP);

        // 得到最终的剖分结果
        double x = resultPTransBack.getX();
        double y = resultPTransBack.getY();
        Vector2D vecU = new Vector2D(obb.getCoordinates()[0], obb.getCoordinates()[1]); // 未normalize
        Vector2D vecV = new Vector2D(obb.getCoordinates()[0], obb.getCoordinates()[3]); // 未normalize
        // TODO: 2024/4/17 9:28 by zhangbz 用来polygonizer的3条LineString目前比较猥琐
        LineString ls1 = ZFactory.jtsgf.createLineString(new Coordinate[]{
                new Coordinate(x, y),
                new Coordinate(x + vecV.getX(), y + vecV.getY()),
        });
        LineString ls2 = ZFactory.jtsgf.createLineString(new Coordinate[]{
                new Coordinate(x, y),
                new Coordinate(x - vecV.getX(), y - vecV.getY()),
        });
        LineString ls3 = ZFactory.jtsgf.createLineString(new Coordinate[]{
                new Coordinate(x, y),
                new Coordinate(x - vecU.getX(), y - vecU.getY()),
        });

        // polygonizer
        Polygonizer pr = new Polygonizer();
        Geometry nodedLineStrings = PolygonToLineString(polygon).get(0);
        nodedLineStrings = nodedLineStrings.union(ls1);
        nodedLineStrings = nodedLineStrings.union(ls2);
        nodedLineStrings = nodedLineStrings.union(ls3);
        pr.add(nodedLineStrings);
        Object[] prObjs = pr.getPolygons().toArray();
        this.subResult = new Polygon[prObjs.length];
        for (int i = 0; i < prObjs.length; i++) {
            subResult[i] = (Polygon) prObjs[i];
        }
    }

    /* ------------- member function ------------- */

    /**
     * @param boundary outline
     * @param obb      obb
     * @param ratio0
     * @param ratio1
     * @param ratio2
     * @return
     */
    public double[] initOpti(Polygon boundary, Polygon obb, double ratio0, double ratio1, double ratio2) {
        // 创建求解problem
        FixedRatioSub_T_Problem problem = new FixedRatioSub_T_Problem(
                boundary,
                obb,
                ratio0, ratio1, ratio2
        );
        //配置交叉算子
        CrossoverOperator<DoubleSolution> crossover;
        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 20.0;
        crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

        //配置变异算子
        MutationOperator<DoubleSolution> mutation;
        double mutationProbability = 1.0 / problem.numberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        //配置选择算子
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(
                new RankingAndCrowdingDistanceComparator<DoubleSolution>());

        //种群数量
        int populationSize = 100;
        int offspringPopulationSize = 100;

        //设置排序方式为总体约束违反度
        Ranking<DoubleSolution> ranking = new FastNonDominatedSortRanking<>(
                new DominanceWithConstraintsComparator<>(
                        new OverallConstraintViolationDegreeComparator<>()));

        //将组件注册到algorithm
//        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problem, crossover, mutation, populationSize)
//                .setMaxEvaluations(25000)
//                .setOffspringPopulationSize(offspringPopulationSize)
//                .setSelectionOperator(selection)
//                .build()
//                ;

//        NSGAII<DoubleSolution> algorithm = new NSGAIIBuilder<>(
//                problem, crossover, mutation, 50)
//                .setMaxEvaluations(5000)
//
//                .build();

//        Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder(
//                problem,
//                new CrowdingDistanceArchive<DoubleSolution>(100))
//                .setMutation(new PolynomialMutation(mutationProbability, mutationDistributionIndex))
//                .setMaxIterations(250)
//                .setSwarmSize(100)
//                .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
//                .build();

//        EvolutionaryAlgorithm<DoubleSolution> algorithm = new NSGAIIBuilder<>(
//                problem,
//                populationSize,
//                offspringPopulationSize,
//                crossover,
//                mutation)
//                .setRanking(ranking)
//                .build();

        Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder(problem, new CrowdingDistanceArchive<>(100))
                .setMutation(mutation)
                .setMaxIterations(25)
                .setSwarmSize(100)
                .setRandomGenerator(new MersenneTwisterGenerator())
                .setSolutionListEvaluator(new SequentialSolutionListEvaluator<>())
                .build();
        //运行算法
        algorithm.run();

        //获取结果集
        List<DoubleSolution> population = algorithm.getResult();

        //打印结果
        //System.out.println("var " + population.get(0).variables());
//        System.out.println("obj " + population.get(0).objectives()[0]);
//        System.out.println("obj " + population.get(0).objectives()[1]);
//        System.out.println("obj " + population.get(0).objectives()[2]);

        List<Double> vars = population.get(0).variables();
        return new double[]{vars.get(0), vars.get(1)};
    }

    /**
     * Polygon -> LineString
     *
     * @param polygon input Polygon
     * @return java.util.List<org.locationtech.jts.geom.LineString>
     */
    private List<LineString> PolygonToLineString(final Polygon polygon) {
        List<LineString> result = new ArrayList<>();
        if (polygon.getNumInteriorRing() == 0) {
            result.add(ZFactory.jtsgf.createLineString(polygon.getCoordinates()));
        } else {
            result.add(polygon.getExteriorRing());
            for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
                result.add(polygon.getInteriorRingN(i));
            }
        }
        return result;
    }

    /* ------------- setter & getter ------------- */

    public Polygon[] getSubResult() {
        return subResult;
    }

    /* ------------- problem class ------------- */

    class FixedRatioSub_T_Problem extends AbstractDoubleProblem {
        private Polygon boundary;
        private LineString boundaryLS;

        private double area_tar_0;
        private double area_tar_1;
        private double area_tar_2;

        private Vector2D vecU;
        private Vector2D vecV;

        private Polygonizer pr;

        private Coordinate coord12Mid;
        private int resultNumTemp = 3;

        protected FixedRatioSub_T_Problem(Polygon boundary, Polygon obb, double r0, double r1, double r2) {
            this.boundary = boundary;
            this.boundaryLS = PolygonToLineString(boundary).get(0);
            double area = boundary.getArea();

            double sum = r0 + r1 + r2;
            this.area_tar_0 = area * (r0 / sum);
            this.area_tar_1 = area * (r1 / sum);
            this.area_tar_2 = area * (r2 / sum);

            // xy ranges
            Geometry envelope = boundary.getEnvelope();
            double minx = envelope.getEnvelopeInternal().getMinX();
            double miny = envelope.getEnvelopeInternal().getMinY();
            double maxx = envelope.getEnvelopeInternal().getMaxX();
            double maxy = envelope.getEnvelopeInternal().getMaxY();

            // obb dir
            this.vecU = new Vector2D(obb.getCoordinates()[0], obb.getCoordinates()[1]); // 未normalize
            this.vecV = new Vector2D(obb.getCoordinates()[0], obb.getCoordinates()[3]); // 未normalize
//            System.out.println(vecU);
//            System.out.println(vecV);
            this.coord12Mid = new Coordinate(
                    (obb.getCoordinates()[1].getX() + obb.getCoordinates()[2].getX()) * 0.5,
                    (obb.getCoordinates()[1].getY() + obb.getCoordinates()[2].getY()) * 0.5
            );

            this.numberOfObjectives(1);//定义目标的数量
            this.numberOfConstraints(2);//定义约束的数量
            this.name("FixedRatioSub_T_Problem");

            //定义变量的最小值
            List<Double> lowerLimit = new ArrayList<>(Arrays.asList(minx, miny));
            //定义变量的最大值
            List<Double> upperLimit = new ArrayList<>(Arrays.asList(maxx, maxy));
            //设置变量的取值范围
            this.variableBounds(lowerLimit, upperLimit);
        }


        //设置目标，用于评价解，默认求目标的最小值
        @Override
        public DoubleSolution evaluate(DoubleSolution solution) {
            double x = solution.variables().get(0);
            double y = solution.variables().get(1);

            // 三条剖分线一定超出轮廓
            // TODO: 2024/4/17 9:28 by zhangbz 用来polygonizer的3条LineString目前比较猥琐
            LineString ls1 = ZFactory.jtsgf.createLineString(new Coordinate[]{
                    new Coordinate(x, y),
                    new Coordinate(x + vecV.getX(), y + vecV.getY()),
            });
            LineString ls2 = ZFactory.jtsgf.createLineString(new Coordinate[]{
                    new Coordinate(x, y),
                    new Coordinate(x - vecV.getX(), y - vecV.getY()),
            });
            LineString ls3 = ZFactory.jtsgf.createLineString(new Coordinate[]{
                    new Coordinate(x, y),
                    new Coordinate(x - vecU.getX(), y - vecU.getY()),
            });

            // polygonizer
            this.pr = new Polygonizer();
            Geometry nodedLineStrings = boundaryLS;
            try {
                nodedLineStrings = nodedLineStrings.union(ls1);
                nodedLineStrings = nodedLineStrings.union(ls2);
                nodedLineStrings = nodedLineStrings.union(ls3);
            } catch (Exception e) {

            }

            pr.add(nodedLineStrings);

            Object[] prObjs = pr.getPolygons().toArray();
            Polygon[] polygons = new Polygon[prObjs.length];
            for (int i = 0; i < prObjs.length; i++) {
                polygons[i] = (Polygon) prObjs[i];
            }
            this.resultNumTemp = polygons.length;

            // 靠几何判断强行找出三个多边形哪个是哪个
            int id0 = -1;
            int id1 = -1;
            int id2 = -1;
            double minDist_to12Mid = Double.MAX_VALUE;
            double minDist_to0 = Double.MAX_VALUE;

            for (int i = 0; i < polygons.length; i++) {
                Polygon p = polygons[i];
                Point cen = p.getCentroid();
                double distSq_to12Mid = Math.pow(cen.getX() - coord12Mid.getX(), 2) + Math.pow(cen.getY() - coord12Mid.getY(), 2);
                if (distSq_to12Mid < minDist_to12Mid) {
                    minDist_to12Mid = distSq_to12Mid;
                    id2 = i;
                }
            }
            for (int i = 0; i < polygons.length; i++) {
                if (i != id2) {
                    Polygon p = polygons[i];
                    Point cen = p.getCentroid();
                    double distSq_to0 = Math.pow(cen.getX() - coord12Mid.getX(), 2) + Math.pow(cen.getY() - coord12Mid.getY(), 2);
                    if (distSq_to0 < minDist_to0) {
                        minDist_to0 = distSq_to0;
                        id1 = i;
                    }
                }
            }
            for (int i = 0; i < polygons.length; i++) {
                if (i != id2 && i != id1) {
                    id0 = i;
                    break;
                }
            }

            double[] area = new double[3];
            area[0] = id0 > -1 ? polygons[id0].getArea() : Integer.MAX_VALUE;
            area[1] = id0 > -1 ? polygons[id1].getArea() : Integer.MAX_VALUE;
            area[2] = id0 > -1 ? polygons[id2].getArea() : Integer.MAX_VALUE;

            // 目标1/2/3：面积与目标面积的差应当尽可能小
            solution.objectives()[0] = Math.abs(area_tar_0 - area[0]) + Math.abs(area_tar_1 - area[1]) + Math.abs(area_tar_2 - area[2]);
//            solution.objectives()[1] = Math.abs(area_tar_1 - area[1]);
//            solution.objectives()[2] = Math.abs(area_tar_2 - area[2]);

            this.evaluateConstraints(solution);
            return solution;
        }

        //设置约束式，用于评价解，默认约束式≥0时，不违背约束条件
        //需注意，在构建算法时设置OverallConstraintViolationDegreeComparator，约束式评价才起作用
        public void evaluateConstraints(DoubleSolution solution) {
            double[] constraint = new double[this.numberOfConstraints()];
            double x = (Double) solution.variables().get(0);
            double y = (Double) solution.variables().get(1);

            // 约束1：点在轮廓内
            boolean contain = boundary.contains(
                    ZFactory.jtsgf.createPoint(new Coordinate(x, y))
            );
            constraint[0] = contain ? 1.0D : -1.0D;

            // 约束2：三个剖分结果
            constraint[1] = resultNumTemp == 3 ? 1.0D : -1.0D;

            IntStream.range(0, this.numberOfConstraints())
                    .forEach(i -> solution.constraints()[i] = constraint[i]);
        }
    }


}
