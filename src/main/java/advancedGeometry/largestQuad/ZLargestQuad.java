package advancedGeometry.largestQuad;

import basicGeometry.ZFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
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

import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2023/11/16
 * @time 15:43
 */
public class ZLargestQuad {
    private final Polygon boundary;
    private final WB_AABB boundaryAABB;
    private Polygon quadResult;

    /* ------------- constructor ------------- */

    public ZLargestQuad(WB_Polygon boundary) {
        this.boundary = ZTransform.WB_PolygonToPolygon(boundary);
        this.boundaryAABB = boundary.getAABB();
    }

    public ZLargestQuad(Polygon boundary) {
        this.boundary = boundary;
        this.boundaryAABB = ZTransform.PolygonToWB_Polygon(boundary).getAABB();
    }

    /* ------------- member function ------------- */

    public void init() {
        //定义优化问题
        LargestQuadProblem problem = new LargestQuadProblem(boundary, boundaryAABB);

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
        List<DoubleSolution> population = algorithm.getResult();

        //打印结果
        //System.out.println("var " + population.get(0).variables());
        //System.out.println("obj " + population.get(0).objectives()[0]);

        List<Double> vars = population.get(0).variables();
        Coordinate[] coords = new Coordinate[]{
                new Coordinate(vars.get(0), vars.get(1)),
                new Coordinate(vars.get(2), vars.get(3)),
                new Coordinate(vars.get(4), vars.get(5)),
                new Coordinate(vars.get(6), vars.get(7)),
                new Coordinate(vars.get(0), vars.get(1)),
        };
        this.quadResult = ZFactory.jtsgf.createPolygon(coords);
    }


    /* ------------- setter & getter ------------- */

    public Polygon getQuadResult() {
        return quadResult;
    }

    public WB_Polygon getQuadResult_WB() {
        return ZTransform.PolygonToWB_Polygon(quadResult);
    }

}
