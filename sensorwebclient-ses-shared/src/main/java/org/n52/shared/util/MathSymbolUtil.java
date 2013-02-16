
package org.n52.shared.util;

import static org.n52.shared.serializable.pojos.Rule.EQUAL_TO;
import static org.n52.shared.serializable.pojos.Rule.GREATER_THAN;
import static org.n52.shared.serializable.pojos.Rule.GREATER_THAN_OR_EQUAL_TO;
import static org.n52.shared.serializable.pojos.Rule.LESS_THAN;
import static org.n52.shared.serializable.pojos.Rule.LESS_THAN_OR_EQUAL_TO;
import static org.n52.shared.serializable.pojos.Rule.NOT_EQUAL_TO;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.n52.shared.serializable.pojos.Rule;

public class MathSymbolUtil {

    private static LinkedHashMap<String, String> mathSymbols = createMathSymbolsMap();

    private static LinkedHashMap<String, String> createMathSymbolsMap() {
        Map<String, String> symbolsHashMap = new HashMap<String, String>();
        symbolsHashMap.put("=", "=");
        symbolsHashMap.put("<>", "<>");
        symbolsHashMap.put(">", ">");
        symbolsHashMap.put("<", "<");
        symbolsHashMap.put(">=", ">=");
        symbolsHashMap.put("<=", "<=");
        Map<String, String> unmodifiableMap = Collections.unmodifiableMap(symbolsHashMap);
        return new LinkedHashMap<String, String>(unmodifiableMap);
    }

    public static LinkedHashMap<String, String> getMathSymbols() {
        return mathSymbols;
    }

    /**
     * Determines the appropriate index for a mathematical symbol. The operator index is needed by serialized
     * {@link Rule} instances.
     * 
     * @param symbol
     *        the math symbol <code>=</code>, <code>&lt;&gt;</code>, <code>&gt;</code>, <code>&lt;</code>,
     *        <code>&gt;=</code>, <code>&lt;=</code>
     * @return the index used by a {@link Rule} instance, or <code>-1</code> if no appropriate index could be
     *         found.
     */
    public static int getIndexFor(String symbol) {
        if (symbol.equals("=")) {
            return EQUAL_TO;
        }
        else if (symbol.equals("<>")) {
            return NOT_EQUAL_TO;
        }
        else if (symbol.equals(">")) {
            return GREATER_THAN;
        }
        else if (symbol.equals("<")) {
            return LESS_THAN;
        }
        else if (symbol.equals(">=")) {
            return GREATER_THAN_OR_EQUAL_TO;
        }
        else if (symbol.equals("<=")) {
            return LESS_THAN_OR_EQUAL_TO;
        }
        else {
            return -1;
        }
    }

    public static int getInverse(int symbolIndex) {
        String symbol = getSymbolForIndex(symbolIndex);
        return getIndexFor(getInverse(symbol));
    }

    /**
     * Determines the inverse mathematical symbol.
     * 
     * @param symbol
     *        the math symbol <code>=</code>, <code>&lt;&gt;</code>, <code>&gt;</code>, <code>&lt;</code>,
     *        <code>&gt;=</code>, <code>&lt;=</code>
     * @return the inverse math symbol, or an empty String if no symbol matches.
     */
    public static String getInverse(String symbol) {
        if (symbol.equals("=")) {
            return "<>";
        }
        else if (symbol.equals("<>")) {
            return "=";
        }
        else if (symbol.equals(">")) {
            return "<=";
        }
        else if (symbol.equals("<")) {
            return ">=";
        }
        else if (symbol.equals(">=")) {
            return "&lt;";
        }
        else if (symbol.equals("<=")) {
            return ">";
        }
        else {
            return "";
        }
    }

    /**
     * Determines the appropriate math symbol index for an OGC filter property. The symbol index is needed by
     * serialized {@link Rule} instances.
     * 
     * @param fesFilter
     *        the OGC filter property, starting with prefix <code>fes:</code>.
     * @return the symbol index used by a {@link Rule} instance, or <code>-1</code> if no appropriate index
     *         could be found.
     */
    public static int getSymbolIndexForFilter(String fesFilter) {
        if (fesFilter.equals("fes:PropertyIsLessThan")) {
            return LESS_THAN;
        }
        else if (fesFilter.equals("fes:PropertyIsGreaterThan")) {
            return GREATER_THAN;
        }
        else if (fesFilter.equals("fes:PropertyIsEqualTo")) {
            return EQUAL_TO;
        }
        else if (fesFilter.equals("fes:PropertyIsGreaterThanOrEqualTo")) {
            return GREATER_THAN_OR_EQUAL_TO;
        }
        else if (fesFilter.equals("fes:PropertyIsLessThanOrEqualTo")) {
            return LESS_THAN_OR_EQUAL_TO;
        }
        else if (fesFilter.equals("fest:PropertyIsNotEqualTo")) {
            return NOT_EQUAL_TO;
        }
        return -1;
    }

    /**
     * Determines the appropriate math symbol for the given index. The symbol index is needed by serialized
     * {@link Rule} instances.
     * 
     * @param symbolIndex
     *        the index used by {@link Rule} instances.
     * @return the math symbol mathing the symbol index, or an empty String if no symbols matches.
     */
    public static String getSymbolForIndex(int symbolIndex) {
        if (symbolIndex == 0) {
            return "=";
        }
        else if (symbolIndex == 1) {
            return "<>";
        }
        else if (symbolIndex == 2) {
            return ">";
        }
        else if (symbolIndex == 3) {
            return "<";
        }
        else if (symbolIndex == 4) {
            return ">=";
        }
        else if (symbolIndex == 5) {
            return "<=";
        }
        return "";
    }

    /**
     * Determines the appropriate OGC filter property for the given math symbol index. The symbol index is
     * needed by serialized {@link Rule} instances. <br>
     * <br>
     * The following OGC filter properties (with prefixes) are in use:
     * <ul>
     * <li><code>fes:PropertyIsLessThan</code></li>
     * <li><code>fes:PropertyIsGreaterThan</code></li>
     * <li><code>fes:PropertyIsEqualTo</code></li>
     * <li><code>fes:PropertyIsGreaterThanOrEqualTo</code></li>
     * <li><code>fes:PropertyIsLessThanOrEqualTo</code></li>
     * <li><code>fes:PropertyIsNotEqualTo</code></li>
     * </ul>
     * 
     * @param symbolIndex
     *        the symbol index used by {@link Rule} instances.
     * @return the appropriate filter property with prefix <code>fes:</code>
     */
    public static String getFesFilterFor(int symbolIndex) {
        if (symbolIndex == LESS_THAN) {
            return "fes:PropertyIsLessThan";
        }
        else if (symbolIndex == GREATER_THAN) {
            return "fes:PropertyIsGreaterThan";
        }
        else if (symbolIndex == EQUAL_TO) {
            return "fes:PropertyIsEqualTo";
        }
        else if (symbolIndex == GREATER_THAN_OR_EQUAL_TO) {
            return "fes:PropertyIsGreaterThanOrEqualTo";
        }
        else if (symbolIndex == LESS_THAN_OR_EQUAL_TO) {
            return "fes:PropertyIsLessThanOrEqualTo";
        }
        else if (symbolIndex == NOT_EQUAL_TO) {
            return "fes:PropertyIsNotEqualTo";
        }
        else {
            return null;
        }
    }
    
}
