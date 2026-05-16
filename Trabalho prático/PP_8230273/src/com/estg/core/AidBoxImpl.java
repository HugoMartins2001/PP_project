package com.estg.core;

import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;

/**
 *
 * @author hugol
 */
public class AidBoxImpl implements AidBox {

    private static final int MAX_CONTAINERS = 10;
    private static final int EXPAMD = 2;

    private String Code;
    private String zone;
    private String RefLocal;
    private Container[] container;
    private int containerCounter;

    public AidBoxImpl(String code, String zone, String RefLocal) {
        this.Code = code;
        this.zone = zone;
        this.RefLocal = RefLocal;
        this.container = new Container[MAX_CONTAINERS];
        this.containerCounter = 0;

    }

    @Override
    public String getCode() {
        return this.Code;
    }

    @Override
    public String getZone() {
        return this.zone;
    }

    @Override
    public String getRefLocal() {
        return this.RefLocal;
    }

    @Override
    public double getDistance(AidBox aidbox) throws AidBoxException {
    }

    @Override
    public double getDuration(AidBox aidbox) throws AidBoxException {
    }

    @Override
    public GeographicCoordinates getCoordinates() {
    }

    private void expand() {
        Container[] newContainer = new Container[container.length * EXPAMD];
        for (int i = 0; i < container.length; i++) {
            newContainer[i] = container[i];
        }

        container = newContainer;
    }

    @Override
    public boolean addContainer(Container cntnr) throws ContainerException {
        if (cntnr == null) throws ContainerException{
            
        }
    }

    @Override
    public Container getContainer(ItemType it) {
    }

    @Override
    public Container[] getContainers() {
    }

}
