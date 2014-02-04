/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.shared.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.n52.shared.serializable.pojos.Rule;

public class MathSymbolUtil {
    
    /*
     * For easy RPC serialization
     */

    public static final int EQUAL_TO_INT = 0;

    public static final int NOT_EQUAL_TO_INT = 1;
    
    public static final int GREATER_THAN_INT = 2;
    
    public static final int LESS_THAN_INT = 3;
    
    public static final int GREATER_THAN_OR_EQUAL_TO_INT = 4;
    
    public static final int LESS_THAN_OR_EQUAL_TO_INT = 5;
    
    /*
     * Normal constants
     */
    
    private static final String EQUAL_SYMBOL = "=";
    
    private static final String NOT_EQUAL_SYMBOL = "<>";
    
    private static final String GREATER_THAN_SYMBOL = ">";
    
    private static final String LESS_THAN_SYMBOL = "&lt;"; // IE doesn't like '<'
    
    private static final String GREATER_EQUAL_SYMBOL = ">=";
    
    private static final String LESS_EQUAL_SYMBOL = "<=";

    private static LinkedHashMap<String, String> mathSymbols = createMathSymbolsMap();

    private static LinkedHashMap<String, String> createMathSymbolsMap() {
        Map<String, String> symbolsHashMap = new HashMap<String, String>();
        symbolsHashMap.put(EQUAL_SYMBOL, EQUAL_SYMBOL);
        symbolsHashMap.put(NOT_EQUAL_SYMBOL, NOT_EQUAL_SYMBOL);
        symbolsHashMap.put(GREATER_THAN_SYMBOL, GREATER_THAN_SYMBOL);
        symbolsHashMap.put(LESS_THAN_SYMBOL, LESS_THAN_SYMBOL);
        symbolsHashMap.put(GREATER_EQUAL_SYMBOL, GREATER_EQUAL_SYMBOL);
        symbolsHashMap.put(LESS_EQUAL_SYMBOL, LESS_EQUAL_SYMBOL);
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
        if (symbol.equals(EQUAL_SYMBOL)) {
            return EQUAL_TO_INT;
        }
        else if (symbol.equals(NOT_EQUAL_SYMBOL)) {
            return NOT_EQUAL_TO_INT;
        }
        else if (symbol.equals(GREATER_THAN_SYMBOL)) {
            return GREATER_THAN_INT;
        }
        else if (symbol.equals(LESS_THAN_SYMBOL)) {
            return LESS_THAN_INT;
        }
        else if (symbol.equals(GREATER_EQUAL_SYMBOL)) {
            return GREATER_THAN_OR_EQUAL_TO_INT;
        }
        else if (symbol.equals(LESS_EQUAL_SYMBOL)) {
            return LESS_THAN_OR_EQUAL_TO_INT;
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
        if (symbol.equals(EQUAL_SYMBOL)) {
            return NOT_EQUAL_SYMBOL;
        }
        else if (symbol.equals(NOT_EQUAL_SYMBOL)) {
            return EQUAL_SYMBOL;
        }
        else if (symbol.equals(GREATER_THAN_SYMBOL)) {
            return LESS_EQUAL_SYMBOL;
        }
        else if (symbol.equals(LESS_THAN_SYMBOL)) {
            return GREATER_EQUAL_SYMBOL;
        }
        else if (symbol.equals(GREATER_EQUAL_SYMBOL)) {
            return LESS_THAN_SYMBOL;
        }
        else if (symbol.equals(LESS_EQUAL_SYMBOL)) {
            return GREATER_THAN_SYMBOL;
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
            return LESS_THAN_INT;
        }
        else if (fesFilter.equals("fes:PropertyIsGreaterThan")) {
            return GREATER_THAN_INT;
        }
        else if (fesFilter.equals("fes:PropertyIsEqualTo")) {
            return EQUAL_TO_INT;
        }
        else if (fesFilter.equals("fes:PropertyIsGreaterThanOrEqualTo")) {
            return GREATER_THAN_OR_EQUAL_TO_INT;
        }
        else if (fesFilter.equals("fes:PropertyIsLessThanOrEqualTo")) {
            return LESS_THAN_OR_EQUAL_TO_INT;
        }
        else if (fesFilter.equals("fest:PropertyIsNotEqualTo")) {
            return NOT_EQUAL_TO_INT;
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
            return EQUAL_SYMBOL;
        }
        else if (symbolIndex == 1) {
            return NOT_EQUAL_SYMBOL;
        }
        else if (symbolIndex == 2) {
            return GREATER_THAN_SYMBOL;
        }
        else if (symbolIndex == 3) {
            return LESS_THAN_SYMBOL;
        }
        else if (symbolIndex == 4) {
            return GREATER_EQUAL_SYMBOL;
        }
        else if (symbolIndex == 5) {
            return LESS_EQUAL_SYMBOL;
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
        if (symbolIndex == LESS_THAN_INT) {
            return "fes:PropertyIsLessThan";
        }
        else if (symbolIndex == GREATER_THAN_INT) {
            return "fes:PropertyIsGreaterThan";
        }
        else if (symbolIndex == EQUAL_TO_INT) {
            return "fes:PropertyIsEqualTo";
        }
        else if (symbolIndex == GREATER_THAN_OR_EQUAL_TO_INT) {
            return "fes:PropertyIsGreaterThanOrEqualTo";
        }
        else if (symbolIndex == LESS_THAN_OR_EQUAL_TO_INT) {
            return "fes:PropertyIsLessThanOrEqualTo";
        }
        else if (symbolIndex == NOT_EQUAL_TO_INT) {
            return "fes:PropertyIsNotEqualTo";
        }
        else {
            return null;
        }
    }
    
}
