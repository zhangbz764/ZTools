package math;

import geometry.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 与自定义图结构相关的计算工具
 * <p>
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/3/30
 * @time 11:03
 * <p>
 * 找到graph上某节点开始点沿边移动一定距离后的若干个点，返回结果点/沿途的所有线段/沿途节点
 * 给定步长，将graph每条edge按照步长剖分，返回全部剖分点
 * 给定起点，递归遍历出graph上从起点出发的所有链（返回ZEdge或ZNode）
 * 找到一个无环图上的最长链
 */
public final class ZGraphMath {

    /**
     * 找到graph上某节点开始点沿边移动一定距离后的若干个点，返回结果点
     *
     * @param startNode start node
     * @param dist      distance to move
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> pointsOnGraphByDist(final ZNode startNode, final ZNode lastNode, final double dist) {
        List<ZPoint> result = new ArrayList<>();
        for (int i = 0; i < startNode.getNeighborNum(); i++) {
            if (startNode.getNeighbor(i) != lastNode) { // 排除父节点
                double curr_span = dist;
                if (startNode.getLinkedEdge(i).getLength() >= curr_span) {
                    // 若edge长度大于当前步长，在这条edge上记录终点
                    result.add(startNode.add(startNode.getVecUnitToNeighbor(i).scaleTo(dist)));
                } else {
                    if (startNode.getNeighbor(i).getNeighborNum() != 1) {
                        result.add(startNode.getNeighbor(i));
                        // 若已检索到端头节点，把端头加入结果，并停止
                        curr_span = curr_span - startNode.getLinkedEdge(i).getLength();
                        result.addAll(pointsOnGraphByDist(startNode.getNeighbor(i), startNode, curr_span));
                    }
                }
            }
        }
        return result;
    }

    /**
    * 找到graph上某节点开始点沿边移动一定距离后，沿途经过的所有节点（不包括起点）
    *
    * @param currNode
    * @param fatherNode
    * @param dist
    * @return java.util.List<geometry.ZNode>
    */
    public static List<ZNode> nodesOnGraphByDist(final ZNode currNode, final ZNode fatherNode, final double dist) {
        List<ZNode> result = new ArrayList<>();
        for (int i = 0; i < currNode.getNeighborNum(); i++) {
            // 排除父节点
            if (currNode.getNeighbor(i) != fatherNode) {
                double curr_span = dist;
                if (currNode.getLinkedEdge(i).getLength() <= curr_span) {
                    result.add(currNode.getNeighbor(i));
                    if (currNode.getNeighbor(i).getNeighborNum() != 1) {
                        // 若已检索到端头节点，则停止
                        curr_span = curr_span - currNode.getLinkedEdge(i).getLength();
                        result.addAll(nodesOnGraphByDist(currNode.getNeighbor(i), currNode, curr_span));
                    }
                }
            }
        }
        return result;
    }

    /**
     * 找到graph上某节点开始点沿边移动一定距离后，沿途的所有线段
     *
     * @param currNode start node
     * @param dist     distance to move
     * @return java.util.List<geometry.ZLine>
     */
    public static List<ZLine> segmentsOnGraphByDist(final ZNode currNode, final ZNode fatherNode, final double dist) {
        List<ZLine> result = new ArrayList<>();
        for (int i = 0; i < currNode.getNeighborNum(); i++) {
            // 排除父节点
            if (currNode.getNeighbor(i) != fatherNode) {
                double curr_span = dist;
                if (currNode.getLinkedEdge(i).getLength() >= curr_span) {
                    // 若edge长度大于当前步长，记录该步长的线段并停止
                    result.add(new ZLine(currNode, currNode.add(currNode.getVecUnitToNeighbor(i).scaleTo(dist))));
                } else {
                    result.add(currNode.getLinkedEdge(i));
                    if (currNode.getNeighbor(i).getNeighborNum() != 1) {
                        // 若已检索到端头节点，则停止
                        curr_span = curr_span - currNode.getLinkedEdge(i).getLength();
                        result.addAll(segmentsOnGraphByDist(currNode.getNeighbor(i), currNode, curr_span));
                    }
                }
            }
        }
        return result;
    }

    // FIXME: 2021/4/9 graph剖分
    public static List<ZPoint> splitGraphEdgeByStep(final ZGraph graph, final ZNode startNode, final ZNode fatherNode, final double step, final double currSpan) {
        List<ZPoint> result = new ArrayList<>();
        if (graph.contains(startNode)) {
            ZPoint p1 = startNode;
            double curr_span = currSpan;
            double curr_dist;

            result.add(startNode);
            for (ZNode nei : startNode.getNeighbors()) {
                if (nei != fatherNode) {
                    ZPoint p2 = nei;
                    curr_dist = p1.distance(p2);
                    while (curr_dist >= curr_span) {
                        ZPoint p = p1.add(p2.sub(p1).unit().scaleTo(curr_span));
                        result.add(p);
                        p1 = p;
                        curr_span = step;
                        curr_dist = p1.distance(p2);
                    }
                    if (nei.isEnd()) {
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
                    p1 = startNode;
                }
            }
        } else {
            System.out.println("start node isn't in the graph, return empty result");
        }
        return result;
    }

    /**
     * 将graph每条edge按照步长剖分，返回全部剖分点
     *
     * @param graph input ZGraph
     * @param step  step for each edge to split
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitGraphEachEdgeByStep(final ZGraph graph, final double step) {
        List<ZPoint> result = new ArrayList<>();
        for (int i = 0; i < graph.getEdgesNum(); i++) {
            List<ZPoint> pts = graph.getEdgeN(i).splitByStep(step);
            pts.remove(0);
            pts.remove(pts.size() - 1);
            result.addAll(pts);
        }
        result.addAll(graph.getNodes());
        return result;
    }

    /**
     * 给定起点，递归遍历出graph上从起点出发的所有链 (返回ZEdge)
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
     * 给定起点，递归遍历出graph上从起点出发的所有链 (返回ZNode)
     *
     * @param currNode    current node
     * @param fatherNode  father node of current point (null for the start)
     * @param fatherChain father chain ahead (new ArrayList including currNode for the start)
     * @return java.util.List<java.util.List < geometry.ZNode>>
     */
    public static List<List<ZNode>> getAllChainNodeFromNode(final ZNode currNode, final ZNode fatherNode, List<ZNode> fatherChain) {
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
     * 找到一个无环图上的最长链
     *
     * @param graph input non-loop graph
     * @return java.util.List<geometry.ZEdge>
     */
    public static List<ZEdge> longestChain(final ZGraph graph) {
        // 找到起始点的最远点
        ZNode start = graph.getNodeN(0);
        List<List<ZNode>> allChainsNode = getAllChainNodeFromNode(start, null, new ArrayList<ZNode>() {{
            add(start);
        }});
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

        // 找到最远点为起点的最长链，即为graph的最长链
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
