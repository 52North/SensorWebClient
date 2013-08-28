package org.n52.io.format;

public class FormatterFactory {
    
    private String format = "default";
    
    private FormatterFactory(String format) {
        if (format != null) {
            this.format = format;
        }
    }
    
    public static FormatterFactory createFormatterFactory() {
        return new FormatterFactory(null);
    }

    public static FormatterFactory createFormatterFactory(String format) {
        return new FormatterFactory(format);
    }
}
