# 自己的java工具包 慎用
# 与其叫readme,不如叫remindme
## ZPoint
自定义的点数据类型，可代表点或向量
## ZNode
`extends ZPoint` 代表graph中的node
## ZLine
自定义的线数据类型，可代表直线、射线、线段，也可转化为`p+td`的形式
## ZEdge
`extends ZLine` 代表graph中的edge
## ZSkeleton
计算直骨架（straight skeleton），用到[campskeleton](https://github.com/twak/campskeleton "campskeleton")这个包。可提取：
* 全部骨架边`getAllEdges`
* 全部top边`getTopEdges`
* 全部side边`getSideEdges`
* 全部bottom边`getBottomEdges`
* 全部脊线`getRidges`
* 全部脊线点`getRidgePoints`
## ZGeomath
 * 求到角的角平分线向量
 * 按极角排序一组向量（返回原列表序号）
 * 按极角排序一组向量（返回排好的新向量或单位向量）
 * 找到多边形内的所有凹点（返回点list或者序号list）
 * 从一组向量中找与输入目标夹角最小者，不区分正负角（返回向量）
 * 求任意两个线型对象交点（需输入类型：line, ray, segment）
 * 求射线与多边形交点
 * 求射线与多边形交点，返回按照与指定点升序排序的交点所在边序号
 * 将线段延长或剪切至多边形最近的交点
 * 判断点是否在直线/射线/线段上（有epsilon）
 * 找到点在多边形哪条边上（可返回WB_Segment, ZLine, 或两顶点序号）
 * 从一组多边形中找到包含输入点的那一个（返回序号）
 * 输入Geometry，设置Jts的Precision Model
 * 使WB_Polygon点序反向
 * 使WB_Polygon法向量Z坐标为正或为负（不是拍平到xy平面，只是翻个面）
 * 输入一个多边形和一个多边形上的点，输入距离，找到沿多边形轮廓走一定距离后的两个点
 * 输入步长，将多边形或多段线轮廓按步长剖分，得到所有点（最后一段步长必然不足长）
 * 输入步长阈值，将多边形或多段线按阈值内最大值等分，得到所有点
 * 输入等分数量，将多边形或多段线等分，得到所有点

增加中...
## ZMath
* 从一组double数组中找到最大值的序号
* 从一组double数组中找到最小值的序号
* 把一组double数组按升序排序（返回一组序号）
* 将目标数字从一个范围映射到另一个范围内的对应数字

增加中...
## ZTransform
Igeo，Jts，HE_Mesh三个包的几何图形转换

**hemesh是傻逼！**

增加中...
