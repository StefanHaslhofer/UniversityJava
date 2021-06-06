package com.company.rmi;

import com.company.impl.VaccinationStationModelImpl;
import com.company.impl.VaccineImpl;
import com.company.model.VaccinationStationModel;
import com.company.model.Vaccine;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    private final VaccinationStationModel<VaccineImpl> model;

    public static void main(String[] args) {
        try {
            new RMIServer().init();
            System.out.println("started server");
        } catch (Exception e) {
            System.err.println("could not start server " + e);
        }
    }

    public RMIServer() throws RemoteException, AlreadyBoundException {
        this.model = new VaccinationStationModelImpl();
        Registry registry = LocateRegistry.createRegistry(RMIConstants.PORT);
        registry.bind(RMIConstants.VACCINATION_SERVER_NAME, model);
    }

    private void init() throws RemoteException {
        insertExampleData(model);
    }

    // i initialize the data server side because otherwise multiple clients would insert double vaccines
    private static void insertExampleData(VaccinationStationModel model) throws RemoteException {
        addVaccine(model, "Comirnaty", "Producer: BioNTech/Pfizer\nAge required: 16\nDoses required: 2", 100);
        addVaccine(model, "COVID-19 Vaccine Moderna", "Producer: Moderna\nAge required: 18\nDoses required: 2", 50);
        addVaccine(model, "Vaxzevria", "Producer: Astra Zeneca\nAge required: 18\nDoses required: 2", 5);
        addVaccine(model, "COVID-19 Vaccine Janssen", "Producer: Janssen\nAge required: 18\nDoses required: 1", 10);
    }

    private static void addVaccine(VaccinationStationModel model, String name, String description, int quantity) throws RemoteException {
        model.createVaccine(name);
        final Vaccine vaccine = model.getVaccine(name);
        // i use the name as identifier
        model.setDescription(vaccine.getName(), description);
        model.increaseQuantity(vaccine.getName(), quantity);
    }
}
