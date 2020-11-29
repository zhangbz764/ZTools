package geometry;

import java.util.ArrayList;
import java.util.List;


/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/11
 * @time 17:55
 * @description extends ZLine 代表graph中的edge
 */
public class ZEdge extends ZLine {
    private ZNode start;
    private ZNode end;

    private List<ZEdge> neighborEdgesAll;
    private List<ZEdge> neighborEdgesStart;
    private List<ZEdge> neighborEdgesEnd;

    /* ------------- constructor ------------- */

    public ZEdge(ZNode start, ZNode end) {
        super(start, end);
        this.start = start;
        this.end = end;
    }

    /* ------------- set & get ------------- */

    public void set(ZNode start, ZNode end) {
        this.start = start;
        this.end = end;
    }

    public ZNode[] getNodes() {
        return new ZNode[]{start, end};
    }

    public ZNode getStart() {
        return start;
    }

    public ZNode getEnd() {
        return end;
    }

    public void setNeighborEdges() {
        if (start.getLinkedEdges().size() > 1) {
            this.neighborEdgesStart = new ArrayList<>();
            neighborEdgesStart.addAll(start.getLinkedEdges());
            neighborEdgesStart.remove(this);
        }
        if (end.getLinkedEdges().size() > 1) {
            this.neighborEdgesEnd = new ArrayList<>();
            neighborEdgesEnd.addAll(end.getLinkedEdges());
            neighborEdgesEnd.remove(this);
        }
        this.neighborEdgesAll = neighborEdgesStart;
        neighborEdgesAll.addAll(neighborEdgesEnd);
    }

    public List<ZEdge> getNeighborEdgesAll() {
        return neighborEdgesAll;
    }

    public List<ZEdge> getNeighborEdgesStart() {
        return neighborEdgesStart;
    }

    public List<ZEdge> getNeighborEdgesEnd() {
        return neighborEdgesEnd;
    }

    /* ------------- geometry method ------------- */

    @Override
    public ZEdge reverse() {
        return new ZEdge(this.end, this.start);
    }
}
