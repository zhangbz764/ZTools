package advancedGeometry.tensorField;

import basicGeometry.ZFactory;
import math.ZGeoMath;
import org.apache.commons.lang3.ArrayUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.math.Vector2D;

import java.util.Arrays;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/9/10
 * @time 20:58
 */
public class ZTensorCross {
    private Coordinate cen;
    private Vector2D[] crossVec;

    /* ------------- constructor ------------- */

    public ZTensorCross() {
        this.cen = new Coordinate(0, 0);
        this.crossVec = new Vector2D[]{
                new Vector2D(1, 0),
                new Vector2D(0, 1),
                new Vector2D(-1, 0),
                new Vector2D(0, -1),
        };

    }

    public ZTensorCross(Coordinate cen, Vector2D major) {
        this.cen = cen;

        Vector2D nor = major.normalize();
        this.crossVec = new Vector2D[]{
                nor,
                new Vector2D(-nor.getY(), nor.getX()),
                new Vector2D(-nor.getX(), -nor.getY()),
                new Vector2D(nor.getY(), -nor.getX()),
        };
    }

    /* ------------- member function ------------- */

    public Vector2D getClosestVec(Vector2D ori) {
        return ZGeoMath.getClosestVec(ori, crossVec);
    }

    public Vector2D getNextVec(Vector2D v) {
        if (ArrayUtils.contains(crossVec, v)) {
            int i = ArrayUtils.indexOf(crossVec, v);
            return crossVec[(i + 1) % crossVec.length];
        } else {
            return null;
        }
    }

    public Vector2D getPrevVec(Vector2D v) {
        if (ArrayUtils.contains(crossVec, v)) {
            int i = ArrayUtils.indexOf(crossVec, v);
            return crossVec[(i + crossVec.length - 1) % crossVec.length];
        } else {
            return null;
        }
    }

    public Vector2D getOppositeVec(Vector2D v) {
        if (ArrayUtils.contains(crossVec, v)) {
            int i = ArrayUtils.indexOf(crossVec, v);
            return crossVec[(i + 2) % crossVec.length];
        } else {
            return null;
        }
    }

    /* ------------- setter & getter ------------- */

    public Coordinate getCen() {
        return cen;
    }

    public Vector2D[] getCrossVec() {
        return crossVec;
    }

    public Point getCenAsPt() {
        return ZFactory.jtsgf.createPoint(cen);
    }

    /* ------------- draw ------------- */
}
