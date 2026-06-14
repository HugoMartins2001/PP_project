package com.estg.pickingManagement;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.ItemType;
import com.estg.core.Measurement;

/**
 *
 * @author hugol
 */
public class RouteValidatorImpl implements RouteValidator {

    @Override
    public boolean validate(Route route, AidBox aidbox) {
        if (route == null || aidbox == null) {
            return false;
        }
        Vehicle vehicle = route.getVehicle();
        if (vehicle == null) {
            return false;
        }
        ItemType supplyType = vehicle.getSupplyType();
        Container newContainer = aidbox.getContainer(supplyType);
        if (newContainer == null) {
            return false;
        }
        
        double currentLoad = 0;
        AidBox[] routeBoxes = route.getRoute();
        if (routeBoxes != null) {
            for (AidBox ab : routeBoxes) {
                if (ab != null) {
                    Container c = ab.getContainer(supplyType);
                    currentLoad += getLatestMeasurementValue(c);
                }
            }
        }
        
        double newLoad = getLatestMeasurementValue(newContainer);
        return (currentLoad + newLoad) <= vehicle.getMaxCapacity();
    }

    private double getLatestMeasurementValue(Container container) {
        if (container == null) {
            return 0;
        }
        Measurement[] ms = container.getMeasurements();
        if (ms == null || ms.length == 0) {
            return 0;
        }
        return ms[ms.length - 1].getValue();
    }

}
