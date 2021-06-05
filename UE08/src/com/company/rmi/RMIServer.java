package com.company.rmi;

import com.company.gui.VaccinationStationGUI;
import com.company.impl.VaccinationStationModelImpl;
import com.company.impl.VaccineImpl;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        VaccinationStationModelImpl model = new VaccinationStationModelImpl();
        Registry registry = LocateRegistry.createRegistry(RMIConstants.PORT);
        registry.bind(RMIConstants.VACCINATION_MODEL_NAME, model);
        System.out.println("started server");
    }
}
