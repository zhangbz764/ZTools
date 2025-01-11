package testUtils;

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

    public static void main(String[] args) {
        PApplet.main("testUtils.Test8ZGraph");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private ZGraph graph;
    private ZNode select;

    private List<ZLine> resultSegments;
    private List<ZNode> resultNodes;

    private List<ZEdge> longestChain;

    private List<ZPoint> splitResult;

    public void setup() {
        long start, end;

//        // graph 1
        List<ZPoint> points = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            points.add(new ZPoint(random(width), random(height)));
        }
        this.graph = ZFactory.createMiniSpanningTree(points);

        List<List<ZNode>> allChains = ZGraphMath.getAllChainNodeFromNode(
                graph.getNodeN(1),
                null,
                new ArrayList<ZNode>() {{
                    add(graph.getNodeN(1));
                }}
        );

        // longest chain
        this.longestChain = ZGraphMath.longestChain(graph);

        // graph find segments
        select = graph.getNodes().get(1);
        this.resultSegments = ZGraphMath.segmentsOnGraphByDist(select, null, 150);
        this.resultNodes = ZGraphMath.nodesOnGraphByDist(select, null, 150);

        // graph split
        this.splitResult = ZGraphMath.splitGraphEdgeByStep(graph, graph.getNodeN(0), null, 30, 30);
        System.out.println("splitResult.size() " + splitResult.size());
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

        // longestChain
        strokeWeight(2);
        stroke(255,0,0);
        for (ZEdge e : longestChain) {
            e.display(this);
        }


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
            rect(select.xf() - 10, select.yf() - 10, 20, 20);
        }

        strokeWeight(1);
        stroke(200);
        if (splitResult != null) {
            for (ZPoint p : splitResult) {
                ZRender.drawZPoint2D(this, p, 2);
            }
        }

    }

//    public void mouseClicked() {
//        for (ZNode node : graph.getNodes()) {
//            if (node.distance(new ZPoint(mouseX, mouseY)) < node.rd()) {
//                select = node;
//                this.resultSegments = ZGraphMath.segmentsOnGraphByDist(node, null, 150);
//                this.resultNodes = ZGraphMath.nodesOnGraphByDist(select, null, 150);
//                System.out.println("find resultSegments by dist on graph: " + resultSegments.size());
//                System.out.println("find resultNodes by dist on graph: " + resultNodes.size());
//            }
//        }
//    }

    public void mouseDragged() {
        select = graph.closestNode(mouseX, mouseY);
        this.resultSegments = ZGraphMath.segmentsOnGraphByDist(select, null, 150);
        this.resultNodes = ZGraphMath.nodesOnGraphByDist(select, null, 150);
    }

    public void keyPressed() {
        if (key == 'r') {
            ZPoint p = new ZPoint(mouseX, mouseY);
            this.graph.addNodeByDist(p);
        }

        if (key == 's') {
            String className = getClass().getSimpleName();
            save("./src/test/resources/exampleImgs/" + className + ".jpg");
        }

    }
}