package testUtils;

import advancedGeometry.subdivision.lb_sub.Subdivision05;
import basicGeometry.ZFactory;
import guo_cam.CameraController;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.math.Vector2D;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.processing.WB_Render;

import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/4/18
 * @time 13:25
 */
public class TestRangeDivision extends PApplet {
    public static void main(String[] args) {
        PApplet.main(TestRangeDivision.class.getName());
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Render render;
    private JtsRender jtsRender;
    private CameraController gcam;

    private Polygon polygon;
    private Polygon obb;
    private Polygon[] subResult;

    public void setup() {
        this.render = new WB_Render(this);
        this.jtsRender = new JtsRender(this);
        this.gcam = new CameraController(this);

        cal();
    }

    private void cal() {
        // 随机多边形
        this.polygon = ZFactory.createRandomPolygon(8, 100, 0.81);
//        this.polygon = ZFactory.jtsgf.createPolygon(new Coordinate[]{
//                new Coordinate(-100, -50),
//                new Coordinate(50, -50),
//                new Coordinate(50, 50),
//                new Coordinate(-100, 50),
//                new Coordinate(-100, -50)
//        });


        this.obb = (Polygon) MinimumDiameter.getMinimumRectangle(polygon);

        // ********  计算  **********
        long start = System.currentTimeMillis();
        Subdivision05 subdivision05 = new Subdivision05(polygon, new double[]{1, 2, 3});
        List<DoubleSolution> solutions = subdivision05.getResults();
        //打印结果
        System.out.println("var " + solutions.get(0).variables());
        System.out.println("obj " + solutions.get(0).objectives()[0]);
//        System.out.println("obj " + solutions.get(0).objectives()[1]);
//        System.out.println("obj " + solutions.get(0).objectives()[2]);

        System.out.println((System.currentTimeMillis() - start) + "ms");
        List<Double> vars = solutions.get(0).variables();
        Point resultP = ZFactory.jtsgf.createPoint(new Coordinate(vars.get(0), vars.get(1)));

        // 得到最终的剖分结果
        double x = resultP.getX();
        double y = resultP.getY();
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
        Geometry nodedLineStrings = ZTransform.PolygonToLineString(polygon).get(0);
        nodedLineStrings = nodedLineStrings.union(ls1);
        nodedLineStrings = nodedLineStrings.union(ls2);
        nodedLineStrings = nodedLineStrings.union(ls3);
        pr.add(nodedLineStrings);
        Object[] prObjs = pr.getPolygons().toArray();
        this.subResult = new Polygon[prObjs.length];
        for (int i = 0; i < prObjs.length; i++) {
            subResult[i] = (Polygon) prObjs[i];
        }

        // 与显示有关的一些内容
        Vector2D vec01 = new Vector2D(obb.getCoordinates()[0], obb.getCoordinates()[1]);
        Vector2D xAxis = new Vector2D(1, 0);

        double angle = vec01.angleTo(xAxis);

        AffineTransformation transformation = new AffineTransformation();
        transformation.rotate(angle);

    }


    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(100);

        jtsRender.drawGeometry(obb);
        for (Polygon polygon1 : subResult) {
            jtsRender.drawGeometry(polygon1);
        }
        fill(0);
        for (int i = 0; i < obb.getCoordinates().length - 1; i++) {
            pushMatrix();
            scale(1, -1);
            translate(0, (float) (-2 * obb.getCoordinates()[i].getY()));
            text(i, (float) obb.getCoordinates()[i].getX(), (float) obb.getCoordinates()[i].getY());
            popMatrix();
        }
        for (Polygon polygon1 : subResult) {
            pushMatrix();
            scale(1, -1);
            translate(0, (float) (-2 * polygon1.getCentroid().getY()));
            text(String.format("%.2f", polygon1.getArea()), (float) polygon1.getCentroid().getX(), (float) polygon1.getCentroid().getY());
            popMatrix();
        }
        noFill();
    }

    @Override
    public void keyPressed() {
        if (key == '1') {
            cal();
        }
    }
}
