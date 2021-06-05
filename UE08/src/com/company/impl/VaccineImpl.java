package com.company.impl;

import com.company.model.Vaccine;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public final class VaccineImpl extends UnicastRemoteObject implements Vaccine {

    private final String name;

    private String description;
    private int quantity;

    public VaccineImpl(String name) throws RemoteException {
        this.name = name;
        this.description = "";
        this.quantity = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /*
     * This method is called to get the string displayed in the gui's list view of available vaccines. We override it to display the vaccine's name in the list.
     */
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
