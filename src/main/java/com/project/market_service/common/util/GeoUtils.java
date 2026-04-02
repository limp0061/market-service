package com.project.market_service.common.util;

import com.project.market_service.common.exception.CommonErrorCode;
import com.project.market_service.common.exception.InvalidValueException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public final class GeoUtils {

    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    public static Point createPoint(double lng, double lat) {
        validate(lng, lat);
        return GEOMETRY_FACTORY.createPoint(new Coordinate(lng, lat)); // 그대로
    }

    private static void validate(double lng, double lat) {
        if (lat < -90 || lat > 90) {
            throw new InvalidValueException(CommonErrorCode.INVALID_INPUT);
        }
        if (lng < -180 || lng > 180) {
            throw new InvalidValueException(CommonErrorCode.INVALID_INPUT);
        }
    }
}
