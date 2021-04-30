package demoTest;

import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_PolyLine;

/**
 * test jts geometry relation
 * cut out WB_PolyLine
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/23
 * @time 11:41
 */
public class TestGeoRelation extends PApplet {
    public void settings() {
        size(1000, 1000, P3D);
    }

    private GeometryFactory gf = new GeometryFactory();

    private Polygon poly1;
    private Polygon poly2;
    private Geometry multiPolygon;
    private LineString ls;
    private Geometry buffer;
    private ZPoint mouse;
    private Polygon mousePoly;

    private WB_PolyLine pl;

    private JtsRender jtsRender;

    public void setup() {
        jtsRender = new JtsRender(this);

        Coordinate[] polygonV1 = new Coordinate[6];
        polygonV1[0] = new Coordinate(100, 100, 0);
        polygonV1[1] = new Coordinate(400, 100, 0);
        polygonV1[2] = new Coordinate(600, 200, 0);
        polygonV1[3] = new Coordinate(250, 400, 0);
        polygonV1[4] = new Coordinate(50, 300, 0);
        polygonV1[5] = new Coordinate(100, 100, 0);

        Coordinate[] polygonV2 = new Coordinate[6];
        polygonV2[0] = new Coordinate(500, 500, 0);
        polygonV2[1] = new Coordinate(700, 700, 0);
        polygonV2[2] = new Coordinate(700, 900, 0);
        polygonV2[3] = new Coordinate(500, 850, 0);
        polygonV2[4] = new Coordinate(400, 600, 0);
        polygonV2[5] = new Coordinate(500, 500, 0);

        Coordinate[] vertices2 = new Coordinate[5];
        vertices2[0] = new Coordinate(100, 100, 0);
        vertices2[1] = new Coordinate(700, 100, 0);
        vertices2[2] = new Coordinate(800, 400, 0);
        vertices2[3] = new Coordinate(500, 800, 0);
        vertices2[4] = new Coordinate(100, 600, 0);

        poly1 = gf.createPolygon(polygonV1);
        poly2 = gf.createPolygon(polygonV2);
        multiPolygon = poly1;
        multiPolygon = multiPolygon.union(poly2);

        println(ZTransform.jtsPolygonToWB_Polygon(poly1).getNormal().zd());
        ls = gf.createLineString(vertices2);

        BufferOp bufferOp = new BufferOp(poly1);
        bufferOp.setEndCapStyle(BufferParameters.CAP_SQUARE);
        buffer = bufferOp.getResultGeometry(20);

        mouse = new ZPoint(500, 500);
        Coordinate[] mousePolyV = new Coordinate[5];
        mousePolyV[0] = new Coordinate(mouse.xd() - 20, mouse.yd() - 20, 0);
        mousePolyV[1] = new Coordinate(mouse.xd() + 20, mouse.yd() - 20, 0);
        mousePolyV[2] = new Coordinate(mouse.xd() + 20, mouse.yd() + 20, 0);
        mousePolyV[3] = new Coordinate(mouse.xd() - 20, mouse.yd() + 20, 0);
        mousePolyV[4] = new Coordinate(mouse.xd() - 20, mouse.yd() - 20, 0);
        mousePoly = gf.createPolygon(mousePolyV);

        pl = ZFactory.createPolylineFromPolygon(
                ZTransform.jtsPolygonToWB_Polygon(poly1), new int[]{2, 3, 4, 0}
        );
    }

    public void draw() {
        background(255);

        strokeWeight(1);
        jtsRender.drawGeometry(multiPolygon);
        jtsRender.drawGeometry(buffer);

        mouse.set(mouseX, mouseY);
        mouse.displayAsPoint(this);
        Coordinate[] mousePolyV = new Coordinate[5];
        mousePolyV[0] = new Coordinate(mouse.xd() - 20, mouse.yd() - 20, 0);
        mousePolyV[1] = new Coordinate(mouse.xd() + 20, mouse.yd() - 20, 0);
        mousePolyV[2] = new Coordinate(mouse.xd() + 20, mouse.yd() + 20, 0);
        mousePolyV[3] = new Coordinate(mouse.xd() - 20, mouse.yd() + 20, 0);
        mousePolyV[4] = new Coordinate(mouse.xd() - 20, mouse.yd() - 20, 0);
        mousePoly = gf.createPolygon(mousePolyV);
        jtsRender.drawGeometry(mousePoly);

        strokeWeight(4);
        for (int i = 0; i < pl.getNumberOfPoints() - 1; i++) {
            line(
                    pl.getPoint(i).xf(),
                    pl.getPoint(i).yf(),
                    pl.getPoint(i + 1).xf(),
                    pl.getPoint(i + 1).yf()
            );
        }
    }

    public void mouseClicked() {
        println(multiPolygon.contains(mousePoly));
    }

}
