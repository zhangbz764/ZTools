package demoTest;

import geometry.*;
import math.ZGraphMath;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/3/30
 * @time 17:47
 */
public class TestZGraph extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    public List<ZNode> nodes = new ArrayList<>();
    public ZGraph graph;
    public ZNode select;

    public List<ZLine> resultSegments;

    public List<ZEdge> longestChain;

    public void setup() {
        long start, end;
//        // 创建graph 1
//        nodes.add(new ZNode(100, 500));
//        nodes.add(new ZNode(200, 500));
//        nodes.add(new ZNode(200, 400));
//        nodes.add(new ZNode(250, 500));
//        nodes.add(new ZNode(300, 600));
//        nodes.add(new ZNode(400, 500));
//        nodes.add(new ZNode(300, 450));
//        int[][] connection = new int[][]{
//                {0, 1},
//                {1, 2},
//                {1, 3},
//                {3, 4},
//                {4, 5},
//                {3, 6}
//        };
//        start = System.currentTimeMillis();
//        this.graph = new ZGraph(nodes, connection);
//        end = System.currentTimeMillis();

        // 创建graph 2
        List<ZLine> segList = new ArrayList<>();
        segList.add(new ZLine(new ZPoint(100, 100), new ZPoint(200, 100)));
        segList.add(new ZLine(new ZPoint(200, 100), new ZPoint(300, 200)));
        segList.add(new ZLine(new ZPoint(200, 100), new ZPoint(200, 200)));
        segList.add(new ZLine(new ZPoint(300, 200), new ZPoint(400, 100)));
        segList.add(new ZLine(new ZPoint(300, 200), new ZPoint(300, 300)));

        start = System.currentTimeMillis();
        this.graph = ZFactory.createZGraphFromSegments(segList);
        end = System.currentTimeMillis();
        System.out.println("运行时间：" + (end - start) + " ms");

        // 测试找全部链
//        List<List<ZEdge>> allChains = ZGraphMath.getAllChainEdgeFromNode(graph.getNodeN(1), null, new ArrayList<ZEdge>());
//        System.out.println("chains: " + allChains.size());
//        for (List<ZEdge> allChain : allChains) {
//            System.out.println("chain: " + allChain.size());
//            for (ZEdge e : allChain) {
//                System.out.println(e.toString());
//            }
//        }
        List<List<ZNode>> allChains = ZGraphMath.getAllChainNodeFromNode(
                graph.getNodeN(1),
                null,
                new ArrayList<ZNode>() {{
                    add(graph.getNodeN(1));
                }}
        );
        System.out.println("chains: " + allChains.size());
        for (List<ZNode> allChain : allChains) {
            System.out.println("chain: " + allChain.size());
            for (ZNode n : allChain) {
                System.out.println(n.toString());
            }
        }

        // 测试找最长链
        this.longestChain = ZGraphMath.longestChain(graph);

        // graph找线段
//        select = nodes.get(1);
//        this.resultSegments = ZGraphMath.segmentsOnGraphByDist(nodes.get(1), null, 80);
        select = graph.getNodes().get(1);
        this.resultSegments = ZGraphMath.segmentsOnGraphByDist(select, null, 80);

        System.out.println("find by dist on graph: " + resultSegments.size());
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        stroke(0);
        // 画graph部分
        strokeWeight(1);
        graph.display(this);
        strokeWeight(4);
        stroke(0, 0, 255);
        for (ZLine l : resultSegments) {
            l.display(this);
        }
        if (select != null) {
            stroke(255, 0, 0);
            rect(select.xf() - 10, select.yf() - 10, 40, 20);
        }

        pushMatrix();
        translate(0, 500);
        for (ZEdge e : longestChain) {
            e.display(this);
        }
        popMatrix();
    }

    public void mouseClicked() {
        for (ZNode node : graph.getNodes()) {
            if (node.distance(new ZPoint(mouseX, mouseY)) < node.rd()) {
                select = node;
                this.resultSegments = ZGraphMath.segmentsOnGraphByDist(node, null, 150);
            }
        }
        System.out.println("find by dist on graph: " + resultSegments.size());
    }
}