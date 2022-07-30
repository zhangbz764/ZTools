package advancedGeometry;

import wblut.geom.WB_AABB;
import wblut.geom.WB_AABBTree;
import wblut.hemesh.HE_Mesh;

import java.util.ArrayList;
import java.util.List;

/**
 * get AABBTree from a mesh
 * modified after HE_Mesh
 *
 * @author ZHANG Baizhou zhangbz
 * @project Ztools
 * @date 2022/7/30
 * @time 16:04
 */
public class ZAABBTree {
    private final WB_AABBTree originalAABBTree;
    private List<List<WB_AABB>> allAABBs;
    private List<WB_AABB> leafAABBs;

    /* ------------- constructor ------------- */

    public ZAABBTree(HE_Mesh mesh, int mnof) {
        this.originalAABBTree = new WB_AABBTree(mesh, mnof);
        System.out.println("AABBTree depth: " + originalAABBTree.getDepth());

        extract();
    }

    /* ------------- member function ------------- */

    private void extract() {
        // extract each layer's aabbs
        this.allAABBs = new ArrayList<>();
        this.leafAABBs = new ArrayList<>();
        // root
        List<WB_AABB> rootAABB = new ArrayList<>();
        rootAABB.add(originalAABBTree.getRoot().getAABB());
        allAABBs.add(rootAABB);
        // next
        List<WB_AABBTree.WB_AABBNode> nodeEachDepth = new ArrayList<>();
        nodeEachDepth.add(originalAABBTree.getRoot());
        for (int i = 0; i < originalAABBTree.getDepth(); ++i) {
            List<WB_AABBTree.WB_AABBNode> currNodes = new ArrayList<>();
            for (WB_AABBTree.WB_AABBNode n : nodeEachDepth) {
                WB_AABBTree.WB_AABBNode ca = n.getChildA();
                WB_AABBTree.WB_AABBNode cb = n.getChildB();
                if (ca != null) {
                    if (ca.isLeaf()) {
                        leafAABBs.add(ca.getAABB());
                    }
                    currNodes.add(ca);
                }
                if (cb != null) {
                    if (cb.isLeaf()) {
                        leafAABBs.add(cb.getAABB());
                    }
                    currNodes.add(cb);
                }
            }
            List<WB_AABB> aabbCurr = new ArrayList<>();
            for (WB_AABBTree.WB_AABBNode n : currNodes) {
                aabbCurr.add(n.getAABB());
            }
            allAABBs.add(aabbCurr);
            nodeEachDepth.clear();
            nodeEachDepth.addAll(currNodes);
        }
    }

    /* ------------- setter & getter ------------- */

    public WB_AABBTree getOriginalAABBTree() {
        return originalAABBTree;
    }

    public int getDepth() {
        return originalAABBTree.getDepth();
    }

    public List<List<WB_AABB>> getAllAABBs() {
        return allAABBs;
    }

    public List<WB_AABB> getAABBLayer(int depth) {
        return allAABBs.get(depth);
    }

    public List<WB_AABB> getLeafAABBs() {
        return leafAABBs;
    }

    public int getLeafAABBNum() {
        return leafAABBs.size();
    }

    /* ------------- draw ------------- */
}
