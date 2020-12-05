# 个人java工具包 慎用
## **ZPoint**
自定义的点数据类型，可代表点或向量
## **ZNode**
`extends ZPoint` 代表graph中的node
## **ZLine**
自定义的线数据类型，可代表直线、射线、线段，也可转化为 `p+td` 的形式
## **ZEdge**
`extends ZLine` 代表graph中的edge
## **ZSkeleton**
使用[campskeleton](https://github.com/twak/campskeleton "campskeleton")计算直骨架（straight skeleton），可以生成2D或3Dskeleton结果，支持带洞多边形。  
支持一些几何元素提取，包括：
* 全部骨架边 `getAllEdges`
* 全部top边 `getTopEdges`
* 全部side边 `getSideEdges`
* 全部bottom边 `getBottomEdges`
* 全部脊线 `getRidges`
* 全部脊线点 `getRidgePoints`
* 全部脊线延长线（到边线中点） `getExtendedRidges`
## **ZGeomath**
一些自定义的几何计算工具
### 向量角度相关
 * 求到角的角平分线向量  
 `getAngleBisectorOrdered`
 * 按极角排序一组向量（返回原列表序号）  
 `sortPolarAngleIndices`
 * 按极角排序一组向量（返回排好的新向量或单位向量）  
 `sortPolarAngle` `sortPolarAngleUnit`
 * 找到多边形内的所有凹点（返回点list或者序号list）  
 `getConcavePoints` `getConcavePointIndices`
 * 从一组向量中找与输入目标夹角最小者，不区分正负角（返回向量）   
 `findClosetVec`
 ### 二维相交相关
 * 检查两个WB_Segment是否相交（用WB_GeometryOP）   
 `checkWB_SegmentIntersect`
 * 求任意两个线型对象交点（需输入类型：line, ray, segment）  
 `simpleLineElementsIntersect2D`
 * 求射线与多边形交点  
 `rayPolygonIntersect2D`
 * 求射线与多边形交点，返回按照与指定点升序排序的交点所在边序号  
 `rayPolygonIntersectIndices2D`
 * 将线段延长或剪切至多边形最近的交点  
 `extendSegmentToPolygon`
 ### 二维距离相关 
 * 从一组线段中找到与目标点距离最近的点  
 `closetPointToLineList`
 ### 二维位置判断相关
 * 判断点是否在直线/射线/线段上（有epsilon）  
 `pointOnLine` `pointOnRay` `pointOnSegment`
 * 从一系列ZLine中找到点在哪条线上，返回线两边的端点  
 `pointOnWhichZLine`
 * 找到点在多边形哪条边上（可返回WB_Segment, ZLine, 或两顶点序号）  
 `pointOnWhichWB_Segment` `pointOnWhichPolyEdge` `pointOnWhichPolyEdgeIndices`
 * 从一组多边形中找到包含输入点的那一个（返回序号）  
 `pointInWhichPolygon`
  ### 二维轮廓找点相关
 * 输入一个多边形和一个多边形上的点，输入距离，找到沿多边形轮廓走一定距离后的两个点  
 `pointsOnEdgeByDist`
 * 输入步长，将多边形或多段线轮廓按步长剖分，得到所有点（最后一段步长必然不足长）   
 `splitPolygonEdgeByStep` `splitWB_PolyLineEdgeByStep`
 * 输入步长阈值，将多边形或多段线按阈值内最大值等分，得到所有点  
 `splitPolygonEdgeByThreshold` `splitWB_PolyLineEdgeByThreshold`
 * 输入等分数量，将多边形或多段线等分，得到所有点   
 `splitPolygonEdge` `splitWB_PolyLineEdge`
 ### 其他
 * 输入Geometry，设置Jts的Precision Model  
 `applyJtsPrecisionModel`
 * 使WB_Polygon点序反向，支持带洞  
 `reversePolygon`
 * 使WB_Polygon法向量Z坐标为正或为负，支持带洞（不是拍平到xy平面，只是翻个面）  
 `PolygonFaceUp` `PolygonFaceDown`
 * 偏移多边形的某一条边线（默认输入为正向首尾相接多边形）  
 `offsetWB_PolygonSegment`

增加中...
## **ZMath**
一些数学工具

增加中...
## **ZTransform**
常用库几何数据的相互转换  
*目前仅涉及简单多边形 部分可包含带洞情况*
### IGeo <-> WB
 * IPoint -> WB_Point
 * IPoint -> WB_Point 带缩放
 * ICurve -> WB_Geometry 根据点数和闭合与否返回WB_Polygon / WB_Polyline / WB_Segment
 * ICurve -> WB_Geometry 根据点数和闭合与否返回WB_Polygon / WB_Polyline / WB_Segment，带缩放
###  IGeo <-> jts 
 * IPoint -> Coordinate
 * IPoint -> Point
 * ICurve -> Geometry 根据点数和闭合与否返回Polygon / LineString
###  WB <-> jts 
 * WB_Polygon -> Polygon 如果WB_Polygon第一点与最后一点不重合，就加上最后一点
 * Polygon -> WB_Polygon
 * LineString -> WB_PolyLine
 * WB_PolyLine -> LineString
 * WB_Segment -> LineString
###  WB <-> WB 
 * WB_Polygon -> WB_Polygon 检查WB_Polygon第一点与最后一点是否重合，不重合则加上
 * WB_Polygon -> WB_PolyLine
 * WB_AABB -> WB_AABB offset WB_AABB

...增加中
## **ZGeoFactory**
包含了jts的GeometryFactory和HE_Mesh的WB_GeometryFactory，以及其他create命令：
* 从一组首尾相接的线段创建Line String / WB_PolyLine  
`createLineString` `createWB_PolyLine`

...增加中
## 增加中...
