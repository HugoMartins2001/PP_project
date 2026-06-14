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
    private GeographicCoordinates coordinates;

    public InstitutionImpl(String name) {
        this.aidboxCounter = 0;
        this.vehicleCounter = 0;
        this.pickingMapCounter = 0;
        this.name = name;
        this.aidboxes = new AidBox[MAX_AIDBOX_SIZE];
        this.vehicles = new Vehicle[MAX_VEHICLE_SIZE];
        this.pickingMaps = new PickingMap[MAX_PICKINGMAP_SIZE];
        this.coordinates = new GeographicCoordinatesImpl(0, 0);
    }

    public InstitutionImpl(String name, GeographicCoordinates coordinates) {
        this(name);
        this.coordinates = coordinates;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public GeographicCoordinates getCoordinates() {
        return this.coordinates;
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
                return false;
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
                if (i != j && containers[i].getType().equals(containers[j].getType())) {
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

        for (int i = 0; i < this.aidboxCounter; i++) {
            Container[] containers = this.aidboxes[i].getContainers();
            for (Container containertemp : containers) {
                if (containertemp.equals(container)) {
                    return true;
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
            VehicleImpl original = (VehicleImpl) this.vehicles[i];
            temp[counter++] = new VehicleImpl(
                    original.getCode(),
                    original.getSupplyType(),
                    original.getMaxCapacity(),
                    original.getStatus()
            );
        }
        return temp;
    }

    @Override
    public boolean addVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("The Vehicle is null");
        }
        for (int i = 0; i < vehicleCounter; i++) {
            if (this.vehicles[i].equals(vehicle)) {
                return false;
            }
        }
        if (this.vehicleCounter == this.vehicles.length) {
            expandVehicles();
        }
        this.vehicles[this.vehicleCounter++] = vehicle;
        return true;
    }

    private void expandVehicles() {
        Vehicle[] newVehicles = new Vehicle[vehicles.length * EXPAND];
        for (int i = 0; i < vehicles.length; i++) {
            newVehicles[i] = vehicles[i];
        }
        vehicles = newVehicles;
    }

    @Override
    public void disableVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("Vehicle is null");
        }
        int index = -1;
        for (int i = 0; i < vehicleCounter; i++) {
            if (this.vehicles[i].equals(vehicle)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new VehicleException("Vehicle does not exist");
        }
        VehicleImpl vehicleImpl = (VehicleImpl) this.vehicles[index];
        if (!vehicleImpl.getStatus()) {
            throw new VehicleException("Vehicle is already disabled");
        }
        vehicleImpl.setStatus(false);
    }

    @Override
    public void enableVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("Vehicle is null");
        }
        int index = -1;
        for (int i = 0; i < vehicleCounter; i++) {
            if (this.vehicles[i].equals(vehicle)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new VehicleException("Vehicle does not exist");
        }
        VehicleImpl vehicleImpl = (VehicleImpl) this.vehicles[index];
        if (vehicleImpl.getStatus()) {
            throw new VehicleException("Vehicle is already enabled");
        }
        vehicleImpl.setStatus(true);
    }

    @Override
    public PickingMap[] getPickingMaps() {
        return Arrays.copyOf(this.pickingMaps, this.pickingMapCounter);
    }

    @Override
    public PickingMap[] getPickingMaps(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return new PickingMap[0];
        }
        int count = 0;
        for (int i = 0; i < this.pickingMapCounter; i++) {
            LocalDateTime mapDate = this.pickingMaps[i].getDate();
            if ((mapDate.isEqual(from) || mapDate.isAfter(from)) && (mapDate.isEqual(to) || mapDate.isBefore(to))) {
                count++;
            }
        }
        PickingMap[] result = new PickingMap[count];
        int idx = 0;
        for (int i = 0; i < this.pickingMapCounter; i++) {
            LocalDateTime mapDate = this.pickingMaps[i].getDate();
            if ((mapDate.isEqual(from) || mapDate.isAfter(from)) && (mapDate.isEqual(to) || mapDate.isBefore(to))) {
                result[idx++] = this.pickingMaps[i];
            }
        }
        return result;
    }

    @Override
    public PickingMap getCurrentPickingMap() throws PickingMapException {
        if (this.pickingMapCounter == 0) {
            throw new PickingMapException("There are no picking maps in the institution");
        }
        PickingMap mostRecent = this.pickingMaps[0];
        for (int i = 1; i < this.pickingMapCounter; i++) {
            if (this.pickingMaps[i].getDate().isAfter(mostRecent.getDate())) {
                mostRecent = this.pickingMaps[i];
            }
        }
        return mostRecent;
    }

    @Override
    public boolean addPickingMap(PickingMap pm) throws PickingMapException {
        if (pm == null) {
            throw new PickingMapException("PickingMap is null");
        }
        for (int i = 0; i < this.pickingMapCounter; i++) {
            if (this.pickingMaps[i].equals(pm)) {
                return false;
            }
        }
        if (this.pickingMapCounter == this.pickingMaps.length) {
            expandPickingMaps();
        }
        this.pickingMaps[this.pickingMapCounter++] = pm;
        return true;
    }

    private void expandPickingMaps() {
        PickingMap[] newMaps = new PickingMap[this.pickingMaps.length * EXPAND];
        for (int i = 0; i < this.pickingMaps.length; i++) {
            newMaps[i] = this.pickingMaps[i];
        }
        this.pickingMaps = newMaps;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public double getDistance(AidBox aidbox) throws AidBoxException {
        if (aidbox == null) {
            throw new AidBoxException("Aid Box is null");
        }
        boolean exists = false;
        for (int i = 0; i < aidboxCounter; i++) {
            if (this.aidboxes[i].equals(aidbox)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            throw new AidBoxException("Aid Box does not exist in the institution");
        }
        if (this.coordinates == null || aidbox.getCoordinates() == null) {
            return 0.0;
        }
        return calculateDistance(
                this.coordinates.getLatitude(),
                this.coordinates.getLongitude(),
                aidbox.getCoordinates().getLatitude(),
                aidbox.getCoordinates().getLongitude()
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Institution Name: ").append(this.name).append("\n");
        if (this.coordinates != null) {
            sb.append("Coordinates: (")
              .append(this.coordinates.getLatitude()).append(", ")
              .append(this.coordinates.getLongitude()).append(")\n");
        }
        sb.append("Number of Aid Boxes: ").append(this.aidboxCounter).append("\n");
        sb.append("Number of Vehicles: ").append(this.vehicleCounter).append("\n");
        sb.append("Number of Picking Maps: ").append(this.pickingMapCounter).append("\n");
        return sb.toString();
    }

}
