package com.estg.core;

/**
 *
 * @author hugol
 */
public class GeographicCoordinatesImpl implements GeographicCoordinates {

    private double latitude;
    private double longitude;

    public GeographicCoordinatesImpl(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public double getLatitude() {
        return this.latitude;
    }

    @Override
    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Latitude : ").append(latitude).append("\n");
        sb.append("Longitude : ").append(longitude).append("\n");
        return sb.toString();
    }

}
