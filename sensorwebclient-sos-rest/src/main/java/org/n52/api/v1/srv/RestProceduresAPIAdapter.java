package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.List;

import org.n52.io.v1.data.out.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.srv.ProceduresParameterService;

public class RestProceduresAPIAdapter implements ProceduresParameterService {

	@Override
	public Procedure[] getProcedures(int offset, int size) {
		List<Procedure> allProcedures = new ArrayList<Procedure>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
			ParameterConverter converter = new ParameterConverter(metadata);
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			allProcedures.addAll(converter.convertProcedures(lookup.getProcedures()));
		}
		return allProcedures.toArray(new Procedure[0]);
	}

	@Override
	public Procedure getProcedure(String item) {
		for (SOSMetadata metadata : getSOSMetadatas()) {
			ParameterConverter converter = new ParameterConverter(metadata);
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			org.n52.shared.serializable.pojos.sos.Procedure result = lookup.getProcedure(item);
			if (result != null) {
				return converter.convertProcedure(result);
			}
		}
		return null;
	}


}
