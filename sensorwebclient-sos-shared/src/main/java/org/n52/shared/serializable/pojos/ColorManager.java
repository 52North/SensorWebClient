package org.n52.shared.serializable.pojos;

import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

public class ColorManager implements Serializable{
	
	private static final long serialVersionUID = 5874636538758855453L;

	Vector<String> colors = new Vector<String>();
	Vector<String> colorsUsed = new Vector<String>();
	
	public ColorManager(){
		colors.add("#000000");colors.add("#800000");colors.add("#FF0000");colors.add("#FF00FF");
		colors.add("#996100");colors.add("#FF6600");colors.add("#FF9900");colors.add("#FFCC00");
		colors.add("#636300");colors.add("#808000");colors.add("#99CC00");colors.add("#FFFF00");
		colors.add("#006300");colors.add("#8000FF");colors.add("#639966");colors.add("#00FF00");
		colors.add("#006366");colors.add("#008080");colors.add("#63CCCC");colors.add("#00FFFF");
		colors.add("#000080");colors.add("#0000FF");colors.add("#6366FF");colors.add("#00CCFF");
		colors.add("#636399");colors.add("#666699");colors.add("#800080");colors.add("#996366");
		colors.add("#636363");colors.add("#808080");colors.add("#999999");colors.add("#C0C0C0");
	}
	
	public String getNewRandomColor(){
		int colorSize = colors.size();
		int rand = new Random().nextInt(colorSize);
		
		if(allColorsUsed()){
			resetUsedColors();
		}
		
		while(isColorUsed(colors.get(rand))){
			rand = new Random().nextInt(colorSize);
		}
		colorsUsed.add(colors.get(rand));
		return colors.get(rand);
	}
	
	public void resetUsedColors(){
		colorsUsed.clear();
	}
	
	public boolean isColorUsed(String color){
		return colorsUsed.contains(color);
	}
	
	public boolean allColorsUsed(){
		return colorsUsed.size() >= colors.size();
	}
}
