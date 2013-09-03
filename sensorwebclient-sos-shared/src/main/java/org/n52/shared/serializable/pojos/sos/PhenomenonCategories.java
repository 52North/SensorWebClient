package org.n52.shared.serializable.pojos.sos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PhenomenonCategories implements Serializable {

	private static final long serialVersionUID = -4214461915858131292L;
	
	private static final HashMap<String, ArrayList<String>> categories = new HashMap<String, ArrayList<String>>();

	static{
		ArrayList<String> list;
		
		list = new  ArrayList<String>();
		list.add("Abfluss");
		categories.put("Abfluss", list);

		list = new  ArrayList<String>();
		list.add("Chloridgehalt (Einzelmessung)");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		categories.put("Chloridgehalt", list);

		list = new  ArrayList<String>();
		list.add("Abstich");
		categories.put("Grundwasserstand", list);

		list = new  ArrayList<String>();
		list.add("Leitfähigkeit (Dauermessung)");
		list.add("Leitfähigkeit (Einzelmessung)");
		categories.put("Leitfähigkeit", list);

		list = new  ArrayList<String>();
		list.add("Windgeschwindigkeit");
		categories.put("Meteorologie", list);

		list = new  ArrayList<String>();
		list.add("Tideniedrigwasser (mittleres)");
		list.add("Tidehub");
		list.add("Tidehub (höchster)");
		list.add("Wasserstand (NN-Bezug)");
		list.add("Tidehochwasser (höchstes)");
		list.add("Tidedauer (kürzeste)");
		list.add("Tidehub (niedrigster)");
		list.add("Ebbedauer (mittlere)");
		list.add("Flutdauer (längste)");
		list.add("Ebbedauer (kürzeste)");
		list.add("Tideniedrigwasser (höchstes)");
		list.add("Tideniedrigwasser");
		list.add("Tideniedrigwasser (NN-Bezug)");
		list.add("Tidehochwasser");
		list.add("Tidehochwasser (höchstes; NN-Bezug)");
		list.add("Tideniedrigwasser (höchstes; NN-Bezug)");
		list.add("Tidedauer");
		list.add("Tidehochwasser (niedrigstes; NN-Bezug)");
		list.add("Tidehochwasser (mittleres)");
		list.add("Tidedauer (mittlere)");
		list.add("Wasserstand (Ganglinie)");
		list.add("Tideniedrigwasser (niedrigstes; NN-Bezug)");
		list.add("Flutdauer (mittlere)");
		list.add("Tidehochwasser (niedrigstes)");
		list.add("Tideniedrigwasser (niedrigstes)");
		list.add("Tidemittelwasser");
		list.add("Tidehochwasser (NN-Bezug)");
		list.add("Tideniedrigwasser (mittleres; NN-Bezug)");
		list.add("Flutdauer (kürzeste)");
		list.add("Tidehub (mittlerer)");
		list.add("Tidehochwasser (mittleres; NN-Bezug)");
		list.add("Tidedauer (längste)");
		list.add("Ebbedauer (längste)");
		categories.put("Pegel", list);

		list = new  ArrayList<String>();
		list.add("Salzgehalt (Dauermessung)");
		list.add("Salzgehalt");
		list.add("Salzgehalt (Einzelmessung)");
		categories.put("Salzgehalt", list);

		list = new  ArrayList<String>();
		list.add("Sauerstoff-Sättigungsindex (Einzelmessung)");
		list.add("Sauerstoff-Sättigungsindex (Tagesminima)");
		list.add("Sauerstoffgehalt (Tagesminima)");
		list.add("Sauerstoffgehalt (Tagesmaxima)");
		list.add("Sauerstoff-Sättigungsindex (Tagesmittelwert)");
		list.add("Sauerstoffgehalt (Einzelmessung)");
		list.add("Sauerstoff-Sättigungsindex (Tagesmaxima)");
		list.add("Sauerstoffgehalt (Tagesmittelwert)");
		categories.put("Sauerstoff", list);

		list = new  ArrayList<String>();
		list.add("Strömungsgeschwindigkeit");
		list.add("Ebbestromgeschwindigkeit (mittlere)");
		list.add("Flutstromrichtung (mittlere)");
		list.add("Kenterpunkte");
		list.add("Strömungsrichtung");
		list.add("Ebbestromgeschwindigkeit (maximale)");
		list.add("Flutstromgeschwindigkeit (mittlere)");
		list.add("Flutstromgeschwindigkeit (maximale)");
		list.add("Ebbestromdauer");
		list.add("Flutstromdauer");
		list.add("Ebbestromrichtung (mittlere)");
		categories.put("Strömung", list);

		list = new  ArrayList<String>();
		list.add("Wassertemperatur (Tagesmaxima)");
		list.add("Wassertemperatur (Tagesmittelwert)");
		list.add("Wassertemperatur (Tagesminima)");
		list.add("Wassertemperatur");
		categories.put("Temperatur", list);

		list = new  ArrayList<String>();
		list.add("Trübung");
		categories.put("Trübung", list);

	}

	public static ArrayList<String> getList(String name){
		if( categories.containsKey(name) ){
			return categories.get(name);
		} else {
			return null;
		}
	}
	
	public static HashMap<String, ArrayList<String>> getLists(){
		return categories;
	}
}
