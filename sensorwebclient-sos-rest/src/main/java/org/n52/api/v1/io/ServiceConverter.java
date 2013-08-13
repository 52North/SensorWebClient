package org.n52.api.v1.io;

import org.n52.io.v1.data.ServiceOutput;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public class ServiceConverter extends OutputConverter<SOSMetadata, ServiceOutput> {

    public ServiceConverter(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public ServiceOutput convertExpanded(SOSMetadata metadata) {
        ServiceOutput convertedService = new ServiceOutput();
        convertedService.setId(metadata.getConfiguredItemName());
        convertedService.setServiceUrl(metadata.getServiceUrl());
        convertedService.setVersion(metadata.getVersion());
        convertedService.setLabel(metadata.getTitle());
        convertedService.setType("SOS");
        return convertedService;
    }

    @Override
    public ServiceOutput convertCondensed(SOSMetadata toConvert) {
        return convertCondensedService();
    }

}
