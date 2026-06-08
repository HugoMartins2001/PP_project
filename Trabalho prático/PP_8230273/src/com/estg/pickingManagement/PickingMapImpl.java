package com.estg.pickingManagement;

import java.time.LocalDateTime;

/**
 *
 * @author hugol
 */
public class PickingMapImpl implements PickingMap {

    private static final int MAX_ROUTES = 10;

    private LocalDateTime date;
    private Route[] routes;

    public PickingMapImpl(LocalDateTime date) {
        this.date = date;
        this.routes = new Route[MAX_ROUTES];
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public Route[] getRoutes() {
        return this.routes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Data : ").append(date).append("\n");
        sb.append("Rotas : ").append(routes).append("\n");
        return sb.toString();
    }

}
