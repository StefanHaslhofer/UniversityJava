package com.company.model;

import java.rmi.RemoteException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class describes a data model for the inventory system of a vaccination station. It manages different kinds
 * of {@link VaccineClass vaccines} and the quantity available for vaccination of each. Changes to this model can be
 * observed using appropriate {@link at.jku.ssw.psw2.ue08.sampleSolution.model.InventoryChangeListener change listeners}.
 *
 * @param <VaccineClass> type of vaccines managed by this model
 */
public interface VaccinationStationModel<VaccineClass extends Vaccine> {

    /**
     * Returns a read-only list of all available vaccines.
     *
     * @return the vaccines
     */
    List<VaccineClass> getVaccines();

    /**
     * Adds a new vaccine to the inventory system.
     *
     * @param name the name of the vaccine to be added
     * @throws IllegalArgumentException if the given name is null or the model already contains a vaccine with this name
     */
    void createVaccine(String name) throws IllegalArgumentException, RemoteException;

    /**
     * Returns the vaccine with the given name.
     *
     * @param name the name of the vaccine to retrieve
     * @return the vaccine
     * @throws IllegalArgumentException if the name is null
     * @throws NoSuchElementException   if there is no vaccine with that name in the model
     */
    VaccineClass getVaccine(String name) throws IllegalArgumentException, NoSuchElementException;

    /**
     * Changes the {@link Vaccine#getDescription() description} of a vaccine.
     *
     * @param vaccine     the vaccine to modify
     * @param description the new description
     * @throws IllegalArgumentException if the new description is null
     */
    void setDescription(VaccineClass vaccine, String description) throws IllegalArgumentException;

    /**
     * Increases the number of stocked doses of a vaccine.
     *
     * @param vaccine  the vaccine whose stock to increase
     * @param increase the number of added doses
     * @throws IllegalArgumentException if the increase is negative or the increase would exceed the capacity of the station
     */
    void increaseQuantity(VaccineClass vaccine, int increase) throws IllegalArgumentException;

    /**
     * Decreases the number of stocked doses of a vaccine.
     *
     * @param vaccine  the vaccine whose stock to decrease
     * @param decrease the number of removed doses
     * @throws IllegalArgumentException if the decrease is negative or higher than the number of currently stocked vaccines
     */
    void decreaseQuantity(VaccineClass vaccine, int decrease) throws IllegalArgumentException;

    /**
     * Deletes the given vaccine from the inventory system.
     *
     * @param vaccine the vaccine to be removed
     * @throws IllegalArgumentException if the given name is null
     */
    void removeVaccine(VaccineClass vaccine) throws IllegalArgumentException;

    /**
     * Adds a listener that is invoked when vaccines are added to or removed from this model.
     *
     * @param listener the listener to add
     */
    void addListener(InventoryChangeListener<VaccineClass> listener);

    /**
     * Removes the given listener from this model, if it is currently registered.
     *
     * @param listener the listener to remove
     */
    void removeListener(InventoryChangeListener<VaccineClass> listener);
}
