package testDependencies;

import dxfExporter.Constants;
import igeo.IG;
import processing.core.PApplet;
import system.DxfOP;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project archijson-reader
 * @date 2024/4/7
 * @time 15:04
 */
public class TestDxfExport extends PApplet {

    public static void main(String[] args) {
        PApplet.main(TestDxfExport.class.getName());
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */


    public void setup() {

        /* ----- 你的主程序setup里的内容 ----- */
        /* ----- 你的主程序setup里的内容 ----- */
        /* ----- 你的主程序setup里的内容 ----- */

        DxfOP dxfOP = new DxfOP();
    }


    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        /* ----- 你的主程序draw里的内容 ----- */
        /* ----- 你的主程序draw里的内容 ----- */
        /* ----- 你的主程序draw里的内容 ----- */
    }


    public void geometryExport(DxfOP op) {
        // 创建图层
        op.createLayer("floor", Constants.DXF_BYLAYER);
        op.createLayer("room", Constants.DXF_AQUA);
        op.createLayer("furniture", Constants.DXF_LIME);
        op.createLayer("text", Constants.DXF_GREEN);

        // 创建图元

        // line 2D / 3D
        WB_Point start = new WB_Point(0, 0, 0);
        WB_Point end = new WB_Point(100, 100, 0);
        op.createLine2D(start, end, "floor");
        op.createLine3D(start,  new WB_Point(100, 100, 100), "test");

        // circle
        op.createCircle(
                new WB_Point(-100, 0, 0),
                25,
                "furniture"
        );

        // polyline（不闭合）
        WB_Point[] polylinePts = new WB_Point[]{
                new WB_Point(200, 0),
                new WB_Point(200, 200),
                new WB_Point(300, 200),
                new WB_Point(300, 0),
        };
        op.createPolyLine(polylinePts,"furniture");

        // polygon（闭合）
        WB_Point[] polygonPts = new WB_Point[]{
                new WB_Point(-200, 0),
                new WB_Point(-200, -200),
                new WB_Point(-300, -200),
                new WB_Point(-300, 0),
        };
        WB_Polygon polygon = new WB_Polygon(
                new WB_Point(-200, 0),
                new WB_Point(-200, -200),
                new WB_Point(-300, -200),
                new WB_Point(-300, 0)
        );

        op.createPolygon(polygon.getPoints().toArray(),"room");

        // 文字
        op.createText(new WB_Point(20,30,40),"Inst.AAA",20, 90,"text");

        // hatch填充
        op.createHatch(polygonPts,"room");
    }

    @Override
    public void keyPressed() {
        if (key == 's') {
            DxfOP op = new DxfOP(); // 一个DxfOP对象。可以理解为一个空的dxf文件，下面要将各种图形的加入到这个dxf文件中
            String savePath = "./export.dxf"; // 文件保存路径

            geometryExport(op); // 我把添加图形的命令写成了一个函数

            op.save(savePath); // 保存
        }
    }
}
