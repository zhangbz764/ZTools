package testDependencies;

import basicGeometry.ZFactory;
import guo_cam.CameraController;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_PolyLine;

/**
 * test jts buffer
 * cut out WB_PolyLine
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/23
 * @time 11:41
 */
public class TestJtsBuffer extends PApplet {

    public static void main(String[] args) {
        PApplet.main("testDependencies.TestJtsBuffer");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private Polygon poly1;
    private Polygon poly2;
    private GeometryCollection collection;
    private Geometry union;
    private LineString ls;
    private Point p = ZFactory.jtsgf.createPoint(new Coordinate(900, 100));
    private Geometry multiGeo;
    private Geometry buffer1;
    private Geometry buffer2;
    private Geometry buffer3;
    private int endCapStyle = 1;
    private int joinStyle = 2;

    private WB_PolyLine pl;

    // utils
    private GeometryFactory gf = new GeometryFactory();
    private CameraController gcam;
    private JtsRender jtsRender;

    public void setup() {
        this.jtsRender = new JtsRender(this);
//        this.gcam = new CameraController(this);

        // jts buffer mode
        Coordinate[] polygonV1 = new Coordinate[6];
        polygonV1[0] = new Coordinate(100, 100, 0);
        polygonV1[1] = new Coordinate(400, 100, 0);
        polygonV1[2] = new Coordinate(600, 200, 0);
        polygonV1[3] = new Coordinate(250, 400, 0);
        polygonV1[4] = new Coordinate(300, 300, 0);
        polygonV1[5] = new Coordinate(100, 100, 0);

        Coordinate[] polygonV2 = new Coordinate[6];
        polygonV2[0] = new Coordinate(500, 500, 0);
        polygonV2[1] = new Coordinate(700, 700, 0);
        polygonV2[2] = new Coordinate(700, 900, 0);
        polygonV2[3] = new Coordinate(500, 850, 0);
        polygonV2[4] = new Coordinate(400, 600, 0);
        polygonV2[5] = new Coordinate(500, 500, 0);

        Coordinate[] lineStringC = new Coordinate[]{
                new Coordinate(800, 300),
                new Coordinate(850, 400),
                new Coordinate(800, 500),
                new Coordinate(950, 700)
        };

        this.poly1 = gf.createPolygon(polygonV1);
        this.poly2 = gf.createPolygon(polygonV2);
        this.collection = gf.createGeometryCollection(new Geometry[]{poly1, poly2});
        this.union = collection.buffer(0);
        this.ls = gf.createLineString(lineStringC);
        this.multiGeo = poly1;
        multiGeo = multiGeo.union(poly2);

        BufferParameters parameters = new BufferParameters(0, endCapStyle, joinStyle, 5.0D);
        this.buffer1 = BufferOp.bufferOp(multiGeo, 20, parameters);
        this.buffer2 = BufferOp.bufferOp(ls, 20, parameters);
        this.buffer3 = BufferOp.bufferOp(p, 20, parameters);
        System.out.println("endCapStyle:  " + endCapStyle);
        System.out.println("joinStyle:  " + joinStyle);
        System.out.println("buffer.getNumPoints(): " + buffer1.getNumPoints());

        pl = ZFactory.createPolylineFromPolygon(
                ZTransform.PolygonToWB_Polygon(poly1), new int[]{2, 3, 4, 0}
        );

    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        strokeWeight(1);
        jtsRender.drawGeometry(multiGeo);
        jtsRender.drawGeometry(buffer1);
        jtsRender.drawGeometry(ls);
        jtsRender.drawGeometry(buffer2);
        jtsRender.drawGeometry(p);
        jtsRender.drawGeometry(buffer3);

        strokeWeight(4);
        jtsRender.drawGeometry(union);
        for (int i = 0; i < pl.getNumberOfPoints() - 1; i++) {
            line(
                    pl.getPoint(i).xf(),
                    pl.getPoint(i).yf(),
                    pl.getPoint(i + 1).xf(),
                    pl.getPoint(i + 1).yf()
            );
        }
    }

    public void keyPressed() {
        if (key == 'q') {
            // end cap style
            endCapStyle = endCapStyle % 3 + 1;
            System.out.println("endCapStyle:  " + endCapStyle);

            BufferParameters parameters = new BufferParameters(0, endCapStyle, joinStyle, 5.0D);
            this.buffer1 = BufferOp.bufferOp(multiGeo, 20, parameters);
            this.buffer2 = BufferOp.bufferOp(ls, 20, parameters);
            this.buffer3 = BufferOp.bufferOp(p, 20, parameters);
            System.out.println("buffer.getNumPoints(): " + buffer1.getNumPoints());
        }
        if (key == 'w') {
            // join style
            joinStyle = joinStyle % 3 + 1;
            System.out.println("joinStyle:  " + joinStyle);

            BufferParameters parameters = new BufferParameters(0, endCapStyle, joinStyle, 5.0D);
            this.buffer1 = BufferOp.bufferOp(multiGeo, 20, parameters);
            this.buffer2 = BufferOp.bufferOp(ls, 20, parameters);
            this.buffer3 = BufferOp.bufferOp(p, 20, parameters);
            System.out.println("buffer.getNumPoints(): " + buffer1.getNumPoints());
        }
    }
}
