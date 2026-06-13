package com.estg.pickingManagement;

import java.time.LocalDateTime;

public class ReportImpl implements Report {

    private int usedVehicles;
    private int pickedContainers;
    private double totalDistance;
    private double totalDuration;
    private int nonPickedContainers;
    private int notUsedVehicles;
    private LocalDateTime date;

    public ReportImpl(int usedVehicles, int pickedContainers, double totalDistance, double totalDuration, int nonPickedContainers, int notUsedVehicles, LocalDateTime date) {
        this.usedVehicles = usedVehicles;
        this.pickedContainers = pickedContainers;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.nonPickedContainers = nonPickedContainers;
        this.notUsedVehicles = notUsedVehicles;
        this.date = date;
    }

    @Override
    public int getUsedVehicles() {
        return this.usedVehicles;
    }

    @Override
    public int getPickedContainers() {
        return this.pickedContainers;
    }

    @Override
    public double getTotalDistance() {
        return this.totalDistance;
    }

    @Override
    public double getTotalDuration() {
        return this.totalDuration;
    }

    @Override
    public int getNonPickedContainers() {
        return this.nonPickedContainers;
    }

    @Override
    public int getNotUsedVehicles() {
        return this.notUsedVehicles;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Data : ").append(date).append("\n");
        sb.append("Data : ").append(pickedContainers).append("\n");
        sb.append("Data : ").append(nonPickedContainers).append("\n");
        sb.append("Data : ").append(totalDistance).append("\n");
        sb.append("Data : ").append(totalDuration).append("\n");
        sb.append("Data : ").append(usedVehicles).append("\n");
        sb.append("Data : ").append(notUsedVehicles).append("\n");
        return sb.toString();
    }
}
