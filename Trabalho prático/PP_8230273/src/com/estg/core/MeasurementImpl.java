package com.estg.core;

import java.time.LocalDateTime;

/**
 *
 * @author hugol
 */
public class MeasurementImpl implements Measurement {

    private LocalDateTime date;
    private double value;

    public MeasurementImpl(LocalDateTime date, double value) {
        this.date = date;
        this.value = value;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Data : ").append(date).append("\n");
        sb.append("Valor : ").append(value).append("\n");
        return sb.toString();
    }

}
