/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2017 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.ejs.birthchart.prototipe;

import static com.ejs.birthchart.prototipe.util.ExtendedMath.equatorialToHorizontal;
import static com.ejs.birthchart.prototipe.util.ExtendedMath.refraction;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ejs.birthchart.prototipe.param.Builder;
import com.ejs.birthchart.prototipe.param.GenericParameter;
import com.ejs.birthchart.prototipe.param.LocationParameter;
import com.ejs.birthchart.prototipe.param.TimeParameter;
import com.ejs.birthchart.prototipe.util.BaseBuilder;
import com.ejs.birthchart.prototipe.util.JulianDate;
import com.ejs.birthchart.prototipe.util.Moon;
import com.ejs.birthchart.prototipe.util.Vector;

/**
 * Calculates the position of the moon.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class MoonPosition {

    private final double azimuth;
    private final double altitude;
    private final double distance;
    private final double parallacticAngle;

    private MoonPosition(double azimuth, double altitude, double distance, double parallacticAngle) {
        this.azimuth = (toDegrees(azimuth) + 180.0) % 360.0;
        this.altitude = toDegrees(altitude);
        this.distance = distance;
        this.parallacticAngle = toDegrees(parallacticAngle);
    }

    /**
     * Starts the computation of {@link MoonPosition}.
     *
     * @return {@link Parameters} to set.
     */
    public static Parameters compute() {
        return new MoonPositionBuilder();
    }

    /**
     * Moon altitude above the horizon, in degrees.
     * <p>
     * {@code 0.0} means the moon's center is at the horizon, {@code 90.0} at the zenith
     * (straight over your head).
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * Moon azimuth, in degrees, north-based.
     * <p>
     * This is the direction along the horizon, measured from north to east. For example,
     * {@code 0.0} means north, {@code 135.0} means southeast, {@code 270.0} means west.
     */
    public double getAzimuth() {
        return azimuth;
    }

    /**
     * Distance to the moon in kilometers.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Parallactic angle of the moon, in degrees.
     */
    public double getParallacticAngle() {
        return parallacticAngle;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoonPosition[azimuth=").append(azimuth);
        sb.append("°, altitude=").append(altitude);
        sb.append("°, distance=").append(distance);
        sb.append(" km, parallacticAngle=").append(parallacticAngle);
        sb.append("°]");
        return sb.toString();
    }

    /**
     * Collects all parameters for {@link MoonPosition}.
     */
    public interface Parameters extends
            GenericParameter<Parameters>,
            LocationParameter<Parameters>,
            TimeParameter<Parameters>,
            Builder<MoonPosition> {
    }

    /**
     * Builder for {@link MoonPosition}. Performs the computations based on the
     * parameters, and creates a {@link MoonPosition} object that holds the result.
     */
    private static class MoonPositionBuilder extends BaseBuilder<Parameters> implements Parameters {
        @Override
        public MoonPosition execute() {
            JulianDate t = getJulianDate();

            double phi = getLatitudeRad();
            double lambda = getLongitudeRad();

            Vector mc = Moon.position(t);
            double h = t.getGreenwichMeanSiderealTime() + lambda - mc.getPhi();

            Vector horizontal = equatorialToHorizontal(h, mc.getTheta(), mc.getR(), phi);

            double hRef = refraction(horizontal.getTheta());

            double pa = atan2(sin(h), tan(phi) * cos(mc.getTheta())) - sin(mc.getTheta()) * cos(h);

            return new MoonPosition(horizontal.getPhi(), horizontal.getTheta() + hRef, mc.getR(), pa);
        }
    }

}
