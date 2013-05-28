
package org.n52.server.service.rest.control;

/**
 * The {@link RestfulKvp} serves as markup interface, so that each controller instance uses the same KVP
 * parameter names.<br/>
 * <br/>
 * <b>Note:</b> Do not code against this type.
 */
public interface RestfulKvp {

    /**
     * How detailed the output shall be. Possible values are:
     * <ul>
     * <li><code>complete</code></li>
     * <li><code>simple</code></li>
     * </ul>
     */
    static final String KVP_SHOW = "show";

    /**
     * An <code>int</code> value which is the index of the first member of the response page (a.k.a. page
     * offset).
     */
    static final String KVP_OFFSET = "offset";

    /**
     * An <code>int</code> value determining the size of the page to be returned.
     */
    static final String KVP_SIZE = "size";

    /**
     * An <code>int</code> value (provided as {@link String}) which defines the default page size.
     * 
     * @see #KVP_SIZE
     */
    static final String KVP_DEFAULT_SIZE = "10";
    
    /**
     * Default <code>show</code>-parameter value.
     */
    static final String KVP_DEFAULT_SHOW = "complete";
    
    static final String KVP_FEATURE = "feature";
    
    static final String KVP_OFFERING = "offering";
    
    static final String KVP_PROCEDURE = "procedure";
    
    static final String KVP_PHENOMENON = "phenomenon";
    
    

}
