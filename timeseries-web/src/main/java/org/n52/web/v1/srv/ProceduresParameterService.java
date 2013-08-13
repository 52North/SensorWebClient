package org.n52.web.v1.srv;

import org.n52.io.v1.data.ProcedureOutput;

public interface ProceduresParameterService {

	ProcedureOutput[] getProcedures(int offset, int size);

	ProcedureOutput getProcedure(String item);

}
