package org.n52.api.v1.io;

import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public class PhenomenonConverter extends OutputConverter<Phenomenon, PhenomenonOutput>{

    public PhenomenonConverter(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public PhenomenonOutput convertExpanded(Phenomenon phenomenon) {
        PhenomenonOutput convertedPhenomenon = convertCondensed(phenomenon);
        convertedPhenomenon.setService(convertCondensedService());
        return convertedPhenomenon;
    }

    @Override
    public PhenomenonOutput convertCondensed(Phenomenon phenomenon) {
        PhenomenonOutput convertedPhenomenon = new PhenomenonOutput();
        convertedPhenomenon.setId(phenomenon.getGlobalId());
        convertedPhenomenon.setLabel(phenomenon.getLabel());
        return convertedPhenomenon;
    }

}
