package demoTest;

import geometry.ZGraph;
import geometry.ZLine;
import geometry.ZNode;
import geometry.ZPoint;
import math.ZGeoMath;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render2D;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试多边形等分、沿多边形边找点、多边形边线offset
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/24
 * @time 17:14
 */
public class TestPolySplit extends PApplet {
    public void settings() {
        size(1000, 1000, P2D);
    }

    WB_Render2D render;
    WB_Polygon poly;
    WB_PolyLine pl;

    List<ZNode> nodes = new ArrayList<>();
    ZNode select;
    ZGraph graph;
    List<ZLine> alongSegments;

    // 测试沿轮廓找点
    ZPoint test = new ZPoint(600, 100);
    ZPoint[] besides = new ZPoint[2];

    // 测试剖分，记录结果的list
    List<ZPoint> split;

    // 测试多边形的边序号，以及offset功能
    int count = 0;
    int index;
    ZLine offset;
    double step;

    public void setup() {
        render = new WB_Render2D(this);

        // 创建多边形和多段线
        WB_Point[] pts1 = new WB_Point[6]; // polygon
        WB_Point[] pts2 = new WB_Point[5]; // polyline
        pts2[0] = pts1[0] = new WB_Point(100, 100);
        pts2[1] = pts1[1] = new WB_Point(700, 100);
        pts2[2] = pts1[2] = new WB_Point(800, 400);
        pts2[3] = pts1[3] = new WB_Point(500, 800);
        pts2[4] = pts1[4] = new WB_Point(100, 600);
        pts1[5] = new WB_Point(100, 100);
        this.poly = new WB_Polygon(pts1);
        this.pl = new WB_PolyLine(pts2);

        // 创建graph
        nodes.add(new ZNode(100, 500));
        nodes.add(new ZNode(200, 500));
        nodes.add(new ZNode(200, 400));
        nodes.add(new ZNode(250, 500));
        nodes.add(new ZNode(300, 600));
        nodes.add(new ZNode(400, 500));
        nodes.add(new ZNode(300, 450));
        int[][] connection = new int[][]{
                {0, 1},
                {1, 2},
                {1, 3},
                {3, 4},
                {4, 5},
                {3, 6}
        };
        this.graph = new ZGraph(nodes, connection);

        // 多边形找点
        besides = ZGeoMath.pointsOnEdgeByDist(test, poly, 450);

        // graph找线段
        select = nodes.get(1);
        this.alongSegments = ZGeoMath.segmentsOnGraphByDist(nodes.get(1), null, 80);
        System.out.println("find by dist on graph: " + alongSegments.size());

        // 按阈值剖分
        step = 50;
        split = ZGeoMath.splitWB_PolyLineEdgeByThreshold(pl, 90, 84);
        println("split: " + split.size());

        // 偏移一条边线
        index = count % poly.getNumberSegments();
        println(index);
        offset = ZGeoMath.offsetWB_PolygonSegment(poly, index, 30);
    }

    public void draw() {
        background(255);
        stroke(0);
//        noFill();
//        strokeWeight(1);
////        render.drawPolygonEdges2D(poly);
//        render.drawPolyLine2D(pl);
//        pushStyle();
//        fill(255, 0, 0);
//        render.drawPoint2D(poly.getSegment(index).getOrigin(), 10);
//        fill(0, 0, 255);
//        render.drawPoint2D(poly.getSegment(index).getEndpoint(), 10);
//
//        fill(0, 255, 0);
//        test.displayAsPoint(this);
//        for (ZPoint p : besides) {
//            p.displayAsPoint(this);
//        }
//        popStyle();
//        strokeWeight(4);
//        render.drawSegment2D(poly.getSegment(index));
//        offset.display(this);
//
//        for (ZPoint p : split) {
//            p.displayAsPoint(this);
//        }

        // 画graph部分
        strokeWeight(1);
        graph.display(this);
        strokeWeight(4);
        stroke(0, 0, 255);
        for (ZLine l : alongSegments) {
            l.display(this);
        }
        if (select != null) {
            stroke(255, 0, 0);
            rect(select.xf() - 10, select.yf() - 10, 40, 20 );
//            ellipse(select.xf(), select.yf(), 20, 20);
        }
    }

    public void mouseClicked() {
        count++;
        index = count % poly.getNumberSegments();
        offset = ZGeoMath.offsetWB_PolygonSegment(poly, index, 30);
        for (ZNode node : graph.getNodes()) {
            if (node.distance(new ZPoint(mouseX, mouseY)) < node.rd()) {
                select = node;
                this.alongSegments = ZGeoMath.segmentsOnGraphByDist(node, null, 150);
            }
        }
    }

    public void mouseDragged() {
        step = mouseX;
        split = ZGeoMath.splitWB_PolyLineEdgeByStep(poly, step);
        println("split: " + split.size());
    }
}
