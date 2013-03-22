package org.n52.shared.requests.query.builder;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.shared.requests.query.QueryRequest;
import org.n52.shared.serializable.pojos.BoundingBox;


public abstract class QueryRequestBuilder {
	
	private String serviceUrl;
	
	private Collection<String> offeringFilter = new ArrayList<String>();
	
	private Collection<String> procedureFilter = new ArrayList<String>();
	
	private Collection<String> phenomenonFilter = new ArrayList<String>();
	
	private Collection<String> featureOfInterestFilter = new ArrayList<String>();
	
	private int pagingStartIndex;
	
	private int pagingInterval;

	private BoundingBox spatialFilter;
	
	public abstract QueryRequest build();
	
	public QueryRequestBuilder addServiceUrl(String serviceUrl){
		this.serviceUrl = serviceUrl;
		return this;
	}

	public QueryRequestBuilder addOfferingFilter(String offeringFilter) {
		this.offeringFilter.add(offeringFilter);
		return this;
	}
	
	public QueryRequestBuilder addProcedureFilter(String procedureFilter) {
		this.procedureFilter.add(procedureFilter);
		return this;
	}

	public QueryRequestBuilder addPhenomenonFilter(String phenomenonFilter) {
		this.phenomenonFilter.add(phenomenonFilter);
		return this;
	}

	public QueryRequestBuilder addFeatureOfInterestFilter(String featureOfInterestFilter) {
		this.featureOfInterestFilter.add(featureOfInterestFilter);
		return this;
	}
	
	public QueryRequestBuilder addOfferingFilter(Collection<String> offeringFilter) {
		this.offeringFilter.addAll(offeringFilter);
		return this;
	}
	
	public QueryRequestBuilder addProcedureFilter(Collection<String> procedureFilter) {
		this.procedureFilter.addAll(procedureFilter);
		return this;
	}

	public QueryRequestBuilder addPhenomenonFilter(Collection<String> phenomenonFilter) {
		this.phenomenonFilter.addAll(phenomenonFilter);
		return this;
	}

	public QueryRequestBuilder addFeatureOfInterestFilter(Collection<String> featureOfInterestFilter) {
		this.featureOfInterestFilter.addAll(featureOfInterestFilter);
		return this;
	}
	public QueryRequestBuilder addPagingStartIndex(int start) {
		this.pagingStartIndex = start;
		return this;
	}
	
	public QueryRequestBuilder addPagingInterval(int interval) {
		this.pagingInterval = interval;
		return this;
	}
	
	public QueryRequestBuilder addSpatialFilter(BoundingBox boundingBox) {
		this.spatialFilter = boundingBox;
		return this;
	}
	
	public String getServiceUrl() {
		return serviceUrl;
	}

	public Collection<String> getOfferingFilter(){
		return offeringFilter;
	}
	
	public Collection<String> getProcedureFilter(){
		return procedureFilter;
	}

	public Collection<String> getPhenomenonFilter() {
		return phenomenonFilter;
	}

	public Collection<String> getFeatureOfInterestFilter() {
		return featureOfInterestFilter;
	}
	
	public int getPagingStartIndex() {
		return pagingStartIndex;
	}
	
	public int getPagingInteval() {
		return pagingInterval;
	}

	public BoundingBox getSpatialFilter() {
		return spatialFilter;
	}
}
