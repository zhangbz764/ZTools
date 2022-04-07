package basicGeometry;

import math.ZGeoMath;
import math.ZMath;

import java.util.ArrayList;
import java.util.List;


/**
 * represent the edge in a graph
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/11
 * @time 17:55
 */
public class ZEdge extends ZLine {
    private ZNode start;
    private ZNode end;

    private List<ZEdge> neighborEdgesAll;
    private List<ZEdge> neighborEdgesStart;
    private List<ZEdge> neighborEdgesEnd;

    private double[][] coveredDomain = new double[][]{
            new double[]{-1, -1}
    };

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

    public void setCoveredDomain(double[][] coveredDomain) {
        this.coveredDomain = coveredDomain;
    }

    public void mergeCoveredDomain(double[][] domain) {
        if (this.coveredDomain[0][0] < 0) {
            // intialize
            coveredDomain = domain;
        } else {
            // already set
            double[][] toMerge = new double[coveredDomain.length + domain.length][];
            System.arraycopy(coveredDomain, 0, toMerge, 0, coveredDomain.length);
            System.arraycopy(domain, 0, toMerge, coveredDomain.length, domain.length);
            coveredDomain = ZMath.mergeDoubleArray(toMerge);
        }
    }

    public double[][] getCoveredDomain() {
        return coveredDomain;
    }

    public int isStartOrEnd(ZNode node) {
        if (node == start) {
            return 0;
        } else if (node == end) {
            return 1;
        } else {
            return -1;
        }
    }

    /* ------------- geometry method ------------- */

    @Override
    public ZEdge reverse() {
        return new ZEdge(this.end, this.start);
    }
}
