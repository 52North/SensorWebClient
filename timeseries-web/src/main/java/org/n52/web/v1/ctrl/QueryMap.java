
package org.n52.web.v1.ctrl;

import org.n52.io.crs.BoundingBox;
import org.n52.io.v1.data.Vicinity;
import org.n52.web.BadRequestException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

public class QueryMap {

    /**
     * How detailed the output shall be. Possible values are:
     * <ul>
     * <li><code>true</code></li>
     * <li><code>false</code></li>
     * </ul>
     */
    private static final String EXPANDED = "expanded";

    /**
     * The default expansion of collection items.
     * 
     * @see #EXPANDED
     */
    private static final boolean DEFAULT_EXPANDED = false;

    /**
     * Determines the index of the first member of the response page (a.k.a. page offset).
     */
    private static final String OFFSET = "offset";

    /**
     * The default page offset.
     * 
     * @see #OFFSET
     */
    private static final int DEFAULT_OFFSET = 0;

    /**
     * Determines the size of the page to be returned.
     */
    private static final String SIZE = "size";

    /**
     * The default page size.
     * 
     * @see #SIZE
     */
    private static final int DEFAULT_SIZE = 100;

    /**
     * Determines the language the output shall have.
     */
    private static final String LANGUAGE = "lang";

    /**
     * The default language.
     * 
     * @see #LANGUAGE
     */
    private static final String DEFAULT_LANGUAGE = "en";

    /**
     * Determines the service filter
     */
    private static final String SERVICE = "service";

    /**
     * Determines the feature filter
     */
    private static final String FEATURE = "feature";
    
    /**
     * Determines the service filter
     */
    private static final String OFFERING = "offering";

    /**
     * Determines the procedure filter
     */
    private static final String PROCEDURE = "procedure";

    /**
     * Determines the phenomenon filter
     */
    private static final String PHENOMENON = "phenomenon";
    
    /**
     * Determines the category filter
     */
    private static final String CATEGORY = "category";
    
    /**
     * Determines the within filter
     */
    private static final String WITHIN = "within";
    
    /**
     * Determines the bbox filter
     */
    private static final String BBOX = "bbox";
    

    private MultiValueMap<String, String> query;

    /**
     *  Use static constructor {@link #createFromQuery(MultiValueMap)}.
     */
    private QueryMap(MultiValueMap<String, String> queryParameters) {
        if (queryParameters == null) {
            query = new LinkedMultiValueMap<String, String>();
        }
        query = queryParameters;
    }

    /**
     * @return the value of {@value #OFFSET} parameter. If not present, the default {@value #DEFAULT_OFFSET}
     *         is returned.
     * @throws BadRequestException
     *         if parameter could not be parsed.
     */
    public int getOffset() {
        if ( !query.containsKey(OFFSET)) {
            return DEFAULT_OFFSET;
        }
        return parseFirstIntegerOfParameter(OFFSET);
    }

    /**
     * @return the value of {@value #SIZE} parameter. If not present, the default {@value #DEFAULT_SIZE} is
     *         returned.
     * @throws BadRequestException
     *         if parameter could not be parsed.
     */
    public int getSize() {
        if ( !query.containsKey(SIZE)) {
            return DEFAULT_SIZE;
        }
        return parseFirstIntegerOfParameter(SIZE);
    }

    /**
     * @return the value of {@value #LANGUAGE} parameter. If not present, the default
     *         {@value #DEFAULT_LANGUAGE} is returned.
     */
    public String getLanguage() {
        if ( !query.containsKey(LANGUAGE)) {
            return DEFAULT_LANGUAGE;
        }
        return query.getFirst(LANGUAGE);
    }

    public String getCategory() {
        return query.getFirst(CATEGORY);
    }

    public String getService() {
        return query.getFirst(SERVICE);
    }

    public String getOffering() {
        return query.getFirst(OFFERING);
    }

    public String getFeature() {
        return query.getFirst(FEATURE);
    }

    public String getProcedure() {
        return query.getFirst(PROCEDURE);
    }

    public String getPhenomenon() {
        return query.getFirst(PHENOMENON);
    }
    

    public BoundingBox getSpatialFilter() {
        if (! query.containsKey(WITHIN)) {
            return null;
        }
        String value = query.getFirst(WITHIN);
        ObjectMapper mapper = new ObjectMapper();
        Vicinity vicinity = mapper.convertValue(value, Vicinity.class);
        
        // TODO if bbox is present, merge bounds!
        
        return vicinity.calculateBounds();
    }

    /**
     * @return the value of {@value #EXPANDED} parameter.
     * @throws BadRequestException
     *         if parameter could not be parsed.
     */
    public boolean isExpanded() {
        if ( !query.containsKey(EXPANDED)) {
            return DEFAULT_EXPANDED;
        }
        return parseFirstBooleanOfParameter(EXPANDED);
    }

    public boolean containsParameter(String parameter) {
        return query.containsKey(parameter);
    }

    public String[] getOther(String parameter) {
        return query.get(parameter).toArray(new String[0]);
    }

    private int parseFirstIntegerOfParameter(String parameter) {
        try {
            String value = query.getFirst(parameter);
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new BadRequestException("Parameter '" + parameter + "' has to be an integer!");
        }
    }

    private boolean parseFirstBooleanOfParameter(String parameter) {
        try {
            String value = query.getFirst(parameter);
            return Boolean.parseBoolean(value);
        }
        catch (NumberFormatException e) {
            throw new BadRequestException("Parameter '" + parameter + "' has to be 'false' or 'true'!");
        }
    }

    public static QueryMap createFromQuery(MultiValueMap<String, String> queryParameters) {
        return new QueryMap(queryParameters);
    }
}
