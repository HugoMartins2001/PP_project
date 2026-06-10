package com.estg.pickingManagement;

import com.estg.core.AidBox;
import com.estg.core.Container;
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
    public void insertAfter(AidBox aidbox, AidBox aidbox1) throws RouteException {
    }

    @Override
    public AidBox[] getRoute() {
    }

    @Override
    public Vehicle getVehicle() {
    }

    @Override
    public double getTotalDistance() {
    }

    @Override
    public double getTotalDuration() {
    }

}
