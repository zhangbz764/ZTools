package demoTest;

import geometry.ZGeoFactory;
import geometry.ZLine;
import geometry.ZPoint;
import processing.core.PApplet;
import wblut.geom.WB_PolyLine;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/1/20
 * @time 16:55
 */
public class TestLineMerger extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    List<ZLine> segments;
    List<WB_PolyLine> polyLines;

    WB_Render render;

    /* ------------- setup ------------- */

    public void setup() {
        render = new WB_Render(this);
        segments = new ArrayList<>();

        segments.add(new ZLine(new ZPoint(100, 100), new ZPoint(100, 200)));
        segments.add(new ZLine(new ZPoint(200, 100), new ZPoint(300, 200)));
        segments.add(new ZLine(new ZPoint(100, 100), new ZPoint(150, 150)));
        segments.add(new ZLine(new ZPoint(300, 200), new ZPoint(300, 300)));
        segments.add(new ZLine(new ZPoint(100, 300), new ZPoint(200, 300)));

        polyLines = ZGeoFactory.createWB_PolyLineList(segments);
        System.out.println(polyLines.size());
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        for (WB_PolyLine pl : polyLines) {
            render.drawPolylineEdges(pl);
        }
    }

}
