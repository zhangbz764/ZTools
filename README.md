# 个人java工具包 慎用

详细说明见 instruction_detail.md  
测试类见 src/test/java/demoTest

#### Global Dependencies:

[Processing 3.3.7](https://processing.org/)  
[jts 1.16.1](https://github.com/locationtech/jts)  
[HE_Mesh 2019](https://github.com/wblut/HE_Mesh)  
[igeo 0.9.3.0](http://igeo.jp/)

## **ZPoint**

自定义的点数据类型，可代表点或向量

## **ZNode**

`extends ZPoint` 代表graph中的node

## **ZLine**

自定义的线数据类型，可代表直线、射线、线段，也可转化为 `p+td` 的形式

## **ZEdge**

`extends ZLine` 代表graph中的edge

## **ZGraph**

（尚未完成）图数据结构，包含若干节点ZNode，连接边ZEdge，记录相互引用关系

## **ZSkeleton**

计算直骨架（straight skeleton），可以生成2D或3Dskeleton结果，支持带洞多边形。  
#### External Dependencies:
[campskeleton](https://github.com/twak/campskeleton "campskeleton")

## **ZLargestRectangleRatio**

计算给定长宽比的最大内接矩形  
#### External Dependencies:  
[LargestRectangle](https://github.com/dawnwords/LargestRectangle "LargestRectangle")  
[JSwarm-PSO](http://jswarm-pso.sourceforge.net/ "JSwarm-PSO")

## **ZGeomath**

一些自定义的几何计算工具  

## **ZGraphMath**

与自定义图结构相关的计算工具

## **ZMath**

一些数学工具

## **ZSubdivision**

（尚未完成）若干种多边形剖分模式

## **ZTransform**

常用库几何数据的相互转换  
*目前主要针对简单多边形，部分涉及带洞*

## **ZGeoFactory**

包含了jts的GeometryFactory和HE_Mesh的WB_GeometryFactory，以及其他创建命令：

## 增加中...
