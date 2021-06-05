package com.company.model;

import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

/**
 * Listener that observes changes to an {@link VaccinationStationModel}.
 *
 * @param <VaccineClass> the implementation class of Vaccines in the observed model
 */
public interface InventoryChangeListener<VaccineClass extends Vaccine> extends Remote {

    /**
     * Actions to be performed each time the station starts using a new kind of vaccine.
     *
     * @param addedVaccine the vaccine that was newly added to the model
     */
    void onVaccineAdded(VaccineClass addedVaccine);

    /**
     * Actions to be performed each time details of the inventory of the vaccination station change.
     *
     * @param changedVaccine the vaccine whose properties were changed
     */
    void onVaccineChanged(VaccineClass changedVaccine);

    /**
     * Actions to be performed each time the vaccination station stops using a particular vaccine.
     *
     * @param removedVaccine the vaccine that was removed from the model
     */
    void onVaccineRemoved(VaccineClass removedVaccine);
}
