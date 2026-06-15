package com.estg.core;

import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import java.util.Arrays;

/**
 *
 * @author hugol
 */
public class AidBoxImpl implements AidBox, Cloneable {

    private static final int EXPAND = 2;

    private String code;
    private String zone;
    private String RefLocal;
    private Container[] containers;
    private int containerCounter;
    private GeographicCoordinates coordinates;

    public AidBoxImpl(String code, String zone, String RefLocal) {
        this.code = code;
        this.zone = zone;
        this.RefLocal = RefLocal;
        this.containers = new Container[10];
        this.containerCounter = 0;
        this.coordinates = new GeographicCoordinatesImpl(0, 0);
    }

    public AidBoxImpl(String code, String zone, String RefLocal, GeographicCoordinates coordinates) {
        this.code = code;
        this.zone = zone;
        this.RefLocal = RefLocal;
        this.containers = new Container[10];
        this.containerCounter = 0;
        this.coordinates = coordinates;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getZone() {
        return this.zone;
    }

    @Override
    public String getRefLocal() {
        return this.RefLocal;
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
            throw new AidBoxException("Aid Box does not exist!");
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
    public double getDuration(AidBox aidbox) throws AidBoxException {
        if (aidbox == null) {
            throw new AidBoxException("Aid Box does not exist!");
        }
        return getDistance(aidbox) * 1.5;
    }

    @Override
    public GeographicCoordinates getCoordinates() {
        return this.coordinates;
    }

    private void expand() {
        Container[] newContainer = new Container[containers.length * EXPAND];
        for (int i = 0; i < containers.length; i++) {
            newContainer[i] = containers[i];
        }

        containers = newContainer;
    }

    @Override
    public boolean addContainer(Container container) throws ContainerException {
        if (container == null) {
            throw new ContainerException("The container is null!");
        }

        if (findContainer(container) != -1) {
            return false;
        }

        if (getContainer(container.getType()) != null) {
            throw new ContainerException("The AidBox has already a container with the given type!");
        }

        if (this.containerCounter == this.containers.length) {
            expand();
        }

        this.containers[this.containerCounter++] = container;

        return true;
    }

    @Override
    public Container getContainer(ItemType itemtype) {
        if (this.containerCounter != 0) {
            for (int i = 0; i < this.containerCounter; i++) {
                if (this.containers[i].getType().equals(itemtype)) {
                    return this.containers[i];
                }
            }
        }
        return null;
    }

    @Override
    public Container[] getContainers() {
        return Arrays.copyOf(this.containers, this.containerCounter);
    }

    private int findContainer(Container container) {
        if (this.containerCounter == 0) {
            return -1;
        }

        for (int i = 0; i < this.containerCounter; i++) {
            if (this.containers[i].equals(container)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        AidBoxImpl other = (AidBoxImpl) obj;
        return this.code.equals(other.getCode());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AidBoxImpl cloned = (AidBoxImpl) super.clone();
        cloned.containers = new Container[this.containers.length];
        for (int i = 0; i < this.containerCounter; i++) {
            cloned.containers[i] = this.containers[i];
        }
        return cloned;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Codigo : ").append(code).append("\n");
        sb.append("Zona : ").append(zone).append("\n");
        sb.append("Referencia do Local : ").append(RefLocal).append("\n");
        return sb.toString();
    }

}
