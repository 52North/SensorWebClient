package org.n52.web.v1.srv;

import org.n52.io.v1.data.out.Phenomenon;

public interface PhenomenaParameterService {

	Phenomenon[] getPhenomena(int offset, int size);

	Phenomenon getPhenomenon(String item);

}
