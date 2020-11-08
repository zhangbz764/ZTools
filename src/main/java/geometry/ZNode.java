package geometry;

import igeo.IPoint;
import org.locationtech.jts.geom.Coordinate;
import processing.core.PApplet;
import wblut.geom.WB_Coord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/23
 * @time 8:53
 * @description extends ZPoint
 */
public class ZNode extends ZPoint {
    private List<ZNode> neighbors;
    private List<ZEdge> linkedEdge;

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

    /* ------------- set & get relations ------------- */

    public void setRelationReady() {
        this.linkedEdge = new ArrayList<>();
        this.neighbors = new ArrayList<>();
    }

    public void setNeighbor(ZNode neighbor) {  // set one node
        this.neighbors.add(neighbor);
    }

    public void setNeighbors(List<ZNode> neighbors) {  // set all nodes
        this.neighbors = neighbors;
    }

    public void setNeighborFromEdge(ZEdge link) { // set one node from a linked edge
        if (linkedEdge.contains(link)) {
            if (this == link.getStart()) {
                this.setNeighbor(link.getEnd());
            } else {
                this.setNeighbor(link.getStart());
            }
        }
    }

    public void setLinkedEdge(ZEdge link) {  // set one edge
        this.linkedEdge.add(link);
    }

    public void setLinkedEdges(List<ZEdge> links) {  // set all edges
        this.linkedEdge = links;
    }

    public List<ZNode> getNeighbor() {
        return this.neighbors;
    }

    public int geiNeighborNum() {
        return this.neighbors.size();
    }

    public List<ZEdge> getLinkedEdge() {
        return this.linkedEdge;
    }

    public int getLinkedEdgeNum() {
        return this.linkedEdge.size();
    }


    /* ------------- draw ------------- */

    /**
     * @return void
     * @description draw neighbor nodes if exists
     */
    public void displayNeighbor(PApplet app) {
        if (this.neighbors != null) {
            app.pushStyle();
            app.noFill();
            app.strokeWeight(3);
            app.stroke(0, 0, 0);
            for (ZPoint n : neighbors) {
                n.displayAsPoint(app);
            }
            app.popStyle();
        }
    }
}
