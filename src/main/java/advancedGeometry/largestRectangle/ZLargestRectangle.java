package advancedGeometry.largestRectangle;

import basicGeometry.ZFactory;
import org.locationtech.jts.geom.Polygon;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.OverallConstraintViolationDegreeComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import transform.ZTransform;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Polygon;

import java.util.List;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/29
 * @time 17:41
 */
public class ZLargestRectangle {
    private final Polygon boundary;
    private final WB_AABB boundaryAABB;
    private Polygon rectResult;

    /* ------------- constructor ------------- */

    public ZLargestRectangle(WB_Polygon boundary) {
        this.boundary = ZTransform.WB_PolygonToPolygon(boundary);
        this.boundaryAABB = boundary.getAABB();
    }

    public ZLargestRectangle(Polygon boundary) {
        this.boundary = boundary;
        this.boundaryAABB = ZTransform.PolygonToWB_Polygon(boundary).getAABB();
    }

    /* ------------- member function ------------- */

    public void init() {
        //定义优化问题
        LargestRectProblem problem = new LargestRectProblem(boundary, boundaryAABB);

        List<DoubleSolution> population = setup(problem);

        //打印结果
        //System.out.println("var " + population.get(0).variables());
        //System.out.println("obj " + population.get(0).objectives()[0]);

        List<Double> vars = population.get(0).variables();

        this.rectResult = ZFactory.createPolygonFromXYWHA(
                vars.get(0),
                vars.get(1),
                vars.get(2),
                vars.get(3),
                vars.get(4)
        );
    }

    public void init(double fixedAspectRatio) {
        //定义优化问题
        LargestFixedRatioRectProblem problem = new LargestFixedRatioRectProblem(boundary, boundaryAABB, fixedAspectRatio);

        List<DoubleSolution> population = setup(problem);

        //打印结果
        //System.out.println("var " + population.get(0).variables());
        //System.out.println("obj " + population.get(0).objectives()[0]);

        List<Double> vars = population.get(0).variables();

        this.rectResult = ZFactory.createPolygonFromXYWHA(
                vars.get(0),
                vars.get(1),
                vars.get(2),
                vars.get(2) * fixedAspectRatio,
                vars.get(3)
        );
    }

    private List<DoubleSolution> setup(AbstractDoubleProblem problem) {
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
        EvolutionaryAlgorithm<DoubleSolution> algorithm = new NSGAIIBuilder<>(
                problem,
                populationSize,
                offspringPopulationSize,
                crossover,
                mutation)
                .setRanking(ranking)
                .build();

        //运行算法
        algorithm.run();

        //获取结果集
        return algorithm.getResult();
    }

    /* ------------- setter & getter ------------- */

    public Polygon getRectResult() {
        return rectResult;
    }

    public WB_Polygon getRectResult_WB() {
        return ZTransform.PolygonToWB_Polygon(rectResult);
    }
}
