/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.shared.serializable.pojos.sos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Serves as a lookup container for timeseries parameters valid for an SOS instance. Typically, each parameter
 * has an id which shall be used to lookup the actual parameter instance. A {@link TimeseriesParametersLookup}
 * instance is hold together with other metadata within the {@link SOSMetadata} of a configured SOS.<br>
 * <br>
 * <b>Note:</b> A TimeseriesParametersLookup serves only parameters available (typically parsed from a
 * capabilities file). There is no logic to combine these parameters to valid {@link SosTimeseries}s which can
 * be used to identify actual timeseries.
 */
public class TimeseriesParametersLookup implements Serializable {

    private static final long serialVersionUID = 7232653582909270958L;

    private final HashMap<String, Feature> features = new HashMap<String, Feature>();

    private final HashMap<String, Phenomenon> phenomenons = new HashMap<String, Phenomenon>();

    private final HashMap<String, Procedure> procedures = new HashMap<String, Procedure>();

    private final HashMap<String, Offering> offerings = new HashMap<String, Offering>();

    public void addLookup(TimeseriesParametersLookup other) {
        features.putAll(other.features);
        phenomenons.putAll(other.phenomenons);
        procedures.putAll(other.procedures);
        offerings.putAll(other.offerings);
    }

    /**
     * Adds a new offering or overrides an existing one.
     *
     * @param offering
     *        the offering to set.
     */
    public void addOffering(Offering offering) {
        offerings.put(offering.getOfferingId(), offering);
    }

    /**
     * @param offering
     *        the offering id to check
     * @return <code>true</code> if an offering exists which is associated to the given id, <code>false</code>
     *         otherwise.
     */
    public boolean containsOffering(String offering) {
        return offerings.containsKey(offering);
    }

    /**
     * @return all <code>Offering</code>s or an empty collection if no offerings are available.
     */
    public Collection<Offering> getOfferings() {
        if (offerings.isEmpty()) {
            return new ArrayList<Offering>();
        }
        else {
            return new ArrayList<Offering>(offerings.values());
        }
    }

    /**
     * @return all <code>Offering</code>s or an empty array if no offerings are available.
     */
    public Offering[] getOfferingsAsArray() {
        return getOfferings().toArray(new Offering[0]);
    }

    /**
     * @param id
     *        the specific offering id
     * @return the offering instance for the given id.
     */
    public Offering getOffering(String id) {
        return this.offerings.get(id);
    }

    /**
     * Adds a new feature or overrides an existing one.
     *
     * @param feature
     *        the feature to set.
     */
    public void addFeature(Feature feature) {
        this.features.put(feature.getFeatureId(), feature);
    }

    /**
     * @param feature
     *        the feature id to check
     * @return <code>true</code> if an feature exists which is associated to the given id, <code>false</code>
     *         otherwise.
     */
    public boolean containsFeature(String feature) {
        return features.containsKey(feature);
    }

    /**
     * @return all <code>FeatureOfInterest</code>s or an empty collection if no features are available.
     */
    public Collection<Feature> getFeatures() {
        if (features.isEmpty()) {
            return new ArrayList<Feature>();
        }
        else {
            return new ArrayList<Feature>(features.values());
        }
    }

    /**
     * @return all <code>FeatureOfInterest</code>s or an empty array if no features are available.
     */
    public Feature[] getFeaturesAsArray() {
        return getFeatures().toArray(new Feature[0]);
    }

    /**
     * @param id
     *        the specific feature id
     * @return the feature instance for the given id.
     */
    public Feature getFeature(String id) {
        return features.get(id);
    }

    /**
     * Adds a new phenomenon or overrides an existing one.
     *
     * @param phenomenon
     *        the phenomenon to set.
     */
    public void addPhenomenon(Phenomenon phenomenon) {
        this.phenomenons.put(phenomenon.getPhenomenonId(), phenomenon);
    }

    /**
     * @param phenomenon
     *        the phenomenon id to check
     * @return <code>true</code> if an phenomenon exists which is associated to the given id,
     *         <code>false</code> otherwise.
     */
    public boolean containsPhenomenon(String phenomenon) {
        return phenomenons.containsKey(phenomenon);
    }

    /**
     * @return all <code>Phenomenon</code>s or an empty collection if no phenomenons are available.
     */
    public Collection<Phenomenon> getPhenomenons() {
        if (phenomenons.isEmpty()) {
            return new ArrayList<Phenomenon>();
        }
        else {
            return new ArrayList<Phenomenon>(phenomenons.values());
        }
    }

    /**
     * @return all <code>Phenomenon</code>s or an empty array if no phenomenons are available.
     */
    public Phenomenon[] getPhenomenonsAsArray() {
        return getPhenomenons().toArray(new Phenomenon[0]);
    }

    /**
     * @param id
     *        the specific phenomenon id
     * @return the phenomenon instance for the given id.
     */
    public Phenomenon getPhenomenon(String id) {
        return this.phenomenons.get(id);
    }

    /**
     * Adds a new procedure or overrides an existing one.
     *
     * @param procedure
     *        the procedure to set.
     */
    public void addProcedure(Procedure procedure) {
        procedures.put(procedure.getProcedureId(), procedure);
    }

    /**
     * @param procedure
     *        the procedure id to check
     * @return <code>true</code> if an procedure exists which is associated to the given id,
     *         <code>false</code> otherwise.
     */
    public boolean containsProcedure(String procedure) {
        return procedures.containsKey(procedure);
    }

    /**
     * @return all <code>Procedure</code>s or an empty collection if no procedure are available.
     */
    public ArrayList<Procedure> getProcedures() {
        if (procedures.isEmpty()) {
            return new ArrayList<Procedure>();
        }
        else {
            return new ArrayList<Procedure>(procedures.values());
        }
    }

    /**
     * @return all <code>Procedure</code>s or an empty array if no procedure are available.
     */
    public Procedure[] getProceduresAsArray() {
        return getProcedures().toArray(new Procedure[0]);
    }

    /**
     * @param id
     *        the specific procedure id
     * @return the procedure instance for the given id.
     */
    public Procedure getProcedure(String id) {
        return this.procedures.get(id);
    }

    /**
     * Deletes the procedure or does nothing if given id is unknown.
     *
     * @param id
     *        the id of the procedure to be deleted.
     */
    public void removeProcedure(String id) {
        procedures.remove(id);
    }

    /**
     * Checks if the timeseries' parameters are already known/loaded to this lookup instance.
     *
     * @param timeseries
     *        the timeseries to check if parameters are loaded.
     * @return <code>true</code> if all parameters are already loaded, <code>false</code> otherwise.
     */
    public boolean hasLoadedCompletely(SosTimeseries timeseries) {
        boolean procedureLoaded = procedures.containsKey(timeseries.getProcedureId());
        boolean featureLoaded = features.containsKey(timeseries.getFeatureId());
        boolean offeringLoaded = offerings.containsKey(timeseries.getOfferingId());
        boolean phenomenonLoaded = phenomenons.containsKey(timeseries.getPhenomenonId());
        return procedureLoaded && featureLoaded && offeringLoaded && phenomenonLoaded;

    }
}
