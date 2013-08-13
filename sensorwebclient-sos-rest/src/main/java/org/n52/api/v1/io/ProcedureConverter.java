package org.n52.api.v1.io;

import org.n52.io.v1.data.ProcedureOutput;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public class ProcedureConverter extends OutputConverter<Procedure, ProcedureOutput> {

    public ProcedureConverter(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public ProcedureOutput convertExpanded(Procedure procedure) {
        ProcedureOutput convertedProcedure = convertCondensed(procedure);
        convertedProcedure.setService(convertCondensedService());
        return convertedProcedure;
    }

    @Override
    public ProcedureOutput convertCondensed(Procedure procedure) {
        ProcedureOutput convertedProcedure = new ProcedureOutput();
        convertedProcedure.setId(procedure.getProcedureId());
        convertedProcedure.setLabel(procedure.getLabel());
        return convertedProcedure;
    }

}
