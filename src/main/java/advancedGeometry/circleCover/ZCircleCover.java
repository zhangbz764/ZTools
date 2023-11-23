package advancedGeometry.circleCover;

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
import wblut.geom.WB_AABB;
import wblut.geom.WB_Point;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2023/11/23
 * @time 14:13
 */
public class ZCircleCover {
    private List<WB_Point> ptsToCover;
    private final WB_AABB boundaryAABB;
    private int num;
    private double radius;


    private List<WB_Point> circleCenters;

    /* ------------- constructor ------------- */

    public ZCircleCover(List<WB_Point> ptsToCover, int circleNum, double circleRadius) {
        this.ptsToCover = ptsToCover;
        this.boundaryAABB = new WB_AABB(ptsToCover);
        this.num = circleNum;
        this.radius = circleRadius;
    }

    /* ------------- member function ------------- */

    public void init() {
        //定义优化问题
        CircleCoverProblem problem = new CircleCoverProblem(ptsToCover, boundaryAABB, num, radius);

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
        int populationSize = 200;
        int offspringPopulationSize = 200;

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
        List<DoubleSolution> population = algorithm.getResult();

        //打印结果
        System.out.println("var " + population.get(0).variables());
        System.out.println("obj " + population.get(0).objectives()[0]);
//        System.out.println("obj " + population.get(0).objectives()[1]);

        List<Double> vars = population.get(0).variables();

        System.out.println(vars.size());
        this.circleCenters = new ArrayList<>();
        for (int i = 0; i < vars.size() - 1; i += 2) {
            WB_Point center = new WB_Point(vars.get(i), vars.get(i + 1));
            circleCenters.add(center);
        }
        System.out.println(circleCenters);
    }

    /* ------------- setter & getter ------------- */

    public List<WB_Point> getCircleCenters() {
        return circleCenters;
    }
}
