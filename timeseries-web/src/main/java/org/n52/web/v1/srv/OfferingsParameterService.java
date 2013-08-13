package org.n52.web.v1.srv;

import org.n52.io.v1.data.OfferingOutput;

public interface OfferingsParameterService {
	
	public OfferingOutput[] getOfferings(int offset, int size);

	public OfferingOutput getOffering(String item);
	
}
