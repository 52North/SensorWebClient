
package org.n52.api.v1.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.n52.io.v1.data.ServiceOutput;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public abstract class OutputConverter<T,E> {

    private SOSMetadata metadata;

    public OutputConverter(SOSMetadata metadata) {
        this.metadata = metadata;
    }

    protected ServiceOutput convertCondensedService() {
        ServiceOutput convertedService = new ServiceOutput();
        convertedService.setId(metadata.getConfiguredItemName());
        convertedService.setServiceUrl(metadata.getServiceUrl());
        return convertedService;
    }
    
    public Collection<E> convertCondensed(T... toConvert) {
        List<E> allConverted = new ArrayList<E>();
        for (T element : toConvert) {
            allConverted.add(convertCondensed(element));
        }
        return allConverted;
    }

    public Collection<E> convertExpanded(T... toConvert) {
        List<E> allConverted = new ArrayList<E>();
        for (T element : toConvert) {
            allConverted.add(convertExpanded(element));
        }
        return allConverted;
    }
    
    public abstract E convertExpanded(T toConvert);
    
    public abstract E convertCondensed(T toConvert);

}
