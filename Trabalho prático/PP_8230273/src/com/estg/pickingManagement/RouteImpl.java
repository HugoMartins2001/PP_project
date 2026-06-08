package com.estg.pickingManagement;

import com.estg.core.AidBox;
import com.estg.pickingManagement.exceptions.RouteException;

/**
 *
 * @author hugol
 */
public class RouteImpl implements Route{

    @Override
    public void addAidBox(AidBox aidbox) throws RouteException {
    }

    @Override
    public AidBox removeAidBox(AidBox aidbox) throws RouteException {
    }

    @Override
    public boolean containsAidBox(AidBox aidbox) {
    }

    @Override
    public void replaceAidBox(AidBox aidbox, AidBox aidbox1) throws RouteException {
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
