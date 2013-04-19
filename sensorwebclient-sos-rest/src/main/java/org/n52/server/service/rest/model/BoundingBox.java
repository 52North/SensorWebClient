package org.n52.server.service.rest.model;

public class BoundingBox {
	
	private Point lowerLeft;
	
	private Point upperRight;
	
	private String srs;

	public Point getLowerLeft() {
		return lowerLeft;
	}

	public void setLowerLeft(Point lowerLeft) {
		this.lowerLeft = lowerLeft;
	}

	public Point getUpperRight() {
		return upperRight;
	}

	public void setUpperRight(Point upperRight) {
		this.upperRight = upperRight;
	}

	public String getSrs() {
		return srs;
	}

	public void setSrs(String srs) {
		this.srs = srs;
	}

}
