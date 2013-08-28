package org.n52.shared.serializable.pojos;

import java.io.Serializable;

public class Scale implements Serializable {

	private static final long serialVersionUID = -1595716959847444176L;

	private Type type;
	
	private double manualScaleMin;
	
	private double manualScaleMax;
	
	public enum Type{
		ZERO, AUTO, MANUAL
	}
	
	public Scale(){
		this.setType(Type.AUTO);
		this.manualScaleMin = 0;
		this.manualScaleMax = 0;
	}

	public Scale(Type type){
		this.setType(type);
	}
	
	public void setType(Type scaleType){
		this.type = scaleType;
	}
	
	public Type getType(){
		return this.type;
	}
	
	public void setManualScaleMin(double min){
		this.manualScaleMin = min;
	}
	
	public double getManualScaleMin(){
		return this.manualScaleMin;
	}

	public void setManualScaleMax(double max){
		this.manualScaleMax = max;
	}
	
	public double getManualScaleMax(){
		return this.manualScaleMax;
	}
	
	public void setManualScale(double min, double max){
		this.setManualScaleMin(min);
		this.setManualScaleMax(max);
	}
	
	public void setAuto(){
		this.type = Type.AUTO;
	}
	
	public void setZero(){
		this.type = Type.ZERO;
	}
	
	public void setManual(){
		this.type = Type.MANUAL;
	}
	
	public boolean isAuto(){
		return this.type == Type.AUTO;
	}
	
	public boolean isZero(){
		return this.type == Type.ZERO;
	}
	
	public boolean isManual(){
		return this.type == Type.MANUAL;
	}
}
