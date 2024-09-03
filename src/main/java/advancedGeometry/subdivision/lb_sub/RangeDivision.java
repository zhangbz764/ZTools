package advancedGeometry.subdivision.lb_sub;

import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.math.Vector2D;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.OverallConstraintViolationDegreeComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * TODO
 *
 * @author libiao
 * @version 1.0
 * @date 2024/4/17 17:41
 * Inst. AAA, S-ARCH, Southeast University
 */
public abstract class RangeDivision extends AbstractDoubleProblem {
    protected Polygon range;        // 基地
    protected Polygon obb;          // 基地的OBB （五个点）
    protected double[] plotsArea;    // 子地块的目标面积
    protected Vector2D uDir, vDir;  // u, v 的方向向量

    /**
     * 构造函数
     *
     * @param range      - 待剖分的基地
     * @param areaRatios - 即将被剖分的地块面积比
     */
    public RangeDivision(Polygon range, double[] areaRatios) {
        this.serRange(range);
        this.setAreaRatios(areaRatios);
    }

    /**
     * 根据 range 待剖分基地, 预处理和基地相关的数据, 如:
     * 保留 range 的引用; 计算基地的 OBB, 计算 uDir 和 vDir
     */
    public void serRange(Polygon range) {
        this.range = range;
        obb = (Polygon) MinimumDiameter.getMinimumRectangle(this.range);

        Coordinate[] obb5 = obb.getCoordinates();
        uDir = new Vector2D(obb5[0], obb5[1]);
        vDir = new Vector2D(obb5[0], obb5[3]);

//        this.coord12Mid = new Coordinate(
//                (obb.getCoordinates()[1].getX() + obb.getCoordinates()[2].getX()) * 0.5,
//                (obb.getCoordinates()[1].getY() + obb.getCoordinates()[2].getY()) * 0.5
//        );
    }

    /**
     * 根据基地的面积 和 各地块面积比, 确定各子地块的面积
     */
    public void setAreaRatios(double[] areaRatios) {
        double areaRange = this.range.getArea();
        plotsArea = new double[areaRatios.length];

        // 各子地块的面积
        double sumRatios = Arrays.stream(areaRatios).sum();
        IntStream.range(0, areaRatios.length).forEach(i -> {
            plotsArea[i] = areaRange * areaRatios[i] / sumRatios;
        });
    }

    /**
     * 设置 GA 参数, 如: 名称, （多）目标的数量, 限定的数量, 各变量参数的上\下限
     */
    protected void setGAParameters(String name, int numberOfObjectives, int numberOfConstraints, List<Double> variableLowerBounds, List<Double> variableUpperBounds) {
        this.name(name);
        this.numberOfObjectives(numberOfObjectives);
        this.numberOfConstraints(numberOfConstraints);
        this.variableBounds(variableLowerBounds, variableUpperBounds);
    }

    /**
     * 运用标准遗传算法获取结果
     */
    protected List<DoubleSolution> getResultsFromNSGAIIModel() {
        //配置交叉算子
        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 20.0;
        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

        //配置变异算子
        double mutationProbability = 1.0 / this.numberOfVariables();
        double mutationDistributionIndex = 20.0;
        MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        //配置选择算子
//        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(
//                new RankingAndCrowdingDistanceComparator<DoubleSolution>());

        //种群数量
        int populationSize = 100;
        int offspringPopulationSize = 100;
        //设置排序方式为总体约束违反度
        Ranking<DoubleSolution> ranking = new FastNonDominatedSortRanking<>(
                new DominanceWithConstraintsComparator<>(
                        new OverallConstraintViolationDegreeComparator<>()
                )
        );

        Algorithm<List<DoubleSolution>> algorithm =new NSGAIIBuilder<>(this,
                crossover,
                mutation,
                populationSize)
                .build();

        //将组件注册到algorithm
//        EvolutionaryAlgorithm<DoubleSolution> algorithm = new NSGAIIBuilder<>(
//                this,
//                populationSize,
//                offspringPopulationSize,
//                crossover,
//                mutation)
//                .setRanking(ranking)
//                .build();

        //运行算法
        algorithm.run();

        //获取结果集
        List<DoubleSolution> population = algorithm.getResult();
        return population;
    }

    /**
     * 运用简单遗传算法获取结果
     */
    protected List<DoubleSolution> getResultsFromSMPSOModel() {
        //配置变异算子
        double mutationProbability = 1.0 / this.numberOfVariables();
        double mutationDistributionIndex = 20.0;
        MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

//        Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder(this, 100)
//                .build();

        Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder(this, new CrowdingDistanceArchive<>(100))
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
//        System.out.println("var " + population.get(0).variables());
//        System.out.println("obj " + population.get(0).objectives()[0]);

//        List<Double> vars = population.get(0).variables();
//        return new double[]{vars.get(0), vars.get(1)};
        return population;
    }

    /**
     * 评价函数由子类实现
     */
    @Override
    public abstract DoubleSolution evaluate(DoubleSolution doubleSolution);


    //设置约束式，用于评价解，默认约束式≥0时，不违背约束条件
    //需注意，在构建算法时设置OverallConstraintViolationDegreeComparator，约束式评价才起作用

    /**
     * 约束函数由子类实现
     */
    public abstract void evaluateConstraints(DoubleSolution solution);
}
