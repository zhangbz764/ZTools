package testDependencies;

import basicGeometry.ZFactory;
import guo_cam.CameraController;
import igeo.ICurve;
import igeo.IG;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;

/**
 * description
 *
 * @author zhangbz ZHANG Baizhou
 * @project shopping_mall
 * @date 2021/9/10
 * @time 13:07
 */
public class TestJtsGeoRelation extends PApplet {

    public static void main(String[] args) {
        PApplet.main("testDependencies.TestJtsGeoRelation");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    // jts intersection
    private LineString[] testLS;
    private Geometry union;

    private JtsRender jtsRender;
    private CameraController gcam;

    public void setup() {
        this.jtsRender = new JtsRender(this);
        this.gcam = new CameraController(this);

        loadPolys();

        for (LineString testL : testLS) {
            System.out.println("LineString pt num: " + testL.getNumPoints());
            System.out.println("isIntersect?: " + testL.intersects(union));
            System.out.println("isTouch?: " + testL.touches(union));
            System.out.println("isContain?: " + union.contains(testL));
        }
    }

    private void loadPolys() {
        String path = "src/test/resources/test_geo_relations.3dm";

        IG.init();
        IG.open(path);

        this.union = ZFactory.jtsgf.createPolygon();
        ICurve[] polyLines = IG.layer("geo").curves();
        for (ICurve polyLine : polyLines) {
            union = union.union(ZTransform.ICurveToJts(polyLine));
        }

        ICurve[] ls = IG.layer("test").curves();
        this.testLS = new LineString[ls.length];
        for (int i = 0; i < ls.length; i++) {
            testLS[i] = (LineString) ZTransform.ICurveToJts(ls[i]);
        }

    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        stroke(0);
        strokeWeight(2);
        jtsRender.drawGeometry(union);

        stroke(255, 0, 0);
        strokeWeight(3);
        for (LineString testL : testLS) {
            jtsRender.drawGeometry(testL);
        }
    }
}
