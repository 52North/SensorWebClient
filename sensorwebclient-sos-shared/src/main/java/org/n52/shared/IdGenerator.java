package org.n52.shared;

public interface IdGenerator {

    /**
     * @param parameters an ordered sequence of parameters to be used for generation.
     * @return a generated id.
     */
    public String generate(String[] parameters);
    
}
