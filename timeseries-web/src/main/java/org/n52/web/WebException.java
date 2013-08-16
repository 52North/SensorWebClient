package org.n52.web;


public interface WebException {
    
    public void setHints(String[] details);
    
    public String[] getHints();
    
    public Throwable getThrowable();

}
