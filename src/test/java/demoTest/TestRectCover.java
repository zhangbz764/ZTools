package demoTest;

import advancedGeometry.rectCover.RCFitnessFunction;
import advancedGeometry.rectCover.RCParticle;
import basicGeometry.ZPoint;
import net.sourceforge.jswarm_pso.Swarm;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/27
 * @time 16:35
 */
public class TestRectCover extends PApplet {
    private final static int ITERATION_TIME = 1000;
    private int particleNum = 2500;
    private static double[] MAX_VELOCITY = new double[]{
            2.0, 2.0, 2.0, 2.0, 0.1,
            2.0, 2.0, 2.0, 2.0, 0.1
    };
    private static double[] MIN_VELOCITY = new double[]{
            0.5, 0.5, 0.5, 0.5, 0.01,
            0.5, 0.5, 0.5, 0.5, 0.01
    };
    private final static int NEIGHBOR_NUM = 0;

    private WB_Polygon boundary;
    private int rectNum = 2;
    private WB_Polygon[] rectResult;

    private WB_Render render;

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    public void setup() {
        render = new WB_Render(this);
        init();
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        render.drawPolygonEdges(boundary);
        if (rectResult != null && rectResult.length > 0) {
            for (WB_Polygon p : rectResult) {
                render.drawPolygonEdges(p);
            }
        }
    }

    /* ------------- function ------------- */

    private void init() {
        initBoundary();
        int i = 0;
        Swarm swarm = initSwarm();
        // evolve
        while (i < ITERATION_TIME) {
            swarm.evolve();
            i++;
        }

        // extract best
        double[] best = swarm.getBestPosition();
        this.rectResult = new WB_Polygon[rectNum];
        for (int j = 0; j < rectNum; j++) {
            double x = best[j * 5];
            double y = best[j * 5 + 1];
            double w = best[j * 5 + 2];
            double h = best[j * 5 + 3];
            double a = best[j * 5 + 4];
            ZPoint base = new ZPoint(x, y);
            ZPoint dir1 = new ZPoint(Math.cos(a), Math.sin(a));
            ZPoint dir2 = dir1.rotate2D(Math.PI * 0.5);
            ZPoint base2 = base.add(dir1.scaleTo(w));

            WB_Point[] rectPoints = new WB_Point[5];
            rectPoints[0] = base.toWB_Point();
            rectPoints[1] = base2.toWB_Point();
            rectPoints[2] = (base2.add(dir2.scaleTo(h))).toWB_Point();
            rectPoints[3] = (base.add(dir2.scaleTo(h))).toWB_Point();
            rectPoints[4] = rectPoints[0];

            rectResult[j] = new WB_Polygon(rectPoints);
        }
    }

    private void initBoundary() {
        WB_Point[] bdpts = new WB_Point[8];
        bdpts[0] = new WB_Point(600, 100);
        bdpts[1] = new WB_Point(600, 600);
        bdpts[2] = new WB_Point(150, 650);
        bdpts[3] = new WB_Point(100, 870);
        bdpts[4] = new WB_Point(900, 900);
        bdpts[5] = new WB_Point(820, 500);
        bdpts[6] = new WB_Point(860, 120);
        bdpts[7] = bdpts[0];
        this.boundary = new WB_Polygon(bdpts);
    }

    private Swarm initSwarm() {
        RCFitnessFunction function = new RCFitnessFunction(rectNum, boundary);
        Swarm swarm = new Swarm(particleNum, new RCParticle(), function);

        double[] maxPosition = new double[rectNum * 5];
        double[] minPosition = new double[rectNum * 5];

        WB_AABB aabb = ZTransform.offsetWB_AABB(boundary.getAABB(), 1.2);
        for (int i = 0; i < rectNum; i++) {
            maxPosition[i * 5] = aabb.getMaxX();
            maxPosition[i * 5 + 1] = aabb.getMaxY();
            maxPosition[i * 5 + 2] = aabb.getWidth();
            maxPosition[i * 5 + 3] = aabb.getHeight();
            maxPosition[i * 5 + 4] = Math.PI;

            minPosition[i * 5] = aabb.getMinX();
            minPosition[i * 5 + 1] = aabb.getMinY();
            minPosition[i * 5 + 2] = 0;
            minPosition[i * 5 + 3] = 0;
            minPosition[i * 5 + 4] = -Math.PI;
        }

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
}
