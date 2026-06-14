package com.estg.pickingManagement;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.Institution;
import com.estg.core.ItemType;
import com.estg.core.Measurement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hugol
 */
public class StrategyImpl implements Strategy {

    @Override
    public Route[] generate(Institution institution, RouteValidator routeValidator) {
        if (institution == null) {
            return new Route[0];
        }
        Vehicle[] vehicles = institution.getVehicles();
        AidBox[] aidBoxes = institution.getAidBoxes();
        if (vehicles == null || aidBoxes == null) {
            return new Route[0];
        }

        // Count active vehicles
        int activeVehiclesCount = 0;
        for (Vehicle v : vehicles) {
            if (v instanceof VehicleImpl && ((VehicleImpl) v).getStatus()) {
                activeVehiclesCount++;
            }
        }
        Vehicle[] activeVehicles = new Vehicle[activeVehiclesCount];
        int idx = 0;
        for (Vehicle v : vehicles) {
            if (v instanceof VehicleImpl && ((VehicleImpl) v).getStatus()) {
                activeVehicles[idx++] = v;
            }
        }

        Route[] tempRoutes = new Route[activeVehicles.length];
        int routeCount = 0;
        Set<String> pickedContainers = new HashSet<>();

        for (Vehicle vehicle : activeVehicles) {
            RouteImpl route = new RouteImpl(vehicle);
            ItemType supplyType = vehicle.getSupplyType();

            for (AidBox aidbox : aidBoxes) {
                if (aidbox == null) continue;

                // Check if this container was already picked by another route
                String key = aidbox.getCode() + "_" + supplyType;
                if (pickedContainers.contains(key)) {
                    continue;
                }

                Container container = aidbox.getContainer(supplyType);
                if (container == null) {
                    continue;
                }

                // Check if there is actual waste to pick
                double load = getLatestValue(container);
                if (load <= 0) {
                    continue;
                }

                // Validate if we can add this aidbox to the route (capacity check)
                if (routeValidator != null && !routeValidator.validate(route, aidbox)) {
                    continue;
                }

                // Add it to the route
                try {
                    route.addAidBox(aidbox);
                    pickedContainers.add(key);
                } catch (Exception e) {
                    // Ignore and try next
                }
            }

            // If the route is not empty, keep it
            if (route.getRoute().length > 0) {
                tempRoutes[routeCount++] = route;
            }
        }

        return Arrays.copyOf(tempRoutes, routeCount);
    }

    private double getLatestValue(Container c) {
        if (c == null) return 0;
        Measurement[] ms = c.getMeasurements();
        if (ms == null || ms.length == 0) return 0;
        return ms[ms.length - 1].getValue();
    }

}
