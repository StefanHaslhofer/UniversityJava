package com.company.model;

/**
 * Represents a vaccine administered by a {@link VaccinationStationModel}. Each vaccine provides a name, a more detailed description and how many doses of it are currently in stock.
 */
public interface Vaccine {

    /**
     * Provides a short name for this vaccine.
     *
     * @return the vaccine's name
     */
    String getName();

    /**
     * Provides additional details about this vaccine such as who produces it, how many doses are required, etc..
     *
     * @return additional information about the vaccine
     */
    String getDescription();

    /**
     * Gets the number of doses of this vaccine that are currently in stock.
     *
     * @return how many doses are in stock
     */
    int getQuantity();
}
