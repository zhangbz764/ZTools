package system;


import dxfExporter.*;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @auther Alessio Baizhou Zhang
 * @date 2021/6/16
 **/
public class DxfOP {
    private DXFExport dxfExport;

    public DxfOP() {
        dxfExport = new DXFExport();
        dxfExport.AutoCADVer = Constants.DXFVERSION_R2000;

        DXFLayer layer = new DXFLayer("default_layer");
        dxfExport.setCurrentLayer(layer);
    }

    public void createLayer(String name, int color) {
        DXFLayer layer = new DXFLayer(name);
        layer.setColor(color);
        dxfExport.setCurrentLayer(layer);
    }

    public void createLine2D(WB_Point p1, WB_Point p2, String layer) {
        DXFData line = new DXFData();
        line.LayerName = Objects.requireNonNullElse(layer, "default_layer");
        line.Point = new DXFPoint(p1.xf(), p1.yf(), 0);
        line.Point1 = new DXFPoint(p2.xf(), p2.yf(), 0);
        dxfExport.addLine(line);
    }

    public void createLine3D(WB_Point p1, WB_Point p2, String layer) {
        DXFData line = new DXFData();
        line.LayerName = Objects.requireNonNullElse(layer, "default_layer");
        line.Point = new DXFPoint(p1.xf(), p1.yf(), p1.zf());
        line.Point1 = new DXFPoint(p2.xf(), p2.yf(), p2.zf());
        dxfExport.addLine(line);
    }

    public void createCircle(WB_Point p, float rad, String layer) {
        DXFData circle = new DXFData();
        circle.LayerName = Objects.requireNonNullElse(layer, "default_layer");
        circle.Point = new DXFPoint(p.xf(), p.yf(), p.zf());
        circle.Radius = rad;
        dxfExport.addCircle(circle);
    }

    public void createPolyLine(List<? extends WB_Coord> pts, String layer) {
        DXFData polyline = new DXFData();
        polyline.Count = pts.size();
        polyline.Points = new ArrayList();
        polyline.LayerName = Objects.requireNonNullElse(layer, "default_layer");
        for (WB_Coord p : pts) {
            polyline.Points.add(new DXFPoint(p.xf(), p.yf(), 0));
        }
        dxfExport.addPolyline(polyline);
    }

    public void createPolyLine(WB_Coord[] pts, String layer) {
        DXFData polyline = new DXFData();
        polyline.Count = pts.length;
        polyline.Points = new ArrayList();
        polyline.LayerName = Objects.requireNonNullElse(layer, "default_layer");
        for (WB_Coord p : pts) {
            polyline.Points.add(new DXFPoint(p.xf(), p.yf(), 0));
        }
        dxfExport.addPolyline(polyline);
    }

    public void createPolygon(List<? extends WB_Coord> pts, String layer) {
        DXFData polyline = new DXFData();
        polyline.Count = pts.size() + 1;
        polyline.Points = new ArrayList();
        polyline.LayerName = Objects.requireNonNullElse(layer, "default_layer");
        for (WB_Coord p : pts) {
            polyline.Points.add(new DXFPoint(p.xf(), p.yf(), 0));
        }
        WB_Coord o = pts.get(0);
        polyline.Points.add(new DXFPoint(o.xf(), o.yf(), 0));
        dxfExport.addPolyline(polyline);
    }

    public void createPolygon(WB_Coord[] pts, String layer) {
        DXFData polyline = new DXFData();
        polyline.Count = pts.length + 1;
        polyline.Points = new ArrayList();
        polyline.LayerName = Objects.requireNonNullElse(layer, "default_layer");
        for (WB_Coord p : pts) {
            polyline.Points.add(new DXFPoint(p.xf(), p.yf(), 0));
        }
        WB_Coord o = pts[0];
        polyline.Points.add(new DXFPoint(o.xf(), o.yf(), 0));
        dxfExport.addPolyline(polyline);
    }

    public void createHatch(List<? extends WB_Coord> pts, String layer) {
        DXFData hatchData = new DXFData();
        //hatch的对象只有一个
        hatchData.Count = 1;
        hatchData.Points = new ArrayList<DXFPoint>();
        hatchData.LayerName = Objects.requireNonNullElse(layer, "default_layer");
        ArrayList newPts = new ArrayList();
        for (WB_Coord p : pts) {
            newPts.add(new DXFPoint(p.xf(), p.yf(), 0));
        }
        hatchData.Points.add(newPts);
        dxfExport.addHatch(hatchData);
    }

    public void createHatch(WB_Coord[] pts, String layer) {
        DXFData hatchData = new DXFData();
        //hatch的对象只有一个
        hatchData.Count = 1;
        hatchData.Points = new ArrayList<DXFPoint>();
        hatchData.LayerName = Objects.requireNonNullElse(layer, "default_layer");
        ArrayList newPts = new ArrayList();
        for (WB_Coord p : pts) {
            newPts.add(new DXFPoint(p.xf(), p.yf(), 0));
        }
        hatchData.Points.add(newPts);
        dxfExport.addHatch(hatchData);
    }

    /**
     * 创建文字
     *
     * @param pos
     * @param content
     * @param size
     * @param angle   旋转方向为围绕pos逆时针旋转，角度制
     * @param layer
     */
    public void createText(WB_Point pos, String content, float size, float angle, String layer) {
        DXFData data = new DXFData();
        data.LayerName = Objects.requireNonNullElse(layer, "default_layer");
        data.Text = content;
        data.Rotation = angle;
        data.FHeight = size;
        data.Point = new DXFPoint(pos.xf(), pos.yf(), 0);
        dxfExport.addText(data);
    }

    public void save(String file) {
        try {
            dxfExport.saveToFile(file);
        } catch (Exception ignored) {
        } finally {
            dxfExport.finalize();
            System.out.println("Dxf Saved! \n" + "Path : " + file);
        }
    }

}
