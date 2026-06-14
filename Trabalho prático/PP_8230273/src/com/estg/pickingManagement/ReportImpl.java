package com.estg.pickingManagement;

import java.time.LocalDateTime;

public class ReportImpl implements Report, Cloneable {

    private int usedVehicles;
    private int pickedContainers;
    private double totalDistance;
    private double totalDuration;
    private int nonPickedContainers;
    private int notUsedVehicles;
    private LocalDateTime date;

    public ReportImpl() {
        this.usedVehicles = 0;
        this.pickedContainers = 0;
        this.totalDistance = 0.0;
        this.totalDuration = 0.0;
        this.nonPickedContainers = 0;
        this.notUsedVehicles = 0;
        this.date = LocalDateTime.now();
    }

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

    public void setUsedVehicles(int usedVehicles) {
        this.usedVehicles = usedVehicles;
    }

    @Override
    public int getPickedContainers() {
        return this.pickedContainers;
    }

    public void setPickedContainers(int pickedContainers) {
        this.pickedContainers = pickedContainers;
    }

    @Override
    public double getTotalDistance() {
        return this.totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    @Override
    public double getTotalDuration() {
        return this.totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    @Override
    public int getNonPickedContainers() {
        return this.nonPickedContainers;
    }

    public void setNonPickedContainers(int nonPickedContainers) {
        this.nonPickedContainers = nonPickedContainers;
    }

    @Override
    public int getNotUsedVehicles() {
        return this.notUsedVehicles;
    }

    public void setNotUsedVehicles(int notUsedVehicles) {
        this.notUsedVehicles = notUsedVehicles;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        sb.append("Date : ").append(date.format(formatter)).append("\n");
        sb.append("Picked Containers : ").append(pickedContainers).append("\n");
        sb.append("Non Picked Containers : ").append(nonPickedContainers).append("\n");
        sb.append("Total Distance : ").append(totalDistance).append("\n");
        sb.append("Total Duration : ").append(totalDuration).append("\n");
        sb.append("Used Vehicles : ").append(usedVehicles).append("\n");
        sb.append("Not Used Vehicles : ").append(notUsedVehicles).append("\n");
        return sb.toString();
    }
}
