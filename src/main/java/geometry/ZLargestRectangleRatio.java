package geometry;

import core.MyFitnessFunction;
import core.MyParticle;
import core.MyRectangle;
import igeo.IVec4R;
import math.ZGeoMath;
import net.sourceforge.jswarm_pso.Swarm;
import org.locationtech.jts.geom.Polygon;
import transform.ZTransform;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Transform2D;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * compute largest inner rectangle in a polygon
 * given a ratio = width / height
 * dependencies: https://github.com/dawnwords/LargestRectangle
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/1/6
 * @time 21:23
 */
public class ZLargestRectangleRatio {
    private Path2D.Double polygonPath;
    private double whRatio;

    private final static int ITERATIONTIME = 1000;
    private int particleNum = 2500;
    private final static double[] MAXVELOCITY = new double[]{1.0, 1.0, 1.0, 0.1};
    private final static double[] MINVELOCITY = new double[]{0.1, 0.1, 0.1, 0.01};
    private final static int NEIGHBORNUM = 0;

    private MyRectangle rectangleResult;
    private WB_Polygon largestRectangle;

    /* ------------- constructor ------------- */

    public ZLargestRectangleRatio(WB_Polygon polygon, double whRatio) {
        initPolygonPath(polygon);
        setWhRatio(whRatio);
    }

    /* ------------- member function ------------- */

    /**
    * main initializer
    *
    * @return void
    */
    public void init() {
        int i = 0;
        Swarm swarm = initSwarm();
        // evolve
        while (i < ITERATIONTIME) {
            swarm.evolve();
            i++;
        }
        this.rectangleResult = getBestRectangle(swarm);
        this.largestRectangle = convertRectangle(rectangleResult);
    }

    /**
     * initialize Path2D from input polygon
     *
     * @param polygon input polygon
     * @return void
     */
    private void initPolygonPath(WB_Polygon polygon) {
        this.polygonPath = new Path2D.Double();
        WB_Polygon input = ZGeoMath.polygonFaceUp(ZTransform.validateWB_Polygon(polygon));

        polygonPath.moveTo(polygon.getPoint(0).xd(), polygon.getPoint(0).yd());
        for (int i = 1; i < polygon.getNumberOfShellPoints() - 1; i++) {
            polygonPath.lineTo(polygon.getPoint(i).xd(), polygon.getPoint(i).yd());
        }
        polygonPath.closePath();
    }

    /**
     * main initializer for the Swarm
     *
     * @return net.sourceforge.jswarm_pso.Swarm
     */
    private Swarm initSwarm() {
        MyFitnessFunction function = new MyFitnessFunction(polygonPath, whRatio);
        Swarm swarm = new Swarm(particleNum, new MyParticle(), function);

        double[] maxPosition = new double[MyParticle.DIMENSION];
        double[] minPosition = new double[MyParticle.DIMENSION];

        Rectangle r = polygonPath.getBounds();
        double whRatio = this.whRatio;

        maxPosition[MyFitnessFunction.X] = r.getX() + r.getWidth();
        maxPosition[MyFitnessFunction.Y] = r.getY() + r.getHeight();
        maxPosition[MyFitnessFunction.H] = diagonal(r.getWidth(), r.getHeight()) / ((whRatio > 1) ? whRatio : 1);
        maxPosition[MyFitnessFunction.A] = Math.PI;

        minPosition[MyFitnessFunction.X] = r.getX();
        minPosition[MyFitnessFunction.Y] = r.getY();
        minPosition[MyFitnessFunction.H] = 0;
        minPosition[MyFitnessFunction.A] = -Math.PI;

        swarm.setMaxPosition(maxPosition);
        swarm.setMinPosition(minPosition);
        swarm.setMaxVelocity(MAXVELOCITY);
        swarm.setMinVelocity(MINVELOCITY);

        // Use neighborhood
        int neighborNum = NEIGHBORNUM;
//        if (neighborNum > 0) {
//            swarm.setNeighborhood(new Neighborhood1D(neighborNum, true));
//            swarm.setNeighborhoodIncrement(0.9);
//        }

        // Set swarm's update parameters
        swarm.setInertia(Swarm.DEFAULT_INERTIA);
        swarm.setParticleIncrement(Swarm.DEFAULT_PARTICLE_INCREMENT);
        swarm.setGlobalIncrement(Swarm.DEFAULT_GLOBAL_INCREMENT);
        return swarm;
    }

    /**
     * get the inner largest rectangle given the ratio
     *
     * @param swarm input swarm
     * @return core.MyRectangle
     */
    private MyRectangle getBestRectangle(Swarm swarm) {
        double[] best = swarm.getBestPosition();
        double x = best[MyFitnessFunction.X];
        double y = best[MyFitnessFunction.Y];
        double a = best[MyFitnessFunction.A];
        double h = best[MyFitnessFunction.H];
        double w = h * whRatio;
        return new MyRectangle(x, y, w, h, a);
    }

    /**
     * convert MyRectangle to WB_Polygon
     *
     * @param myRectangle rectangle
     * @return wblut.geom.WB_Polygon
     */
    private WB_Polygon convertRectangle(MyRectangle myRectangle) {
        WB_Transform2D transform2D = new WB_Transform2D();
        List<WB_Point> points = new ArrayList<>();
        points.add(new WB_Point(myRectangle.x, myRectangle.y));
        points.add(new WB_Point(myRectangle.x, myRectangle.y - myRectangle.h));
        points.add(new WB_Point(myRectangle.x + myRectangle.w, myRectangle.y - myRectangle.h));
        points.add(new WB_Point(myRectangle.x + myRectangle.w, myRectangle.y));

        transform2D.addRotateAboutPoint(myRectangle.r, new WB_Point(myRectangle.x, myRectangle.y));
        for (WB_Point p : points) {
            p.set(transform2D.applyAsPoint2D(p));
        }
        points.add(points.get(0));
        return new WB_Polygon(points);
    }

    private double diagonal(double w, double h) {
        return Math.sqrt(w * w + h * h);
    }

    /* ------------- setter & getter ------------- */

    public void setWhRatio(double whRatio) {
        this.whRatio = whRatio;
    }

    public void setParticleNum(int particleNum) {
        this.particleNum = particleNum;
    }

    public MyRectangle getRectangleResult() {
        return rectangleResult;
    }

    public WB_Polygon getLargestRectangle() {
        return largestRectangle;
    }

    /* ------------- draw ------------- */

}
