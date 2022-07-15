package demoTest;

import basicGeometry.*;
import guo_cam.CameraController;
import igeo.ICurve;
import igeo.IG;
import math.ZGeoMath;
import math.ZMath;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.processing.WB_Render;

import java.util.*;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project shopping_mall
 * @date 2022/4/2
 * @time 17:02
 */
public class TestZGraph2 extends PApplet {
    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private ZGraph graph;

    // test 1
    int selectedID = 0;
    private ZNode select;
    private List<ZNode> selectedNodes;
    private List<List<ZLine>> passedSegs;

    // test 2
    private WB_PolyLine traffic;
    private WB_PolyLine[] originalSides;
    private List<Side> sides;
    private Side firseSelected;
    private List<Side> selectedSides;

    private JtsRender jtsRender;
    private WB_Render render;
    private CameraController gcam;

    public void setup() {
        this.jtsRender = new JtsRender(this);
        this.render = new WB_Render(this);
        this.gcam = new CameraController(this);
        gcam.top();

//        loadGraph2();
        loadGraph3();
        createGraph();
        optimize2();
        this.firseSelected = selectedSides.get(0);

//        loadGraph();
//        this.passedSegs = new ArrayList<>();
//        this.selectedNodes = optimize();

//        this.select = graph.getNodeN(selectedID);
//        this.passedSegs = segmentsOnGraphByDistWithRecord(select, null, 50);
//        List<Integer> coveredNodeIDs = new ArrayList<>();
//        for (int i = 0; i < graph.getNodesNum(); i++) {
//            if (graph.getNodeN(i).covered) {
//                coveredNodeIDs.add(i);
//            }
//        }
//        System.out.println(coveredNodeIDs);

//        double[][] test = new double[][]{
//                new double[]{0, 1},
//                new double[]{1, 3},
//                new double[]{4, 6},
//                new double[]{5, 7}
//        };
//        double[][] test2 = new double[][]{
//                new double[]{0.5, 1.5},
//                new double[]{1.4, 3.3},
//                new double[]{4.5, 6.8},
//                new double[]{5.7, 7.9}
//        };
//        double[][] toMerge = new double[test.length + test2.length][];
//        System.arraycopy(test, 0, toMerge, 0, test.length);
//        System.arraycopy(test2, 0, toMerge, test.length, test2.length);
//        double[][] merge2 = ZMath.mergeDoubleArray(toMerge);
//        System.out.println(Arrays.deepToString(merge2));
    }

    private void loadGraph2() {
        IG.init();
        IG.open("E:\\AAA_Study\\0_JavaTools\\Ztools\\src\\test\\resources\\test_graph_mall.3dm");
        ICurve[] graphEdges = IG.layer("graph").curves();
        List<ZLine> edges = new ArrayList<>();
        for (ICurve c : graphEdges) {
            edges.add(new ZLine(
                    new ZPoint(c.cp(0)),
                    new ZPoint(c.cp(1))
            ));
        }
        this.traffic = ZFactory.createWB_PolyLineFromSegs(edges);
        double length = 0;
        for (int i = 0; i < traffic.getNumberSegments(); i++) {
            length += traffic.getSegment(i).getLength();
        }

        ICurve[] sideLines = IG.layer("depth").curves();
        this.originalSides = new WB_PolyLine[sideLines.length];
        for (int i = 0; i < sideLines.length; i++) {
            originalSides[i] = ZTransform.ICurveToWB_PolyLine(sideLines[i]);
        }
    }

    private void loadGraph3() {
        IG.init();
        IG.open("E:\\AAA_Study\\0_JavaTools\\Ztools\\src\\test\\resources\\test_graph_mall2.3dm");

        ICurve[] trafficCurve = IG.layer("traffic").curves();
        this.traffic = ZTransform.ICurveToWB_PolyLine(trafficCurve[0]);

        ICurve[] cutLines = IG.layer("cutline").curves();
        this.originalSides = new WB_PolyLine[cutLines.length];
        for (int i = 0; i < cutLines.length; i++) {
            originalSides[i] = ZTransform.ICurveToWB_PolyLine(cutLines[i]);
        }
    }

    private void createGraph() {
        class NodeTemp {
            private WB_Point pt;
            private double distToStart;
            private WB_PolyLine side;

            private NodeTemp(WB_Point pt, double d, WB_PolyLine side) {
                this.pt = pt;
                this.distToStart = d;
                this.side = side;
            }
        }

        // create graph for traffic
        NodeTemp[] trafficCoords = new NodeTemp[traffic.getNumberOfPoints()];
        for (int i = 0; i < traffic.getNumberOfPoints(); i++) {
            WB_Point coord = traffic.getPoint(i);
            double distToStart = 0;
            for (int j = 0; j < i; j++) {
                distToStart += traffic.getSegment(j).getLength();
            }

            trafficCoords[i] = new NodeTemp(coord, distToStart, null);
        }
        NodeTemp[] closestCoords = new NodeTemp[originalSides.length];
        for (int i = 0; i < originalSides.length; i++) {
            WB_Point closest = WB_GeometryOp.getClosestPoint2D(
                    originalSides[i].getPoint(originalSides[i].getNumberOfPoints() - 1),
                    traffic
            );
            double distToStart = ZGeoMath.distFromStart(traffic, closest);
            closestCoords[i] = new NodeTemp(closest, distToStart, originalSides[i]);
        }

        // sort and get nodes
        // create Side objects
        List<NodeTemp> pointFiltered = new ArrayList<>();
        pointFiltered.addAll(Arrays.asList(trafficCoords));
        pointFiltered.addAll(Arrays.asList(closestCoords));
        class NodeTempComparator implements Comparator<NodeTemp> {
            @Override
            public int compare(NodeTemp n1, NodeTemp n2) {
                return Double.compare(n1.distToStart, n2.distToStart);
            }
        }
        pointFiltered.sort(new NodeTempComparator());

        this.sides = new ArrayList<>();
        List<ZNode> graphNodes = new ArrayList<>();
        double currD = -1;
        for (NodeTemp temp : pointFiltered) {
            if (temp.side != null) { // if NodeTemp has side, create one object
                Side s = new Side(temp.side);
                // exclude coincident points
                double dist = temp.distToStart;
                if (dist - currD > ZGeoMath.epsilon) {
                    ZNode n = new ZNode(temp.pt);
                    graphNodes.add(n);

                    // if NodeTemp has side, record ZNode
                    s.setClosestNode(n);
                    currD = dist;
                } else {
                    // if NodeTemp has side, record the last one in the list
                    s.setClosestNode(graphNodes.get(graphNodes.size() - 1));
                }
                sides.add(s);
            } else { // if doesn't, just create ZNode
                // exclude coincident points
                double dist = temp.distToStart;
                if (dist - currD > ZGeoMath.epsilon) {
                    ZNode n = new ZNode(temp.pt);
                    graphNodes.add(n);
                    currD = dist;
                }
            }
        }
        int[][] matrix = new int[graphNodes.size() - 1][];
        for (int i = 0; i < graphNodes.size() - 1; i++) {
            matrix[i] = new int[]{i, i + 1};
        }
        this.graph = new ZGraph(graphNodes, matrix);
        double length = 0;
        for (ZEdge e : graph.getEdges()) {
            length += e.getLength();
        }
    }

    private void optimize2() {
        this.passedSegs = new ArrayList<>();
        this.selectedSides = new ArrayList<>();
        List<Integer> possibleSideIDs = ZMath.createIntegerList(0, sides.size());

        // all edge length and covered length
        double allLength = 0;
        for (ZEdge e : graph.getEdges()) {
            allLength += e.getLength();
        }
        double coveredLength = 0;

        // loop while not fully covered
        while (coveredLength < allLength) {
            System.out.println(possibleSideIDs);
            // new covered length
            double[] newCoveredLength = new double[possibleSideIDs.size()];

            System.out.println("current domain");
            for (ZEdge e : graph.getEdges()) {
                System.out.println(Arrays.deepToString(e.getCoveredDomain()));
            }
            double len2 = 0;
            for (ZEdge e : graph.getEdges()) {
                double l = e.getLength();
                double[][] domain = e.getCoveredDomain();
                for (double[] d : domain) {
                    len2 += l * (d[1] - d[0]);
                }
            }
            System.out.println("current covered length " + len2);
            // test possible side 1 by 1
            for (int i = 0; i < possibleSideIDs.size(); i++) {
                int id = possibleSideIDs.get(i);
                Side s = sides.get(id);

                // how long remain
                double evacDistRemain = 50 - s.sideLineL - s.distToClosest;
                if (evacDistRemain > 0) {
                    ZNode closest = s.closestNode;

                    // temporarily record new covered domain of passed edge
                    Map<ZEdge, double[][]> tempEdgeAndDomain = segmentsOnGraphByDist(closest, null, evacDistRemain);

                    // calculate newly covered length
                    System.out.println(">> temp domain of  " + id);
                    double len = 0;
                    for (ZEdge e : tempEdgeAndDomain.keySet()) {
                        double l = e.getLength();
                        double[][] domain = tempEdgeAndDomain.get(e);
                        System.out.println(Arrays.deepToString(domain));
                        for (double[] d : domain) {
                            len += l * (d[1] - d[0]);
                        }
                    }
                    // add not-passed edge covered length
                    for (ZEdge e : graph.getEdges()){
                        if(!tempEdgeAndDomain.containsKey(e)){
                            double l = e.getLength();
                            double[][] domain = e.getCoveredDomain();
                            for (double[] d : domain) {
                                len += l * (d[1] - d[0]);
                            }
                        }
                    }
                    newCoveredLength[i] = len;
                }
            }

            // get the most increased node
            System.out.println(Arrays.toString(newCoveredLength));
            int max = ZMath.getMaxIndex(newCoveredLength);
            System.out.println("selected id: " + possibleSideIDs.get(max));
            Side select = sides.get(possibleSideIDs.get(max));

            // update domain properties for edges and nodes by do it again
            double evacDistRemain = 50 - select.sideLineL - select.distToClosest;
            List<ZLine> result = segmentsOnGraphByDistWithRecord(select.closestNode, null, evacDistRemain);
            this.passedSegs.add(result);

            // update coveredLength
            coveredLength = newCoveredLength[max];
            System.out.println(coveredLength);
            selectedSides.add(select);

            // exclude id if the node is covered, continue loop
            possibleSideIDs.remove(max);

            System.out.println(">>> domain");
            for (ZEdge e : graph.getEdges()) {
                System.out.println(Arrays.deepToString(e.getCoveredDomain()));
            }
        }
    }

    static class Side {
        private WB_PolyLine sideLine;
        private ZPoint start;
        private double sideLineL;

        private ZNode closestNode;
        private double distToClosest;

        private Side(WB_PolyLine sideLine) {
            this.sideLine = sideLine;
            this.start = new ZPoint(sideLine.getPoint(0));
            this.sideLineL = 0;
            for (int i = 0; i < sideLine.getNumberSegments(); i++) {
                sideLineL += sideLine.getSegment(i).getLength();
            }
        }

        private void setClosestNode(ZNode closestNode) {
            this.closestNode = closestNode;
            this.distToClosest = sideLine.getPoint(sideLine.getNumberOfPoints() - 1).getDistance2D(closestNode.toWB_Point());
        }
    }

    @Deprecated
    private void loadGraph() {
        IG.init();
        IG.open("E:\\AAA_Study\\0_JavaTools\\Ztools\\src\\test\\resources\\test_graph_mall.3dm");
        ICurve[] graphEdges = IG.layer("graph2").curves();
        List<ZLine> edges = new ArrayList<>();
        for (ICurve c : graphEdges) {
            edges.add(new ZLine(
                    new ZPoint(c.cp(0)),
                    new ZPoint(c.cp(1))
            ));
        }
        this.graph = ZFactory.createZGraphFromSegments(edges);

        System.out.println("graph nodes: " + graph.getNodesNum());
        System.out.println("graph edges: " + graph.getEdgesNum());
    }

    @Deprecated
    private List<ZNode> optimize() {
        List<ZNode> selectedNodes = new ArrayList<>();
        List<Integer> possibleNodeIDs = new ArrayList<>();
        // possible node IDs: side node
        for (int i = 0; i < graph.getNodesNum(); i++) {
            if (graph.getNodeN(i).getNeighborNum() == 1) {
                possibleNodeIDs.add(i);
            }
        }

        // all edge length and covered length
        double allLength = 0;
        for (ZEdge e : graph.getEdges()) {
            allLength += e.getLength();
        }
        double coveredLength = 0;

        // loop while not fully covered
        while (coveredLength < allLength) {
            System.out.println(">>> loop");
            System.out.println("possible ids num:  " + possibleNodeIDs.size() + " >>> " + possibleNodeIDs);
            // new covered length
            double[] newCoveredLength = new double[possibleNodeIDs.size()];

            // test possible node 1 by 1
            for (int i = 0; i < possibleNodeIDs.size(); i++) {
                int id = possibleNodeIDs.get(i);
                ZNode n = graph.getNodeN(id);

                // temporarily record new covered domain of each edge
                Map<ZEdge, double[][]> tempEdgeAndDomain = segmentsOnGraphByDist(n, null, 50);

                // calculate new covered length
                double len = 0;
                for (ZEdge e : tempEdgeAndDomain.keySet()) {
                    double l = e.getLength();
                    double[][] domain = tempEdgeAndDomain.get(e);
                    for (double[] d : domain) {
                        len += l * (d[1] - d[0]);
                    }
                }
                // add not-passed edge covered length
                for (ZEdge e : graph.getEdges()){
                    if(!tempEdgeAndDomain.containsKey(e)){
                        double l = e.getLength();
                        double[][] domain = e.getCoveredDomain();
                        for (double[] d : domain) {
                            len += l * (d[1] - d[0]);
                        }
                    }
                }
                newCoveredLength[i] = len;
            }

            // get the most increased node
            System.out.println(Arrays.toString(newCoveredLength));
            int max = ZMath.getMaxIndex(newCoveredLength);
            System.out.println("selected id: " + possibleNodeIDs.get(max));
            ZNode select = graph.getNodeN(possibleNodeIDs.get(max));

            // update domain properties for edges and nodes by do it again
            List<ZLine> result = segmentsOnGraphByDistWithRecord(select, null, 50);
            this.passedSegs.add(result);

            // update coveredLength
            coveredLength = newCoveredLength[max];
            System.out.println(coveredLength);
            selectedNodes.add(select);

            // exclude id if the node is covered, continue loop
            List<Integer> coveredNodeIDs = new ArrayList<>();
            for (Integer id : possibleNodeIDs) {
                System.out.println(graph.getNodeN(id).covered);
                if (graph.getNodeN(id).covered) {
                    coveredNodeIDs.add(id);
                }
            }
            System.out.println("coveredNodeIDs in a loop: " + coveredNodeIDs);
            possibleNodeIDs.removeAll(coveredNodeIDs);
        }

        return selectedNodes;
    }

    /**
     * giving start node and distance, find the segments passed along graph edges
     *
     * @param currNode start node
     * @param dist     distance to move
     * @return java.util.List<geometry.ZLine>
     */
    public Map<ZEdge, double[][]> segmentsOnGraphByDist(final ZNode currNode, final ZNode fatherNode, final double dist) {
        Map<ZEdge, double[][]> result = new HashMap<>();
        for (int i = 0; i < currNode.getNeighborNum(); i++) {
            // exclude father node
            ZNode neighN = currNode.getNeighbor(i);
            if (neighN != fatherNode) {
                double curr_span = dist;
                ZEdge neighE = currNode.getLinkedEdge(i);
                if (neighE.getLength() >= curr_span) {
                    // record the segment along this edge
                    // record covered domain for the edge temporarily
                    double ratio = curr_span / neighE.getLength();
                    double[][] tempDomain = new double[][]{{0, 0}};
                    double[][] coveredDomain = neighE.getCoveredDomain();
                    if (neighE.isStartOrEnd(neighN) == 0) {// neighbor is start
                        tempDomain = new double[][]{
                                {1 - ratio, 1}
                        };
                        if (coveredDomain[0][0] < 0) {
                            // intialize
                        } else {
                            // already set
                            double[][] toMerge = new double[coveredDomain.length + tempDomain.length][];
                            System.arraycopy(coveredDomain, 0, toMerge, 0, coveredDomain.length);
                            System.arraycopy(tempDomain, 0, toMerge, coveredDomain.length, tempDomain.length);
                            tempDomain = ZMath.mergeDoubleArray(toMerge);
                        }
                    } else if (neighE.isStartOrEnd(neighN) == 1) {// neighbor is end
                        tempDomain = new double[][]{
                                {0, ratio}
                        };
                        if (coveredDomain[0][0] < 0) {
                            // intialize
                        } else {
                            // already set
                            double[][] toMerge = new double[coveredDomain.length + tempDomain.length][];
                            System.arraycopy(coveredDomain, 0, toMerge, 0, coveredDomain.length);
                            System.arraycopy(tempDomain, 0, toMerge, coveredDomain.length, tempDomain.length);
                            tempDomain = ZMath.mergeDoubleArray(toMerge);
                        }
                    }
                    result.put(neighE, tempDomain);
                } else {
                    result.put(neighE, new double[][]{{0, 1}});
                    if (neighN.getNeighborNum() != 1) {
                        // record the end node and stop
                        curr_span = curr_span - currNode.getLinkedEdge(i).getLength();
                        result.putAll(segmentsOnGraphByDist(neighN, currNode, curr_span));
                    }
                }
            }
        }
        return result;
    }

    /**
     * giving start node and distance, find the segments passed along graph edges
     *
     * @param currNode start node
     * @param dist     distance to move
     * @return java.util.List<geometry.ZLine>
     */
    public List<ZLine> segmentsOnGraphByDistWithRecord(final ZNode currNode, final ZNode fatherNode, final double dist) {
        currNode.covered = true;
        List<ZLine> result = new ArrayList<>();
        for (int i = 0; i < currNode.getNeighborNum(); i++) {
            // exclude father node
            ZNode neighN = currNode.getNeighbor(i);
            if (neighN != fatherNode) {
                double curr_span = dist;
                ZEdge neighE = currNode.getLinkedEdge(i);
                if (neighE.getLength() > curr_span) { // edge length >= span, cannot visit next node
                    // record covered domain for the edge
                    double ratio = curr_span / neighE.getLength();
                    if (neighE.isStartOrEnd(neighN) == 0) {// neighbor is start
                        neighE.mergeCoveredDomain(new double[][]{
                                {1 - ratio, 1}
                        });
                    } else if (neighE.isStartOrEnd(neighN) == 1) {// neighbor is end
                        neighE.mergeCoveredDomain(new double[][]{
                                {0, ratio}
                        });
                    }
                    result.add(new ZLine(currNode, currNode.add(currNode.getVecNorToNeighbor(i).scaleTo(dist))));
                } else { // edge length < span, need to visit next
                    neighN.covered = true;
                    neighE.mergeCoveredDomain(new double[][]{
                            {0, 1}
                    });
                    result.add(neighE);
                    if (neighN.getNeighborNum() != 1) {
                        // record the end node and stop
                        curr_span = curr_span - currNode.getLinkedEdge(i).getLength();
                        result.addAll(segmentsOnGraphByDistWithRecord(neighN, currNode, curr_span));
                    }
                }
            }
        }
        return result;
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        strokeWeight(1);
        stroke(0);
        for (WB_PolyLine p : originalSides) {
            render.drawPolylineEdges(p);
        }
        render.drawPolylineEdges(traffic);

        for (Side p : selectedSides) {
            ellipse(
                    p.start.xf(),
                    p.start.yf(),
                    5, 5
            );
            ellipse(
                    p.start.xf(),
                    p.start.yf(),
                    100, 100
            );
        }
        ellipse(
                firseSelected.start.xf(),
                firseSelected.start.yf(),
                10, 10
        );

        strokeWeight(4);
        stroke(0, 0, 255);
        for (ZLine l : passedSegs.get(0)) {
            l.display(this);
        }

//        pushStyle();
//
//        noFill();
//        strokeWeight(1);
//        graph.display(this);
//

//
////        fill(255, 0, 0);
////        noStroke();
////        ZRender.drawZPoint(this, select, 5);
//
//        if (selectedNodes != null) {
//            for (ZNode n : selectedNodes) {
//                ZRender.drawZPoint(this, n, 5);
//            }
//        }
//
//
//        popStyle();

//        pushStyle();
//        fill(0);
//        noStroke();
//        for (ZEdge e : graph.getEdges()) {
//            String s = e.getCoveredDomain()[0] + "," + e.getCoveredDomain()[1];
//            text(s, e.getCenter().xf(), e.getCenter().yf());
//        }
//        popStyle();
    }

//    public void mouseClicked() {
//        for (ZNode node : graph.getNodes()) {
//            if (node.distance(new ZPoint(mouseX, mouseY)) < node.rd()) {
//                select = node;
//                passedSegs = segmentsOnGraphByDistWithRecord(select, null, 50);
//            }
//        }
//    }

    public void keyPressed() {
        if (key == 'q') {
            selectedID++;
            this.select = graph.getNodeN(selectedID);
//            this.passedSegs = segmentsOnGraphByDistWithRecord(select, null, 50);
            List<Integer> coveredNodeIDs = new ArrayList<>();
            for (int i = 0; i < graph.getNodesNum(); i++) {
                if (graph.getNodeN(i).covered) {
                    coveredNodeIDs.add(i);
                }
            }
            System.out.println(coveredNodeIDs);
        }
    }
}
