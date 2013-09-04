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
		list.add("Chloridgehalt_(Einzelmessung)");
		categories.put("Chloridgehalt", list);

		list = new  ArrayList<String>();
		list.add("Abstich");
		categories.put("Grundwasserstand", list);

		list = new  ArrayList<String>();
		list.add("Leitfaehigkeit_(Dauermessung)");
		list.add("Leitfaehigkeit_(Einzelmessung)");
		categories.put("Leitfaehigkeit", list);

		list = new  ArrayList<String>();
		list.add("Windgeschwindigkeit");
		categories.put("Meteorologie", list);

		list = new  ArrayList<String>();
		list.add("Tideniedrigwasser_(mittleres)");
		list.add("Tidehub");
		list.add("Tidehub_(hoechster)");
		list.add("Wasserstand_(NN-Bezug)");
		list.add("Tidehochwasser_(hoechstes)");
		list.add("Tidedauer_(kuerzeste)");
		list.add("Tidehub_(niedrigster)");
		list.add("Ebbedauer_(mittlere)");
		list.add("Flutdauer_(laengste)");
		list.add("Ebbedauer_(kuerzeste)");
		list.add("Tideniedrigwasser_(hoechstes)");
		list.add("Tideniedrigwasser");
		list.add("Tideniedrigwasser_(NN-Bezug)");
		list.add("Tidehochwasser");
		list.add("Tidehochwasser_(hoechstes;_NN-Bezug)");
		list.add("Tideniedrigwasser_(hoechstes;_NN-Bezug)");
		list.add("Tidedauer");
		list.add("Tidehochwasser_(niedrigstes;_NN-Bezug)");
		list.add("Tidehochwasser_(mittleres)");
		list.add("Tidedauer_(mittlere)");
		list.add("Wasserstand_(Ganglinie)");
		list.add("Tideniedrigwasser_(niedrigstes;_NN-Bezug)");
		list.add("Flutdauer_(mittlere)");
		list.add("Tidehochwasser_(niedrigstes)");
		list.add("Tideniedrigwasser_(niedrigstes)");
		list.add("Tidemittelwasser");
		list.add("Tidehochwasser_(NN-Bezug)");
		list.add("Tideniedrigwasser_(mittleres;_NN-Bezug)");
		list.add("Flutdauer_(kuerzeste)");
		list.add("Tidehub_(mittlerer)");
		list.add("Tidehochwasser_(mittleres;_NN-Bezug)");
		list.add("Tidedauer_(laengste)");
		list.add("Ebbedauer_(laengste)");
		categories.put("Pegel", list);

		list = new  ArrayList<String>();
		list.add("Salzgehalt_(Dauermessung)");
		list.add("Salzgehalt");
		list.add("Salzgehalt_(Einzelmessung)");
		categories.put("Salzgehalt", list);

		list = new  ArrayList<String>();
		list.add("Sauerstoff-Saettigungsindex_(Einzelmessung)");
		list.add("Sauerstoff-Saettigungsindex_(Tagesminima)");
		list.add("Sauerstoffgehalt_(Tagesminima)");
		list.add("Sauerstoffgehalt_(Tagesmaxima)");
		list.add("Sauerstoff-Saettigungsindex_(Tagesmittelwert)");
		list.add("Sauerstoffgehalt_(Einzelmessung)");
		list.add("Sauerstoff-Saettigungsindex_(Tagesmaxima)");
		list.add("Sauerstoffgehalt_(Tagesmittelwert)");
		categories.put("Sauerstoff", list);

		list = new  ArrayList<String>();
		list.add("Stroemungsgeschwindigkeit");
		list.add("Ebbestromgeschwindigkeit_(mittlere)");
		list.add("Flutstromrichtung_(mittlere)");
		list.add("Kenterpunkte");
		list.add("Stroemungsrichtung");
		list.add("Ebbestromgeschwindigkeit_(maximale)");
		list.add("Flutstromgeschwindigkeit_(mittlere)");
		list.add("Flutstromgeschwindigkeit_(maximale)");
		list.add("Ebbestromdauer");
		list.add("Flutstromdauer");
		list.add("Ebbestromrichtung_(mittlere)");
		categories.put("Stroemung", list);

		list = new  ArrayList<String>();
		list.add("Temperatur_(Tagesmaxima)");
		list.add("Temperatur_(Tagesmittelwert)");
		list.add("Temperatur_(Tagesminima)");
		list.add("Temperatur");
		list.add("Wassertemperatur_(Tagesmaxima)");
		list.add("Wassertemperatur_(Tagesmittelwert)");
		list.add("Wassertemperatur_(Tagesminima)");
		list.add("Wassertemperatur");
		categories.put("Temperatur", list);

		list = new  ArrayList<String>();
		list.add("Truebung");
		categories.put("Truebung", list);

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
