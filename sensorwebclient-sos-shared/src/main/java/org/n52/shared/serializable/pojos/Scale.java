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
		this(Type.AUTO, 0d, 0d);
	}

	public Scale(Type type){
		this(type, 0d, 0d);
	}
	
	public Scale(Type type, double min, double max){
		this.setType(type);
		this.setManualScaleMin(min);
		this.setManualScaleMax(max);
	}

	public Scale(double min, double max){
		this(Type.MANUAL, min, max);
	}
	
	public void setType(Type type){
		this.type = type != null 
			? type 
			: Type.AUTO;
	}
	
	public Type getType(){
		return this.type != null
			? this.type
			: Type.AUTO;
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
	
	/**
	 * If true sets type to AUTO else to ZERO
	 * (for backwards compatibility when only ZERO and AUTO existed)
	 * Use setType(ScaleType) 
	 * @param autoScaled
	 * @deprecated
	 */
	public void setAuto(boolean autoScaled){
		this.setType(autoScaled ? Scale.Type.AUTO : Scale.Type.ZERO );
	}
	
	public void setZero(){
		this.type = Type.ZERO;
	}
	
	/**
	 * If true sets type to ZERO else to AUTO
	 * (for backwards compatibility when only ZERO and AUTO existed)
	 * Use setType(ScaleType) 
	 * @param zeroScaled
	 * @deprecated
	 */
	public void setZero(boolean zeroScaled){
		this.setType( zeroScaled ? Scale.Type.ZERO : Scale.Type.AUTO);
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
	
	public static Scale copy(Scale scale){
		if(scale != null){
			return new Scale(scale.getType(), scale.getManualScaleMin(), scale.getManualScaleMax());
		} else {
			return new Scale();
		}
	}
	
	public Scale getCopy(){
		return new Scale(this.getType(), this.getManualScaleMin(), this.getManualScaleMax());
	}
}
