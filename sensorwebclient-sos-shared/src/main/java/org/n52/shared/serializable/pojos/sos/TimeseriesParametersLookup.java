
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

    private HashMap<String, Feature> features = new HashMap<String, Feature>();

    private HashMap<String, Phenomenon> phenomenons = new HashMap<String, Phenomenon>();

    private HashMap<String, Procedure> procedures = new HashMap<String, Procedure>();

    private HashMap<String, Offering> offerings = new HashMap<String, Offering>();

    /**
     * Adds a new offering or overrides an existing one.
     * 
     * @param offering
     *        the offering to set.
     */
    public void addOffering(Offering offering) {
        offerings.put(offering.getId(), offering);
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
        if (offerings.size() == 0) {
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
        this.features.put(feature.getId(), feature);
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
        if (features.size() == 0) {
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
        this.phenomenons.put(phenomenon.getId(), phenomenon);
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
        if (phenomenons.size() == 0) {
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
        procedures.put(procedure.getId(), procedure);
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
        if (procedures.size() == 0) {
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
        boolean procedureLoaded = procedures.containsKey(timeseries.getProcedure());
        boolean featureLoaded = features.containsKey(timeseries.getFeature());
        boolean offeringLoaded = offerings.containsKey(timeseries.getOffering());
        boolean phenomenonLoaded = phenomenons.containsKey(timeseries.getPhenomenon());
        return procedureLoaded && featureLoaded && offeringLoaded && phenomenonLoaded;

    }
}
