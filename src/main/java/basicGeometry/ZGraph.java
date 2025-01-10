package basicGeometry;

import math.ZGeoMath;
import processing.core.PApplet;
import render.ZRender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * custom graph class
 * including nodes, edges, and reference relations
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/29
 * @time 21:06
 */
public class ZGraph {
    private List<ZNode> nodes;
    private List<ZEdge> edges;
    private int[][] matrix;

    private List<ZNode> boundaryNodes;

    private boolean isLoop;
    private boolean isPath;

    /* ------------- constructor ------------- */

    public ZGraph() {

    }

    /**
     * @param nodes input nodes
     * @param edges input edges
     */
    public ZGraph(List<? extends ZNode> nodes, List<ZEdge> edges) {
        setNodes(nodes);
        setEdges(edges);
        this.matrix = new int[edges.size()][];
        for (int i = 0; i < edges.size(); i++) {
            ZNode start = edges.get(i).getStart();
            ZNode end = edges.get(i).getEnd();
            int startIndex = nodes.indexOf(start);
            int endIndex = nodes.indexOf(end);
            if (startIndex != -1 && endIndex != -1) {
                matrix[i] = new int[]{startIndex, endIndex};
            }
        }
    }

    /**
     * @param nodes  input nodes
     * @param matrix connection of the nodes. e.g. {{0,1},{1,2}}
     */
    public ZGraph(List<ZNode> nodes, int[][] matrix) {
        setNodes(nodes);
        setMatrix(matrix);
        for (ZNode node : nodes) {
            node.setRelationReady();
        }
        this.edges = new ArrayList<>();
        for (int[] connection : matrix) {
            nodes.get(connection[0]).addNeighbor(nodes.get(connection[1]));
            nodes.get(connection[1]).addNeighbor(nodes.get(connection[0]));
            ZEdge edge = new ZEdge(nodes.get(connection[0]), nodes.get(connection[1]));
            edges.add(edge);
            nodes.get(connection[0]).addLinkedEdge(edge);
            nodes.get(connection[1]).addLinkedEdge(edge);
        }
    }

    /* ------------- member function ------------- */

    /**
     * find boundary nodes in the graph
     */
    private void findBoundaryNodes() {
        this.boundaryNodes = new ArrayList<>();
        for (ZNode node : nodes) {
            if (node.getNeighborNum() == 1) {
                boundaryNodes.add(node);
            }
        }
    }

    public boolean contains(ZNode node) {
        return this.nodes.contains(node);
    }

    public boolean contains(ZEdge edge) {
        return this.edges.contains(edge);
    }

    /**
     * add a node to the graph by giving a point and an edge to insert
     *
     * @param point input point
     * @param edge  target edge
     */
    public void addNodeIntoEdge(ZPoint point, ZEdge edge) {
        ZNode newNode;
        if (point instanceof ZNode) {
            newNode = (ZNode) point;
        } else {
            newNode = new ZNode(point.xd(), point.yd(), point.zd());
        }
        ZNode s = edge.getStart();
        ZNode e = edge.getEnd();

        newNode.setRelationReady();

        newNode.addNeighbor(s);
        s.removeNeighbor(e);
        s.addNeighbor(newNode);
        ZEdge newEdge1 = new ZEdge(s, newNode);
        s.addLinkedEdge(newEdge1);
        newNode.addLinkedEdge(newEdge1);

        newNode.addNeighbor(e);
        e.removeNeighbor(s);
        e.addNeighbor(newNode);
        ZEdge newEdge2 = new ZEdge(newNode, e);
        e.addLinkedEdge(newEdge2);
        newNode.addLinkedEdge(newEdge2);

        this.nodes.add(newNode);
        this.edges.add(newEdge1);
        this.edges.add(newEdge2);
        this.edges.remove(edge);
    }

    /**
     * add a node to the graph by finding the closest edge to insert
     *
     * @param point input point
     */
    public void addNodeByDist(ZPoint point) {
        ZEdge targetEdge = null;
        for (ZEdge edge : getEdges()) {
            if (ZGeoMath.pointOnSegment(point, edge)) {
                // on edge
                targetEdge = edge;
                break;
            }
        }
        if (targetEdge == null) {
            // not on edge
            int closest = ZGeoMath.closestSegment(point, this.edges);
            targetEdge = edges.get(closest);
        }
        addNodeIntoEdge(point, targetEdge);
    }

    /**
     * check if the graph has a loop
     *
     * @return boolean
     */
    public boolean checkLoop() {
        List<ZNode> visited = new ArrayList<>();
        this.isLoop = checkLoopCore(null, nodes.get(0), visited);
        return isLoop;
    }

    private boolean checkLoopCore(ZNode fatherNode, ZNode currNode, List<ZNode> visited) {
        for (int i = 0; i < currNode.getNeighborNum(); i++) {
            ZNode neighbor = currNode.getNeighbor(i);
            if (neighbor != fatherNode) {
                if (visited.contains(neighbor)) {
                    return true;
                } else {
                    visited.add(neighbor);
                    checkLoopCore(currNode, neighbor, visited);
                }
            }
        }
        return false;
    }

    /**
     * check if the graph is a single path
     *
     * @return boolean
     */
    public boolean checkPath() {
        isPath = true;
        for (ZNode n : nodes) {
            if (n.getNeighborNum() > 2) {
                isPath = false;
                break;
            }
        }
        return isPath;
    }

    /**
    * get the closest node to a given point
    *
    * @param x point x
    * @param y point y
    * @return basicGeometry.ZNode
    */
    public ZNode closestNode(double x, double y) {
        ZNode node = null;
        double minD = Double.MAX_VALUE;
        for (ZNode n : nodes) {
            double dist = n.distanceSq(x, y, 0);
            if (dist < minD) {
                minD = dist;
                node = n;
            }
        }
        return node;
    }

    /* ------------- setter & getter ------------- */

    public void setEdges(List<ZEdge> edges) {
        this.edges = edges;
    }

    public void setNodes(List<? extends ZNode> nodes) {
        this.nodes = new ArrayList<>();
        this.nodes.addAll(nodes);
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public List<ZEdge> getEdges() {
        return edges;
    }

    public List<ZNode> getNodes() {
        return nodes;
    }

    public ZNode getNodeN(int index) {
        return nodes.get(index);
    }

    public ZEdge getEdgeN(int index) {
        return edges.get(index);
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public int getNodesNum() {
        return nodes.size();
    }

    public int getEdgesNum() {
        return edges.size();
    }

    public List<ZNode> getBoundaryNodes() {
        if (boundaryNodes == null) {
            findBoundaryNodes();
        }
        return boundaryNodes;
    }

    public void addNode(ZNode node, List<ZNode> neighbors) {
        assert new HashSet<>(nodes).containsAll(neighbors) : "input neighbors are not in this graph";
        nodes.add(node);
        for (ZNode neighbor : neighbors) {
            edges.add(new ZEdge(node, neighbor));
        }
    }

    public boolean isLoop() {
        return isLoop;
    }

    public boolean isPath() {
        return isPath;
    }

    /* ------------- draw ------------- */

    public void display(PApplet app) {
        for (ZEdge edge : edges) {
            edge.display(app);
        }
        for (ZNode node : nodes) {
            ZRender.drawZPoint2D(app, node, 3);
        }
    }
}
