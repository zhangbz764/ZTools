package math;

import basicGeometry.*;

import java.util.ArrayList;
import java.util.List;

/**
 * graph tools
 * <p>
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/3/30
 * @time 11:03
 * <p>
 * giving start node and distance, find the destinations / nodes passed / segments passed / along graph edges
 * giving step and start node, split graph edges
 * giving step and start node, split graph edges by each
 * find all chains from the start node (return ZEdge of ZNode)
 * find the longest chain in a non-loop graph
 */
public final class ZGraphMath {

    /**
     * giving start node and distance, find the destinations along graph edges
     *
     * @param startNode start node
     * @param dist      distance to move
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> pointsOnGraphByDist(final ZNode startNode, final ZNode lastNode, final double dist) {
        List<ZPoint> result = new ArrayList<>();
        for (int i = 0; i < startNode.getNeighborNum(); i++) {
            if (startNode.getNeighbor(i) != lastNode) { // exclude father node
                double curr_span = dist;
                if (startNode.getLinkedEdge(i).getLength() >= curr_span) {
                    // record destination on this edge
                    result.add(startNode.add(startNode.getVecNorToNeighbor(i).scaleTo(dist)));
                } else {
                    if (startNode.getNeighbor(i).getNeighborNum() != 1) {
                        result.add(startNode.getNeighbor(i));
                        // record the end node and stop
                        curr_span = curr_span - startNode.getLinkedEdge(i).getLength();
                        result.addAll(pointsOnGraphByDist(startNode.getNeighbor(i), startNode, curr_span));
                    }
                }
            }
        }
        return result;
    }

    /**
     * giving start node and distance, find the nodes passed along graph edges
     * (start node not included)
     *
     * @param currNode   current node
     * @param fatherNode node of last recursion (null to initialize)
     * @param dist       move distance
     * @return java.util.List<geometry.ZNode>
     */
    public static List<ZNode> nodesOnGraphByDist(final ZNode currNode, final ZNode fatherNode, final double dist) {
        List<ZNode> result = new ArrayList<>();
        for (int i = 0; i < currNode.getNeighborNum(); i++) {
            // exclude father node
            if (currNode.getNeighbor(i) != fatherNode) {
                double curr_span = dist;
                if (currNode.getLinkedEdge(i).getLength() <= curr_span) {
                    result.add(currNode.getNeighbor(i));
                    if (currNode.getNeighbor(i).getNeighborNum() != 1) {
                        // record the end node and stop
                        curr_span = curr_span - currNode.getLinkedEdge(i).getLength();
                        result.addAll(nodesOnGraphByDist(currNode.getNeighbor(i), currNode, curr_span));
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
    public static List<ZLine> segmentsOnGraphByDist(final ZNode currNode, final ZNode fatherNode, final double dist) {
        List<ZLine> result = new ArrayList<>();
        for (int i = 0; i < currNode.getNeighborNum(); i++) {
            // exclude father node
            if (currNode.getNeighbor(i) != fatherNode) {
                double curr_span = dist;
                if (currNode.getLinkedEdge(i).getLength() >= curr_span) {
                    // record the segment along this edge
                    result.add(new ZLine(currNode, currNode.add(currNode.getVecNorToNeighbor(i).scaleTo(dist))));
                } else {
                    result.add(currNode.getLinkedEdge(i));
                    if (currNode.getNeighbor(i).getNeighborNum() != 1) {
                        // record the end node and stop
                        curr_span = curr_span - currNode.getLinkedEdge(i).getLength();
                        result.addAll(segmentsOnGraphByDist(currNode.getNeighbor(i), currNode, curr_span));
                    }
                }
            }
        }
        return result;
    }

    /**
     * giving step and start node, split graph edges
     *
     * @param graph      input ZGraph
     * @param startNode  node to start
     * @param fatherNode node of last recursion (null to initialize)
     * @param step       divide step
     * @param currSpan   current span (= step to initialize)
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitGraphEdgeByStep(
            final ZGraph graph,
            final ZNode startNode,
            final ZNode fatherNode,
            final double step,
            final double currSpan
    ) {
        List<ZPoint> result = new ArrayList<>();
        if (graph.contains(startNode)) {
            ZPoint p1 = startNode;
            double curr_span = currSpan;
            double curr_dist;

            // determine if it's the first time to loop
            if (step == currSpan && fatherNode == null) {
                result.add(startNode);
            }

            for (ZNode nei : startNode.getNeighbors()) {
                if (nei != fatherNode) {
                    curr_dist = p1.distance(nei);

                    // end node of this edge will be record if exact divided
                    while (curr_dist - curr_span >= 0) {
                        ZPoint p = p1.add(nei.sub(p1).normalize().scaleTo(curr_span));
                        result.add(p);
                        p1 = p;
                        curr_span = step;
                        curr_dist = p1.distance(nei);
                    }

                    // determine if the neighbor is the end and not exact divided
                    if (nei.isEnd() && curr_dist != 0) {
                        result.add(nei);
                    } else {
                        curr_span = curr_span - curr_dist;
                        List<ZPoint> nextResult = splitGraphEdgeByStep(graph, nei, startNode, step, curr_span);
                        for (ZPoint p : nextResult) {
                            if (!result.contains(p)) {
                                result.add(p);
                            }
                        }
                    }
                    curr_span = currSpan;
                    p1 = startNode;
                }
            }
        } else {
            System.out.println("start node isn't in the graph, return empty result");
        }
        return result;
    }

    /**
     * giving step and start node, split graph edges by each
     *
     * @param graph input ZGraph
     * @param step  step for each edge to split
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitGraphEachEdgeByStep(final ZGraph graph, final double step) {
        List<ZPoint> result = new ArrayList<>();
        for (int i = 0; i < graph.getEdgesNum(); i++) {
            List<ZPoint> pts = graph.getEdgeN(i).divideByStep(step);
            pts.remove(0);
            pts.remove(pts.size() - 1);
            result.addAll(pts);
        }
        result.addAll(graph.getNodes());
        return result;
    }

    /**
     * find all chains from the start node (return ZEdge)
     *
     * @param currNode    current node
     * @param fatherNode  father node of current point (null for the start)
     * @param fatherChain father chain ahead (new ArrayList for the start)
     * @return java.util.List<java.util.List < geometry.ZEdge>>
     */
    public static List<List<ZEdge>> getAllChainEdgeFromNode(final ZNode currNode, final ZNode fatherNode, List<ZEdge> fatherChain) {
        List<List<ZEdge>> result = new ArrayList<>();
        List<ZEdge> currChain = new ArrayList<>(fatherChain);

        for (int i = 0; i < currNode.getNeighborNum(); i++) {
            ZNode neighbor = currNode.getNeighbor(i);
            if (neighbor != fatherNode) {
                if (neighbor.getNeighborNum() == 1) {
                    currChain.add(currNode.getLinkedEdge(i));
                    result.add(currChain);
                    currChain = new ArrayList<>(fatherChain);
                } else {
                    currChain.add(currNode.getLinkedEdge(i));
                    result.addAll(getAllChainEdgeFromNode(neighbor, currNode, currChain));
                    currChain = new ArrayList<>(fatherChain);
                }
            }
        }
        return result;
    }

    /**
     * find all chains from the start node (return ZNode)
     *
     * @param currNode    current node
     * @param fatherNode  father node of current point (null for the start)
     * @param fatherChain father chain ahead (new ArrayList including currNode for the start)
     * @return java.util.List<java.util.List < geometry.ZNode>>
     */
    public static List<List<ZNode>> getAllChainNodeFromNode(final ZNode currNode, final ZNode fatherNode, final List<ZNode> fatherChain) {
        List<List<ZNode>> result = new ArrayList<>();
        List<ZNode> currChain = new ArrayList<>(fatherChain);

        for (int i = 0; i < currNode.getNeighborNum(); i++) {
            ZNode neighbor = currNode.getNeighbor(i);
            if (neighbor != fatherNode) {
                if (neighbor.isEnd()) {
                    currChain.add(neighbor);
                    result.add(currChain);
                    currChain = new ArrayList<>(fatherChain);
                } else {
                    currChain.add(neighbor);
                    result.addAll(getAllChainNodeFromNode(neighbor, currNode, currChain));
                    currChain = new ArrayList<>(fatherChain);
                }
            }
        }
        return result;
    }

    /**
     * find the longest chain in a non-loop graph
     *
     * @param graph input non-loop graph
     * @return java.util.List<geometry.ZEdge>
     */
    public static List<ZEdge> longestChain(final ZGraph graph) {
        // find the farthest node of a random start node
        ZNode start = graph.getNodeN(0);
        List<ZNode> fatherChain = new ArrayList<>();
        fatherChain.add(start);
        List<List<ZNode>> allChainsNode = getAllChainNodeFromNode(start, null, fatherChain);
        double[] lengths1 = new double[allChainsNode.size()];
        for (int i = 0; i < allChainsNode.size(); i++) {
            double currLength = 0;
            for (int j = 0; j < allChainsNode.get(i).size() - 1; j++) {
                double length = allChainsNode.get(i).get(j).distance(allChainsNode.get(i).get(j + 1));
                currLength += length;
            }
            lengths1[i] = currLength;
        }
        int farthestIndex = ZMath.getMaxIndex(lengths1);
        List<ZNode> farthestChain = allChainsNode.get(farthestIndex);
        ZNode farthest = farthestChain.get(farthestChain.size() - 1);

        // find the longest chain from the farthest node
        List<List<ZEdge>> allChainsEdge = getAllChainEdgeFromNode(farthest, null, new ArrayList<ZEdge>());
        double[] lengths2 = new double[allChainsEdge.size()];
        for (int i = 0; i < allChainsEdge.size(); i++) {
            double currLength = 0;
            for (ZEdge e : allChainsEdge.get(i)) {
                currLength += e.getLength();
            }
            lengths2[i] = currLength;
        }
        int longestIndex = ZMath.getMaxIndex(lengths2);

        return allChainsEdge.get(longestIndex);
    }
}
