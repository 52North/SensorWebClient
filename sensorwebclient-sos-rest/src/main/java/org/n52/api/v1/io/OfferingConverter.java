package org.n52.api.v1.io;

import org.n52.io.v1.data.OfferingOutput;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public class OfferingConverter extends OutputConverter<Offering, OfferingOutput> {

    public OfferingConverter(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public OfferingOutput convertExpanded(Offering offering) {
        OfferingOutput convertedOffering = convertCondensed(offering);
        convertedOffering.setService(convertCondensedService());
        return convertedOffering;
    }

    @Override
    public OfferingOutput convertCondensed(Offering offering) {
        OfferingOutput convertedOffering = new OfferingOutput();
        convertedOffering.setId(offering.getGlobalId());
        convertedOffering.setLabel(offering.getLabel());
        return convertedOffering;
    }

}
