package org.n52.shared.serializable.pojos.sos;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;



public class SOSMetadataTest {
    
    private SOSMetadata testMetadata;
    
    @Before 
    public void setUp() {
        testMetadata = new SOSMetadata("http://url", "test service", "2.0.0");
    }

    @Test public void 
    shouldReturnAnEmptyListIfNoOfferingsAreAvailable()
    {
        assertThat(testMetadata.getOfferings(), is(empty()));
    }
    
    @Test public void 
    shouldReturnAnEmptyArrayIfNoOfferingsAreAvailable()
    {
        assertThat(testMetadata.getOfferingsAsArray(), is(emptyArray()));
    }
    
    @Test public void 
    shouldReturnAnEmptyListIfNoFeaturesAreAvailable()
    {
        assertThat(testMetadata.getFeatures(), is(empty()));
    }
    
    @Test public void 
    shouldReturnAnEmptyArrayIfNoFeaturesAreAvailable()
    {
        assertThat(testMetadata.getFeaturesAsArray(), is(emptyArray()));
    }
    
    @Test public void 
    shouldReturnAnEmptyListIfNoPhenomenonsAreAvailable()
    {
        assertThat(testMetadata.getPhenomenons(), is(empty()));
    }
    
    @Test public void 
    shouldReturnAnEmptyArrayIfNoPhenomenonsAreAvailable()
    {
        assertThat(testMetadata.getPhenomenonsAsArray(), is(emptyArray()));
    }

    @Test public void 
    shouldReturnAnEmptyListIfNoProceduresAreAvailable()
    {
        assertThat(testMetadata.getProcedures(), is(empty()));
    }
    
    @Test public void 
    shouldReturnAnEmptyArrayIfNoProceduresAreAvailable()
    {
        assertThat(testMetadata.getProceduresAsArray(), is(emptyArray()));
    }
    

}
