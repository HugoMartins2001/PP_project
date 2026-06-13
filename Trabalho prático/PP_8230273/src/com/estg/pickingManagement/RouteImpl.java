package com.estg.pickingManagement;

import com.estg.core.AidBox;
import com.estg.core.AidBoxImpl;
import com.estg.core.Container;
import com.estg.core.exceptions.AidBoxException;
import com.estg.pickingManagement.exceptions.RouteException;

/**
 *
 * @author hugol
 */
public class RouteImpl implements Route {

    private final static int EXPAND = 2;
    private final static int INITIAL_AIDBOX_ARRAY = 10;

    private int aidboxCounter;
    private AidBox[] route;
    private Vehicle vehicle;
    private Report report;

    public RouteImpl(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.route = new AidBox[INITIAL_AIDBOX_ARRAY];
        this.aidboxCounter = 0;
    }

    public RouteImpl(Vehicle vehicle, Report report) {
        this.vehicle = vehicle;
        this.report = report;
        this.route = new AidBox[INITIAL_AIDBOX_ARRAY];
        this.aidboxCounter = 0;
    }

    public RouteImpl(Vehicle vehicle, AidBox[] route, Report report) {
        this.vehicle = vehicle;
        this.route = route;
        this.aidboxCounter = route.length;
        this.report = report;
    }

    @Override
    public void addAidBox(AidBox aidbox) throws RouteException {
        if (aidbox == null) {
            throw new RouteException("The aidbox is null!");
        }

        if (containsAidBox(aidbox)) {
            throw new RouteException("The aidbox is already in the route");
        }

        if (!AidboxIsCompatible(aidbox)) {
            throw new RouteException("The Aid Box is not compatible with the Vehicle of the route");
        }

        if (this.route.length == this.aidboxCounter) {
            ExpandRoute();
        }

        this.route[this.aidboxCounter++] = aidbox;
    }

    private void ExpandRoute() {
        AidBox[] newRoute = new AidBox[route.length * EXPAND];
        for (int i = 0; i < route.length; i++) {
            newRoute[i] = route[i];
        }

        route = newRoute;
    }

    private boolean AidboxIsCompatible(AidBox aidbox) {
        Container[] containers = aidbox.getContainers();
        for (int i = 0; i < containers.length; i++) {
            if (containers[i] != null) {
                if (vehicle.getMaxCapacity() != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public AidBox removeAidBox(AidBox aidbox) throws RouteException {
        if (aidbox == null) {
            throw new RouteException("The aidbox cannot be null!");
        }

        int index = this.getAidBoxIndex(aidbox);

        if (index == -1) {
            throw new RouteException("The aidbox is not on the route!");
        }

        for (int i = index; i < this.aidboxCounter - 1; i++) {
            this.route[i] = this.route[i + 1];
        }

        this.route[--this.aidboxCounter] = null;

        return aidbox;
    }

    private int getAidBoxIndex(AidBox aidbox) {
        for (int i = 0; i < this.aidboxCounter; i++) {
            if (this.route[i].equals(aidbox)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean containsAidBox(AidBox aidbox) {
        for (int i = 0; i < aidboxCounter; i++) {
            if (aidbox.equals(this.route[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void replaceAidBox(AidBox from, AidBox to) throws RouteException {
        if (from == null || to == null) {
            throw new RouteException("The aidbox to replace or the aidbox to replace with cannot be null!");
        }
        int index1 = this.getAidBoxIndex(from);

        if (index1 == -1) {
            throw new RouteException("The aidbox to replace is not on the route!");
        }
        int index = this.getAidBoxIndex(to);

        if (index != -1) {
            throw new RouteException("The aidbox to insert is on the route!");
        }
        if (!AidboxIsCompatible(to)) {
            throw new RouteException("AidBox to insert is not compatible with the Vehicle of the route");
        }

        route[index1] = to;
    }

    @Override
    public void insertAfter(AidBox after, AidBox toInsert) throws RouteException {
        if (after == null || toInsert == null) {
            throw new RouteException("Any Aid Box is null");
        }

        int indexAfter = this.getAidBoxIndex(after);
        if (indexAfter == -1) {
            throw new RouteException("The Aid Box to replace is not in the route");
        }

        if (containsAidBox(toInsert)) {
            throw new RouteException("The Aid Box to insert is already in the route");
        }

        if (!AidboxIsCompatible(toInsert)) {
            throw new RouteException("The Aid Box to insert is not compatible with the Vehicle of the route");
        }

        // Expand the array if it has reached its limit
        if (this.route.length == this.aidboxCounter) {
            ExpandRoute();
        }

        // Shift elements to the right to make room AFTER the found AidBox
        for (int i = this.aidboxCounter; i > indexAfter + 1; i--) {
            this.route[i] = this.route[i - 1];
        }

        // Insert the new AidBox
        this.route[indexAfter + 1] = toInsert;
        this.aidboxCounter++;
    }

    @Override
    public AidBox[] getRoute() {
        // Creates a new array to prevent external modification of the internal route array.
        // It's sized exactly to aidboxCounter to avoid returning empty trailing nulls.
        AidBox[] routeCopy = new AidBox[this.aidboxCounter];
        int counter = 0;
        for (int i = 0; i < this.aidboxCounter; i++) {
            try {
                Object clone = ((AidBoxImpl) this.route[i]).clone();
                routeCopy[counter++] = (AidBox) clone;
            } catch (CloneNotSupportedException exception) {
                System.out.println("Error while cloning route!");
            }
        }
        return routeCopy;
    }

    @Override
    public Vehicle getVehicle() {
        return this.vehicle;
    }

    @Override
    public double getTotalDistance() {
        double totalDistance = 0;

        for (int i = 0; i < this.aidboxCounter - 1; i++) {
            try {
                totalDistance += this.route[i].getDistance(this.route[i + 1]);
            } catch (AidBoxException exception) {
                totalDistance += 0;
            }
        }

        return totalDistance;
    }

    @Override
    public double getTotalDuration() {
        double totalDuration = 0;

        for (int i = 0; i < this.aidboxCounter - 1; i++) {
            try {
                totalDuration += this.route[i].getDuration(this.route[i + 1]);
            } catch (AidBoxException exception) {
                totalDuration += 0;
            }
        }

        return totalDuration;
    }

}
