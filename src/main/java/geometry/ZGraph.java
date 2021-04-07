package geometry;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * 图数据结构，包含若干节点ZNode，连接边ZEdge，记录相互引用关系
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

    /* ------------- constructor ------------- */

    public ZGraph() {

    }

    /**
     * @param nodes input nodes
     * @param edges input edges
     */
    public ZGraph(List<ZNode> nodes, List<ZEdge> edges) {
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
     * 找出graph边界节点，即仅有单个连接的
     *
     * @return void
     */
    private void findBoundaryNodes() {
        this.boundaryNodes = new ArrayList<>();
        for (ZNode node : nodes) {
            if (node.getNeighborNum() == 1) {
                boundaryNodes.add(node);
            }
        }
    }

    // TODO: 2021/4/1 check loop in a graph 
    public boolean checkLoop() {
        ZNode startNode = nodes.get(0);
        for (int i = 0; i < startNode.getNeighborNum(); i++) {

        }
        return false;
    }

    /* ------------- setter & getter ------------- */

    public void setEdges(List<ZEdge> edges) {
        this.edges = edges;
    }

    public void setNodes(List<ZNode> nodes) {
        this.nodes = nodes;
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

    public ZNode getNodeN(int index){
        return nodes.get(index);
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
        assert nodes.containsAll(neighbors) : "input neighbors are not in this graph";
        nodes.add(node);
        for (ZNode neighbor : neighbors) {
            edges.add(new ZEdge(node, neighbor));
        }
    }

    /* ------------- draw ------------- */

    public void display(PApplet app) {
        for (ZEdge edge : edges) {
            edge.display(app);
        }
        for (ZNode node : nodes) {
            node.displayAsPoint(app,3);
        }
    }
}
