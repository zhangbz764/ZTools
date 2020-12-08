package subdivision;

import geometry.ZGeoFactory;
import geometry.ZLine;
import geometry.ZSkeleton;
import processing.core.PApplet;
import render.JtsRender;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * double-strip subdivision
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/7
 * @time 14:25
 */
public class ZSD_DoubleStrip implements ZSubdivision {
    private final WB_Polygon originPolygon;
    private List<WB_Polygon> allSubPolygons;

    private List<List<Integer>> logicalStreets;
    private ZSkeleton skeleton;
    private HE_Mesh skeletonMesh;
    private List<WB_Polygon> unionByStreet;

    public ZSD_DoubleStrip(WB_Polygon originPolygon) {
        this.originPolygon = originPolygon;

        this.logicalStreets = new ArrayList<>();
        logicalStreets.add(Arrays.asList(0, 1));
        logicalStreets.add(Collections.singletonList(2));
        logicalStreets.add(Arrays.asList(3, 4, 5));
        logicalStreets.add(Arrays.asList(6, 7));

        performDivide();
    }

    @Override
    public void performDivide() {
        this.skeleton = new ZSkeleton(originPolygon);
        this.skeletonMesh = skeleton.getSkeletonMesh();

        List<WB_Polygon> allFacePolys = skeleton.getAllFacePolys();
        this.unionByStreet = new ArrayList<>();
        for (List<Integer> logicalStreet : logicalStreets) {
            List<WB_Polygon> strip = new ArrayList<>();
            for (Integer index : logicalStreet) {
                strip = ZGeoFactory.wbgf.unionPolygons2D(allFacePolys.get(index), strip);
            }
            unionByStreet.addAll(strip);
        }

    }

    @Override
    public WB_Polygon getOriginPolygon() {
        return originPolygon;
    }

    @Override
    public List<WB_Polygon> getAllSubPolygons() {
        return allSubPolygons;
    }

    @Override
    public HE_Mesh getMesh() {
        return new HEC_FromPolygons(allSubPolygons).create();
    }

    @Override
    public void display(PApplet app, WB_Render render, JtsRender jtsRender) {
        app.noFill();
        for (WB_Polygon p : unionByStreet) {
            render.drawPolygonEdges2D(p);
        }
        app.fill(0);
        app.textSize(15);
        for (int j = 0; j < skeleton.getAllFacePolys().size(); j++) {
            app.text(j,
                    skeleton.getAllFacePolys().get(j).getCenter().xf(),
                    skeleton.getAllFacePolys().get(j).getCenter().yf(),
                    skeleton.getAllFacePolys().get(j).getCenter().zf()
            );
        }
    }
}
