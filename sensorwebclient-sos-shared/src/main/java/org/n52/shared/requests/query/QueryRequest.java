package org.n52.shared.requests.query;

import java.io.Serializable;
import java.util.Collection;

import org.n52.shared.requests.query.builder.QueryRequestBuilder;
import org.n52.shared.serializable.pojos.BoundingBox;

public class QueryRequest implements Serializable {
	
	private static final long serialVersionUID = -2469479744020996103L;
	
	private String serviceUrl;

	private Collection<String> offeringFilter;
	
	private Collection<String> procedureFilter;
	
	private Collection<String> phenomenonFilter;
	
	private Collection<String> featureOfInterestFilter;
	
	private int pagingStartIndex;
	
	private int pagingInterval;

	private BoundingBox spatialFilter;
	
	public QueryRequest() {
		// for serialization
	}

	public QueryRequest(QueryRequestBuilder queryBuilder) {
		this.serviceUrl = queryBuilder.getServiceUrl();
		this.offeringFilter = queryBuilder.getOfferingFilter();
		this.procedureFilter = queryBuilder.getProcedureFilter();
		this.phenomenonFilter = queryBuilder.getPhenomenonFilter();
		this.featureOfInterestFilter = queryBuilder.getFeatureOfInterestFilter();
		this.pagingStartIndex = queryBuilder.getPagingStartIndex();
		this.pagingInterval = queryBuilder.getPagingInteval();
		this.spatialFilter = queryBuilder.getSpatialFilter();
	}
	
	public Collection<String> getOfferingFilter() {
		return offeringFilter;
	}

	public void setOfferingFilter(Collection<String> offeringFilter) {
		this.offeringFilter = offeringFilter;
	}

	public Collection<String> getProcedureFilter() {
		return procedureFilter;
	}

	public void setProcedureFilter(Collection<String> procedureFilter) {
		this.procedureFilter = procedureFilter;
	}

	public Collection<String> getPhenomenonFilter() {
		return phenomenonFilter;
	}

	public void setPhenomenonFilter(Collection<String> phenomenonFilter) {
		this.phenomenonFilter = phenomenonFilter;
	}

	public Collection<String> getFeatureOfInterestFilter() {
		return featureOfInterestFilter;
	}

	public void setFeatureOfInterestFilter(Collection<String> featureOfInterestFilter) {
		this.featureOfInterestFilter = featureOfInterestFilter;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public int getPagingStartIndex() {
		return pagingStartIndex;
	}

	public void setPagingStartIndex(int pagingStartIndex) {
		this.pagingStartIndex = pagingStartIndex;
	}

	public int getPagingInterval() {
		return pagingInterval;
	}

	public void setPagingInterval(int pagingInterval) {
		this.pagingInterval = pagingInterval;
	}

	public BoundingBox getSpatialFilter() {
		return spatialFilter;
	}

	public void setSpatialFilter(BoundingBox spatialFilter) {
		this.spatialFilter = spatialFilter;
	}
}
