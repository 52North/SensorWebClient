package org.n52.shared.serializable.pojos.sos;

import org.n52.shared.IdGenerator;
import org.n52.shared.MD5HashIdGenerator;

public class SosService extends TimeseriesParameter {

    private static final long serialVersionUID = -3749074157142942875L;

    private String version;
    
    SosService() {
        // for serialization
    }
    
    public SosService(String serviceUrl, String version) {
        super(serviceUrl, new String[]{serviceUrl, version});
        this.version = version;
    }
    
    public String getServiceUrl() {
        return getParameterId();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append(" [").append("serviceUrl: '").append(getServiceUrl());
        sb.append("', ").append("label: '").append(getLabel());
        return sb.append("']").toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (getLabel() == null) ? 0 : getLabel().hashCode());
        result = prime * result + ( (getServiceUrl() == null) ? 0 : getServiceUrl().hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SosService other = (SosService) obj;
        if (getServiceUrl() == null) {
            if (other.getServiceUrl() != null)
                return false;
        }
        else if ( !getServiceUrl().equals(other.getServiceUrl()))
            return false;
        return true;
    }

	@Override
	protected String generateGlobalId(String id, String[] parametersToGenerateId) {
		IdGenerator idGenerator = new MD5HashIdGenerator("srv_");
        return idGenerator.generate(parametersToGenerateId);
	}
}
