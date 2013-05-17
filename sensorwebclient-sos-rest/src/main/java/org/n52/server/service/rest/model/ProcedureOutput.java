package org.n52.server.service.rest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.shared.serializable.pojos.sos.Procedure;

/**
 * Procedure output data to be used for de-/marshalling web views.
 */
public class ProcedureOutput {
    
    private String id;
    
    private String label;

    private Map<String, Object> referenceValues;

    public static ProcedureOutput[] createSimpleProcedureOuput(Procedure[] procedures) {
        List<ProcedureOutput> procedureOutput = new ArrayList<ProcedureOutput>();
        for (Procedure procedure : procedures) {
            procedureOutput.add(createSimpleProcedureOuput(procedure));
        }
        return procedureOutput.toArray(new ProcedureOutput[0]);
    }

    public static ProcedureOutput createSimpleProcedureOuput(Procedure procedure) {
        ProcedureOutput procedureOutput = new ProcedureOutput();
        procedureOutput.setId(procedure.getId());
        procedureOutput.setLabel(procedure.getLabel());
        procedureOutput.createReferenceValues(procedure);
        return procedureOutput;
    }
    
    /**
     * @see #createSimpleProcedureOuput(Procedure)
     * @see #createSimpleProcedureOuput(Procedure[])
     */
    private ProcedureOutput() {
        // use static constructor methods
    }

    private void createReferenceValues(Procedure procedure) {
        Map<String, Object> referenceValues = new HashMap<String, Object>();
        for (String key : procedure.getRefValues()) {
            referenceValues.put(key, procedure.getRefValue(key).getValue());
        }
        // null values indicate optional attributes
        this.referenceValues = referenceValues.isEmpty() ? null : referenceValues;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }

    public Map<String, Object> getReferenceValues() {
        return referenceValues;
    }

    public void setReferenceValues(Map<String, Object> referenceValues) {
        this.referenceValues = referenceValues;
    }
    
}
