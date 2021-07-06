package advancedGeometry;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/6/17
 * @time 16:09
 */
public class ZBlob {
//    private int blobNum;
//
//    private double blobR;
//    private int nodesPerBlob = 12;
//    private int nodeNum;
//
//    private double spring = 0.2;
//    private double damping = 0.1;
//    private double gas = -0.02;
//    private double nominalArea;
//    private double gravity = 0.01;
//    private double springLength;
//
//    private Blob[] blobs;

    /* ------------- constructor ------------- */

    public ZBlob(double nominalArea, double springLength, int blobNum) {

    }

    /* ------------- member function ------------- */

    /* ------------- inner class ------------- */

//    class Blob {
//        BlobNode[] nodes;
//        double blobArea;
//        ZPoint centroid;
//
//        Blob(ZPoint xo) {
//            nodes = new BlobNode[nodesPerBlob];
//            for (int i = 0; i < nodes.length; i++) {
//                double phi = Math.PI * 2 * i / nodes.length;
//                nodes[i] = new BlobNode(new ZPoint(
//                        xo.xd() + blobR * Math.cos(phi), xo.yd() + blobR * Math.sin(phi))
//                );
//            }
//        }
//
//        void update() {
//            for (int i = 0; i < nodes.length; i++) {
//                int j = i + 1;
//                if (j >= nodes.length) {
//                    j = 0;
//                }
//                ZPoint dx = nodes[j].loc.sub(nodes[i].loc);
//
//                double restore = springLength - dx.getLength();
//                restore *= spring;
//                nodes[i].n.add(dx);
//                nodes[j].n.add(dx);
//                dx.normalize();
//                ZPoint dv = nodes[j].v.sub(nodes[i].v);
//                double damper = dv.dot2D(dx) * -damping;
//
//                ZPoint F = dx.scaleTo(restore + damper);
//                nodes[j].f.add(F);
//                nodes[i].f.sub(F);
//            }
//            double pressure = gas * (nominalArea - blobArea + 32);
//            for (int i = 0; i < nodes.length; i++) {
//                nodes[i].update(pressure);
//            }
//        }
//
//        void getArea() {
//            blobArea = 0;
//            for (int i = 0; i < nodes.length; i++) {
//                int j = i + 1;
//                if (j >= nodes.length) {
//                    j = 0;
//                }
//                blobArea += nodes[i].loc.xd() * nodes[j].loc.yd();
//                blobArea -= nodes[j].loc.xd() * nodes[i].loc.yd();
//            }
//            blobArea *= 0.5;
//        }
//
//        void getCentroid() {
//            float cx = 0;
//            float cy = 0;
//            for (int i = 0; i < nodes.length; i++) {
//                int j = i + 1;
//                if (j >= nodes.length) {
//                    j = 0;
//                }
//                cx += (nodes[i].loc.xd() + nodes[j].loc.xd())
//                        * (nodes[i].loc.xd() * nodes[j].loc.yd()
//                        - nodes[j].loc.xd() * nodes[i].loc.yd());
//                cy += (nodes[i].loc.yd() + nodes[j].loc.yd())
//                        * (nodes[i].loc.xd() * nodes[j].loc.yd()
//                        - nodes[j].loc.xd() * nodes[i].loc.yd());
//            }
//            centroid = new ZPoint(cx, cy);
//            centroid.scaleSelf(1.0 / (6.0 * blobArea));
//        }
//
////        void draw() {
////            beginShape();
////            for (int i = 0; i < nodes.length; i++) {
////                vertex(nodes[i].loc.x, nodes[i].loc.y);
////            }
////            endShape(CLOSE);
////            ellipse(centroid.x, centroid.y, radius / 2, radius / 2);
////        }
//
//        ZPoint forceField(ZPoint testPoint, boolean Newtons3rd) {
//            ZPoint[] sides = new ZPoint[nodes.length];
//            ZPoint[] intermediates = new ZPoint[nodes.length];
//            ZPoint[] normals = new ZPoint[nodes.length];
//            boolean[] isInside = new boolean[nodes.length];
//            double[] normDist = new double[nodes.length];
//            for (int i = 0; i < nodes.length; i++) {
//                intermediates[i] = new ZPoint();
//            }
//
//            for (int i = 0; i < nodes.length; i++) {
//                int j = i + 1;
//                if (j >= nodes.length) {
//                    j = 0;
//                }
//                sides[i] = nodes[j].loc.sub(nodes[i].loc);
//                sides[i].normalize();
//                normals[i] = new ZPoint(-sides[i].yd(), sides[i].xd());
//                normDist[i] = testPoint.sub(nodes[i].loc).dot2D(normals[i]);
//                if (normDist[i] > 0) {
//                    isInside[i] = true;
//                } else {
//                    isInside[i] = false;
//                }
//                intermediates[i].add(sides[i]);
//                intermediates[j].add(sides[i]);
//            }
//            for (int i = 0; i < nodes.length; i++) {
//                intermediates[i].normalize();
//            }
//            double minDist = 1000000000;
//            ZPoint fieldForce = new ZPoint();
//            int equalOpposite = -1;
//            for (int i = 0; i < nodes.length; i++) {
//                int j = i + 1;
//                if (j >= nodes.length) {
//                    j = 0;
//                }
//                if (testPoint.sub(nodes[i].loc).dot2D(intermediates[i]) > 0
//                        && testPoint.sub(nodes[j].loc).dot2D(intermediates[j]) < 0
//                        && testPoint.sub(nodes[i].loc).dot2D(normals[i]) > 0
//                ) {
//                    if (minDist > normDist[i]) {
//                        minDist = normDist[i];
//                        fieldForce = normals[i].scaleTo(minDist * -spring);
//                        equalOpposite = i;
//                    }
//                }
//            }
//            if (equalOpposite >= 0 && Newtons3rd) {
//                int i = equalOpposite;
//                int j = i + 1;
//                if (j >= nodes.length) {
//                    j = 0;
//                }
//                nodes[i].f.sub(fieldForce.scaleTo(0.5));
//                nodes[j].f.sub(fieldForce.scaleTo(0.5));
//            }
//            return (fieldForce);
//        }
//
//        boolean pointIsInPolygon(ZPoint testPoint) {
//            boolean c = false;
//            for (int i = 0; i < nodes.length; i++) {
//                int j = i - 1;
//                if (j == -1) {
//                    j = nodes.length - 1;
//                }
//                if (((nodes[i].loc.yd() > testPoint.yd()) != (nodes[j].loc.yd() > testPoint.yd())) &&
//                        (testPoint.xd() < (nodes[j].loc.xd() - nodes[i].loc.xd())
//                                * (testPoint.yd() - nodes[i].loc.yd())
//                                / (nodes[j].loc.yd() - nodes[i].loc.yd()) + nodes[i].loc.xd()))
//                    c = !c;
//            }
//            return c;
//        }
//    }
//
//    class BlobNode {
//        ZPoint loc;
//        ZPoint v;
//        ZPoint f;
//        ZPoint n;
//
//        BlobNode(ZPoint xo) {
//            loc = xo;
//            v = new ZPoint();
//            f = new ZPoint();
//            n = new ZPoint();
//        }
//
//        void update(double pressure) {
//            boolean dampen = false;
//            if (loc.xd() < 0) {
//                f.setX(f.xd() - loc.xd() * spring);
//                dampen = true;
//            }
//
//
//            if (loc.xd() > width) {
//                f.xd() -= (loc.xd() - width) * spring;
//                dampen = true;
//            }
//
//            if (loc.yd() < 0) {
//                f.setY(f.yd() - loc.yd() * spring);
//                dampen = true;
//            }
//
//
//            if (loc.yd() > height) {
//                f.yd() -= (loc.yd() - height) * spring;
//                dampen = true;
//            }
//
//            if (dampen) {
//                v.scaleSelf(0.2);
//            }
//
//            n = new ZPoint(-n.yd(), n.xd());
//            n.normalize();
//            f.add(n.scaleTo(pressure));
//            n = new ZPoint();
//            v.add(f);
//            f = new ZPoint(0, gravity);
//            loc.add(v);
//        }
//    }

    /* ------------- setter & getter ------------- */



    /* ------------- draw ------------- */
}
