package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.List;

import org.n52.api.v1.io.PhenomenonConverter;
import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.ctrl.QueryMap;
import org.n52.web.v1.srv.ParameterService;

public class PhenomenonOutputAdapter implements ParameterService<PhenomenonOutput> {

	@Override
	public PhenomenonOutput[] getExpandedParameters(QueryMap map) {
		List<PhenomenonOutput> allPhenomenons = new ArrayList<PhenomenonOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
		    PhenomenonConverter converter = new PhenomenonConverter(metadata);
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			allPhenomenons.addAll(converter.convertExpanded(lookup.getPhenomenonsAsArray()));
		}
		return allPhenomenons.toArray(new PhenomenonOutput[0]);
	}
	
	@Override
    public PhenomenonOutput[] getCondensedParameters(QueryMap map) {
        List<PhenomenonOutput> allPhenomenons = new ArrayList<PhenomenonOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            PhenomenonConverter converter = new PhenomenonConverter(metadata);
            TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
            allPhenomenons.addAll(converter.convertCondensed(lookup.getPhenomenonsAsArray()));
        }
        return allPhenomenons.toArray(new PhenomenonOutput[0]);
    }

	@Override
	public PhenomenonOutput getParameter(String phenomenonId) {
		for (SOSMetadata metadata : getSOSMetadatas()) {
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			if(lookup.containsPhenomenon(phenomenonId)) {
			    PhenomenonConverter converter = new PhenomenonConverter(metadata);
				return converter.convertExpanded(lookup.getPhenomenon(phenomenonId));
			}
		}
		return null;
	}

}
