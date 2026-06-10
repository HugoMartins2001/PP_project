package com.estg.pickingManagement;

import com.estg.core.ItemType;

/**
 *
 * @author hugol
 */
public class VehicleImpl implements Vehicle {

    private ItemType supplyType;
    private double capacities;
    private boolean status;

    public VehicleImpl(String code, ItemType supplyType) {
        this.supplyType = supplyType;
        this.status = true; // Assume vehicle is initially enabled
    }

    public VehicleImpl(String code, ItemType supplyType, boolean status) {
        this.supplyType = supplyType;
        this.status = status;
    }

    @Override
    public ItemType getSupplyType() {
        return this.supplyType;
    }

    @Override
    public double getMaxCapacity() {
        return this.capacities;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Type of supply : ").append(supplyType).append("\n");
        sb.append("Max capacity : ").append(capacities).append("\n");
        return sb.toString();
    }

}
