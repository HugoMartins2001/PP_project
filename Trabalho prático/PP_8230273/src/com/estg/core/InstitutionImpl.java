package com.estg.core;

import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.MeasurementException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.core.exceptions.VehicleException;
import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.VehicleImpl;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 *
 * @author hugol
 */
public class InstitutionImpl implements Institution {

    private static final int MAX_AIDBOX_SIZE = 10;
    private static final int MAX_VEHICLE_SIZE = 10;
    private static final int MAX_PICKINGMAP_SIZE = 10;
    private static final int EXPAND = 2;

    private String name;
    private int aidboxCounter;
    private int vehicleCounter;
    private int pickingMapCounter;
    private Vehicle[] vehicles;
    private PickingMap[] pickingMaps;
    private Measurement[] measurements;
    private AidBox[] aidboxes;
    private Container[] containers;
    private double distance;

    public InstitutionImpl(String name) {
        this.aidboxCounter = 0;
        this.pickingMapCounter = 0;
        this.name = name;
        this.aidboxes = new AidBox[MAX_AIDBOX_SIZE];
        this.vehicles = new Vehicle[MAX_VEHICLE_SIZE];
        this.pickingMaps = new PickingMap[MAX_PICKINGMAP_SIZE];
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean addAidBox(AidBox aidbox) throws AidBoxException {
        if (aidbox == null) {
            throw new AidBoxException("The Aid Box is null");
        }

        if (invalidAidBox(aidbox)) {
            throw new AidBoxException("The Aid Box have duplicate containers of a certain waste type");
        }

        for (int i = 0; i < aidboxCounter; i++) {
            if (aidbox.equals(this.aidboxes[i])) {
                return false; //se já existir
            }
        }

        if (this.aidboxCounter == this.aidboxes.length) {
            expandAidBoxes();
        }

        this.aidboxes[this.aidboxCounter++] = aidbox;

        return true;
    }

    private boolean invalidAidBox(AidBox aidbox) {
        Container[] containers = aidbox.getContainers();

        for (int i = 0; i < containers.length; i++) {
            for (int j = 0; j < containers.length; j++) {
                if (containers[i].getType().equals(containers[j].getType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void expandAidBoxes() {
        AidBox[] newAidbox = new AidBox[aidboxes.length * EXPAND];

        for (int i = 0; i < aidboxes.length; i++) {
            newAidbox[i] = aidboxes[i];
        }

        aidboxes = newAidbox;
    }

    @Override
    public boolean addMeasurement(Measurement measurement, Container container) throws ContainerException, MeasurementException {
        if (!containsContainer(container)) {
            throw new ContainerException("The container doesn't exist");
        }

        if (measurement.getValue() < 0 || measurement.getValue() > container.getCapacity()) {
            throw new MeasurementException("The value is lesser than 0 and higher to the capacity");
        }

        Measurement[] measurements = container.getMeasurements();
        for (Measurement measurementtemp : measurements) {
            if (measurementtemp.getDate().equals(measurement.getDate())) {
                return false; // Measurement with the same date already exists
            }
        }

        container.addMeasurement(measurement);

        return true;

    }

    private boolean containsContainer(Container container) {
        if (this.aidboxCounter == 0) {
            return false;
        }

        for (AidBox aidbox : this.aidboxes) {
            Container[] containers = aidbox.getContainers();
            for (Container containertemp : containers) {
                if (containertemp.equals(container)) {
                    return true; // Foi encontrado uma aidbox
                }
            }
        }
        return false;
    }

    @Override
    public AidBox[] getAidBoxes() {
        return Arrays.copyOf(aidboxes, aidboxCounter);
    }

    @Override
    public Container getContainer(AidBox aidbox, ItemType item) throws ContainerException {
        if (aidbox == null) {
            throw new ContainerException("The container does not exist");
        } else if (aidbox.getContainer(item) == null) {
            throw new ContainerException("The container with the given item type doesn't exist");
        }

        if (findAidbox(aidbox) == -1) {
            throw new ContainerException("The aidbox doess not exist");
        } else {
            return aidboxes[findAidbox(aidbox)].getContainer(item);
        }
    }

    private int findAidbox(AidBox aidbox) {
        for (int i = 0; i < aidboxCounter; i++) {
            if (aidboxes[i].equals(aidbox)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Vehicle[] getVehicles() {
        Vehicle[] temp = new Vehicle[this.vehicleCounter];
        int counter = 0;
        for (int i = 0; i < vehicleCounter; i++) {
            temp[counter++] = new VehicleImpl(this.vehicles[i].getCode(),
                    ((VehicleImpl) this.vehicles[i]).getMaxCapacity(),
                    ((VehicleImpl) this.vehicles[i]).getStatus());
        }
        return temp;
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
