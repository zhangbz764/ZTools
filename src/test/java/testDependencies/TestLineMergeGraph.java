package testDependencies;

import basicGeometry.ZFactory;
import guo_cam.CameraController;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.operation.linemerge.LineMergeGraph;
import org.locationtech.jts.operation.linemerge.LineMerger;
import processing.core.PApplet;
import render.JtsRender;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2023/3/27
 * @time 10:50
 */
public class TestLineMergeGraph extends PApplet {
    public static void main(String[] args) {
        PApplet.main("testDependencies.TestLineMergeGraph");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private LineMergeGraph lmg;
    private LineMerger lm;

    private CameraController cam;
    private JtsRender jtsRender;

    public void setup() {
        this.cam = new CameraController(this);
        this.jtsRender = new JtsRender(this);

        // LineMergeGraph
        this.lmg = new LineMergeGraph();

        lmg.addEdge(ZFactory.jtsgf.createLineString(new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(100, 0)
        }));
        lmg.addEdge(ZFactory.jtsgf.createLineString(new Coordinate[]{
                new Coordinate(100, 0),
                new Coordinate(100, 100)
        }));
        lmg.addEdge(ZFactory.jtsgf.createLineString(new Coordinate[]{
                new Coordinate(50, -50),
                new Coordinate(50, 50),
                new Coordinate(150, 50),
        }));
        lmg.addEdge(ZFactory.jtsgf.createLineString(new Coordinate[]{
                new Coordinate(50, -50),
                new Coordinate(50, 50),
                new Coordinate(150, 50),
        }));

        System.out.println(lmg.getEdges());

        // LineMerger
        this.lm = new LineMerger();
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
    }

}
