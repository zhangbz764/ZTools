package advancedGeometry.subdivision;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.shape.random.RandomPointsBuilder;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/6
 * @time 15:15
 * @description
 */
public class ZSD_Voronoi extends ZSubdivision {
    private int pointNum = 20;
    private WB_Point[] voronoiGenerator;

    /* ------------- constructor ------------- */

    public ZSD_Voronoi(WB_Polygon originPolygon) {
        super(originPolygon);
//        RandomPointsBuilder randomPointsBuilder = new RandomPointsBuilder();
//        randomPointsBuilder.setExtent(ZTransform.WB_PolygonToJtsPolygon(originPolygon));
//        randomPointsBuilder.setNumPoints(20);
//
//        this.randomPoints = randomPointsBuilder.getGeometry().getCoordinates();
//        System.out.println(randomPoints.length);
    }

    @Override
    public void performDivide() {
        // get random points
        RandomPointsBuilder randomPointsBuilder = new RandomPointsBuilder();
        randomPointsBuilder.setExtent(ZTransform.WB_PolygonToJtsPolygon(super.getOriginPolygon()));
        randomPointsBuilder.setNumPoints(pointNum);
        Coordinate[] randomPoints = randomPointsBuilder.getGeometry().getCoordinates();
        System.out.println(randomPoints.length);

        // generate voronoi
        voronoiGenerator = new WB_Point[randomPoints.length];
        for (int i = 0; i < randomPoints.length; i++) {
            voronoiGenerator[i] = new WB_Point(randomPoints[i].x, randomPoints[i].y);
        }

        WB_Voronoi2D voronoi = WB_VoronoiCreator.getClippedVoronoi2D(voronoiGenerator, super.getOriginPolygon());
        List<WB_Polygon> allSubPolygons = new ArrayList<>();
        for (WB_VoronoiCell2D cell : voronoi.getCells()) {
            allSubPolygons.add(cell.getPolygon());
        }
        super.setAllSubPolygons(allSubPolygons);
        super.setRandomColor();
    }

    @Override
    public void setCellConstraint(double constraint) {
        this.pointNum = (int) constraint;
    }

    /* ------------- setter & getter ------------- */

    /* ------------- draw ------------- */

    public void display(PApplet app, WB_Render render) {
        app.pushStyle();
        super.displayWithColor(app, render);
        app.noFill();
        for (WB_Point p : voronoiGenerator) {
            app.ellipse(p.xf(), p.yf(), 5, 5);
        }
        app.popStyle();
    }
}
