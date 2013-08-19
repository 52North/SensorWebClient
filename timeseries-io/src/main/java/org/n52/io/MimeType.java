package org.n52.io;

/**
 * @author henning
 *
 */
public enum MimeType {

    APPLICATION_JSON("application/json", "json"), IMAGE_PNG("image/png", "png"), APPLICATION_PDF("application/pdf","pdf");
    
    private String mimeType;
    
    private String formatName;
    
    private MimeType(String mimeType, String formatName) {
        this.mimeType = mimeType;
        this.formatName = formatName;
    }

    public String getMimeType() {
        return mimeType;
    }
    
    public String getFormatName() {
        return formatName;
    }

    @Override
    public String toString() {
        return getMimeType();
    }
    
}
