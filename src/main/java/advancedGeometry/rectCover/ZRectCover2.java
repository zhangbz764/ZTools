package advancedGeometry.rectCover;

import basicGeometry.ZFactory;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.OverallConstraintViolationDegreeComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import transform.ZTransform;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * given number of rectangles
 * cover a polygon with tightest rectangles
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/30
 * @time 13:09
 */
public class ZRectCover2 {
    private Polygon polygon;
    private final WB_AABB polygonAABB;
    private int rectNum = 3;
    private List<Polygon> bestRects;

    /* ------------- constructor ------------- */

    public ZRectCover2(WB_Polygon polygon, int rectNum) {
        this.polygon = ZTransform.WB_PolygonToPolygon(polygon);
        this.rectNum = rectNum;
        this.polygonAABB = polygon.getAABB();
        init();
    }

    public ZRectCover2(Polygon polygon, int rectNum) {
        this.polygon = polygon;
        this.rectNum = rectNum;
        this.polygonAABB = ZTransform.PolygonToWB_Polygon(polygon).getAABB();
        init();
    }

    /* ------------- member function ------------- */

    public void init() {
        if (this.rectNum < 1) {
            throw new IllegalArgumentException("at least 1 covering rectangle");
        } else if (this.rectNum == 1) {
            Geometry obb = MinimumDiameter.getMinimumRectangle(polygon);
            this.bestRects = new ArrayList<>();
            bestRects.add((Polygon) obb);
        } else {
            this.bestRects = new ArrayList<>();

            //定义优化问题
            RectCoverProblem problem = new RectCoverProblem(polygon, polygonAABB, rectNum);

            //配置交叉算子
            CrossoverOperator<DoubleSolution> crossover;
            double crossoverProbability = 0.9;
            double crossoverDistributionIndex = 20.0;
            crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

            //配置变异算子
            double mutationProbability = 1.0 / problem.numberOfVariables();
            double mutationDistributionIndex = 20.0;
            MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability,
                    mutationDistributionIndex);

            //配置选择算子
            SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<>(
                    new RankingAndCrowdingDistanceComparator<>());

            //种群数量
            int populationSize = 300;
            int offspringPopulationSize = 300;

            //设置排序方式为总体约束违反度
            Ranking<DoubleSolution> ranking = new FastNonDominatedSortRanking<>(
                    new DominanceWithConstraintsComparator<>(
                            new OverallConstraintViolationDegreeComparator<>()));


            //将组件注册到algorithm
            EvolutionaryAlgorithm<DoubleSolution> algorithm = new NSGAIIBuilder<>(
                    problem,
                    populationSize,
                    offspringPopulationSize,
                    crossover,
                    mutation)
                    .setRanking(ranking)
                    .build();

//            NSGAIII<DoubleSolution> algorithm = new NSGAIIIBuilder<>(
//                    problem
//            )
//                            .setCrossoverOperator(crossover)
//                            .setMutationOperator(mutation)
//                            .setSelectionOperator(selection)
//                            .setMaxIterations(1500)
//                            .setNumberOfDivisions(12)
//                            .build();

            //运行算法
            AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
//            algorithm.run();

            //获取结果集
            List<DoubleSolution> population = algorithm.getResult();

            //打印结果
            System.out.println("var " + population.get(0).variables());
            System.out.println("obj " + population.get(0).objectives()[0]);

            List<Double> vars = population.get(0).variables();
            for (int i = 0; i < rectNum; i++) {
                Polygon rect = ZFactory.createRectFromXYWHA(
                        vars.get(i * 5),
                        vars.get(i * 5 + 1),
                        vars.get(i * 5 + 2),
                        vars.get(i * 5 + 3),
                        vars.get(i * 5 + 4)
                );
                bestRects.add(rect);
            }
        }
    }

    /* ------------- setter & getter ------------- */

    public List<Polygon> getBestRects() {
        return bestRects;
    }

    /* ------------- draw ------------- */

}
