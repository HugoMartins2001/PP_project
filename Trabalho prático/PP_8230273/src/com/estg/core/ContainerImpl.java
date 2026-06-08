package com.estg.core;

import com.estg.core.exceptions.MeasurementException;
import java.time.LocalDate;

/**
 *
 * @author hugol
 */
public abstract class ContainerImpl implements Container {

    private static final int INITIAL_SIZE = 10;
    private static final int EXPAND = 2;

    private double capacity;
    private String code;
    private Measurement[] measurements;
    private int measurementcounter;
    private ItemType type;

    public ContainerImpl(double capacity, String code, ItemType type) {
        this.capacity = capacity;
        this.code = code;
        this.type = type;
        this.measurements = new Measurement[INITIAL_SIZE];
        this.measurementcounter = 0;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public double getCapacity() {
        return this.capacity;
    }

    @Override
    public ItemType getType() {
        return this.type;
    }

    @Override
    public Measurement[] getMeasurements() {

    }

    @Override
    public Measurement[] getMeasurements(LocalDate date) {

    }

    @Override
    public boolean addMeasurement(Measurement measurement) throws MeasurementException {
        if (measurement == null) {
            throw new MeasurementException("The measurement cannot be null.");
        }

        if (measurement.getValue() < 0) {
            throw new MeasurementException("The measurement value cannot be less then 0");
        }

        if (this.measurementcounter != 0) {
            Measurement lastMeasurementDate = this.measurements[this.measurementcounter - 1];

            if (measurement.getDate().isBefore(measurement.getDate())) {
                throw new MeasurementException("The date cannot be older than the last date");
            }

            if (lastMeasurementDate.getDate().isEqual(measurement.getDate())) {
                if (lastMeasurementDate.getValue() != measurement.getValue()) {
                    throw new MeasurementException("The measurement for the given date already exists but the values are different");
                } else {
                    return false;
                }
            }
        }

        if (this.measurements.length == this.measurementcounter) {
            Expand();
        }

        this.measurements[this.measurementcounter++] = measurement;

        return true;
    }

    private void Expand() {
        Measurement[] newMeasurement = new Measurement[measurements.length * EXPAND];
        for (int i = 0; i < measurements.length; i++) {
            newMeasurement[i] = measurements[i];
        }

        measurements = newMeasurement;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        ContainerImpl other = (ContainerImpl) obj;
        return this.code.equals(other.getCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Código : ").append(code).append("\n");
        sb.append("Capacidade : ").append(capacity).append("\n");
        sb.append("Item type : ").append(type.ItemType_To_String(type)).append("\n");
        return sb.toString();
    }
}
