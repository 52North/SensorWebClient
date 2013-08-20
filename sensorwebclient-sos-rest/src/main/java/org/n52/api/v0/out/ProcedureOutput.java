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

package org.n52.api.v0.out;

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
        procedureOutput.setId(procedure.getProcedureId());
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
