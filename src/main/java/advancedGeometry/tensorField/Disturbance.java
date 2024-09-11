package advancedGeometry.tensorField;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import wblut.geom.WB_AABB;

import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/9/10
 * @time 16:08
 */
public interface Disturbance {
    public abstract Envelope unionEnvelope(Envelope env);

    public abstract List<LineString> toLineString();

    public abstract Geometry getGeometry();
}
