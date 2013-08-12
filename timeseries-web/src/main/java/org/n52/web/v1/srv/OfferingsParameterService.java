package org.n52.web.v1.srv;

import org.n52.io.v1.data.out.Offering;

public interface OfferingsParameterService {
	
	public Offering[] getOfferings(int offset, int size);

	public Offering getOffering(String item);
	
}
