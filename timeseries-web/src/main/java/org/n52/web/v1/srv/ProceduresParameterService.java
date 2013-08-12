package org.n52.web.v1.srv;

import org.n52.io.v1.data.out.Procedure;

public interface ProceduresParameterService {

	Procedure[] getProcedures(int offset, int size);

	Procedure getProcedure(String item);

}
