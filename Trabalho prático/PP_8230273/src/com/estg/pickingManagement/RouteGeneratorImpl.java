package com.estg.pickingManagement;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.Institution;
import com.estg.core.ItemType;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hugol
 */
public class RouteGeneratorImpl implements RouteGenerator {

    @Override
    public Route[] generateRoutes(Institution institution, Strategy strategy, RouteValidator routeValidator, Report report) {
        if (institution == null || strategy == null) {
            return new Route[0];
        }
        Route[] routes = strategy.generate(institution, routeValidator);
        
        // Populate the report
        if (report instanceof ReportImpl) {
            ReportImpl reportImpl = (ReportImpl) report;
            
            // 1. Used vehicles: number of routes generated
            int usedVehiclesCount = routes.length;
            reportImpl.setUsedVehicles(usedVehiclesCount);
            
            // 2. Picked containers count, total distance, total duration
            int pickedContainersCount = 0;
            double totalDistance = 0;
            double totalDuration = 0;
            Set<String> pickedContainerKeys = new HashSet<>();
            
            for (Route r : routes) {
                if (r == null) continue;
                AidBox[] routeBoxes = r.getRoute();
                if (routeBoxes != null) {
                    pickedContainersCount += routeBoxes.length;
                    ItemType type = r.getVehicle().getSupplyType();
                    for (AidBox ab : routeBoxes) {
                        if (ab != null) {
                            pickedContainerKeys.add(ab.getCode() + "_" + type);
                        }
                    }
                }
                totalDistance += r.getTotalDistance();
                totalDuration += r.getTotalDuration();
            }
            
            reportImpl.setPickedContainers(pickedContainersCount);
            reportImpl.setTotalDistance(totalDistance);
            reportImpl.setTotalDuration(totalDuration);
            
            // 3. Non-picked containers: total compatible containers in the institution minus picked
            int totalContainersInInstitution = 0;
            AidBox[] allAidBoxes = institution.getAidBoxes();
            if (allAidBoxes != null) {
                for (AidBox ab : allAidBoxes) {
                    if (ab != null) {
                        Container[] containers = ab.getContainers();
                        if (containers != null) {
                            for (Container c : containers) {
                                if (c != null) {
                                    totalContainersInInstitution++;
                                }
                            }
                        }
                    }
                }
            }
            int nonPickedContainersCount = totalContainersInInstitution - pickedContainerKeys.size();
            if (nonPickedContainersCount < 0) {
                nonPickedContainersCount = 0;
            }
            reportImpl.setNonPickedContainers(nonPickedContainersCount);
            
            // 4. Not used vehicles: total vehicles minus used
            Vehicle[] allVehicles = institution.getVehicles();
            int totalVehiclesCount = (allVehicles != null) ? allVehicles.length : 0;
            int notUsedVehiclesCount = totalVehiclesCount - usedVehiclesCount;
            if (notUsedVehiclesCount < 0) {
                notUsedVehiclesCount = 0;
            }
            reportImpl.setNotUsedVehicles(notUsedVehiclesCount);
            
            // 5. Date
            reportImpl.setDate(LocalDateTime.now());
        }
        
        return routes;
    }
    
}
