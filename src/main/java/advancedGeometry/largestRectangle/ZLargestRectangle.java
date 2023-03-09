package advancedGeometry.largestRectangle;

import net.sourceforge.jswarm_pso.Swarm;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/29
 * @time 17:41
 */
public class ZLargestRectangle {
    private final static int ITERATION_TIME = 1000;
    private int particleNum = 2500;
    private final static double[] MAX_VELOCITY = new double[]{1.0, 1.0, 1.0, 1.0, 0.1};
    private final static double[] MIN_VELOCITY = new double[]{0.1, 0.1, 0.1, 0.1, 0.01};

    private WB_Polygon boundary;
    private WB_Polygon rectangleResult;

    /* ------------- constructor ------------- */

    public ZLargestRectangle(WB_Polygon boundary) {
        setBoundary(boundary);
    }

    /* ------------- member function ------------- */

    public void init() {
        int i = 0;
        Swarm swarm = initSwarm();
        // evolve
        while (i < ITERATION_TIME) {
            swarm.evolve();
            i++;
        }
        this.rectangleResult = getBestRectangleResult(swarm);
    }

    private Swarm initSwarm() {
        LRFitnessFunction function = new LRFitnessFunction(boundary);
        Swarm swarm = new Swarm(particleNum, new LRParticle(), function);

        double[] maxPosition = new double[LRParticle.DIMENSION];
        double[] minPosition = new double[LRParticle.DIMENSION];

        WB_AABB aabb = boundary.getAABB();
        maxPosition[LRFitnessFunction.X] = aabb.getMaxX();
        maxPosition[LRFitnessFunction.Y] = aabb.getMaxY();
        maxPosition[LRFitnessFunction.W] = aabb.getWidth();
        maxPosition[LRFitnessFunction.H] = aabb.getHeight();
        maxPosition[LRFitnessFunction.A] = Math.PI;

        minPosition[LRFitnessFunction.X] = aabb.getMinX();
        minPosition[LRFitnessFunction.Y] = aabb.getMinY();
        minPosition[LRFitnessFunction.W] = 0;
        minPosition[LRFitnessFunction.H] = 0;
        minPosition[LRFitnessFunction.A] = -Math.PI;

        swarm.setMaxPosition(maxPosition);
        swarm.setMinPosition(minPosition);
        swarm.setMaxVelocity(MAX_VELOCITY);
        swarm.setMinVelocity(MIN_VELOCITY);

        // Set swarm's update parameters
        swarm.setInertia(Swarm.DEFAULT_INERTIA);
        swarm.setParticleIncrement(Swarm.DEFAULT_PARTICLE_INCREMENT);
        swarm.setGlobalIncrement(Swarm.DEFAULT_GLOBAL_INCREMENT);
        return swarm;
    }

    private WB_Polygon getBestRectangleResult(Swarm swarm) {
        double[] best = swarm.getBestPosition();
        double x = best[LRFitnessFunction.X];
        double y = best[LRFitnessFunction.Y];
        double w = best[LRFitnessFunction.W];
        double h = best[LRFitnessFunction.H];
        double a = best[LRFitnessFunction.A];

        WB_Point base = new WB_Point(x, y);
        WB_Vector dir1 = new WB_Vector(Math.cos(a), Math.sin(a));
        WB_Vector dir2 = dir1.rotateAboutOrigin2D(Math.PI * 0.5);
        WB_Point base2 = base.add(dir1.scale(w));

        WB_Point[] rectPoints = new WB_Point[5];
        rectPoints[0] = base;
        rectPoints[1] = base2;
        rectPoints[2] = (base2.add(dir2.scale(h)));
        rectPoints[3] = (base.add(dir2.scale(h)));
        rectPoints[4] = rectPoints[0];

        return new WB_Polygon(rectPoints);
    }

    /* ------------- setter & getter ------------- */

    public void setBoundary(WB_Polygon boundary) {
        this.boundary = boundary;
    }

    public WB_Polygon getRectangleResult() {
        return rectangleResult;
    }

}
