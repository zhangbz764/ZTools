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

    private List<ZNode> boundaryNodes;

    /* ------------- constructor ------------- */

    public ZGraph() {

    }

    /**
     * @param edges input edges
     */
    public ZGraph(List<ZEdge> edges) {
        setEdges(edges);
        this.nodes = new ArrayList<>();
        for (ZEdge edge : edges) {
            for (ZNode edgeNode : edge.getNodes()) {
                if (!nodes.contains(edgeNode)) {
                    nodes.add(edgeNode);
                }
            }
        }
    }

    /**
     * @param nodes       input nodes
     * @param connections connection of the nodes. e.g. {{0,1},{1,2}}
     */
    public ZGraph(List<ZNode> nodes, int[][] connections) {
        setNodes(nodes);
        for (ZNode node : nodes) {
            node.setRelationReady();
        }
        this.edges = new ArrayList<>();
        for (int[] connection : connections) {
            nodes.get(connection[0]).setNeighbor(nodes.get(connection[1]));
            nodes.get(connection[1]).setNeighbor(nodes.get(connection[0]));
            ZEdge edge = new ZEdge(nodes.get(connection[0]), nodes.get(connection[1]));
            edges.add(edge);
            nodes.get(connection[0]).setLinkedEdges(edge);
            nodes.get(connection[1]).setLinkedEdges(edge);
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
            if (node.geiNeighborNum() == 1) {
                boundaryNodes.add(node);
            }
        }
    }

    /* ------------- setter & getter ------------- */

    public void setEdges(List<ZEdge> edges) {
        this.edges = edges;
    }

    public void setNodes(List<ZNode> nodes) {
        this.nodes = nodes;
    }

    public List<ZEdge> getEdges() {
        return edges;
    }

    public List<ZNode> getNodes() {
        return nodes;
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
            node.displayAsPoint(app);
        }
    }
}
