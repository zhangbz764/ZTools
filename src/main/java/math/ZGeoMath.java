package math;

import geometry.ZEdge;
import geometry.ZLine;
import geometry.ZNode;
import geometry.ZPoint;
import org.locationtech.jts.geom.*;
import transform.ZTransform;
import wblut.geom.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 一些自定义的几何计算工具
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/9/29
 * @time 15:38
 * ### 向量角度相关
 * 求到角的角平分线向量
 * 按极角排序一组向量（返回原列表序号）
 * 按极角排序一组向量（返回排好的新向量或单位向量）
 * 找到多边形内的所有凹点（返回点list或者序号list）
 * 从一组向量中找与输入目标夹角最小者，不区分正负角（返回向量）
 * ### 二维相交相关
 * 检查两个WB_Segment是否相交（用WB_GeometryOP）
 * 求任意两个线型对象交点（需输入类型：line, ray, segment）
 * 求射线与多边形交点
 * 求射线与多边形交点，返回按照与指定点升序排序的交点所在边序号
 * 将线段延长或剪切至多边形最近的交点
 * ### 二维距离相关
 * 从一组线段中找到与目标点距离最近的点
 * ### 二维位置判断相关
 * 判断点是否在直线/射线/线段上（有epsilon）
 * 从一系列ZLine中找到点在哪条线上，返回线两边的端点
 * 找到点在多边形哪条边上（可返回WB_Segment, ZLine, 或两顶点序号）
 * 从一组多边形中找到包含输入点的那一个（返回序号）
 * ### 二维轮廓找点相关
 * 找到graph上某节点开始点沿边移动一定距离后的若干个点，返回结果点，或沿途的所有线段
 * 输入一个多边形和一个多边形上的点，输入距离，找到沿多边形轮廓走一定距离后的两个点
 * 输入步长，将多边形或多段线轮廓按步长剖分，得到所有点（最后一段步长必然不足长）
 * 输入步长，剖分多段线或多边形的边 (WB_PolyLine)，返回剖分点与所在边序号的LinkedHashMap
 * 给定阈值上下限，将多边形或多段线按阈值内最大值等分，得到所有点
 * 给定阈值上下限，剖分多段线(WB_PolyLine)，返回剖分点与所在边序号的LinkedHashMap
 * 给定阈值上下限，剖分多段线的每条边(WB_PolyLine)，即剖分结果一定包含每个顶点，但步长不同
 * 输入等分数量，将多边形或多段线等分，得到所有点
 * ### 其他
 * 输入Geometry，设置Jts的Precision Model
 * 使WB_Polygon点序反向
 * 检查两个WB_Polygon是否同向
 * 使WB_Polygon法向量Z坐标为正或为负（不是拍平到xy平面，只是翻个面）
 * 偏移多边形的某一条边线（默认输入为正向首尾相接多边形）
 * 偏移多边形的若干条边线（默认输入为正向首尾相接多边形），返回多段线或多边形
 * <p>
 * ...增加中
 */
public final class ZGeoMath {
    private static final GeometryFactory gf = new GeometryFactory();
    private static final WB_GeometryFactory wbgf = new WB_GeometryFactory();
    // 精度阈值
    private static final double epsilon = 0.00000001;

    /*-------- 向量角度相关 --------*/

    /**
     * 按照v0到v1的顺序求角平分线（包括优角）
     *
     * @param v0 first vector
     * @param v1 second vector
     * @return geometry.ZPoint
     */
    public static ZPoint getAngleBisectorOrdered(final ZPoint v0, final ZPoint v1) {
        if (v0.cross2D(v1) > 0) {  // v0->v1小于180度
            return v0.unit().add(v1.unit()).unit();
        } else if (v0.cross2D(v1) < 0) { // v0->v1大于180度
            return v0.unit().add(v1.unit()).unit().scaleTo(-1);
        } else {
            if (v0.dot2D(v1) > 0) { // v0->v1为0度
                return v0.unit();
            } else { // v0->v1为180度
                ZPoint perpendicular = new ZPoint(v0.yd(), -v0.xd());
                if (!(v0.cross2D(perpendicular) > 0)) {
                    perpendicular.scaleSelf(-1);
                }
                return perpendicular.unit();
            }
        }
    }

    /**
     * 找到一个多边形里所有凹顶点(WB_Polygon)
     *
     * @param polygon input WB_Polygon
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> getConcavePoints(WB_Polygon polygon) {
        List<ZPoint> concavePoints = new ArrayList<>();
        WB_Polygon faceUp = polygonFaceUp(polygon); // 保证正向
        for (int i = 1; i < faceUp.getNumberOfPoints(); i++) {
            ZPoint prev = new ZPoint(faceUp.getPoint(i - 1).sub(faceUp.getPoint(i)));
            ZPoint next = new ZPoint(faceUp.getPoint((i + 1) % (faceUp.getNumberOfPoints() - 1)).sub(faceUp.getPoint(i)));
            double crossValue = next.cross2D(prev);
            if (crossValue < 0) {
                concavePoints.add(new ZPoint(faceUp.getPoint(i)));
            }
        }
        return concavePoints;
    }

    /**
     * 找到一个多边形里所有凹顶点(jts Polygon)
     *
     * @param polygon input jts Polygon
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> getConcavePoints(Polygon polygon) {
        WB_Polygon wbPolygon = ZTransform.jtsPolygonToWB_Polygon(polygon);
        return getConcavePoints(wbPolygon);
    }

    /**
     * 找到一个多边形里所有凹顶点的序号(WB)
     *
     * @param polygon input WB_Polygon
     * @return java.util.List<java.lang.Integer> - indices of input polygon
     */
    public static List<Integer> getConcavePointIndices(WB_Polygon polygon) {
        List<Integer> concavePoints = new ArrayList<>();
        if (polygon.getNormal().zd() > 0) {
            for (int i = 1; i < polygon.getNumberOfPoints(); i++) {
                ZPoint prev = new ZPoint(polygon.getPoint(i - 1).sub(polygon.getPoint(i)));
                ZPoint next = new ZPoint(polygon.getPoint((i + 1) % (polygon.getNumberOfPoints() - 1)).sub(polygon.getPoint(i)));
                double crossValue = next.cross2D(prev);
                if (crossValue < 0) {
                    if (i == polygon.getNumberOfPoints() - 1) {
                        concavePoints.add(0);
                    } else {
                        concavePoints.add(i);
                    }
                }
            }
        } else {
            for (int i = 1; i < polygon.getNumberOfPoints(); i++) {
                ZPoint prev = new ZPoint(polygon.getPoint(i - 1).sub(polygon.getPoint(i)));
                ZPoint next = new ZPoint(polygon.getPoint((i + 1) % (polygon.getNumberOfPoints() - 1)).sub(polygon.getPoint(i)));
                double crossValue = prev.cross2D(next);
                if (crossValue < 0) {
                    if (i == polygon.getNumberOfPoints() - 1) {
                        concavePoints.add(0);
                    } else {
                        concavePoints.add(i);
                    }
                }
            }
        }
        return concavePoints;
    }

    /**
     * 找到一个多边形里所有凹顶点的序号(jts)
     *
     * @param polygon input jts Polygon
     * @return java.util.List<java.lang.Integer> - indices of input polygon
     */
    public static List<Integer> getConcavePointIndices(Polygon polygon) {
        WB_Polygon wbPolygon = ZTransform.jtsPolygonToWB_Polygon(polygon);
        return getConcavePointIndices(wbPolygon);
    }

    /**
     * 按极角排序一系列向量（以第0条为基准），返回在原列表中的序号
     *
     * @param vectors vector list to be sorted
     * @return int[] - indices of input list
     */
    public static int[] sortPolarAngleIndices(final List<? extends ZPoint> vectors) {
        assert vectors.size() > 0 : "input list must at least include 1 vector";
        double[] atanValue = new double[vectors.size()];
        for (int i = 0; i < vectors.size(); i++) {
            double curr_value = Math.atan2(vectors.get(i).yd(), vectors.get(i).xd());
            atanValue[i] = curr_value;
        }
        return ZMath.getArraySortedIndices(atanValue);
    }

    /**
     * 按极角排序一系列向量（以第0条为基准），返回排好的向量
     *
     * @param vectors vector list to be sorted
     * @return geometry.ZPoint[]
     */
    public static ZPoint[] sortPolarAngle(final List<? extends ZPoint> vectors) {
        assert vectors.size() > 0 : "input list must at least include 1 vector";
        int[] newOrder = sortPolarAngleIndices(vectors);
        ZPoint[] sorted = new ZPoint[vectors.size()];
        for (int i = 0; i < newOrder.length; i++) {
            sorted[i] = vectors.get(newOrder[i]);
        }
        return sorted;
    }

    /**
     * 按极角排序一系列向量（以第0条为基准），返回排好的单位向量
     *
     * @param vectors vector list to be sorted
     * @return geometry.ZPoint[] - unit
     */
    public static ZPoint[] sortPolarAngleUnit(final List<? extends ZPoint> vectors) {
        assert vectors.size() > 0 : "input list must at least include 1 vector";
        int[] newOrder = sortPolarAngleIndices(vectors);
        ZPoint[] sorted = new ZPoint[vectors.size()];
        for (int i = 0; i < newOrder.length; i++) {
            sorted[i] = vectors.get(newOrder[i]).unit();
        }
        return sorted;
    }

    /**
     * 从一系列向量中找到与目标向量夹角最小的（不区分正反）
     *
     * @param target target vector
     * @param other  vector list
     * @return geometry.ZPoint
     */
    public static ZPoint findClosetVec(final ZPoint target, final List<? extends ZPoint> other) {
        assert other != null && other.size() != 0 : "invalid input vectors";
        double[] dotValue = new double[other.size()];
        for (int i = 0; i < other.size(); i++) {
            dotValue[i] = target.unit().dot2D(other.get(i).unit());
        }
        int maxIndex = ZMath.getMaxIndex(dotValue);
        return other.get(maxIndex);
    }

    /*-------- 二维相交相关 --------*/

    /**
     * 检测两条WB_Segment是否相交
     *
     * @param seg0 first segment
     * @param seg1 second segment
     * @return boolean
     */
    public static boolean checkWB_SegmentIntersect(final WB_Segment seg0, final WB_Segment seg1) {
        return WB_GeometryOp2D.checkIntersection2DProper(seg0.getOrigin(), seg0.getEndpoint(), seg1.getOrigin(), seg1.getEndpoint());
    }

    /**
     * 根据输入线型对象类型算交点 line, ray or segment
     *
     * @param l0    first line
     * @param type0 first type "line" "ray" "segment"
     * @param l1    second line
     * @param type1 second type "line" "ray" "segment"
     * @return geometry.ZPoint
     */
    public static ZPoint simpleLineElementsIntersect2D(final ZLine l0, final String type0, final ZLine l1, final String type1) {
        return simpleLineElementsIntersect2D(l0.toLinePD(), type0, l1.toLinePD(), type1);
    }

    /**
     * 根据输入线型对象类型算交点 line, ray or segment
     *
     * @param l0    first line
     * @param type0 first type "line" "ray" "segment"
     * @param l1    second line
     * @param type1 second type "line" "ray" "segment"
     * @return geometry.ZPoint
     */
    public static ZPoint simpleLineElementsIntersect2D(final ZPoint[] l0, final String type0, final ZPoint[] l1, final String type1) {
        if (type0.equals("line") && type1.equals("line")) {
            return lineIntersect2D(l0, l1);
        } else if (type0.equals("segment") && type1.equals("segment")) {
            return segmentIntersect2D(l0, l1);
        } else if (type0.equals("ray") && type1.equals("ray")) {
            return rayIntersect2D(l0, l1);
        } else if (type0.equals("line") && type1.equals("ray")) {
            return lineRayIntersect2D(l0, l1);
        } else if (type0.equals("ray") && type1.equals("line")) {
            return lineRayIntersect2D(l1, l0);
        } else if (type0.equals("line") && type1.equals("segment")) {
            return lineSegmentIntersect2D(l0, l1);
        } else if (type0.equals("segment") && type1.equals("line")) {
            return lineSegmentIntersect2D(l1, l0);
        } else if (type0.equals("ray") && type1.equals("segment")) {
            return raySegmentIntersect2D(l0, l1);
        } else if (type0.equals("segment") && type1.equals("ray")) {
            return lineRayIntersect2D(l1, l0);
        } else {
            throw new IllegalArgumentException("input type must be line, ray or segment");
        }
    }

    /**
     * 求两条直线的交点（用定点p和向量d描述, 一对ZPoint数组）
     *
     * @param line0 first line {point P, direction d}
     * @param line1 second line {point P, direction d}
     * @return geometry.ZPoint
     */
    public static ZPoint lineIntersect2D(final ZPoint[] line0, final ZPoint[] line1) {
        ZPoint delta = line1[0].sub(line0[0]);
        double crossDelta = delta.cross2D(line0[1]);
        double crossBase = line0[1].cross2D(line1[1]);
        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double t = crossDelta / crossBase;
            return line1[0].add(line1[1].scaleTo(t));
        }
    }

    /**
     * 求两条射线的交点
     *
     * @param ray0 first ray {point P, direction d}
     * @param ray1 second ray {point P, direction d}
     * @return geometry.ZPoint
     */
    public static ZPoint rayIntersect2D(final ZPoint[] ray0, final ZPoint[] ray1) {
        ZPoint delta = ray1[0].sub(ray0[0]);
        double crossBase = ray0[1].cross2D(ray1[1]);
        double crossDelta0 = delta.cross2D(ray0[1]);
        double crossDelta1 = delta.cross2D(ray1[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double s = crossDelta1 / crossBase; // ray0
            double t = crossDelta0 / crossBase; // ray1
            if (s >= 0 && t >= 0) {
                return ray1[0].add(ray1[1].scaleTo(t));
            } else {
//                System.out.println("intersection is not on one of these line elements");
                return null;
            }
        }
    }

    /**
     * 求两线段的交点（用定点p和向量d描述,d为 终点-起点）
     *
     * @param seg0 first segment {point P, direction d}
     * @param seg1 first segment {point P, direction d}
     * @return geometry.ZPoint
     */
    public static ZPoint segmentIntersect2D(final ZPoint[] seg0, final ZPoint[] seg1) {
        ZPoint delta = seg1[0].sub(seg0[0]);
        double crossBase = seg0[1].cross2D(seg1[1]);
        double crossDelta0 = delta.cross2D(seg0[1]);
        double crossDelta1 = delta.cross2D(seg1[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double s = crossDelta1 / crossBase; // seg0
            double t = crossDelta0 / crossBase; // seg1
            if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
                return seg1[0].add(seg1[1].scaleTo(t));
            } else {
//                System.out.println("intersection is not on one of these line elements");
                return null;
            }
        }
    }

    /**
     * 求直线和射线的交点
     *
     * @param line line {point P, direction d}
     * @param ray  ray {point P, direction d}
     * @return geometry.ZPoint
     */
    public static ZPoint lineRayIntersect2D(final ZPoint[] line, final ZPoint[] ray) {
        ZPoint delta = ray[0].sub(line[0]);
        double crossBase = line[1].cross2D(ray[1]);
        double crossDelta0 = delta.cross2D(line[1]);
        double crossDelta1 = delta.cross2D(ray[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double s = crossDelta1 / crossBase; // line
            double t = crossDelta0 / crossBase; // ray
            if (t >= 0) {
                return ray[0].add(ray[1].scaleTo(t));
            } else {
//                System.out.println("intersection is not on one of these line elements");
                return null;
            }
        }
    }

    /**
     * 求直线和线段的交点
     *
     * @param line line {point P, direction d}
     * @param seg  segment {point P, direction d}
     * @return geometry.ZPoint
     */
    public static ZPoint lineSegmentIntersect2D(final ZPoint[] line, final ZPoint[] seg) {
        ZPoint delta = seg[0].sub(line[0]);
        double crossBase = line[1].cross2D(seg[1]);
        double crossDelta0 = delta.cross2D(line[1]);
        double crossDelta1 = delta.cross2D(seg[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double s = crossDelta1 / crossBase; // line
            double t = crossDelta0 / crossBase; // seg
            if (t >= 0 && t <= 1) {
                return seg[0].add(seg[1].scaleTo(t));
            } else {
//                System.out.println("intersection is not on one of these line elements");
                return null;
            }
        }
    }

    /**
     * 求射线和线段交点
     *
     * @param ray ray {point P, direction d}
     * @param seg segment {point P, direction d}
     * @return geometry.ZPoint
     */
    public static ZPoint raySegmentIntersect2D(final ZPoint[] ray, final ZPoint[] seg) {
        ZPoint delta = seg[0].sub(ray[0]);
        double crossBase = ray[1].cross2D(seg[1]);
        double crossDelta0 = delta.cross2D(ray[1]);
        double crossDelta1 = delta.cross2D(seg[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double s = crossDelta1 / crossBase; // ray
            double t = crossDelta0 / crossBase; // seg
            if (s >= 0 && t >= 0 && t <= 1) {
                return seg[0].add(seg[1].scaleTo(t));
            } else {
//                System.out.println("intersection is not on one of these line elements");
                return null;
            }
        }
    }


    /**
     * 求射线与多边形交点（每段线段是 [——) 关系）
     *
     * @param ray  ray {point P, direction d}
     * @param poly input polygon
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> rayPolygonIntersect2D(final ZPoint[] ray, final WB_Polygon poly) {
        List<ZPoint> result = new ArrayList<>();
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            ZPoint[] polySeg = new ZLine(poly.getSegment(i)).toLinePD();
            ZPoint delta = polySeg[0].sub(ray[0]);
            double crossBase = ray[1].cross2D(polySeg[1]);
            double crossDelta0 = delta.cross2D(ray[1]);
            double crossDelta1 = delta.cross2D(polySeg[1]);

            if (Math.abs(crossBase) >= epsilon) {
                double s = crossDelta1 / crossBase; // ray
                double t = crossDelta0 / crossBase; // seg
                if (s >= 0 && t > 0 && t <= 1) {
                    result.add(polySeg[0].add(polySeg[1].scaleTo(t)));
                }
            }
        }
        return result;
    }

    /**
     * 求射线与多边形交点，返回按交点距离排序的边序号list
     *
     * @param ray  ray {point P, direction d}
     * @param poly input polygon
     * @return java.util.List<java.lang.Integer> - sorted indices of input polygon
     */
    public static List<Integer> rayPolygonIntersectIndices2D(final ZPoint[] ray, final WB_Polygon poly) {
        List<Integer> indicesResult = new ArrayList<>();
        List<Double> resultDist = new ArrayList<>();
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            ZPoint[] polySeg = new ZLine(poly.getSegment(i)).toLinePD();
            ZPoint delta = polySeg[0].sub(ray[0]);
            double crossBase = ray[1].cross2D(polySeg[1]);
            double crossDelta0 = delta.cross2D(ray[1]);
            double crossDelta1 = delta.cross2D(polySeg[1]);

            if (Math.abs(crossBase) >= epsilon) {
                double s = crossDelta1 / crossBase; // ray
                double t = crossDelta0 / crossBase; // seg
                if (s >= 0 && t > 0 && t <= 1) {
                    indicesResult.add(i);
                    resultDist.add(polySeg[0].add(polySeg[1].scaleTo(t)).distanceSq(ray[0]));
                }
            }
        }

        // 得到交点距离排序后的序号
        if (resultDist.size() > 1) {
            double[] distArray = new double[resultDist.size()];
            for (int i = 0; i < resultDist.size(); i++) {
                distArray[i] = resultDist.get(i);
            }
            int[] ascending = ZMath.getArraySortedIndices(distArray);

            List<Integer> newOrder = new ArrayList<>();
            for (int j = 0; j < indicesResult.size(); j++) {
                newOrder.add(indicesResult.get(ascending[j]));
            }
            return newOrder;
        } else {
            return indicesResult;
        }
    }

    /**
     * 将线段延长或剪切至多边形最近的交点
     *
     * @param segment segment {point P, direction d}
     * @param poly    input polygon
     * @return geometry.ZLine
     */
    public static ZLine extendSegmentToPolygon(final ZPoint[] segment, final WB_Polygon poly) {
        List<ZPoint> interResult = rayPolygonIntersect2D(segment, poly);
        if (interResult.size() > 1) {
            double[] resultDist = new double[interResult.size()];
            for (int i = 0; i < interResult.size(); i++) {
                resultDist[i] = segment[0].distanceSq(interResult.get(i));
            }
            int[] ascending = ZMath.getArraySortedIndices(resultDist);
            return new ZLine(segment[0], interResult.get(ascending[0]));
        } else if (interResult.size() == 1) {
            return new ZLine(segment[0], interResult.get(0));
        } else {
            return null;
        }
    }

    /*-------- 二维距离相关 --------*/

    /**
     * 从一组线段中找到与目标点距离最近的点
     *
     * @param p     target point
     * @param lines list of lines
     * @return geometry.ZPoint
     */
    public static ZPoint closetPointToLineList(final ZPoint p, final List<? extends ZLine> lines) {
        ZPoint closet = new ZPoint(WB_GeometryOp2D.getClosestPoint2D(p.toWB_Point(), lines.get(0).toWB_Segment()));
        for (int i = 1; i < lines.size(); i++) {
            ZPoint curr = new ZPoint(WB_GeometryOp2D.getClosestPoint2D(p.toWB_Point(), lines.get(i).toWB_Segment()));
            if (p.distanceSq(closet) > p.distanceSq(curr)) {
                closet = curr;
            }
        }
        return closet;
    }

    /*-------- 二维位置判断相关 --------*/

    /**
     * 判断点是否在直线上（通过叉乘判断，包含浮点误差）
     *
     * @param p    input point
     * @param line input line
     * @return boolean
     */
    public static boolean pointOnLine(final ZPoint p, final ZLine line) {
        double crossValue = line.getDirection().cross2D(p.sub(line.getPt0()));
        return Math.abs(crossValue) < epsilon;
    }

    /**
     * 判断点是否在射线上（通过叉乘判断，包含浮点误差）
     *
     * @param p   input point
     * @param ray input ray
     * @return boolean
     */
    public static boolean pointOnRay(final ZPoint p, final ZLine ray) {
        double crossValue = ray.getDirection().cross2D(p.sub(ray.getPt0()));
        if (Math.abs(crossValue) < epsilon) {
            double minX = Math.min(ray.getPt0().xd(), ray.getPt1().xd());
            double minY = Math.min(ray.getPt0().yd(), ray.getPt1().yd());
            return minX <= p.xd() && minY <= p.yd();
        } else {
            return false;
        }
    }

    /**
     * 判断点是否在线段上（通过叉乘判断，包含浮点误差）
     *
     * @param p   input point
     * @param seg input segment
     * @return boolean
     */
    public static boolean pointOnSegment(final ZPoint p, final ZLine seg) {
        double crossValue = seg.getDirection().cross2D(p.sub(seg.getPt0()));
        if (Math.abs(crossValue) < epsilon) {
            double minX = Math.min(seg.getPt0().xd(), seg.getPt1().xd());
            double maxX = Math.max(seg.getPt0().xd(), seg.getPt1().xd());
            double minY = Math.min(seg.getPt0().yd(), seg.getPt1().yd());
            double maxY = Math.max(seg.getPt0().yd(), seg.getPt1().yd());
            return minX <= p.xd() && p.xd() <= maxX && minY <= p.yd() && p.yd() <= maxY;
        } else {
            return false;
        }
    }

    /**
     * 找到点在多边形哪条边上，返回WB_Segment（可能null）
     *
     * @param p    input point
     * @param poly input polygon
     * @return wblut.geom.WB_Segment - segment of input polygon
     */
    public static WB_Segment pointOnWhichWB_Segment(final ZPoint p, final WB_Polygon poly) {
        WB_Segment result = null;
        for (WB_Segment segment : poly.toSegments()) {
            ZLine seg = new ZLine(segment);
            if (pointOnSegment(p, seg)) {
                result = segment;
                break;
            }
        }

        return result;
    }

    /**
     * 从一系列ZLine中找到点在哪条线上，返回线两边的端点
     *
     * @param p     input point
     * @param edges list of edges
     * @return geometry.ZNode[] - nodes of result edge
     */
    public static ZNode[] pointOnWhichZEdge(final ZPoint p, final List<? extends ZEdge> edges) {
        ZNode[] result = new ZNode[2];
        for (ZEdge edge : edges) {
            if (pointOnSegment(p, edge)) {
                result = edge.getNodes();
            }
        }
        return result;
    }

    /**
     * 找到点在多边形哪条边上，返回ZLine（可能null）
     *
     * @param p    input point
     * @param poly input polygon
     * @return geometry.ZLine
     */
    public static ZLine pointOnWhichPolyEdge(final ZPoint p, final WB_Polygon poly) {
        ZLine result = null;
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            ZLine seg = new ZLine(poly.getSegment(i));
            if (pointOnSegment(p, seg)) {
                result = seg;
                break;
            }
        }
        return result;
    }

    /**
     * 找到点在多边形哪条边上，返回该边两顶点的序号（可能-1）
     *
     * @param p    input point
     * @param poly input polygon
     * @return int[] - indices of result segment
     */
    public static int[] pointOnWhichPolyEdgeIndices(final ZPoint p, final WB_Polygon poly) {
        int[] result = new int[]{-1, -1};
        for (int i = 0; i < poly.getNumberOfPoints() - 1; i++) {
            ZLine seg = new ZLine(poly.getPoint(i), poly.getPoint(i + 1));
            if (pointOnSegment(p, seg)) {
                result[0] = i;
                result[1] = (i + 1) % (poly.getNumberOfPoints() - 1);
                break;
            }
        }
        return result;
    }

    /**
     * 用jts判断，找到包含输入点的多边形，返回index，均不包含则返回-1
     *
     * @param p     input point
     * @param polys list of polygons
     * @return int - index of input list
     */
    public static int pointInWhichPolygon(final ZPoint p, final List<Polygon> polys) {
        int index = -1; // default
        for (int i = 0; i < polys.size(); i++) {
            if (p.toJtsPoint().within(polys.get(i))) {
                index = i;
            }
        }
        return index;
    }

    /**
     * 用jts判断，找到包含输入点的多边形，返回index，均不包含则返回-1
     *
     * @param p     input point
     * @param polys array of polygons
     * @return int - index of input array
     */
    public static int pointInWhichPolygon(final ZPoint p, final Polygon[] polys) {
        int index = -1; // default
        for (int i = 0; i < polys.length; i++) {
            if (p.toJtsPoint().within(polys[i])) {
                index = i;
            }
        }
        return index;
    }

    /*-------- 二维轮廓找点相关 --------*/

    /**
     * 找到graph上某节点开始点沿边移动一定距离后的若干个点，返回结果点
     *
     * @param startNode start node
     * @param dist      distance to move
     * @return java.util.List<geometry.ZPoint>
     */
    public List<ZPoint> pointsOnGraphByDist(final ZNode startNode, final ZNode lastNode, final double dist) {
        List<ZPoint> result = new ArrayList<>();
        for (int i = 0; i < startNode.geiNeighborNum(); i++) {
            if (startNode.getNeighbor(i) != lastNode) { // 排除父节点
                double curr_span = dist;
                if (startNode.getLinkedEdge(i).getLength() >= curr_span) {
                    // 若edge长度大于当前步长，在这条edge上记录终点
                    result.add(startNode.add(startNode.getVecUnitToNeighbor(i).scaleTo(dist)));
                } else {
                    if (startNode.getNeighbor(i).geiNeighborNum() != 1) {
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
     * 找到graph上某节点开始点沿边移动一定距离后的若干个点，返回沿途的所有线段
     *
     * @param startNode start node
     * @param dist      distance to move
     * @return java.util.List<geometry.ZLine>
     */
    public static List<ZLine> segmentsOnGraphByDist(final ZNode startNode, final ZNode lastNode, final double dist) {
        List<ZLine> result = new ArrayList<>();
        for (int i = 0; i < startNode.geiNeighborNum(); i++) {
            if (startNode.getNeighbor(i) != lastNode) { // 排除父节点
                double curr_span = dist;
                if (startNode.getLinkedEdge(i).getLength() >= curr_span) {
                    // 若edge长度大于当前步长，记录该步长的线段并停止
                    result.add(new ZLine(startNode, startNode.add(startNode.getVecUnitToNeighbor(i).scaleTo(dist))));
                } else {
                    result.add(startNode.getLinkedEdge(i));
                    if (startNode.getNeighbor(i).geiNeighborNum() != 1) {
                        // 若已检索到端头节点，则停止
                        curr_span = curr_span - startNode.getLinkedEdge(i).getLength();
                        result.addAll(segmentsOnGraphByDist(startNode.getNeighbor(i), startNode, curr_span));
                    }
                }
            }
        }
        return result;
    }

    /**
     * 找到多边形边上某点沿边移动一定距离后的两个点（0为多边形点序正向，1为逆向）
     *
     * @param origin input point (should be on the edge of polygon)
     * @param poly   input polygon
     * @param dist   distance to move
     * @return geometry.ZPoint[] - forwards and backwards
     */
    public static ZPoint[] pointsOnEdgeByDist(final ZPoint origin, final WB_Polygon poly, double dist) {
        // find point on which edge
        int[] onWhich = pointOnWhichPolyEdgeIndices(origin, poly);
        if (onWhich[0] >= 0 && onWhich[1] >= 0) {
            ZPoint forward;
            ZPoint backward;
            int start = onWhich[0];
            int end = onWhich[1];

            // start
            double cur_spanF = dist;
            double cur_spanB = dist;
            ZPoint f1 = origin;
            ZPoint b1 = origin;

            ZPoint f2 = new ZPoint(poly.getPoint(end)); // 正向下一顶点
            ZPoint b2 = new ZPoint(poly.getPoint(start)); // 逆向下一顶点
            double cur_distF = f1.distance(f2); // 与正向下一顶点距离
            double cur_distB = b1.distance(b2); // 与逆向下一顶点距离

            forward = f1.add(f2.sub(f1).unit().scaleTo(cur_spanF));
            while (cur_spanF > cur_distF) {
                f1 = f2;
                end = (end + 1) % (poly.getNumberOfPoints() - 1);
                f2 = new ZPoint(poly.getPoint(end));

                cur_spanF = cur_spanF - cur_distF;
                cur_distF = f1.distance(f2);
                forward = f1.add(f2.sub(f1).unit().scaleTo(cur_spanF));
            }

            backward = b1.add(b2.sub(b1).unit().scaleTo(cur_spanB));
            while (cur_spanB > cur_distB) {
                b1 = b2;
                start = start - 1;
                if (start == -1) { // 保证倒序数数
                    start = poly.getNumberOfPoints() - 1 - 1;
                }
                b2 = new ZPoint(poly.getPoint(start));

                cur_spanB = cur_spanB - cur_distB;
                cur_distB = b1.distance(b2);
                backward = b1.add(b2.sub(b1).unit().scaleTo(cur_spanB));
            }

            return new ZPoint[]{forward, backward};
        } else {
            System.out.println(origin.toString());
            throw new NullPointerException("point not on polygon edges");
        }
    }

    /**
     * 设置步长，剖分多边形的边 (Polygon)
     *
     * @param poly input polygon
     * @param step step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolygonEdgeByStep(final Polygon poly, final double step) {
        // 得到多边形所有点
        Coordinate[] polyPoints = poly.getCoordinates();

        // 初始值
        ZPoint p1 = new ZPoint(polyPoints[0]);
        double curr_span = step;
        double curr_dist;

        List<ZPoint> result = new ArrayList<>();
        result.add(p1);
        for (int i = 1; i < poly.getNumPoints(); i++) {
            ZPoint p2 = new ZPoint(polyPoints[i]);
            curr_dist = p1.distance(p2);

            while (curr_dist >= curr_span) {
                ZPoint p = p1.add(p2.sub(p1).unit().scaleTo(curr_span));
                result.add(p);
                p1 = p;
                curr_span = step;
                curr_dist = p1.distance(p2);
            }
            p1 = p2;
            curr_span = curr_span - curr_dist;
        }
        if (result.get(0).distance(result.get(result.size() - 1)) < epsilon) {
            result.remove(result.size() - 1);
        }
        return result;
    }

    /**
     * 设置步长，剖分多段线或多边形的边 (WB_PolyLine)
     *
     * @param poly input polyline (polygon)
     * @param step step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitWB_PolyLineEdgeByStep(final WB_PolyLine poly, final double step) {
        // 得到多边形所有点
        WB_Coord[] polyPoints = poly.getPoints().toArray();

        // 初始值
        ZPoint start = new ZPoint(polyPoints[0]);
        ZPoint end = new ZPoint(polyPoints[polyPoints.length - 1]);

        ZPoint p1 = start;
        double curr_span = step;
        double curr_dist;

        List<ZPoint> result = new ArrayList<>();
        result.add(p1);
        for (int i = 1; i < poly.getNumberOfPoints(); i++) {
            ZPoint p2 = new ZPoint(polyPoints[i]);
            curr_dist = p1.distance(p2);
            while (curr_dist >= curr_span) {
                ZPoint p = p1.add(p2.sub(p1).unit().scaleTo(curr_span));
                result.add(p);
                p1 = p;
                curr_span = step;
                curr_dist = p1.distance(p2);
            }
            p1 = p2;
            curr_span = curr_span - curr_dist;
        }

        // 如果封闭，点数=段数，如果开放，点数=段数+1
        if (poly instanceof WB_Ring) {
            if (start.distance(result.get(result.size() - 1)) < epsilon) {
                result.remove(result.size() - 1);
            }
        } else {
            if (end.distance(result.get(result.size() - 1)) > epsilon) {
                result.add(end);
            }
        }

        return result;
    }

    /**
     * 设置步长，剖分多段线或多边形的边 (WB_PolyLine)，返回剖分点与所在边序号的LinkedHashMap
     *
     * @param poly input polyline (polygon)
     * @param step step to divide
     * @return java.util.Map<geometry.ZPoint, java.lang.Integer>
     */
    public static Map<ZPoint, Integer> splitWB_PolyLineEdgeByStepWithPos(final WB_PolyLine poly, final double step) {
        // 得到多边形所有点
        WB_Coord[] polyPoints = poly.getPoints().toArray();

        // 初始值
        ZPoint start = new ZPoint(polyPoints[0]);
        ZPoint end = new ZPoint(polyPoints[polyPoints.length - 1]);

        ZPoint p1 = start;
        double curr_span = step;
        double curr_dist;

        List<ZPoint> pointList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();

        pointList.add(p1);
        indexList.add(0);
        for (int i = 1; i < poly.getNumberOfPoints(); i++) {
            ZPoint p2 = new ZPoint(polyPoints[i]);
            curr_dist = p1.distance(p2);
            while (curr_dist >= curr_span) {
                ZPoint p = p1.add(p2.sub(p1).unit().scaleTo(curr_span));
                pointList.add(p);
                indexList.add(i - 1);
                p1 = p;
                curr_span = step;
                curr_dist = p1.distance(p2);
            }
            p1 = p2;
            curr_span = curr_span - curr_dist;
        }

        // 如果封闭，点数=段数，如果开放，点数=段数+1
        if (poly instanceof WB_Ring) {
            if (start.distance(pointList.get(pointList.size() - 1)) < epsilon) {
                pointList.remove(pointList.size() - 1);
                indexList.remove(indexList.size() - 1);
            }
        } else {
            if (end.distance(pointList.get(pointList.size() - 1)) > epsilon) {
                pointList.add(end);
                indexList.add(poly.getNumberSegments() - 1);
            }
        }

        // 创建linkedHashMap
        Map<ZPoint, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < pointList.size(); i++) {
            result.put(pointList.get(i), indexList.get(i));
        }
        return result;
    }

    /**
     * 给定阈值上下限，剖分多边形(Polygon)
     *
     * @param poly    input polygon
     * @param maxStep max step to divide
     * @param minStep min step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolygonEdgeByThreshold(final Polygon poly, final double maxStep, final double minStep) {
        double finalStep = 0;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            double curr_step = poly.getLength() / i;
            if (curr_step >= minStep && curr_step <= maxStep) {
                finalStep = curr_step;
                break;
            } else if (curr_step < minStep) {
                return null;
            }
        }
        //        System.out.println("step:" + finalStep);
        return splitPolygonEdgeByStep(poly, finalStep);
    }

    /**
     * 给定阈值上下限，剖分多段线(WB_PolyLine)
     *
     * @param poly    input polyline (polygon)
     * @param maxStep max step to divide
     * @param minStep min step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitWB_PolyLineEdgeByThreshold(final WB_PolyLine poly, final double maxStep, final double minStep) {
        assert maxStep >= minStep : "please input valid threshold";
        double length = 0;
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            length = length + poly.getSegment(i).getLength();
        }
        double finalStep = 0;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            double curr_step = length / i;
            if (curr_step >= minStep && curr_step <= maxStep) {
                finalStep = curr_step;
                break;
            } else if (curr_step < minStep) {
                return new ArrayList<ZPoint>();
            }
        }
//        System.out.println("step:" + finalStep);
        return splitWB_PolyLineEdgeByStep(poly, finalStep);
    }

    /**
     * 给定阈值上下限，剖分多段线(WB_PolyLine)，返回剖分点与所在边序号的LinkedHashMap
     *
     * @param poly    input polyline (polygon)
     * @param maxStep max step to divide
     * @param minStep min step to divide
     * @return java.util.Map<geometry.ZPoint, java.lang.Integer>
     */
    public static Map<ZPoint, Integer> splitWB_PolyLineEdgeByThresholdWithPos(final WB_PolyLine poly, final double maxStep, final double minStep) {
        assert maxStep >= minStep : "please input valid threshold";
        double length = 0;
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            length = length + poly.getSegment(i).getLength();
        }
        double finalStep = 0;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            double curr_step = length / i;
            if (curr_step >= minStep && curr_step <= maxStep) {
                finalStep = curr_step;
                break;
            } else if (curr_step < minStep) {
                return new LinkedHashMap<>();
            }
        }
        return splitWB_PolyLineEdgeByStepWithPos(poly, finalStep);
    }

    /**
     * 给定阈值上下限，剖分多段线的每条边(WB_PolyLine)，即剖分结果一定包含每个顶点，但步长不同
     *
     * @param poly    input polyline (polygon)
     * @param maxStep max step to divide
     * @param minStep min step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitWB_PolyLineEachEdgeByThreshold(final WB_PolyLine poly, final double maxStep, final double minStep) {
        assert maxStep >= minStep : "please input valid threshold";
        List<ZPoint> result = new ArrayList<>();
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            WB_Segment segment = poly.getSegment(i);
            double length = segment.getLength();
            double stepOnEdge = 0;
            int splitNum = -1;
            for (int j = 1; j < Integer.MAX_VALUE; j++) {
                double curr_step = length / j;
                if (curr_step >= minStep && curr_step <= maxStep) {
                    stepOnEdge = curr_step;
                    splitNum = j;
                    break;
                } else if (curr_step < minStep) {
                    break;
                }
            }

            if (stepOnEdge != 0) {
                ZPoint curr = new ZPoint(segment.getOrigin());
                result.add(curr);
                for (int j = 0; j < splitNum - 1; j++) {
                    curr = curr.add(new ZPoint(segment.getDirection()).scaleTo(stepOnEdge));
                    result.add(curr);
                }
            } else {
                result.add(new ZPoint(segment.getOrigin()));
            }
        }

        // 如果封闭，点数=段数，如果开放，点数=段数+1
        if (!poly.getPoint(0).equals(poly.getPoint(poly.getNumberOfPoints() - 1))) {
            System.out.println("polyline is open");
            result.add(new ZPoint(poly.getPoint(poly.getNumberOfPoints() - 1)));
        }
        return result;
    }

    /**
     * 将多边形轮廓等分为若干点(Polygon)
     *
     * @param poly     input polygon
     * @param splitNum number to split
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolygonEdge(final Polygon poly, final int splitNum) {
        double step = poly.getLength() / splitNum;
        return splitPolygonEdgeByStep(poly, step);
    }

    /**
     * 将多边形或多段线轮廓等分为若干点(WB_PolyLine)
     *
     * @param poly     input polyline (polygon)
     * @param splitNum number to split
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitWB_PolyLineEdge(final WB_PolyLine poly, final int splitNum) {
        // 计算步长
        double length = 0;
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            length = length + poly.getSegment(i).getLength();
        }
        double step = length / splitNum;

        return splitWB_PolyLineEdgeByStep(poly, step);
    }

    /*-------- 其他几何运算 --------*/

    /**
     * 设置 jts precision model (FLOAT, FLOAT_SINGLE, FIXED)
     *
     * @param geometry geometry to be applied
     * @param pm       precision model
     * @return void
     */
    public static void applyJtsPrecisionModel(final Geometry geometry, final PrecisionModel pm) {
        Coordinate[] coordinates = geometry.getCoordinates();
        for (int i = 0; i < coordinates.length; i++) {
            Coordinate coordinate = coordinates[i];
            pm.makePrecise(coordinate);
        }
    }

    /**
     * WB_Polygon 点序反向（支持带洞）
     *
     * @param original input polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon reversePolygon(final WB_Polygon original) {
        if (original.getNumberOfHoles() == 0) {
            WB_Point[] newPoints = new WB_Point[original.getNumberOfPoints()];
            for (int i = 0; i < newPoints.length; i++) {
                newPoints[i] = original.getPoint(newPoints.length - 1 - i);
            }
            return new WB_Polygon(newPoints);
        } else {
            WB_Point[] newExteriorPoints = new WB_Point[original.getNumberOfShellPoints()];
            for (int i = 0; i < original.getNumberOfShellPoints(); i++) {
                newExteriorPoints[i] = original.getPoint(original.getNumberOfShellPoints() - 1 - i);
            }

            final int[] npc = original.getNumberOfPointsPerContour();
            int index = npc[0];
            WB_Point[][] newInteriorPoints = new WB_Point[original.getNumberOfHoles()][];

            for (int i = 0; i < original.getNumberOfHoles(); i++) {
                WB_Point[] newHole = new WB_Point[npc[i + 1]];
                for (int j = 0; j < newHole.length; j++) {
                    newHole[j] = new WB_Point(original.getPoint(newHole.length - 1 - j + index));
                }
                newInteriorPoints[i] = newHole;
                index = index + npc[i + 1];
            }

            return new WB_Polygon(newExteriorPoints, newInteriorPoints);
        }
    }

    /**
     * 检查两个WB_Polygon是否同向
     *
     * @param p1 polygon1
     * @param p2 polygon2
     * @return boolean
     */
    public static boolean isNormalEquals(final WB_Polygon p1, final WB_Polygon p2) {
        return p1.getNormal().equals(p2.getNormal());
    }

    /**
     * 让WB_Polygon法向量朝向Z轴正向（支持带洞）
     *
     * @param polygon input polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon polygonFaceUp(final WB_Polygon polygon) {
        if (polygon.getNormal().zd() < 0) {
            return reversePolygon(polygon);
        } else {
            return polygon;
        }
    }

    /**
     * 让WB_Polygon法向量朝向Z轴负向（支持带洞）
     *
     * @param polygon input polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon polygonFaceDown(final WB_Polygon polygon) {
        if (polygon.getNormal().zd() > 0) {
            return reversePolygon(polygon);
        } else {
            return polygon;
        }
    }

    /**
     * 偏移多边形的某一条边线（默认输入为正向首尾相接多边形）
     *
     * @param poly  input polygon
     * @param index segment index to be offset
     * @param dist  offset distance
     * @return geometry.ZLine
     */
    public static ZLine offsetWB_PolygonSegment(final WB_Polygon poly, final int index, final double dist) {
        // make sure polygon's start and end point are coincident
        WB_Polygon polygon = ZTransform.validateWB_Polygon(poly);
        assert index <= polygon.getNumberSegments() && index >= 0 : "index out of polygon point number";

        int next = (index + 1) % polygon.getNumberSegments();
        int prev = (index + polygon.getNumberSegments() - 1) % polygon.getNumberSegments();

        ZPoint v1 = new ZPoint(polygon.getSegment(prev).getOrigin()).sub(new ZPoint(polygon.getSegment(prev).getEndpoint()));
        ZPoint v2 = new ZPoint(polygon.getSegment(index).getEndpoint()).sub(new ZPoint(polygon.getSegment(index).getOrigin()));
        ZPoint bisector1 = getAngleBisectorOrdered(v1, v2);
        ZPoint point1 = new ZPoint(polygon.getSegment(index).getOrigin()).add(bisector1.scaleTo(dist / Math.abs(v1.unit().cross2D(bisector1))));

        ZPoint v3 = new ZPoint(polygon.getSegment(index).getOrigin()).sub(new ZPoint(polygon.getSegment(index).getEndpoint()));
        ZPoint v4 = new ZPoint(polygon.getSegment(next).getEndpoint()).sub(new ZPoint(polygon.getSegment(next).getOrigin()));
        ZPoint bisector2 = getAngleBisectorOrdered(v3, v4);
        ZPoint point2 = new ZPoint(polygon.getSegment(index).getEndpoint()).add(bisector2.scaleTo(dist / Math.abs(v3.unit().cross2D(bisector2))));

        return new ZLine(point1, point2);
    }

    /**
     * 偏移多边形的若干条边线（默认输入为正向首尾相接多边形），返回多段线或多边形
     *
     * @param poly  input polygon
     * @param index segment indices to be offset
     * @param dist  offset distance
     * @return wblut.geom.WB_PolyLine
     */
    public static WB_PolyLine offsetWB_PolygonSegments(final WB_Polygon poly, final int[] index, final double dist) {
        // make sure polygon's start and end point are coincident
        WB_Polygon polygon = ZTransform.validateWB_Polygon(poly);

        WB_Point[] linePoints = new WB_Point[index.length + 1];
        for (int i = 0; i < index.length; i++) {
            int prev = (index[i] + polygon.getNumberSegments() - 1) % polygon.getNumberSegments();

            ZPoint v1 = new ZPoint(polygon.getSegment(prev).getOrigin()).sub(new ZPoint(polygon.getSegment(prev).getEndpoint()));
            ZPoint v2 = new ZPoint(polygon.getSegment(index[i]).getEndpoint()).sub(new ZPoint(polygon.getSegment(index[i]).getOrigin()));
            ZPoint bisector1 = getAngleBisectorOrdered(v1, v2);
            ZPoint point1 = new ZPoint(polygon.getSegment(index[i]).getOrigin()).add(bisector1.scaleTo(dist / Math.abs(v1.unit().cross2D(bisector1))));

            linePoints[i] = point1.toWB_Point();
        }

        int next = (index[index.length - 1] + 1) % polygon.getNumberSegments();
        ZPoint v3 = new ZPoint(polygon.getSegment(index[index.length - 1]).getOrigin()).sub(new ZPoint(polygon.getSegment(index[index.length - 1]).getEndpoint()));
        ZPoint v4 = new ZPoint(polygon.getSegment(next).getEndpoint()).sub(new ZPoint(polygon.getSegment(next).getOrigin()));
        ZPoint bisector2 = getAngleBisectorOrdered(v3, v4);
        ZPoint point2 = new ZPoint(polygon.getSegment(index[index.length - 1]).getEndpoint()).add(bisector2.scaleTo(dist / Math.abs(v3.unit().cross2D(bisector2))));
        linePoints[linePoints.length - 1] = point2.toWB_Point();

        if (linePoints[0].equals(linePoints[linePoints.length - 1])) {
            return new WB_Polygon(linePoints);
        } else {
            return new WB_PolyLine(linePoints);
        }
    }
}
