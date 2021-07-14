package demoTest2;

import basicGeometry.ZLine;
import basicGeometry.ZPoint;
import processing.core.PApplet;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/7/7
 * @time 21:00
 */
public class BallBounce extends PApplet {
    /* ------------- settings ------------- */

    public void settings() {
        size(500, 500, P3D);
    }

    /* ------------- setup ------------- */

    private float[] ball;
    private float[] segment;
    private float deltaX = 5;
    private float deltaY = 5;

    private ZPoint p = new ZPoint(200, 0);
    private ZPoint q;

    public void setup() {
        this.ball = new float[]{random(width), random(height)};
        this.segment = new float[]{250, 250, 250, 400};

        ZLine l = new ZLine(250, 250, 250, 400);
        q = p.mirrorPoint(l);
        System.out.println(q);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        ellipse(p.xf(), p.yf(), 10, 10);
        ellipse(q.xf(), q.yf(), 10, 10);

        ellipse(ball[0], ball[1], 10, 10);
        line(segment[0], segment[1], segment[2], segment[3]);

        ball[0] += deltaX;
        ball[1] += deltaY;
        if (ball[0] >= width || ball[0] <= 0) {
            deltaX = -deltaX;
        }
        if (ball[1] >= height || ball[1] <= 0) {
            deltaY = -deltaY;
        }

        if (ball[0] >= segment[0]
                && ball[1] <= segment[2]
                && (ball[1] - (0.5 * ball[0] + 50)) * (ball[1] + deltaY - (0.5 * (ball[0] + deltaX) + 50)) < 0
        ) {
            deltaX = random(2, 4);
            deltaY = random(2, 4);
        }
    }

    public void keyPressed() {
        if (key == 's') {
            ball[0] += deltaX;
            ball[1] += deltaY;
        }
    }

}
