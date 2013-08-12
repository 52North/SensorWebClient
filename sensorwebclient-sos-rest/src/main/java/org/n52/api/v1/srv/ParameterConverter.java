package org.n52.api.v1.srv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.n52.io.v1.data.out.Feature;
import org.n52.io.v1.data.out.Offering;
import org.n52.io.v1.data.out.Phenomenon;
import org.n52.io.v1.data.out.Procedure;
import org.n52.io.v1.data.out.Service;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public class ParameterConverter {

	private SOSMetadata metadata;

	public ParameterConverter(SOSMetadata metadata) {
		this.metadata = metadata;
	}
	
	public Service convertService() {
		Service convertedService = new Service();
		convertedService.setId(metadata.getConfiguredItemName());
		convertedService.setLabel(metadata.getTitle());
		convertedService.setServiceUrl(metadata.getServiceUrl());
		convertedService.setType("SOS"); // TODO what else?
		convertedService.setVersion(metadata.getVersion());
		return convertedService;
	}

	public Offering convertOffering(org.n52.shared.serializable.pojos.sos.Offering offering) {
		Offering convertedOffering = new Offering();
		convertedOffering.setId(offering.getId());
		convertedOffering.setLabel(offering.getLabel());
		convertedOffering.setService(convertService());
		return convertedOffering;
	}

	public Collection<? extends Offering> convertOfferings(Collection<org.n52.shared.serializable.pojos.sos.Offering> offerings) {
		List<Offering> allOfferings = new ArrayList<Offering>();
		for (org.n52.shared.serializable.pojos.sos.Offering offering : offerings) {
			allOfferings.add(convertOffering(offering));
		}
		return allOfferings;
	}

	public Collection<? extends Feature> convertFeatures(
			Collection<org.n52.shared.serializable.pojos.sos.Feature> features) {
		List<Feature> allFeatures = new ArrayList<Feature>();
		for (org.n52.shared.serializable.pojos.sos.Feature feature : features) {
			allFeatures.add(convertFeature(feature));
		}
		return allFeatures;
	}

	public Feature convertFeature(
			org.n52.shared.serializable.pojos.sos.Feature feature) {
		Feature convertedFeature = new Feature();
		convertedFeature.setId(feature.getId());
		convertedFeature.setLabel(feature.getLabel());
		convertedFeature.setService(convertService());
		return convertedFeature;
	}

	public Procedure convertProcedure(
			org.n52.shared.serializable.pojos.sos.Procedure procedure) {
		Procedure convertedProcedure = new Procedure();
		convertedProcedure.setId(procedure.getId());
		convertedProcedure.setLabel(procedure.getLabel());
		convertedProcedure.setService(convertService());
		return convertedProcedure;
	}

	public Collection<? extends Procedure> convertProcedures(
			ArrayList<org.n52.shared.serializable.pojos.sos.Procedure> procedures) {
		List<Procedure> allProcedures = new ArrayList<Procedure>();
		for (org.n52.shared.serializable.pojos.sos.Procedure procedure : procedures) {
			allProcedures.add(convertProcedure(procedure));
		}
		return allProcedures;
	}

	public Phenomenon convertPhenomenon(
			org.n52.shared.serializable.pojos.sos.Phenomenon phenomenon) {
		Phenomenon convertedPhenomenon = new Phenomenon();
		convertedPhenomenon.setId(phenomenon.getId());
		convertedPhenomenon.setLabel(phenomenon.getLabel());
		convertedPhenomenon.setService(convertService());
		return convertedPhenomenon;
	}

	public Collection<? extends Phenomenon> convertPhenomenon(
			Collection<org.n52.shared.serializable.pojos.sos.Phenomenon> phenomenons) {
		List<Phenomenon> allPhenomena = new ArrayList<Phenomenon>();
		for (org.n52.shared.serializable.pojos.sos.Phenomenon phenomenon : phenomenons) {
			allPhenomena.add(convertPhenomenon(phenomenon));
		}
		return allPhenomena;
	}
}
