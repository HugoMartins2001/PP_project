package com.estg.pickingManagement;

import com.estg.core.ItemType;

/**
 *
 * @author hugol
 */
public class VehicleImpl implements Vehicle {

    private String code;
    private ItemType supplyType;
    private double capacity;
    private boolean status;

    public VehicleImpl(String code, ItemType supplyType) {
        this.code = code;
        this.supplyType = supplyType;
        this.capacity = 0.0;
        this.status = true;
    }

    public VehicleImpl(String code, ItemType supplyType, double capacity) {
        this.code = code;
        this.supplyType = supplyType;
        this.capacity = capacity;
        this.status = true;
    }

    public VehicleImpl(String code, ItemType supplyType, double capacity, boolean status) {
        this.code = code;
        this.supplyType = supplyType;
        this.capacity = capacity;
        this.status = status;
    }

    public String getCode() {
        return this.code;
    }

    public boolean getStatus() {
        return this.status;
    }

    public String getStatusToString() {
        return this.status ? "Active" : "Inactive";
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public ItemType getSupplyType() {
        return this.supplyType;
    }

    @Override
    public double getMaxCapacity() {
        return this.capacity;
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
        VehicleImpl other = (VehicleImpl) obj;
        if (this.code == null || other.code == null) {
            return false;
        }
        return this.code.equals(other.code);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Code : ").append(code).append("\n");
        sb.append("Type of supply : ").append(supplyType).append("\n");
        sb.append("Max capacity : ").append(capacity).append("\n");
        sb.append("Status : ").append(status ? "Active" : "Inactive").append("\n");
        return sb.toString();
    }

}
