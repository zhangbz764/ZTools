package testUtils;

import processing.core.PApplet;

/**
 * ZTools Test
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/9
 * @time 16:22
 */
public class MainTestMyself {
    public static void main(String[] args) {

        /* test jts convexhull and ZGeoMath concave points */
//        PApplet.main("demoTest.Test1ConvexConcave");

        /* test extend and trim in ZGeoMath */
//        PApplet.main("demoTest.Test2ExtendTrim");

        /* test ZSkeleton with holes */
//        PApplet.main("demoTest.Test3Skeleton");

        /* test ZCatmullRom, ZBSpline and WB_BSpline */
//        PApplet.main("demoTest.Test4Curve");

        /* test ZLargestRectangle */
        PApplet.main("testMyself.Test5LargestRect");

        /* test vector methods in ZGeoMath */
//        PApplet.main("demoTest.Test6VectorTools");

        /* test polygon tools in ZGeoMath */
//        PApplet.main("demoTest.Test7PolygonTools");

        /* test ZGraph and graph methods */
//        PApplet.main("demoTest.Test8ZGraph");

        /* test arc and circle related tools */
//        PApplet.main("demoTest.Test9ArcCircle");

        /* test shape descriptors */
//        PApplet.main("demoTest.Test10ShapeDescriptor");

        /* test K-Means clustering */
//        PApplet.main("testMyself.Test11KMeans");

//        PApplet.main("testMyself.TestZGraph2");
    }
}
