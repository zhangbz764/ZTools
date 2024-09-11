package advancedGeometry.tensorField;

import basicGeometry.ZFactory;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import transform.ZTransform;
import wblut.geom.WB_AABB;

import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/9/10
 * @time 17:23
 */
public class DisturbPoly implements Disturbance {
    private Polygon polygon;

    /* ------------- constructor ------------- */

    public DisturbPoly(Polygon poly) {
        this.polygon = poly;
    }

    @Override
    public Envelope unionEnvelope(Envelope env) {
        env = ZTransform.addEnvelop(env, polygon.getEnvelopeInternal());
        return env;
    }

    @Override
    public List<LineString> toLineString() {
        return ZFactory.breakGeometryToSegments(polygon);
    }

    @Override
    public Geometry getGeometry() {
        return polygon;
    }

    /* ------------- member function ------------- */


    /* ------------- setter & getter ------------- */



    /* ------------- draw ------------- */
}
