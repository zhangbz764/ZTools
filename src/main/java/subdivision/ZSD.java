package subdivision;

import wblut.geom.WB_Polygon;
import wblut.hemesh.HE_Mesh;

import java.util.List;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/1
 * @time 23:02
 * @description
 */
public interface ZSD {

    public abstract void performDivide();

    /* ------------- setter & getter ------------- */

    public abstract WB_Polygon getOriginPolygon();

    public abstract List<WB_Polygon> getAllSubPolygons();

    public abstract HE_Mesh getMesh();

}
