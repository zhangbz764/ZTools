package geometry;

import java.util.ArrayList;
import java.util.List;


/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/11
 * @time 17:55
 * @description
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
        if (start.getLinkedEdge().size() > 1) {
            this.neighborEdgesStart = new ArrayList<>();
            neighborEdgesStart.addAll(start.getLinkedEdge());
            neighborEdgesStart.remove(this);
        }
        if (end.getLinkedEdge().size() > 1) {
            this.neighborEdgesEnd = new ArrayList<>();
            neighborEdgesEnd.addAll(end.getLinkedEdge());
            neighborEdgesEnd.remove(this);
        }
        this.neighborEdgesAll = neighborEdgesStart;
        neighborEdgesAll.addAll(neighborEdgesEnd);
    }
}
