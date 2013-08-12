package org.n52.api.v0.out;

import org.n52.shared.serializable.pojos.sos.SOSMetadata;

/**
 * Represents a configured service instance to be used as for data output.
 */
public class ServiceInstance {

    private String itemName;
    
    private String title;
    
    private String url;
    
    private String type;

    private String version;
    
    public ServiceInstance(SOSMetadata metadata) {
        this.itemName = metadata.getConfiguredItemName();
        this.title = metadata.getTitle();
        this.url = metadata.getServiceUrl();
        this.version = metadata.getSosVersion();
        this.type = "SOS";
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
}
