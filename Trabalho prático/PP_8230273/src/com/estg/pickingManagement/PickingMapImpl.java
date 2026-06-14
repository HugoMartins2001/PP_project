package com.estg.pickingManagement;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 *
 * @author hugol
 */
public class PickingMapImpl implements PickingMap {

    private LocalDateTime date;
    private Route[] routes;
    private int routeCounter;

    public PickingMapImpl(LocalDateTime date) {
        this.date = date;
        this.routes = new Route[10];
        this.routeCounter = 0;
    }

    public PickingMapImpl(LocalDateTime date, Route[] routes) {
        this.date = date;
        this.routes = routes;
        this.routeCounter = routes.length;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public Route[] getRoutes() {
        return Arrays.copyOf(this.routes, this.routeCounter);
    }

    public void addRoute(Route route) {
        if (route == null) {
            return;
        }
        if (this.routeCounter == this.routes.length) {
            this.routes = Arrays.copyOf(this.routes, this.routes.length * 2);
        }
        this.routes[this.routeCounter++] = route;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        sb.append("Data : ").append(date.format(formatter)).append("\n");
        sb.append("Rotas :\n");
        Route[] rts = getRoutes();
        if (rts == null || rts.length == 0) {
            sb.append("  Nenhuma rota gerada.\n");
        } else {
            for (int i = 0; i < rts.length; i++) {
                Route r = rts[i];
                if (r == null) continue;
                sb.append("  - Rota ").append(i + 1).append(":\n");
                sb.append("    Veículo: ").append(((VehicleImpl) r.getVehicle()).getCode()).append(" (").append(r.getVehicle().getSupplyType()).append(")\n");
                sb.append("    Distância: ").append(String.format("%.2f", r.getTotalDistance())).append(" km\n");
                sb.append("    Duração: ").append(String.format("%.2f", r.getTotalDuration())).append(" min\n");
                com.estg.core.AidBox[] routeBoxes = r.getRoute();
                sb.append("    Trajeto: Base");
                if (routeBoxes != null) {
                    for (int j = 0; j < routeBoxes.length; j++) {
                        if (routeBoxes[j] != null) {
                            sb.append(" -> ").append(routeBoxes[j].getCode());
                        }
                    }
                }
                sb.append(" -> Base\n");
            }
        }
        return sb.toString();
    }

}
