package com.estg.core;

import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.MeasurementException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.core.exceptions.VehicleException;
import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Vehicle;
import java.time.LocalDateTime;

/**
 *
 * @author hugol
 */
public class InstituionImpl implements Institution{

    @Override
    public String getName() {
    }

    @Override
    public boolean addAidBox(AidBox aidbox) throws AidBoxException {
    }

    @Override
    public boolean addMeasurement(Measurement msrmnt, Container cntnr) throws ContainerException, MeasurementException {
    }

    @Override
    public AidBox[] getAidBoxes() {
    }

    @Override
    public Container getContainer(AidBox aidbox, ItemType it) throws ContainerException {
    }

    @Override
    public Vehicle[] getVehicles() {
    }

    @Override
    public boolean addVehicle(Vehicle vhcl) throws VehicleException {
    }

    @Override
    public void disableVehicle(Vehicle vhcl) throws VehicleException {
    }

    @Override
    public void enableVehicle(Vehicle vhcl) throws VehicleException {
    }

    @Override
    public PickingMap[] getPickingMaps() {
    }

    @Override
    public PickingMap[] getPickingMaps(LocalDateTime ldt, LocalDateTime ldt1) {
    }

    @Override
    public PickingMap getCurrentPickingMap() throws PickingMapException {
    }

    @Override
    public boolean addPickingMap(PickingMap pm) throws PickingMapException {
    }

    @Override
    public double getDistance(AidBox aidbox) throws AidBoxException {
    }
    
}
