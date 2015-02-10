/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.server.ses.util;

import java.util.ArrayList;
import java.util.List;

import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.server.ses.service.SesUserServiceImpl;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.responses.SesClientResponseType;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.BasicRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRule;
import org.n52.shared.serializable.pojos.ComplexRuleDTO;

public class SearchUtil {
    
    // final output lists
    private static ArrayList<BasicRuleDTO> finalBasicList = new ArrayList<BasicRuleDTO>();
    private static ArrayList<ComplexRuleDTO> finalComplexList = new ArrayList<ComplexRuleDTO>();
    
    public static SesClientResponse search(String text, int criterion, String userID) throws Exception {
        
        finalBasicList = new ArrayList<BasicRuleDTO>();
        finalComplexList = new ArrayList<ComplexRuleDTO>();
        
        String row = "";
        
        if (criterion == 1) {
            row = "eml";
            hibernateSearch(userID, row, text);
            row = "description";
            hibernateSearch(userID, row, text);
            row = "name";
            hibernateSearch(userID, row, text);
        } else if (criterion == 2) {
            // Titel
            row = "name";
            hibernateSearch(userID, row, text);
        } else if (criterion == 3) {
            // Description
            row = "description";
            hibernateSearch(userID, row, text);
        } else if (criterion == 4) {
            // Sensor
            row = "sensor";
            hibernateSearch(userID, row, text);
        } else if (criterion == 5) {
            // Phenomenon
            row = "phenomenon";
            hibernateSearch(userID, row, text);
        }
        
        // remove duplicates
        BasicRuleDTO basicRule;
        ComplexRuleDTO complexRule;
        String ruleName;
        
        for (int i = 0; i < finalBasicList.size(); i++) {
            basicRule = finalBasicList.get(i);
            ruleName = basicRule.getName();
            for (int j = i+1; j < finalBasicList.size(); j++) {
                if (ruleName.equals(finalBasicList.get(j).getName())) {
                    finalBasicList.remove(j);              
                }
            }
        }
        for (int i = 0; i < finalComplexList.size(); i++) {
            complexRule = finalComplexList.get(i);
            ruleName = complexRule.getName();
            for (int j = i+1; j < finalComplexList.size(); j++) {
                if (ruleName.equals(finalComplexList.get(j).getName())) {
                    finalComplexList.remove(j);              
                }
            }
        }
        
        return new SesClientResponse(SesClientResponseType.SEARCH_RESULT, finalBasicList, finalComplexList);
    }
    
    /**
     * 
     * @param userID
     * @param row
     * @param text
     */
    private static void hibernateSearch(String userID, String row, String text){
        // search
        List<BasicRule> allPublishedBasic = HibernateUtil.searchBasic(row, text);
        for (int i = 0; i < allPublishedBasic.size(); i++) {
            finalBasicList.add(SesUserServiceImpl.createBasicRuleDTO(allPublishedBasic.get(i)));
        }
        
        List<BasicRule> allOwnBasic = HibernateUtil.searchOwnBasic(userID, row, text);
        for (int i = 0; i < allOwnBasic.size(); i++) {
            finalBasicList.add(SesUserServiceImpl.createBasicRuleDTO(allOwnBasic.get(i)));
        }
        
        List<ComplexRule> allPublishedComplex = HibernateUtil.searchComplex(row, text);
        for (int i = 0; i < allPublishedComplex.size(); i++) {
            finalComplexList.add(SesUserServiceImpl.createComplexRuleDTO(allPublishedComplex.get(i)));
        }
        
        List<ComplexRule> allOwnComplex = HibernateUtil.searchOwnComplex(userID, row, text);
        for (int i = 0; i < allOwnComplex.size(); i++) {
            finalComplexList.add(SesUserServiceImpl.createComplexRuleDTO(allOwnComplex.get(i)));
        }
    }
}