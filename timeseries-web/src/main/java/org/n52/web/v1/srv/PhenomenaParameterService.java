package org.n52.web.v1.srv;

import org.n52.io.v1.data.PhenomenonOutput;

public interface PhenomenaParameterService {

	PhenomenonOutput[] getPhenomena(int offset, int size);

	PhenomenonOutput getPhenomenon(String item);

}
