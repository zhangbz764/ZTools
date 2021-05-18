# 个人java工具包 慎用

详细说明见 [instruction_detail](https://github.com/Agent14zbz/ZTools/blob/main/instruction_detail.md "instruction_detail.md")
测试类见 [demoTest](https://github.com/Agent14zbz/ZTools/tree/main/src/test/java/demoTest "demoTest")

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

计算直骨架（straight skeleton），可以生成2D或3D结果，支持带洞多边形  
#### External Dependencies:
[campskeleton](https://github.com/twak/campskeleton "campskeleton")

## **ZCatmullRom**

创建Catmull-Rom曲线  
inspired by [CatmullRomSpline](https://github.com/jurajstrecha/CatmullRomSpline)

## **ZLargestRectangle**

粒子群算法优化计算多边形的最大内接矩形  
inspired by [LargestRectangle](https://github.com/dawnwords/LargestRectangle "LargestRectangle")
#### External Dependencies:  
[JSwarm-PSO](http://jswarm-pso.sourceforge.net/ "JSwarm-PSO")

## **ZRectCover**

使用轮廓点+射线的方法近似找到给定数量的最小矩形覆盖

## **ZGeomath**

一些自定义的几何计算工具  

## **ZGraphMath**

与自定义图结构相关的计算工具

## **ZMath**

一些数学工具

## **ZPermuCombi**

整数的排列组合

## **ZSubdivision**

（尚未完成）若干种多边形剖分模式

## **ZTransform**

常用库几何数据的相互转换  
*目前主要针对简单多边形，部分涉及带洞*

## **ZFactory**

包含了jts的GeometryFactory和HE_Mesh的WB_GeometryFactory，以及其他创建命令：

## 增加中...
