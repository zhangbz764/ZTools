package demoTest;

import processing.core.PApplet;

/**
 * ZTools Test
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/9
 * @time 16:22
 */
public class ToolTests {
    public static void main(String[] args) {

        /* 测试极角排序、角平分线等向量运算 */
//        PApplet.main("demoTest.TestPolarAngle");

        /* 测试jts几何图形布尔关系 */
//        PApplet.main("demoTest.TestGeoRelation");

        /* 测试多边形等分、沿多边形边找点、多边形边线offset */
//        PApplet.main("demoTest.TestPolySplit");

        /* 测试hemesh里的最近点计算、线段trim和extend计算、op里的检测线段二维相交 */
//        PApplet.main("demoTest.TestDistCloset");

        /* 测试ZSkeleton */
//        PApplet.main("demoTest.TestSkeleton");

        /* 测试hemesh的mesh以及union */
//        PApplet.main("demoTest.TestHe_Mesh");

        /* 测试jts的convexhull、找凹点、直线多边形交点及排序 */
        PApplet.main("demoTest.TestConvexHull");

        /* 测试ZSkeleton带洞 */
//        PApplet.main("demoTest.TestCampSkeleton");

        /* 测试几种剖分方法 */
//        PApplet.main("demoTest.TestSubdivision");

    }
}
