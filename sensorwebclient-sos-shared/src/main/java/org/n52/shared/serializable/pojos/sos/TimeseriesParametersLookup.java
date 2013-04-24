
package org.n52.shared.serializable.pojos.sos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Serves as a lookup container for timeseries parameters valid for an SOS instance. Typically, each parameter
 * has an id which shall be used to lookup the actual parameter instance. A {@link TimeseriesParametersLookup}
 * instance is hold together with other metadata within the {@link SOSMetadata} of a configured SOS.<br>
 * <br>
 * <b>Note:</b> A TimeseriesParametersLookup serves only parameters available (typically parsed from a
 * capabilities file). There is no logic to combine these parameters to valid {@link ParameterConstellation}s
 * which can be used to identify actual timeseries.
 * 
 * @see ParameterConstellation
 */
public class TimeseriesParametersLookup {

    private HashMap<String, FeatureOfInterest> features = new HashMap<String, FeatureOfInterest>();

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
    public void addFeature(FeatureOfInterest feature) {
        this.features.put(feature.getId(), feature);
    }

    /**
     * @return all <code>FeatureOfInterest</code>s or an empty collection if no features are available.
     */
    public Collection<FeatureOfInterest> getFeatures() {
        if (features.size() == 0) {
            return new ArrayList<FeatureOfInterest>();
        }
        else {
            return new ArrayList<FeatureOfInterest>(features.values());
        }
    }

    /**
     * @return all <code>FeatureOfInterest</code>s or an empty array if no features are available.
     */
    public FeatureOfInterest[] getFeaturesAsArray() {
        return getFeatures().toArray(new FeatureOfInterest[0]);
    }

    /**
     * @param id
     *        the specific feature id
     * @return the feature instance for the given id.
     */
    public FeatureOfInterest getFeature(String id) {
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
}
