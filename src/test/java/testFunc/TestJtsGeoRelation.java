package testFunc;

import basicGeometry.ZFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;

/**
 * description
 *
 * @author zhangbz ZHANG Baizhou
 * @project shopping_mall
 * @date 2021/9/10
 * @time 13:07
 */
public class TestJtsGeoRelation extends PApplet {

    /* ------------- settings ------------- */

    public void settings(){
        size(1000,1000,P3D);
    }

    /* ------------- setup ------------- */

    // jts intersection
    private LineString l1;
    private Geometry p1;

    public void setup(){
        this.l1 = ZFactory.jtsgf.createLineString(
                new Coordinate[]{
                        new Coordinate(0, 1000),
                        new Coordinate(30, 950),
                        new Coordinate(100, 850),
                        new Coordinate(250, 850),
                        new Coordinate(400, 700),
                }
        );
    }

    /* ------------- draw ------------- */

    public void draw(){
        background(255);
    }
}
