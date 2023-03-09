package testMyself;

import basicGeometry.ZPoint;
import math.ZGeoMath;
import processing.core.PApplet;
import render.ZRender;

import java.util.ArrayList;
import java.util.List;

/**
 * test vector methods in ZGeoMath
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/20
 * @time 14:22
 */
public class Test6VectorTools extends PApplet {
    public void settings() {
        size(1000, 1000, P3D);
    }

    private ZPoint oo = new ZPoint(700, 200);
    private ZPoint aa = new ZPoint(800, 100);
    private ZPoint bb = new ZPoint(500, 500);

    private ZPoint[] pts = new ZPoint[6];
    private List<ZPoint> vcs;
    private ZPoint o = new ZPoint(500, 500);
    private int[] sort = new int[pts.length];

    private ZPoint bisector;

    public void randomP() {
        vcs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ZPoint pt = new ZPoint(random(200, 800), random(200, 800));
            pts[i] = pt;
            vcs.add(pt.sub(o));
        }
        sort = ZGeoMath.sortPolarAngleIndices(vcs);

    }


    public void setup() {
        println(Math.atan2(0, 20));
        randomP();
    }

    public void mouseClicked() {
        randomP();
    }

    public void draw() {
        background(255);
        ellipse((float) pts[0].xf(), (float) pts[0].yd(), 5, 5);

        for (int i = 0; i < sort.length; i++) {
            strokeWeight((i + 1) * 2);
            line((float) o.xd(), (float) o.yd(), (float) pts[sort[i]].xd(), (float) pts[sort[i]].yd());
            text(i, (float) pts[sort[i]].xd(), (float) pts[sort[i]].yd());
        }

        bb.set(mouseX, mouseY);
        line((float) oo.xd(), (float) oo.yd(), (float) aa.xd(), (float) aa.yd());
        line((float) oo.xd(), (float) oo.yd(), (float) bb.xd(), (float) bb.yd());

        ZPoint center = new ZPoint((oo.xd() + bb.xd()) * 0.5, (oo.yd() + bb.yd()) * 0.5);
        ZPoint rotate = bb.add(center.sub(bb).normalize().rotate2D(Math.PI * 1.25).scaleTo(100));

        line((float) bb.xd(), (float) bb.yd(), (float) rotate.xd(), (float) rotate.yd());

        bisector = ZGeoMath.getAngleBisectorOrdered(aa.sub(oo), bb.sub(oo));
        ZRender.drawZPointAsVec2D(this, bisector, oo, 50);
    }

}
