package testUtils;

import advancedGeometry.tensorField.DisturbPoly;
import advancedGeometry.tensorField.Disturbance;
import advancedGeometry.tensorField.ZTensorCross;
import advancedGeometry.tensorField.ZTensorField;
import basicGeometry.ZFactory;
import guo_cam.CameraController;
import guo_cam.Vec_Guo;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.math.Vector2D;
import processing.core.PApplet;
import render.JtsRender;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/9/10
 * @time 17:05
 */
public class Test23RawField extends PApplet {
    public static void main(String[] args) {
        PApplet.main(Test23RawField.class.getName());
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    private JtsRender jtsRender;
    private CameraController gcam;

    private Polygon boundary;
    private List<Disturbance> disturbances;
    private ZTensorField field = new ZTensorField();
    private List<ZTensorCross> crosses;

    private double step = 20;
    private LineString divLine;

    /* ------------- setup ------------- */

    public void setup() {
        this.jtsRender = new JtsRender(this);
        this.gcam = new CameraController(this);

        this.boundary = ZFactory.createRandomPolygon(10, 200, 0.8);
        field.setRange(boundary);

        this.addDisturbances();

        field.setDisturbances(disturbances);
        this.crosses = new ArrayList<>();

        calCrossesToDisplay();


    }

    private void addDisturbances() {
        this.disturbances = new ArrayList<>();
        disturbances.add(new DisturbPoly(boundary));
    }

    private void calCrossesToDisplay() {
        Envelope envelope = boundary.getEnvelopeInternal();
        double w = envelope.getWidth();
        double h = envelope.getHeight();

        double currX = envelope.getMinX();

        while (currX < envelope.getMaxX()) {
            double currY = envelope.getMinY();
            while (currY < envelope.getMaxY()) {
                ZTensorCross tensor = field.getTensorAtPoint(new Coordinate(currX, currY));
                crosses.add(tensor);
                currY += step;
            }
            currX += step;
        }
        System.out.println(crosses.size());
    }

    /* ------------- draw ------------- */

    @Override
    public void mouseClicked() {
        Vec_Guo c = gcam.getCoordinateFromScreenOnXYPlane(mouseX, mouseY);
        System.out.println(c);
        this.divLine = field.getLineStringAlongField(new Coordinate(c.x, c.y), new Vector2D(1, 0), 20);
    }

    public void draw() {
        background(255);
        gcam.drawSystem(100);

        stroke(0);
        strokeWeight(1);
        for (Disturbance disturbance : disturbances) {
            jtsRender.drawGeometry(disturbance.getGeometry());
        }

        for (ZTensorCross cross : crosses) {
            Coordinate cen = cross.getCen();
            Vector2D v0 = cross.getCrossVec()[0];
            Vector2D v1 = cross.getCrossVec()[1];
            Vector2D v2 = cross.getCrossVec()[2];
            Vector2D v3 = cross.getCrossVec()[3];

            stroke(255, 0, 0);
            strokeWeight(2);
            line((float) cen.getX(), (float) cen.getY(), (float) (cen.getX() + v0.getX() * 5), (float) (cen.getY() + v0.getY() * 5));
            stroke(0, 0, 255);
            line((float) cen.getX(), (float) cen.getY(), (float) (cen.getX() + v1.getX() * 5), (float) (cen.getY() + v1.getY() * 5));
            stroke(0);
            strokeWeight(1);
            line((float) cen.getX(), (float) cen.getY(), (float) (cen.getX() + v2.getX() * 5), (float) (cen.getY() + v2.getY() * 5));
            line((float) cen.getX(), (float) cen.getY(), (float) (cen.getX() + v3.getX() * 5), (float) (cen.getY() + v3.getY() * 5));
        }

        if (divLine != null) {
            stroke(255, 0, 255);
            strokeWeight(3);
            jtsRender.drawGeometry(divLine);
        }

    }

}
