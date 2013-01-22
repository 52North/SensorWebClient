package org.n52.client.ses.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RuleOperatorUtil {

    private static LinkedHashMap<String, String> ruleOperators = createRuleOperatorsMap();
    
    private static LinkedHashMap<String, String> createRuleOperatorsMap() {
        Map<String, String> operatorHashMap = new HashMap<String, String>();
        operatorHashMap.put("=", "=");
        operatorHashMap.put("<>", "<>");
        operatorHashMap.put(">", ">");
        operatorHashMap.put("&lt;", "&lt;");
        operatorHashMap.put(">=", ">=");
        operatorHashMap.put("<=", "<=");
        Map<String, String> unmodifiableMap = Collections.unmodifiableMap(operatorHashMap);
        return new LinkedHashMap<String, String>(unmodifiableMap);
    }
    
    public static LinkedHashMap<String, String> getRuleOperators() {
        return ruleOperators;
    }

    public static String getOperatorFrom(int operatorIndex) {
        if (operatorIndex == 0) {
            return "=";
        } else if (operatorIndex == 1) {
            return "<>";
        } else if (operatorIndex == 2) {
            return ">";
        } else if (operatorIndex == 3) {
            return "&lt;";
        } else if (operatorIndex == 4) {
            return ">=";
        } else if (operatorIndex == 5) {
            return "<=";
        }
        return "";
    }
    
    public static int getOperatorIndex(String operator) {
        int operatorIndex = 0;
        if (operator.equals("=")) {
            operatorIndex = 0;
        } else if (operator.equals("<>")) {
            operatorIndex = 1;
        } else if (operator.equals(">")) {
            operatorIndex = 2;
        } else if (operator.equals("&lt;")) {
            operatorIndex = 3;
        } else if (operator.equals(">=")) {
            operatorIndex = 4;
        } else if (operator.equals("<=")) {
            operatorIndex = 5;
        }
        
        return operatorIndex;
    }
    
    public static String getInverseOperator(String operator) {
        String inverseOperator = "";
        if (operator.equals("=")) {
            inverseOperator = "<>";
        } else if (operator.equals("<>")) {
            inverseOperator = "=";
        } else if (operator.equals(">")) {
            inverseOperator = "<=";
        } else if (operator.equals("&lt;")) {
            inverseOperator = ">=";
        } else if (operator.equals(">=")) {
            inverseOperator = "&lt;";
        } else if (operator.equals("<=")) {
            inverseOperator = ">";
        }
        
        return inverseOperator;
    }
    
    public static int getIndexOfInverseOperator(int operatorIndex) {
        String operator = getOperatorFrom(operatorIndex);
        return getOperatorIndex(getInverseOperator(operator));
    }
}
