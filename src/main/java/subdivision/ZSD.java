package subdivision;

import processing.core.PApplet;
import render.JtsRender;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

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

    /* ------------- draw ------------- */

    public abstract void display(PApplet app, WB_Render render, JtsRender jtsRender);
}
