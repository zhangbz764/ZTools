package demoTest;

import igeo.ICurve;
import igeo.IG;
import org.locationtech.jts.geom.*;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 测试jts多种几何形的buffer效果
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/3/11
 * @time 13:41
 */
public class TestJtsBuffer extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    public List<Geometry> geometries;
    public Geometry buffer;

    public JtsRender jtsRender;

    public void setup() {
        jtsRender = new JtsRender(this);

        System.out.println(this.getClass().getClassLoader().getResource("").getPath());
        // 载入几何模型
        String path = Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("./test_jts_buffer.3dm")
        ).getPath();

        IG.init();
        IG.open(path);

        this.geometries = new ArrayList<>();

        ICurve[] polyLines = IG.layer("geometries").curves();
        for (ICurve polyLine : polyLines) {
            Geometry geo = ZTransform.ICurveToJts(polyLine);
            geometries.add(geo);
        }

        // 做buffer
        GeometryFactory gf = new GeometryFactory();

        Geometry[] geos = geometries.toArray(new Geometry[0]);
        GeometryCollection collection = gf.createGeometryCollection(geos);

        buffer = collection.buffer(20);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        noFill();
        stroke(0);
        strokeWeight(1);
        for (Geometry g : geometries) {
            jtsRender.drawGeometry(g);
        }
        stroke(255, 0, 0);
        strokeWeight(3);
        jtsRender.drawGeometry(buffer);
    }

}
