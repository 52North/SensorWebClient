
package org.n52.io.v0.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.shared.serializable.pojos.sos.Procedure;

/**
 * {@link Procedure} output data to be used for de-/marshalling web views.
 */
public class ProcedureOutput {

    private String id;

    private String label;

    private Map<String, Object> referenceValues;

    /**
     * Creates a full view on the given procedures.
     * 
     * @param procedures
     *        the procedures to create an output for.
     * @return the output data.
     */
    public static ProcedureOutput[] createCompleteProcedureOutput(Procedure[] procedures) {
        List<ProcedureOutput> procedureOutput = new ArrayList<ProcedureOutput>();
        for (Procedure procedure : procedures) {
            procedureOutput.add(createCompleteProcedureOutput(procedure));
        }
        return procedureOutput.toArray(new ProcedureOutput[0]);
    }

    /**
     * Creates a full view on the given procedure.
     * 
     * @param procedure
     *        the procedure to create an output for.
     * @return the output data.
     */
    public static ProcedureOutput createCompleteProcedureOutput(Procedure procedure) {
        ProcedureOutput procedureOutput = createSimpleProcedureOutput(procedure);
        procedureOutput.createReferenceValues(procedure);
        return procedureOutput;
    }

    /**
     * Creates a condensed view on the given procedures.
     * 
     * @param procedures
     *        the procedures to create an output for.
     * @return the output data.
     */
    public static ProcedureOutput[] createSimpleProcedureOutput(Procedure[] procedures) {
        List<ProcedureOutput> procedureOutput = new ArrayList<ProcedureOutput>();
        for (Procedure procedure : procedures) {
            procedureOutput.add(createSimpleProcedureOutput(procedure));
        }
        return procedureOutput.toArray(new ProcedureOutput[0]);
    }

    /**
     * Creates a condensed view on the given procedure.
     * 
     * @param procedure
     *        the procedure to create an output for.
     * @return the output data.
     */
    public static ProcedureOutput createSimpleProcedureOutput(Procedure procedure) {
        ProcedureOutput procedureOutput = new ProcedureOutput();
        procedureOutput.setId(procedure.getId());
        procedureOutput.setLabel(procedure.getLabel());
        return procedureOutput;
    }

    /**
     * @see #createCompleteProcedureOutput(Procedure)
     * @see #createCompleteProcedureOutput(Procedure[])
     * @see #createSimpleProcedureOutput(Procedure)
     * @see #createSimpleProcedureOutput(Procedure[])
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
