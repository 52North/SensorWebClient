
package org.n52.io;

import static java.util.ResourceBundle.getBundle;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {

    private static final String MESSAGES = "locales/messages";

    private final ResourceBundle bundle;

    private Locale locale;

    private I18N(ResourceBundle bundle, Locale locale) {
        this.locale = locale;
        this.bundle = bundle;
    }

    public String get(String string) {
        return bundle.getString(string);
    }

    /**
     * @return the 2-char language code.
     * @see Locale#getLanguage()
     */
    public String getLocale() {
        return locale.getLanguage();
    }

    public static I18N getDefaultLocalizer() {
        return getMessageLocalizer("en");
    }

    public static I18N getMessageLocalizer(String language) {
        Locale locale = new Locale(language);
        return new I18N(getBundle(MESSAGES, locale), locale);
    }

}
