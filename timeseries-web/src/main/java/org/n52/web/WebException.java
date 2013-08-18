package org.n52.web;


public interface WebException {
    
    public void addHint(String details);
    
    public String[] getHints();
    
    public Throwable getThrowable();

}
