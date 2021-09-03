#### Global Dependencies:

[Processing 3.3.7](https://processing.org/)  
[jts 1.16.1](https://github.com/locationtech/jts)  
[HE_Mesh 2019.0.2](https://github.com/wblut/HE_Mesh)  
[igeo 0.9.4.1](http://igeo.jp/)

## **ZPoint**

自定义的点数据类型，可代表点或向量

## **ZNode**

graph中的node

## **ZLine**

自定义的线数据类型，可代表直线、射线、线段，也可转化为 *定点p+方向td* 的形式

## **ZEdge**

graph中的edge

## **ZGraph**

（需完善）图，包含若干节点ZNode，连接边ZEdge，记录相互引用关系

## **ZSkeleton**

计算多边形直骨架（straight skeleton），可以生成2D或3Dskeleton结果，支持带洞多边形。

#### External Dependencies:

[campskeleton](https://github.com/twak/campskeleton "campskeleton")

支持一些几何元素提取，包括：

* 全部骨架边 `getAllEdges`
* 全部top边 `getTopEdges`
* 全部side边 `getSideEdges`
* 全部bottom边 `getBottomEdges`
* 全部脊线 `getRidges`
* 全部脊线点 `getRidgePoints`
* 全部脊线延长线（到边线中点） `getExtendedRidges`

## **ZCatmullRom**

创建Catmull-Rom曲线  
inspired by [CatmullRomSpline](https://github.com/jurajstrecha/CatmullRomSpline)

## **ZLargestRectangle**

使用粒子群算法优化计算多边形的最大内接矩形  
inspired by [LargestRectangle](https://github.com/dawnwords/LargestRectangle "LargestRectangle")

#### External Dependencies:

[JSwarm-PSO](http://jswarm-pso.sourceforge.net/ "JSwarm-PSO")

## **ZRectCover**

（需完善）使用轮廓点+射线的方法近似找到给定数量的最小矩形覆盖

## **ZGeomath**

一些自定义的几何计算工具

#### 向量角度相关

* 求到角的角平分线向量  
  `getAngleBisectorOrdered`
* 按极角排序一组向量（返回原列表序号）  
  `sortPolarAngleIndices`
* 按极角排序一组向量（返回排好的新向量或单位向量）  
  `sortPolarAngle` `sortPolarAngleNor`
* 找到多边形内的所有凹点（返回点list或者序号list）  
  `getConcavePoints` `getConcavePointIndices`
* 从一组向量中找与输入目标夹角最小者，不区分正负角（返回向量）   
  `findClosestVec`

#### 二维相交相关

* 检查两个WB_Segment是否相交（用WB_GeometryOP）   
  `checkWB_SegmentIntersect`
* 检查两个线型对象是否相交    
  `checkRaySegmentIntersection` `checkLineSegmentIntersection`
* 检查射线与多段线是否相交    
  `checkRayPolyLineIntersection`
* 求任意两个线型对象交点（需输入类型：line, ray, segment）  
  `simpleLineElementsIntersect2D`
* 求射线、直线与多边形交点  
  `rayPolygonIntersect2D` `linePolygonIntersect2D`
* 求线段与多段线交点  
  `segmentPolyLineIntersect2D`
* 求射线与多边形交点，返回按照与指定点升序排序的交点所在边序号  
  `rayPolygonIntersectIndices2D`
* 将线段延长或剪切至多边形最近的交点  
  `extendSegmentToPolygon`
* 将多边形内的线段两端延长至多边形的交点（起点在多边形内）  
  `extendSegmentToPolygonBothSides`

#### 二维距离相关

* 从一组线段中找到与目标点距离最近的点  
  `closestPointToLineList`
* 从一个WB_Polygon，一组线段中找到与目标点距离最近边的序号  
  `closestSegment`

#### 二维位置判断相关

* 判断点是否在直线/射线/线段上（有epsilon）  
  `pointOnLine` `pointOnRay` `pointOnSegment`
* 从一系列ZLine中找到点在哪条线上，返回线两边的端点  
  `pointOnWhichZLine`
* 找到点在多边形哪条边上（可返回WB_Segment, ZLine, 或两顶点序号）  
  `pointOnWhichWB_Segment` `pointOnWhichPolyEdge` `pointOnWhichPolyEdgeIndices`
* 从一组多边形中找到包含输入点的那一个（返回序号）  
  `pointInWhichPolygon`

#### 二维轮廓相关

* 计算多边形或多段线上一点距离形状起点的沿线距离  
  `distFromStart`
* 输入一个多边形和一个多边形上的点，输入距离，找到沿多边形轮廓移动一定距离后的两个点  
  `pointsOnEdgeByDist`
* 找到多段线上曲率最大的点/顶点序号  
  `maxCurvaturePt` `maxCurvatureC`
* 输入步长，将多边形或多段线轮廓按步长剖分，得到所有点（最后一段步长必然不足长）   
  `splitPolygonEdgeByStep` `splitPolyLineByStep`
* 输入步长与抖动范围，剖分多段线或多边形的边，得到所有点（最后一段步长必然不足长）  
  `splitPolyLineByRandomStep`
* 输入步长，剖分多段线或多边形的边 (WB_PolyLine)，返回剖分点与所在边序号的LinkedHashMap  
  `splitPolyLineByStepWithDir`
* 给定阈值上下限，将多边形或多段线按阈值内最大值等分，得到所有点    
  `splitPolygonEdgeByThreshold` `splitPolyLineByThreshold`
* 给定阈值上下限，剖分多段线(WB_PolyLine)，返回剖分点与所在边序号的LinkedHashMap    
  `splitPolyLineByThresholdWithDir`
* 给定阈值上下限，剖分多段线的每条边(WB_PolyLine)，即剖分结果一定包含每个顶点，但步长不同  
  `splitPolyLineEachEdgeByThreshold`
* 输入等分数量，将多边形或多段线等分，得到所有点   
  `splitPolygonEdge` `splitPolyLineEdge`

#### 多边形工具

* 通过一系列点计算其围合的多边形面积  
  `areaFromPoints`
* 计算多边形/多段线的总长（替代HE_Mesh方法）    
  `getPolyLength`
* 给定距离，得到多边形/多段线从起点出发沿线的点（替代HE_Mesh方法）    
  `getPointOnPolyEdge`
* 计算多边形最小外接矩形的朝向（与较长边垂直）  
  `miniRectDir`
* 使WB_Polygon点序反向，支持带洞  
  `reversePolygon`
* 检查两个WB_Polygon是否同向  
  `isNormalEquals`
* 使WB_Polygon法向量Z坐标为正或为负，支持带洞（不是拍平到xy平面，只是翻个面）  
  `PolygonFaceUp` `PolygonFaceDown`
* 找到多边形中最长边和最短边，返回序号  
  `getLongestAndShortestSegment`
* 偏移多边形的某一条边线（默认输入为正向首尾相接多边形）  
  `offsetWB_PolygonSegment`
* 偏移多边形的若干条边线（默认输入为正向首尾相接多边形），返回多段线或多边形  
  `offsetWB_PolygonSegments`
* 多边形倒圆角  
  `roundPolygon`

#### 其他

* 输入Geometry，设置Jts的Precision Model  
  `applyJtsPrecisionModel`
* 找到一组点的中心点  
  `centerFromPoints`
* 将OBB对半切分   
  `halvingOBB`
* 得到基本OBBTree  
  `performOBBTree`
* 将Coordinate的z坐标从NaN改为0  
  `filterNaN`

## **ZGraphMath**

与自定义图结构相关的计算工具

* 找到graph上某节点开始点沿边移动一定距离后的若干个点，返回结果点/沿途的所有线段/沿途节点  
  `pointsOnGraphByDist` `segmentsOnGraphByDist` `nodesOnGraphByDist`
* 给定步长，将graph每条edge按照步长剖分，返回全部剖分点  
  `splitGraphEachEdgeByStep`
* 给定步长和起点，得到整个graph的剖分点  
  `splitGraphEdgeByStep`
* 给定起点，递归遍历出graph上从起点出发的所有链（返回ZEdge或ZNode）  
  `getAllChainEdgeFromNode` `getAllChainNodeFromNode`
* 找到一个无环图上的最长链  
  `longestChain`

## **ZFileOP**

本地文件读取与写入方法

## **ZMath**

一些数学工具

#### 角度相关

* 半角公式、二倍角公式

#### 数组相关

* 创建一组升序数列
* 最大最小值序号
* 升序排序的原序号

#### 映射与随机相关

* 给定范围生成随机数
* 给定范围生成一组随机数
* 给定范围生成随机整数
* 给定长度生成一组随机整数（摇号）
* 将目标数字从一个范围映射到另一个范围内的对应数字

#### 其他

* 阶乘

## **ZPermuCombi**

整数序号的排列组合

## **ZSubdivision**

（需完善）若干种多边形剖分模式

## **ZTransform**

常用库几何数据的相互转换  
*大部分仅支持简单多边形，部分支持带洞*

#### IGeo <-> WB

* IPoint <-> WB_Point
* IPoint -> WB_Point 带缩放
* ICurve -> WB_Geometry 根据点数和闭合与否返回WB_Polygon / WB_Polyline / WB_Segment
* ICurve -> WB_Geometry 根据点数和闭合与否返回WB_Polygon / WB_Polyline / WB_Segment，带缩放
* ICurve <-> WB_PolyLine 带缩放
* WB_Coord -> IVec
* IVecI -> WB_Point
* WB_Segment -> ICurve
* WB_Circle <-> ICircle

#### IGeo <-> jts

* IPoint <-> Coordinate
* IPoint <-> Point
* IVecI -> Coordinate
* IVecI -> Point
* Coordinate -> IVec
* Point -> IVec
* ICurve -> Geometry 根据点数和闭合与否返回Polygon / LineString

#### WB <-> jts

* WB_Coord <-> Point
* WB_Coord <-> Coordinate
* WB_Polygon <-> Polygon (支持带洞)
* LineString <-> WB_PolyLine
* WB_Segment -> LineString
* WB_Polygon <-> LineString (支持带洞)
* Polygon -> WB_PolyLine (支持带洞)

#### WB <-> WB

* WB_Polygon -> WB_Polygon 检查WB_Polygon第一点与最后一点是否重合，不重合则加上
* WB_Polygon -> WB_PolyLine
* WB_AABB -> WB_AABB offset WB_AABB

#### jts <-> jts

* Polygon <-> LineString

## **ZFactory**

包含了jts的GeometryFactory和HE_Mesh的WB_GeometryFactory，以及其他创建命令：

#### 创建几何图形

* 通过List创建LingString和Polygon  
  `createLineStringFromList` `createPolygonFromList`
* 从一组首尾相接的线段创建Line String / WB_PolyLine, 若有多条，则取最长  
  `createLineString` `createWB_PolyLine`
* 将一系列首尾相接线段合成一组WB_PolyLine list  
  `createWB_PolyLineList`
* 将WB_PolyLine / Line String在端点处断开，创建一组新折线  
  `breakWB_PolyLine` `breakLineString`
* 通过两个折线上的点来截取LingString  
  `cutLineString2Points`
* 给定线段序号，从WB_Polygon中创建一截WB_PolyLine  
  `createPolylineFromPolygon`
* 将一条LineString向两端头微微延长一定距离（规避误差）  
  `createExtendedLineString`
* 通过圆心、起点、终点创建圆弧（需指定顺逆时针）  
  `createArc`

#### 创建图

* 从一组线段创建ZGraph  
  `createZGraphFromSegments`
* 从一组点根据距离创建最小生成树(Prim)  
  `createMiniSpanningTree`
  