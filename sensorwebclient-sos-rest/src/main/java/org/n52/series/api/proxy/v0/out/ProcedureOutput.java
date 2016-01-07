/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.series.api.proxy.v0.out;

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
        procedureOutput.setId(procedure.getGlobalId());
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
