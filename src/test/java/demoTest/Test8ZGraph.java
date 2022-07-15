package demoTest;

import basicGeometry.*;
import math.ZGraphMath;
import processing.core.PApplet;
import render.ZRender;

import java.util.ArrayList;
import java.util.List;

/**
 * test ZGraph and graph methods
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/3/30
 * @time 17:47
 */
public class Test8ZGraph extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private List<ZNode> nodes = new ArrayList<>();
    private ZGraph graph;
    private ZNode select;

    private List<ZLine> resultSegments;
    private List<ZNode> resultNodes;

    private List<ZEdge> longestChain;

    private List<ZPoint> splitResult;

    public void setup() {
        long start, end;

//        // graph 1
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
        start = System.currentTimeMillis();
        this.graph = new ZGraph(nodes, connection);
        end = System.currentTimeMillis();

        // mini spanning tree
//        start = System.currentTimeMillis();
//        this.graph = ZFactory.createMiniSpanningTree(nodes);
//        end = System.currentTimeMillis();
//        System.out.println("time processed: " + (end - start) + " ms");

        // graph 2
//        List<ZLine> segList = new ArrayList<>();
//        segList.add(new ZLine(new ZPoint(100, 100), new ZPoint(200, 100)));
//        segList.add(new ZLine(new ZPoint(200, 100), new ZPoint(300, 200)));
//        segList.add(new ZLine(new ZPoint(200, 100), new ZPoint(200, 200)));
//        segList.add(new ZLine(new ZPoint(300, 200), new ZPoint(400, 100)));
//        segList.add(new ZLine(new ZPoint(300, 200), new ZPoint(300, 300)));

//        start = System.currentTimeMillis();
//        this.graph = ZFactory.createZGraphFromSegments(segList);
//        end = System.currentTimeMillis();
//        System.out.println("time processed: " + (end - start) + " ms");

//        // jts graph
//        Coordinate[] test = new Coordinate[3];
//        test[0] = new Coordinate(50, 400);
//        test[1] = new Coordinate(100, 500);
//        test[2] = new Coordinate(200, 50);
//
//        List<ZLine> segments = new ArrayList<>();
//        segments.add(new ZLine(100, 500, 200, 500));
//        segments.add(new ZLine(200, 500, 200, 400));
//        segments.add(new ZLine(200, 500, 250, 500));
//        segments.add(new ZLine(250, 500, 300, 600));
//        segments.add(new ZLine(250, 500, 300, 450));
//        segments.add(new ZLine(300, 450, 400, 500));
//
//        this.graph = ZFactory.createZGraphFromSegments(segments);

        // all chains
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
//        System.out.println("chains: " + allChains.size());
//        for (List<ZNode> allChain : allChains) {
//            System.out.println("chain: " + allChain.size());
//            for (ZNode n : allChain) {
//                System.out.println(n.toString());
//            }
//        }

        // longest chain
        this.longestChain = ZGraphMath.longestChain(graph);

        // graph find segments
//        select = nodes.get(1);
//        this.resultSegments = ZGraphMath.segmentsOnGraphByDist(nodes.get(1), null, 80);
        select = graph.getNodes().get(1);
        this.resultSegments = ZGraphMath.segmentsOnGraphByDist(select, null, 150);
        this.resultNodes = ZGraphMath.nodesOnGraphByDist(select, null, 150);

        // graph split
        this.splitResult = ZGraphMath.splitGraphEdgeByStep(graph, graph.getNodeN(0), null, 30, 30);
//        this.splitResult = ZGraphMath.splitGraphEachEdgeByStep(graph, 20);
        System.out.println("splitResult.size() " + splitResult.size());
        for (ZPoint p : splitResult) {
            System.out.println(p.toString());
        }
        System.out.println("find resultSegments by dist on graph: " + resultSegments.size());
        System.out.println("find resultNodes by dist on graph: " + resultNodes.size());
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        stroke(0);
        // graph
        strokeWeight(1);
        graph.display(this);
        strokeWeight(4);
        stroke(0, 0, 255);
        for (ZLine l : resultSegments) {
            l.display(this);
        }
        for (ZNode n : resultNodes) {
            ZRender.drawZPoint2D(this, n, 8);
        }
        if (select != null) {
            stroke(255, 0, 0);
            rect(select.xf() - 10, select.yf() - 10, 40, 20);
        }

        if (splitResult != null) {
            for (ZPoint p : splitResult) {
                ZRender.drawZPoint2D(this, p, 2);
            }
        }

        pushMatrix();
        translate(0, 300);
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
                this.resultNodes = ZGraphMath.nodesOnGraphByDist(select, null, 150);
                System.out.println("find resultSegments by dist on graph: " + resultSegments.size());
                System.out.println("find resultNodes by dist on graph: " + resultNodes.size());
                ;
            }
        }
    }

    public void keyPressed() {
        if (key == 'r') {
            ZPoint p = new ZPoint(mouseX, mouseY);
            this.graph.addNodeByDist(p);
        }
    }
}