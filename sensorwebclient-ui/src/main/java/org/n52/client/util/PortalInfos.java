package org.n52.client.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import com.google.gwt.user.client.Window;

public class PortalInfos implements Serializable {

	private static final long serialVersionUID = 5072696065309330817L;

	private static ArrayList<PortalInfo> portalInfos;

	protected PortalInfos() {
		// Exists only to defeat instantiation.
		throw new AssertionError();
	}

	/**
	 * Returns the current portal based on the URL. If not found in list, the
	 * first PortalInfo is returned.
	 * 
	 * @return
	 */
	public static PortalInfo getCurrent() {
		if (getAll() != null && getAll().size() > 0) {
			String currentHostname = Window.Location.getHostName();
			for (PortalInfo portalInfo : getAll()) {
				if (portalInfo.usesUrl(currentHostname)) {
					return portalInfo;
				}
			}
			return getAll().get(0);
		}
		return null;
	}

	public static ArrayList<PortalInfo> getAll() {
		if (PortalInfos.portalInfos == null) {
			Vector<String> urlVariants;
			ArrayList<PortalInfo> initPortalInfos = new ArrayList<PortalInfo>();

			urlVariants = new Vector<String>();
			urlVariants.add("küstendaten.de");
			urlVariants.add("kuestendaten.de");
			urlVariants.add("xn--kstendaten-9db.de");
			initPortalInfos.add(new PortalInfo("Küstendaten", "http://www.kuestendaten.de/", urlVariants));

			urlVariants = new Vector<String>();
			urlVariants.add("portalnsk.de");
			initPortalInfos.add(new PortalInfo("Nordseeküste", "http://www.portalnsk.de/", urlVariants));

			urlVariants = new Vector<String>();
			urlVariants.add("portaltideems.de");
			urlVariants.add("portal-tideems.de");
			urlVariants.add("emsverbesserung.de");
			initPortalInfos.add(new PortalInfo("Tideems", "http://www.portaltideems.de/", urlVariants));

			urlVariants = new Vector<String>();
			urlVariants.add("portaltideelbe.de");
			urlVariants.add("portal-tideelbe.de");
			initPortalInfos.add(new PortalInfo("Tideelbe", "http://www.portaltideelbe.de/", urlVariants));

			urlVariants = new Vector<String>();
			urlVariants.add("portalnok.de");
			initPortalInfos.add(new PortalInfo("Nord-Ostsee-Kanal", "http://www.portalnok.de/", urlVariants));

			urlVariants = new Vector<String>();
			urlVariants.add("portalosk.de");
			initPortalInfos.add(new PortalInfo("Ostseeküste", "http://www.portalosk.de/", urlVariants));

			PortalInfos.portalInfos = initPortalInfos;
		}
		return PortalInfos.portalInfos;
	}
}
