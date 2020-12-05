package subdivision;

import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_GeometryOp4D;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Mesh;

import java.util.List;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/1
 * @time 23:01
 * @description
 */
public class ZSD_Stripe implements ZSD {
    private WB_Polygon originPolygon;
    private List<WB_Polygon> allSubPolygons;

    private double span;

    /* ------------- constructor ------------- */

    public ZSD_Stripe(WB_Polygon originPolygon) {
        this.originPolygon = originPolygon;
    }

    @Override
    public void performDivide() {

    }

    /* ------------- setter & getter ------------- */

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
}
