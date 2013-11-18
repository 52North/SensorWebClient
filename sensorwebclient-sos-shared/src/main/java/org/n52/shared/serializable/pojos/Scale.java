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
	
	/**
	 * Create a default Scale object (Auto-Scaling)
	 */
	public Scale(){
		this(Type.AUTO, 0d, 0d);
	}

	/**
	 * Create a Scale object of type type
	 * @param type
	 */
	public Scale(Type type){
		this(type, 0d, 0d);
	}
	
	/**
	 * Create a Scale object of type type and with min/max values
	 * (usually used with type MANUAL)
	 * @param type
	 * @param min
	 * @param max
	 */
	public Scale(Type type, double min, double max){
		this.setType(type);
		this.setManualScaleMin(min);
		this.setManualScaleMax(max);
	}

	/**
	 * Creates a Scale object of type MANUAL with min/max values
	 * @param min
	 * @param max
	 */
	public Scale(double min, double max){
		this(Type.MANUAL, min, max);
	}
	
	/**
	 * Swithces the type of the object to type
	 * @param type
	 */
	public void setType(Type type){
		this.type = type != null 
			? type 
			: Type.AUTO;
	}
	
	/**
	 * Tries to read the Type from typeString.
	 * If it fails, AUTO will be set.
	 * @param typeString
	 */
	public void setType(String typeString){
		try{
			setType(Type.valueOf(typeString));
		}catch(Exception e){
			setType(Type.AUTO);
		}
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
	
	/**
	 * Returns a copy of given Scale object
	 * @param scale
	 * @return
	 */
	public static Scale copy(Scale scale){
		if(scale != null){
			return new Scale(scale.getType(), scale.getManualScaleMin(), scale.getManualScaleMax());
		} else {
			return new Scale();
		}
	}
	
	/**
	 * Returns a new Scale object copy of itself
	 * @return
	 */
	public Scale getCopy(){
		return copy(this);
	}
}
