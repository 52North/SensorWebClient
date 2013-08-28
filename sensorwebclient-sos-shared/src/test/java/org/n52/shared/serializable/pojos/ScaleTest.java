package org.n52.shared.serializable.pojos;

import org.junit.Before;
import org.junit.Test;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

public class ScaleTest {
	
	SosTimeseries ts;
	Station station;
	int width;
	int height;
	
	TimeseriesProperties props;
	
	@Before
	public void init(){
		System.out.println("Init");
		this.ts = new SosTimeseries();
		this.station = new Station("1");
		this.width = 300;
		this.height = 200;
	}
	
	@Ignore
	public void test(){
		resetProps();
		printScaleValues(props.getScale());
		
		resetProps();
		props.setScale(new Scale(0.234,3.63245));
		printScaleValues(props.getScale());

		resetProps();
		props.getScale().setZero();
		printScaleValues(props.getScale());

	}
	
	private void printScaleValues(Scale scale){
		System.out.println("ObjectId: " + scale);
		System.out.println("Type: " + scale.getType());
		if( scale.isManual() ){
			System.out.println("ManualScaleMin: " + scale.getManualScaleMin());
			System.out.println("ManualScaleMax: " + scale.getManualScaleMax());
		}
		System.out.println("");
	}
	
	private void resetProps(){
		props = new TimeseriesProperties(ts, station, width, height);
	}
}