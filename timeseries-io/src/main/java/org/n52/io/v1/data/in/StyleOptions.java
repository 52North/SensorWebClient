package org.n52.io.v1.data.in;

import java.util.Random;


public class StyleOptions {
	
    /**
     * Color as 6-digit hex value.
     */
    private String color = getRandomHexColor();
    
    private DiagramOptions diagram;

    private static String getRandomHexColor() {
        String redHex = getNextFormattedRandomNumber();
        String yellowHex = getNextFormattedRandomNumber();
        String blueHex = getNextFormattedRandomNumber();
        return "#" + redHex + yellowHex + blueHex;
    }

    private static String getNextFormattedRandomNumber() {
        String randomHex = Integer.toHexString(new Random().nextInt(256));
        if (randomHex.length() == 1) {
            // ensure two digits
            randomHex = "0" + randomHex;
        }
        return randomHex;
    }
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public DiagramOptions getDiagram() {
		return diagram;
	}

	public void setDiagram(DiagramOptions diagram) {
		this.diagram = diagram;
	}

}
