package com.company.rmi;

import com.company.gui.VaccinationStationGUI;
import com.company.impl.VaccinationStationModelImpl;
import com.company.impl.VaccineImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    public static void main(String[] args) throws RemoteException {
        final Registry registry = LocateRegistry.getRegistry(RMIConstants.HOST, RMIConstants.PORT);
        VaccinationStationGUI gui = (Bank) registry.lookup(RMIConstants.BANK_EXPORT_NAME);
        new RMIClient(bank).doActions();
    }
}
