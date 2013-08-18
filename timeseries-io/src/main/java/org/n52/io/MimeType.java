package org.n52.io;

/**
 * @author henning
 *
 */
public enum MimeType {

    APPLICATION_JSON("application/json"), IMAGE_PNG("image/png"), APPLICATION_PDF("application/pdf");
    
    private String mimeType;
    
    private MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return getMimeType();
    }
    
}
