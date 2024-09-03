package testDependencies;

import basicGeometry.ZFactory;
import math.ZGeoMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.math.Vector2D;
import processing.core.PApplet;
import render.JtsRender;

import java.util.Arrays;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/2/25
 * @time 17:25
 */
public class TestJtsAffineTrans extends PApplet {
    public static void main(String[] args) {
        PApplet.main("testDependencies.TestJtsAffineTrans");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private Polygon boundary;
    private Polygon furniture;

    private Polygon boundaryT;
    private Polygon furnitureT;

    private JtsRender jtsRender;
    //    private CameraController gcam;

    public void setup() {
        this.jtsRender = new JtsRender(this);
        float fov      = PI/3;  // field of view
        float nearClip = 1;
        float farClip  = 100000;
        float aspect   = (float) width / height;
        perspective(fov, aspect, nearClip, farClip);
//        perspective();


        this.boundary = ZFactory.jtsgf.createPolygon(new Coordinate[]{
                new Coordinate(0, 0, 0),
                new Coordinate(100, 0, 0),
                new Coordinate(100, 70, 0),
                new Coordinate(0, 70, 0),
                new Coordinate(0, 0, 0),
        });
        this.boundaryT = ZFactory.jtsgf.createPolygon(new Coordinate[]{
                new Coordinate(200, 0, 0),
                new Coordinate(350, 0, 0),
                new Coordinate(380, 150, 0),
                new Coordinate(200, 100, 0),
                new Coordinate(200, 0, 0),
        });
        Vector2D bTVec01 = new Vector2D(boundaryT.getCoordinates()[0], boundaryT.getCoordinates()[1]).normalize();
        Vector2D bTVec32 = new Vector2D(boundaryT.getCoordinates()[3], boundaryT.getCoordinates()[2]).normalize();
        Vector2D bTVec03 = new Vector2D(boundaryT.getCoordinates()[0], boundaryT.getCoordinates()[3]).normalize();
        Vector2D bTVec12 = new Vector2D(boundaryT.getCoordinates()[1], boundaryT.getCoordinates()[2]).normalize();

        Vector2D base0 = new Vector2D(boundaryT.getCoordinates()[0]);
        Vector2D base1 = new Vector2D(boundaryT.getCoordinates()[1]);
        Vector2D base3 = new Vector2D(boundaryT.getCoordinates()[3]);
        double d01 = boundaryT.getCoordinates()[0].distance(boundaryT.getCoordinates()[1]);
        double d32 = boundaryT.getCoordinates()[3].distance(boundaryT.getCoordinates()[2]);
        double d03 = boundaryT.getCoordinates()[0].distance(boundaryT.getCoordinates()[3]);
        double d12 = boundaryT.getCoordinates()[1].distance(boundaryT.getCoordinates()[2]);

        this.furniture = ZFactory.jtsgf.createPolygon(new Coordinate[]{
                new Coordinate(20, 20),
                new Coordinate(40, 20),
                new Coordinate(40, 50),
                new Coordinate(20, 50),
                new Coordinate(20, 20),
        });

        Coordinate[] transCoords = new Coordinate[furniture.getCoordinates().length];
        for (int i = 0; i < furniture.getCoordinates().length; i++) {
            Coordinate c = furniture.getCoordinates()[i];
            double scX = c.x / 100;
            double scY = c.y / 70;

            Coordinate ptOn01 = base0.add(bTVec01.multiply(scX * d01)).toCoordinate();
            Coordinate ptOn32 = base3.add(bTVec32.multiply(scX * d32)).toCoordinate();
            Coordinate ptOn03 = base0.add(bTVec03.multiply(scY * d03)).toCoordinate();
            Coordinate ptOn12 = base1.add(bTVec12.multiply(scY * d12)).toCoordinate();

            System.out.println(ptOn01);
            System.out.println(ptOn32);
            System.out.println(ptOn03);
            System.out.println(ptOn12);
            Coordinate newC = ZGeoMath.lineIntersection2D(ptOn01, ptOn32, ptOn03, ptOn12);

            transCoords[i] = newC;
        }
        System.out.println(Arrays.toString(transCoords));
        this.furnitureT = ZFactory.jtsgf.createPolygon(transCoords);

    }


    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        jtsRender.drawGeometry(boundary);
        jtsRender.drawGeometry(boundaryT);

        jtsRender.drawGeometry(furniture);
        jtsRender.drawGeometry(furnitureT);
    }

}
