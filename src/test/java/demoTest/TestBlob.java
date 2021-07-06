package demoTest;

import basicGeometry.ZPoint;
import processing.core.PApplet;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/6/17
 * @time 17:51
 */
public class TestBlob extends PApplet {

    private int blobNum = 64;

    private double blobR = 16;
    private int nodesPerBlob = 12;

    private double spring = 0.2;
    private double damping = 0.1;
    private double gas = -0.02;
    private double nominalArea;
    private double gravity = 0.01;
    private double springLength;

    private Blob[] blobs;

    /* ------------- settings ------------- */

    public void settings() {
        size(400, 300);
    }

    /* ------------- setup ------------- */

    public void setup() {
        nominalArea = Math.PI * Math.pow(blobR, 2);
        springLength = (2 * Math.PI * blobR) / nodesPerBlob;
        blobs = new Blob[blobNum];
        int blobRows = (int) Math.floor(width / (2 * blobR));
        for (int i = 0; i < blobs.length; i++) {
            ZPoint blobCenter = new ZPoint(
                    blobR + (i % blobRows) * 2 * blobR,
                    height - (blobR + Math.floor(i / blobRows) * 2 * blobR * Math.pow(3, 0.5) / 2)
            );
            if (Math.floor(i / blobRows) % 2 == 0) {
                double x = blobCenter.xd();
                blobCenter.setX(x + blobR);
            }
            blobs[i] = new Blob(blobCenter);
        }
        stroke(0);
        fill(0, 255, 0);
        strokeWeight(0.5f);
        smooth();
        ellipseMode(CENTER);
    }

    class Blob {
        BlobNode[] nodes;
        double blobArea;
        ZPoint centroid;

        Blob(ZPoint center) {
            nodes = new BlobNode[nodesPerBlob];
            for (int i = 0; i < nodes.length; i++) {
                double theta = Math.PI * 2 * i / nodes.length;
                nodes[i] = new BlobNode(new ZPoint(
                        center.xd() + blobR * Math.cos(theta), center.yd() + blobR * Math.sin(theta))
                );
            }
        }

        void update() {
            for (int i = 0; i < nodes.length; i++) {
                int j = i + 1;
                if (j >= nodes.length) {
                    j = 0;
                }
                ZPoint dLoc = nodes[j].loc.sub(nodes[i].loc);

                double restore = springLength - dLoc.getLength();
                restore *= spring;
                nodes[i].n.add(dLoc);
                nodes[j].n.add(dLoc);

                ZPoint dLocUnit = dLoc.normalize();
                ZPoint dv = nodes[j].v.sub(nodes[i].v);
                double damper = dv.dot2D(dLocUnit) * -damping;

                ZPoint F = dLocUnit.scaleTo(restore + damper);
                nodes[j].f.add(F);
                nodes[i].f.sub(F);
            }
            double pressure = gas * (nominalArea - blobArea + 32);
            for (int i = 0; i < nodes.length; i++) {
                nodes[i].update(pressure);
            }
        }

        void getArea() {
            blobArea = 0;
            for (int i = 0; i < nodes.length; i++) {
                int j = i + 1;
                if (j >= nodes.length) {
                    j = 0;
                }
                blobArea += nodes[i].loc.xd() * nodes[j].loc.yd();
                blobArea -= nodes[j].loc.xd() * nodes[i].loc.yd();
            }
            blobArea *= 0.5;
        }

        void getCentroid() {
            float cx = 0;
            float cy = 0;
            for (int i = 0; i < nodes.length; i++) {
                int j = i + 1;
                if (j >= nodes.length) {
                    j = 0;
                }
                cx += (nodes[i].loc.xd() + nodes[j].loc.xd()) * (nodes[i].loc.xd() * nodes[j].loc.yd() - nodes[j].loc.xd() * nodes[i].loc.yd());
                cy += (nodes[i].loc.yd() + nodes[j].loc.yd())
                        * (nodes[i].loc.xd() * nodes[j].loc.yd()
                        - nodes[j].loc.xd() * nodes[i].loc.yd());
            }
            centroid = new ZPoint(cx, cy);
            centroid.scaleSelf(1.0 / (6.0 * blobArea));
        }

        void draw() {
            beginShape();
            for (int i = 0; i < nodes.length; i++) {
                vertex(nodes[i].loc.xf(), nodes[i].loc.yf());
            }
            endShape(CLOSE);
            ellipse(centroid.xf(), centroid.yf(), (float) blobR / 2, (float) blobR / 2);
        }

        ZPoint forceField(ZPoint testPoint, boolean Newtons3rd) {
            ZPoint[] sides = new ZPoint[nodes.length];
            ZPoint[] intermediates = new ZPoint[nodes.length];
            ZPoint[] normals = new ZPoint[nodes.length];
            double[] normDist = new double[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                intermediates[i] = new ZPoint();
            }

            for (int i = 0; i < nodes.length; i++) {
                int j = i + 1;
                if (j >= nodes.length) {
                    j = 0;
                }
                sides[i] = nodes[j].loc.sub(nodes[i].loc);
                sides[i].normalizeSelf();
                normals[i] = new ZPoint(-sides[i].yd(), sides[i].xd());
                normDist[i] = testPoint.sub(nodes[i].loc).dot2D(normals[i]);

                intermediates[i].add(sides[i]);
                intermediates[j].add(sides[i]);

                intermediates[i].normalizeSelf();
            }

            double minDist = 1000000000;
            ZPoint fieldForce = new ZPoint();
            int equalOpposite = -1;
            for (int i = 0; i < nodes.length; i++) {
                int j = i + 1;
                if (j >= nodes.length) {
                    j = 0;
                }
                if (testPoint.sub(nodes[i].loc).dot2D(intermediates[i]) > 0
                        && testPoint.sub(nodes[j].loc).dot2D(intermediates[j]) < 0
                        && testPoint.sub(nodes[i].loc).dot2D(normals[i]) > 0
                ) {
                    if (minDist > normDist[i]) {
                        minDist = normDist[i];
                        fieldForce = normals[i].scaleTo(minDist * -spring);
                        equalOpposite = i;
                    }
                }
            }
            if (equalOpposite >= 0 && Newtons3rd) {
                int i = equalOpposite;
                int j = i + 1;
                if (j >= nodes.length) {
                    j = 0;
                }
                nodes[i].f.sub(fieldForce.scaleTo(0.5));
                nodes[j].f.sub(fieldForce.scaleTo(0.5));
            }
            return (fieldForce);
        }

        boolean pointIsInPolygon(ZPoint testPoint) {
            boolean c = false;
            for (int i = 0; i < nodes.length; i++) {
                int j = i - 1;
                if (j == -1) {
                    j = nodes.length - 1;
                }
                if (((nodes[i].loc.yd() > testPoint.yd()) != (nodes[j].loc.yd() > testPoint.yd())) &&
                        (testPoint.xd() < (nodes[j].loc.xd() - nodes[i].loc.xd())
                                * (testPoint.yd() - nodes[i].loc.yd())
                                / (nodes[j].loc.yd() - nodes[i].loc.yd()) + nodes[i].loc.xd()))
                    c = !c;
            }
            return c;
        }
    }

    class BlobNode {
        ZPoint loc;
        ZPoint v;
        ZPoint f;
        ZPoint n;

        BlobNode(ZPoint xo) {
            loc = xo;
            v = new ZPoint();
            f = new ZPoint();
            n = new ZPoint();
        }

        void update(double pressure) {
            boolean dampen = false;
            if (loc.xd() < 0) {
                f.setX(f.xd() - loc.xd() * spring);
                dampen = true;
            }


            if (loc.xd() > width) {
                f.setX(f.xd() - (loc.xd() - width) * spring);
                dampen = true;
            }

            if (loc.yd() < 0) {
                f.setY(f.yd() - loc.yd() * spring);
                dampen = true;
            }


            if (loc.yd() > height) {
                f.setY(f.yd() - (loc.yd() - height) * spring);
                dampen = true;
            }

            if (dampen) {
                v.scaleSelf(0.2);
            }

            n = new ZPoint(-n.yd(), n.xd());
            n.normalize();
            f.add(n.scaleTo(pressure));
            n = new ZPoint();
            v.add(f);
            f = new ZPoint(0, gravity);
            loc.add(v);
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        if (frameCount % 60 == 1) {
            println(frameRate);
        }
        background(255);
        ZPoint mouseV = new ZPoint(mouseX, mouseY);
        ZPoint pmouseV = new ZPoint(pmouseX, pmouseY);
        float pushrad = 8;
        if (mousePressed) {
            for (int i = 0; i < blobs.length; i++) {
                for (int j = 0; j < blobs[i].nodes.length; j++) {
                    ZPoint dx = blobs[i].nodes[j].loc.sub(mouseV);
                    if (dx.getLength() < pushrad) {
                        blobs[i].nodes[j].v.add((mouseV.sub(pmouseV).sub(blobs[i].nodes[j].v)).scaleTo(0.5));
                    }
                }
            }
        }
        for (int i = 0; i < blobs.length; i++) {
            blobs[i].getArea();
            blobs[i].getCentroid();
        }
        for (int i = 1; i < blobs.length; i++) {
            for (int j = 0; j < i; j++) {
                ZPoint dx = blobs[j].centroid.sub(blobs[i].centroid);
                if (dx.getLength() < blobR) {
                    double restore = (blobR - dx.getLength()) * spring;
                    dx.normalize();
                    dx.scaleSelf(restore);
                    for (int k = 0; k < blobs[j].nodes.length; k++) {
                        blobs[j].nodes[k].f.add(dx);
                    }
                    for (int k = 0; k < blobs[i].nodes.length; k++) {
                        blobs[i].nodes[k].f.sub(dx);
                    }
                }
            }
        }
        for (int i = 1; i < blobs.length; i++) {
            for (int j = 0; j < i; j++) {
                for (int k = 0; k < blobs[j].nodes.length; k++) {
                    if (blobs[i].pointIsInPolygon(blobs[j].nodes[k].loc)) {
                        blobs[j].nodes[k].f.add(blobs[i].forceField(
                                blobs[j].nodes[k].loc, true));
                    }
                }
                for (int k = 0; k < blobs[i].nodes.length; k++) {
                    if (blobs[j].pointIsInPolygon(blobs[i].nodes[k].loc)) {
                        blobs[i].nodes[k].f.add(blobs[j].forceField(
                                blobs[i].nodes[k].loc, true));
                    }
                }
            }
        }
        for (int i = 0; i < blobs.length; i++) {
            blobs[i].update();
            blobs[i].draw();
        }
    }

}
