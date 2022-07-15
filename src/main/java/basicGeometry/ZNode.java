package basicGeometry;

import igeo.IPoint;
import org.locationtech.jts.geom.Coordinate;
import processing.core.PApplet;
import wblut.geom.WB_Coord;

import java.util.ArrayList;
import java.util.List;

/**
 * represent the node in a graph
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/23
 * @time 8:53
 */
public class ZNode extends ZPoint {
    private List<ZNode> neighbors;
    private List<ZPoint> vecNorToNeighbors;
    private List<ZEdge> linkedEdges;

    public boolean covered = false;

    /* ------------- constructor ------------- */

    public ZNode() {

    }

    public ZNode(double x, double y, double z) {
        super(x, y, z);
    }

    public ZNode(double x, double y) {
        super(x, y);
    }

    public ZNode(Coordinate c) {
        super(c);
    }

    public ZNode(WB_Coord c) {
        super(c);
    }

    public ZNode(IPoint p) {
        super(p);
    }

    public void setRelationReady() {
        this.linkedEdges = new ArrayList<>();
        this.neighbors = new ArrayList<>();
        this.vecNorToNeighbors = new ArrayList<>();
    }

    /* ------------- set & get relations ------------- */

    public void addNeighbor(ZNode neighbor) {  // set one node
        this.neighbors.add(neighbor);
        this.vecNorToNeighbors.add(neighbor.sub(this).normalize());
    }

    public void removeNeighbor(ZNode neighbor) { // remove one node
        ZEdge edge = null;
        for (ZEdge e : linkedEdges) {
            if (e.getStart() == neighbor || e.getEnd() == neighbor) {
                edge = e;
            }
        }
        this.linkedEdges.remove(edge);
        this.neighbors.remove(neighbor);
    }

    public void addLinkedEdge(ZEdge link) {  // set one edge
        this.linkedEdges.add(link);
    }

    public void setLinkedEdges(List<ZEdge> links) {  // set all edges
        this.linkedEdges = links;
    }

    public List<ZNode> getNeighbors() {
        return this.neighbors;
    }

    public ZNode getNeighbor(int i) {
        return this.neighbors.get(i);
    }

    public int getNeighborNum() {
        return this.neighbors.size();
    }

    public List<ZPoint> getVecNorToNeighbors() {
        return this.vecNorToNeighbors;
    }

    public ZPoint getVecNorToNeighbor(int i) {
        return this.vecNorToNeighbors.get(i);
    }

    public List<ZEdge> getLinkedEdges() {
        return this.linkedEdges;
    }

    public ZEdge getLinkedEdge(int i) {
        return this.linkedEdges.get(i);
    }

    public int getLinkedEdgeNum() {
        return this.linkedEdges.size();
    }

    public boolean isEnd() {
        if (this.neighbors.size() == 1) {
            return true;
        } else if (this.neighbors.size() > 1) {
            return false;
        } else {
            throw new NullPointerException("no neighbors");
        }
    }
}
