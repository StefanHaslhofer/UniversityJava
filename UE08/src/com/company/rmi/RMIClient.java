package com.company.rmi;

import com.company.gui.VaccinationStationGUI;
import com.company.model.VaccinationStationModel;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    private final VaccinationStationModel stationModel;

    RMIClient(VaccinationStationModel stationModel) {
        this.stationModel = stationModel;
    }

    private void init() throws RemoteException {
        VaccinationStationGUI.startGui(stationModel);
    }

    public static void main(String[] args) {
        try {
            Registry reg = LocateRegistry.getRegistry(RMIConstants.HOST, RMIConstants.PORT);
            VaccinationStationModel stationModel = (VaccinationStationModel) reg.lookup(RMIConstants.VACCINATION_SERVER_NAME);

            new RMIClient(stationModel).init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
