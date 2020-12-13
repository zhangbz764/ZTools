package demoTest;

import geometry.ZPoint;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;

/**
 * 测试jts几何图形布尔关系
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

    GeometryFactory gf = new GeometryFactory();
    Polygon poly;
    LineString ls;
    Geometry buffer;
    ZPoint mouse;
    JtsRender jrender;

    public void setup() {
        jrender = new JtsRender(this);

        Coordinate[] vertices = new Coordinate[6];
        vertices[0] = new Coordinate(100, 100, 0);
        vertices[1] = new Coordinate(700, 100, 0);
        vertices[2] = new Coordinate(800, 400, 0);
        vertices[3] = new Coordinate(500, 800, 0);
        vertices[4] = new Coordinate(100, 600, 0);
        vertices[5] = new Coordinate(100, 100, 0);

        Coordinate[] vertices2 = new Coordinate[5];
        vertices2[0] = new Coordinate(100, 100, 0);
        vertices2[1] = new Coordinate(700, 100, 0);
        vertices2[2] = new Coordinate(800, 400, 0);
        vertices2[3] = new Coordinate(500, 800, 0);
        vertices2[4] = new Coordinate(100, 600, 0);

        poly = gf.createPolygon(vertices);
        println(ZTransform.jtsPolygonToWB_Polygon(poly).getNormal().zd());
        ls = gf.createLineString(vertices2);

        BufferOp bufferOp = new BufferOp(poly);
        bufferOp.setEndCapStyle(BufferParameters.CAP_SQUARE);
        buffer = bufferOp.getResultGeometry(20);
        mouse = new ZPoint(500, 500);
    }

    public void draw() {
        background(255);

        jrender.drawGeometry(poly);
        jrender.drawGeometry(buffer);
        mouse.set(mouseX, mouseY);
        mouse.displayAsPoint(this);
    }

    public void mouseClicked() {
        println(poly.contains(mouse.toJtsPoint()));
    }

}