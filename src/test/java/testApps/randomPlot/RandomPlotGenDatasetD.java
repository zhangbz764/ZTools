package testApps.randomPlot;

import guo_cam.CameraController;
import math.ZGeoMath;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/5/10
 * @time 14:36
 */
public class RandomPlotGenDatasetD extends PApplet {
    public static void main(String[] args) {
        PApplet.main(RandomPlotGenDatasetD.class.getName());
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(128, 128, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Render render;
    private CameraController gcam;

    private WB_Polygon boundary;
    private List<WB_Segment> diviSegs;
    private List<WB_Polygon> diviPolys;


    public void setup() {
        this.render = new WB_Render(this);
        frameRate(60);
        genTypeD();

//
//        Timer timer = new Timer();
//        int count = 0;
//        int finalCount = count;
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                genTypeA();
//                save("E:\\2_data\\plotTestData\\" + "a" + finalCount + ".jpg");
//            }
//        };
//        count++;
//        timer.schedule(task, 0, 100);
    }

    /**
     *
     */
    private void genTypeA() {
        double randomW = 120;
        double randomH = 120;

        double minX = width * 0.5 - randomW * 0.5;
        double maxX = width * 0.5 + randomW * 0.5;
        double minY = height * 0.5 - randomH * 0.5;
        double maxY = height * 0.5 + randomH * 0.5;

        this.boundary = new WB_Polygon(
                new WB_Point(minX, minY),
                new WB_Point(maxX, minY),
                new WB_Point(maxX, maxY),
                new WB_Point(minX, maxY),
                new WB_Point(minX, minY)
        );

        this.diviSegs = new ArrayList<>();
        double randomUPos1 = random((float) (width * 0.5 - randomH * 0.3), (float) (width * 0.5 + randomH * 0.3));
        double randomUPos2 = random((float) (width * 0.5 - randomH * 0.3), (float) (width * 0.5 + randomH * 0.3));
        double randomVPos = random((float) (height * 0.5 - randomH * 0.3), (float) (height * 0.5 + randomH * 0.3));

        WB_Segment seg1 = new WB_Segment(new WB_Point(minX, randomVPos), new WB_Point(maxX, randomVPos));
        WB_Segment seg2 = new WB_Segment(new WB_Point(randomUPos1, randomVPos), new WB_Point(randomUPos1, minY));
        WB_Segment seg3 = new WB_Segment(new WB_Point(randomUPos2, randomVPos), new WB_Point(randomUPos2, maxY));

        diviSegs.add(seg1);
        diviSegs.add(seg2);
        diviSegs.add(seg3);
    }

    /**
     *
     */
    private void genTypeB() {
        double randomW = 120;
        double randomH = 120;

        double minX = width * 0.5 - randomW * 0.5;
        double maxX = width * 0.5 + randomW * 0.5;
        double minY = height * 0.5 - randomH * 0.5;
        double maxY = height * 0.5 + randomH * 0.5;

        this.boundary = new WB_Polygon(
                new WB_Point(minX, minY),
                new WB_Point(maxX, minY),
                new WB_Point(maxX, maxY),
                new WB_Point(minX, maxY),
                new WB_Point(minX, minY)
        );

        this.diviSegs = new ArrayList<>();
        double randomUPos1 = random((float) (width * 0.5 - randomH * 0.3), (float) (width * 0.5 - randomH * 0.1));
        double randomUPos2 = random((float) (width * 0.5 + randomH * 0.1), (float) (width * 0.5 + randomH * 0.3));
        double randomVPos1 = random((float) (height * 0.5 - randomH * 0.3), (float) (height * 0.5 + randomH * 0.3));
        double randomVPos2 = random((float) (height * 0.5 - randomH * 0.3), (float) (height * 0.5 + randomH * 0.3));

        WB_Segment seg1 = new WB_Segment(new WB_Point(randomUPos1, minY), new WB_Point(randomUPos1, maxY));
        WB_Segment seg2 = new WB_Segment(new WB_Point(randomUPos2, minY), new WB_Point(randomUPos2, maxY));
        WB_Segment seg3 = new WB_Segment(new WB_Point(minX, randomVPos1), new WB_Point(randomUPos1, randomVPos1));
        WB_Segment seg4 = new WB_Segment(new WB_Point(randomUPos2, randomVPos2), new WB_Point(maxX, randomVPos2));

        diviSegs.add(seg1);
        diviSegs.add(seg2);
        diviSegs.add(seg3);
        diviSegs.add(seg4);
    }

    /**
     *
     */
    private void genTypeC() {
        double randomW = 120;
        double randomH = 120;
        double minX = width * 0.5 - randomW * 0.5;
        double maxX = width * 0.5 + randomW * 0.5;
        double minY = height * 0.5 - randomH * 0.5;
        double maxY = height * 0.5 + randomH * 0.5;

        this.boundary = new WB_Polygon(
                new WB_Point(minX, minY),
                new WB_Point(maxX, minY),
                new WB_Point(maxX, maxY),
                new WB_Point(minX, maxY),
                new WB_Point(minX, minY)
        );

        this.diviPolys = new ArrayList<>();
        double minU = random((float) (width * 0.5 - randomW * 0.3), (float) (width * 0.5 - randomW * 0.15));
        double maxU = random((float) (width * 0.5 + randomW * 0.15), (float) (width * 0.5 + randomW * 0.3));
        double minV = random((float) (height * 0.5 - randomH * 0.3), (float) (height * 0.5 - randomH * 0.15));
        double maxV = random((float) (height * 0.5 + randomH * 0.15), (float) (height * 0.5 + randomH * 0.3));

        diviPolys.add(new WB_Polygon(
                new WB_Point(minU, minV),
                new WB_Point(maxU, minV),
                new WB_Point(maxU, maxV),
                new WB_Point(minU, maxV),
                new WB_Point(minU, minV)
        ));

        this.diviSegs = new ArrayList<>();
        double thresholdU = (maxU - minU) * 0.2;
        double thresholdV = (maxV - minV) * 0.2;
        double randomUPos1 = random((float) (minU + thresholdU), (float) (maxU - thresholdU));
        double randomUPos2 = random((float) (minU + thresholdU), (float) (maxU - thresholdU));
        double randomVPos1 = random((float) (minV + thresholdV), (float) (maxV - thresholdV));
        double randomVPos2 = random((float) (minV + thresholdV), (float) (maxV - thresholdV));

        WB_Segment seg1 = new WB_Segment(new WB_Point(minX, randomVPos1), new WB_Point(minU, randomVPos1));
        WB_Segment seg2 = new WB_Segment(new WB_Point(maxU, randomVPos2), new WB_Point(maxX, randomVPos2));
        WB_Segment seg3 = new WB_Segment(new WB_Point(randomUPos1, minV), new WB_Point(randomUPos1, minY));
        WB_Segment seg4 = new WB_Segment(new WB_Point(randomUPos2, maxV), new WB_Point(randomUPos2, maxY));

        diviSegs.add(seg1);
        diviSegs.add(seg2);
        diviSegs.add(seg3);
        diviSegs.add(seg4);
    }

    /**
     *
     */
    private void genTypeD() {
        double randomW = 120;
        double randomH = 120;

        double minX = width * 0.5 - randomW * 0.5;
        double maxX = width * 0.5 + randomW * 0.5;
        double minY = height * 0.5 - randomH * 0.5;
        double maxY = height * 0.5 + randomH * 0.5;

        this.boundary = new WB_Polygon(
                new WB_Point(minX, minY),
                new WB_Point(maxX, minY),
                new WB_Point(maxX, maxY),
                new WB_Point(minX, maxY),
                new WB_Point(minX, minY)
        );

        this.diviSegs = new ArrayList<>();
        double randomUPos1 = random((float) (width * 0.5 - randomH * 0.3), (float) (width * 0.5 - randomH * 0.2));
        double randomUPos2 = random((float) (width * 0.5 + randomH * 0.2), (float) (width * 0.5 + randomH * 0.3));
        double randomVPos = random((float) (height * 0.5 - randomH * 0.3), (float) (height * 0.5 - randomH * 0.1));

        WB_Segment seg1 = new WB_Segment(new WB_Point(randomUPos1, maxY), new WB_Point(randomUPos1, randomVPos));
        WB_Segment seg2 = new WB_Segment(new WB_Point(randomUPos1, randomVPos), new WB_Point(randomUPos2, randomVPos));
        WB_Segment seg3 = new WB_Segment(new WB_Point(randomUPos2, randomVPos), new WB_Point(randomUPos2, maxY));

        diviSegs.add(seg1);
        diviSegs.add(seg2);
        diviSegs.add(seg3);

        WB_Polygon uShape = new WB_Polygon(
                new WB_Point(minX, minY),
                new WB_Point(maxX, minY),
                new WB_Point(maxX, maxY),
                new WB_Point(randomUPos2, maxY),
                new WB_Point(randomUPos2, randomVPos),
                new WB_Point(randomUPos1, randomVPos),
                new WB_Point(randomUPos1, maxY),
                new WB_Point(minX, maxY),
                new WB_Point(minX, minY)
        );
        WB_PolyLine center = new WB_PolyLine(
                new WB_Point((randomUPos1 - minX) * 0.5 + minX, maxY),
                new WB_Point((randomUPos1 - minX) * 0.5 + minX, (randomVPos - minY) * 0.5 + minY),
                new WB_Point((maxX - randomUPos2) * 0.5 + randomUPos2, (randomVPos - minY) * 0.5 + minY),
                new WB_Point((maxX - randomUPos2) * 0.5 + randomUPos2, maxY)
        );
        List<WB_Point> wbPoints = ZGeoMath.dividePolyLineEdge(center, 6);
        wbPoints.remove(0);
        wbPoints.remove(wbPoints.size() - 1);

        WB_Voronoi2D voronoi = WB_VoronoiCreator.getClippedVoronoi2D(wbPoints, uShape);
        this.diviPolys = new ArrayList<>();
        for (WB_VoronoiCell2D cell : voronoi.getCells()) {
            diviPolys.add(cell.getPolygon());
        }
    }


//    /**
//     *
//     */
//    private void genTypeD() {
//        double randomW = 100 + random(-20, 20);
//        double randomH = 100 + random(-20, 20);
//
//        double minX = width * 0.5 - randomW * 0.5;
//        double maxX = width * 0.5 + randomW * 0.5;
//        double minY = height * 0.5 - randomH * 0.5;
//        double maxY = height * 0.5 + randomH * 0.5;
//
//        this.boundary = new WB_Polygon(
//                new WB_Point(minX, minY),
//                new WB_Point(maxX, minY),
//                new WB_Point(maxX, maxY),
//                new WB_Point(minX, maxY),
//                new WB_Point(minX, minY)
//        );
//
//        double bufferDist = randomW > randomH ? randomH * 0.25 : randomW * 0.25;
//        WB_Polygon buffer = ZFactory.wbgf.createBufferedPolygons(boundary, -bufferDist).get(0);
//
//        List<WB_Point> wbPoints = ZGeoMath.dividePolyLineEdge(buffer, 6);
//        WB_Voronoi2D voronoi = WB_VoronoiCreator.getClippedVoronoi2D(wbPoints, boundary);
//
//        this.diviPolys = new ArrayList<>();
//        for (WB_VoronoiCell2D cell : voronoi.getCells()) {
//            diviPolys.add(cell.getPolygon());
//        }
//    }


    private void genType(int code) {
        if (code == 1) {
            genTypeA();
        } else if (code == 2) {
            genTypeB();
        } else if (code == 3) {
            genTypeC();
        } else {
            genTypeD();
        }
    }

    /* ------------- draw ------------- */
    int numFrames = 10000;      // 总共要保存的帧数
    int imgCount = 0;        // 已保存的帧数
    int lastSavedTime = 0;     // 上次保存的时间
    int saveInterval = 250;    // 保存间隔，单位为毫秒

    int typeCode = 1;

    public void draw() {
        background(255);

        pushStyle();
        strokeWeight(5);
        if (boundary != null)
            render.drawPolygonEdges(boundary);
        strokeWeight(5f);
        if (diviSegs != null) {
            for (WB_Segment diviSeg : diviSegs) {
                render.drawSegment2D(diviSeg);
            }
        }
        if (diviPolys != null) {
            for (WB_Polygon divPoly : diviPolys) {
                render.drawPolygonEdges(divPoly);
            }
        }
        popStyle();

        // 获取当前时间
        int currentTime = millis();

        // 检查是否到达保存间隔
        if (currentTime - lastSavedTime >= saveInterval) {
            // 重新执行绘制程序
            genTypeD();

            // 拼接文件名，包含帧数
            String filename = "E:\\2_data\\plotTestData\\typeD\\" + imgCount + ".jpg";
            // 保存当前画布内容为图像文件
            saveFrame(filename);

            // 增加保存的帧数计数
            imgCount++;

            // 更新上次保存的时间
            lastSavedTime = currentTime;

            // 检查是否达到了总共要保存的帧数
            if (imgCount >= numFrames) {
                // 如果达到了，终止程序
                exit();
            }
        }

//        // 获取当前时间
//        int currentTime = millis();
//
//        if (imgCount + 1 > numFrames) {
//            typeCode++;
//            imgCount = 0;
//            if (typeCode > 4) {
//                exit();
//            }
//        }
//        // 检查是否到达保存间隔
//        if (currentTime - lastSavedTime >= saveInterval) {
//
//
//            // 重新执行绘制程序
//            genType(typeCode);
//
//            // 拼接文件名，包含帧数
//            String filename = "E:\\2_data\\plotTestData\\" + "type" + typeCode + "\\" + imgCount + ".jpg";
//            // 保存当前画布内容为图像文件
//            saveFrame(filename);
//
//            // 增加保存的帧数计数
//            imgCount++;
//
//            // 更新上次保存的时间
//            lastSavedTime = currentTime;
//        }
    }

    @Override
    public void keyPressed() {
        if (key == '1') genTypeA();
        if (key == '2') genTypeB();
        if (key == '3') genTypeC();
        if (key == '4') genTypeD();
    }
}
