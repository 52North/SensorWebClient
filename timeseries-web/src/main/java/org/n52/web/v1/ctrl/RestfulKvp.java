
package org.n52.web.v1.ctrl;

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
     * <li><code>true</code></li>
     * <li><code>false</code></li>
     * </ul>
     */
    static final String KVP_EXPANDED = "expanded";

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
    * An <code>int</code> value (provided as {@link String}) which defines the default page offset.
    * 
    * @see #KVP_OFFSET
    */
    static final String KVP_DEFAULT_OFFSET = "-1";
    
    /**
     * An <code>int</code> value (provided as {@link String}) which defines the default page size.
     * 
     * @see #KVP_SIZE
     */
    static final String KVP_DEFAULT_SIZE = "10";
    
    /**
     * Default <code>show</code>-parameter value.
     */
    static final String KVP_DEFAULT_EXPANDED = "false";
    
    /**
     * The spatial surrounding, given by a lon/lat ordered EPSG:4326 coordinate and radius.
     */
    static final String KVP_WITHIN = "within";
    
    static final String KVP_FEATURE = "feature";
    
    static final String KVP_OFFERING = "offering";
    
    static final String KVP_PROCEDURE = "procedure";
    
    static final String KVP_PHENOMENON = "phenomenon";
    
    

}
