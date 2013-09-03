package org.n52.client.util;

import com.smartgwt.client.widgets.Label;

public class LabelFactory {

	public LabelFactory() {
		throw new AssertionError();
	}

	public static Label getFormattedLabel() {
		Label base = getBaseLabel();
		base.addStyleName("text");
		base.setAutoWidth();
		base.setMargin(5);
		base.setWrap(false);
		return base;
	}

	public static Label getFormattedLabel(String contents) {
		Label label = getFormattedLabel();
		label.setContents(contents);
		return label;
	}

	public static Label getFormattedLinkLabel() {
		Label base = getFormattedLabel();
		base.addStyleName("link");
		return base;
	}

	public static Label getFormattedLinkLabel(String contents) {
		Label label = getFormattedLinkLabel();
		label.setContents(contents);
		return label;
	}

	public static Label getBaseLabel() {
		Label base = new Label() {
			@Override
			public void addStyleName(String style) {
				setStyleName(getStyleName() + " " + style);
			}
		};
		return base;
	}
}
