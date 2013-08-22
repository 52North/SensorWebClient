/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.api.v1.srv;

import static org.n52.api.v1.srv.QueryParameterAdapter.createQueryParameters;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.api.v1.io.ProcedureConverter;
import org.n52.io.v1.data.ProcedureOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.ctrl.QueryMap;
import org.n52.web.v1.srv.ParameterService;

public class ProcedureOutputAdapter implements ParameterService<ProcedureOutput> {

	@Override
	public ProcedureOutput[] getExpandedParameters(QueryMap map) {
        QueryParameters query = createQueryParameters(map);
		List<ProcedureOutput> allProcedures = new ArrayList<ProcedureOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
		    ProcedureConverter converter = new ProcedureConverter(metadata);
			allProcedures.addAll(converter.convertExpanded(filter(metadata, query)));
		}
		return allProcedures.toArray(new ProcedureOutput[0]);
	}
	
	@Override
    public ProcedureOutput[] getCondensedParameters(QueryMap map) {
        QueryParameters query = createQueryParameters(map);
        List<ProcedureOutput> allProcedures = new ArrayList<ProcedureOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            ProcedureConverter converter = new ProcedureConverter(metadata);
            allProcedures.addAll(converter.convertCondensed(filter(metadata, query)));
        }
        return allProcedures.toArray(new ProcedureOutput[0]);
    }

    private Procedure[] filter(SOSMetadata metadata, QueryParameters query) {
        Set<Procedure> allProcedures = new HashSet<Procedure>();
        for (SosTimeseries timeseries : metadata.getTimeseriesRelatedWith(query)) {
          allProcedures.add(timeseries.getProcedure());
        }
        return allProcedures.toArray(new Procedure[0]);
    }

	@Override
    public ProcedureOutput[] getParameters(String[] procedureIds) {
	    List<ProcedureOutput> selectedProcedures = new ArrayList<ProcedureOutput>();
        for (String procedureId : procedureIds) {
            ProcedureOutput procedure = getParameter(procedureId);
            if (procedure != null) {
                selectedProcedures.add(procedure);
            }
        }
        return selectedProcedures.toArray(new ProcedureOutput[0]);
    }

    @Override
	public ProcedureOutput getParameter(String procedureId) {
		for (SOSMetadata metadata : getSOSMetadatas()) {
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			for (Procedure procedure : lookup.getProcedures()) {
				if (procedure.getGlobalId().equals(procedureId)) {
					ProcedureConverter converter = new ProcedureConverter(metadata);
					return converter.convertExpanded(procedure);
				}
			}
		}
		return null;
	}


}
