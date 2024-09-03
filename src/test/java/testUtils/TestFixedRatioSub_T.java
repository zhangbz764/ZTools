package testUtils;

import advancedGeometry.subdivision.lb_sub.FixedRatioSub_T;
import basicGeometry.ZFactory;
import guo_cam.CameraController;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.math.Vector2D;
import processing.core.PApplet;
import render.JtsRender;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/4/16
 * @time 23:10
 */
public class TestFixedRatioSub_T extends PApplet {
    public static void main(String[] args) {
        PApplet.main(TestFixedRatioSub_T.class.getName());
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1600, 900, P3D);
    }

    /* ------------- setup ------------- */

    private JtsRender jtsRender;
    private CameraController gcam;

    private Polygon polygon;
    private Polygon obb;
    private Polygon[] subResult;

    private Geometry transformed;
    private Geometry transformedOBB;

    public void setup() {
        this.jtsRender = new JtsRender(this);
        this.gcam = new CameraController(this);
        gcam.top();


        cal();
    }


    private void cal() {
        // 随机多边形
        this.polygon = randomPolygon(8, 100, 0.81);


        // ********  计算  **********
        long start = System.currentTimeMillis();
        FixedRatioSub_T fixedRatioSubT = new FixedRatioSub_T(polygon, 1, 2, 3);
        this.subResult = fixedRatioSubT.getSubResult();
        System.out.println((System.currentTimeMillis() - start) + "ms");
        // ********  计算  **********

        // 与显示有关的一些内容
        MinimumDiameter minimumDiameter = new MinimumDiameter(polygon);
        this.obb = (Polygon) minimumDiameter.getMinimumRectangle();

        Vector2D vec01 = new Vector2D(obb.getCoordinates()[0], obb.getCoordinates()[1]);
        Vector2D xAxis = new Vector2D(1, 0);

        double angle = vec01.angleTo(xAxis);

        AffineTransformation transformation = new AffineTransformation();
        transformation.rotate(angle);

        this.transformed = transformation.transform(polygon);
        this.transformedOBB = transformation.transform(obb);
    }

    /**
     * generate random simple polygon around origin
     *
     * @param ptNum     number of polygon points, excluding the last point
     * @param scale     scale of the polygon
     * @param threshold threshold to random
     * @return
     */
    private Polygon randomPolygon(int ptNum, double scale, double threshold) {
        double thre = threshold >= 1 || threshold <= 0 ? 0.5 : threshold;
        double angle = (Math.PI * 2) / ptNum;

        Coordinate[] coords = new Coordinate[ptNum + 1];
        for (int i = 0; i < ptNum; i++) {
            double ran = (Math.random() * 2 * thre + 1 - thre) * scale;
            Vector2D v = new Vector2D(Math.cos(angle * i) * ran, Math.sin(angle * i) * ran);

            Coordinate coord = v.toCoordinate();
            coords[i] = coord;
        }
        coords[coords.length - 1] = new Coordinate(coords[0].x, coords[0].y);

        return ZFactory.jtsgf.createPolygon(coords);
    }


    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(100);

        // 原始多边形和obb
        strokeWeight(2);
        stroke(0);
        jtsRender.drawGeometry(polygon);
        jtsRender.drawGeometry(obb);
        fill(0);
        for (int i = 0; i < obb.getCoordinates().length - 1; i++) {
            pushMatrix();
            scale(1, -1);
            translate(0, (float) (-2 * obb.getCoordinates()[i].getY()));
            text(i, (float) obb.getCoordinates()[i].getX(), (float) obb.getCoordinates()[i].getY());
            popMatrix();
        }
        noFill();


        // 旋转后的多边形和obb，方便求解
        translate(-400, 0);
        stroke(255, 0, 0);
        jtsRender.drawGeometry(transformed);
        jtsRender.drawGeometry(transformedOBB);
        fill(0);
        for (int i = 0; i < transformedOBB.getCoordinates().length - 1; i++) {
            pushMatrix();
            scale(1, -1);
            translate(0, (float) (-2 * transformedOBB.getCoordinates()[i].getY()));
            text(i, (float) transformedOBB.getCoordinates()[i].getX(), (float) transformedOBB.getCoordinates()[i].getY());
            popMatrix();
        }
        noFill();

        // 结果
        translate(800, 0);
        strokeWeight(2);
        stroke(0, 0, 255);
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
