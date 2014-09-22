/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server.ses.eml;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.n52.server.ses.SesConfig;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.ComplexRule;
import org.n52.shared.serializable.pojos.ComplexRuleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Deprecated
public class ComplexRule_BuilderV2 {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexRule_BuilderV2.class);
    private static Document finalDoc;
    private static NodeList finalComplexPatternList;
    private static int patternCount = 0;
    private static int size = 0;
    private static int treeCount = 0;
    private static int patternIDCount = 0;

    private static ArrayList<String> enterList = new ArrayList<String>();
    private static ArrayList<String> exitList = new ArrayList<String>();
    private static ArrayList<String> ruleList = new ArrayList<String>();

    private static ArrayList<String> patterIDs = new ArrayList<String>();
    private static ArrayList<String> messages = new ArrayList<String>();
    private static ArrayList<String> outputNames = new ArrayList<String>();

    /**
     * This method combines three to six rules to a Complex rule.
     * 
     * @param rule
     * @param treeContent
     * @return {@link ComplexRule}
     * @throws Exception
     */
    public static ComplexRule combineRules(ComplexRuleData rule, ArrayList<String> treeContent) throws Exception {
       
    	String eml;
        String finalEml;

        try {
        	// count of complex patterns (CP) which are used to combine the rules
            // 2 rules: 3 <CP>
            // 3 rules: 5 <CP>
            // 4 rules: 7 <CP>
            // ...

            messages.add("Einstiegsklausel");
            messages.add("Ausstiegsklausel");
            messages.add("EMPTY");

            // new outputNames
            outputNames.add(rule.getTitle() + "_overshoot_output");
            outputNames.add(rule.getTitle() + "_undershoot_output");

            ArrayList<String> ruleNames = new ArrayList<String>();
            ArrayList<String> operators = new ArrayList<String>();
            
            // This ArrayList contains all rules which should be combined
            ArrayList<Object> RULES = new ArrayList<Object>();

            // parse the tree to get the rule names and the operators
            for (int i = 0; i < treeContent.size(); i++) {
                String content = treeContent.get(i);
                if ((!content.equals(Constants.AND) && !content.equals(Constants.OR) && !content.equals(Constants.AND_NOT))) {
                    // rule name was found
                	ruleNames.add(content);
                } else {
                	// operator was found
                    operators.add(content);
                }
            }

            // add basic or complex rules to RULES list
            for (int i = 0; i < ruleNames.size(); i++) {
//                BasicRule basic = HibernateUtil.getBasicRuleByName(ruleNames.get(i));
                BasicRule basic = HibernateUtil.getBasicRuleByUuid(ruleNames.get(i)); // XXX
                ComplexRule complex = HibernateUtil.getComplexRuleByName(ruleNames.get(i));
                
                if (basic != null) {
                    RULES.add(basic);
                }
                
                if (complex != null) {
                    RULES.add(complex);
                }
            }
            
            // rule name
            final String ruleName = rule.getTitle();

            // new patternIDs
            ArrayList<String> patterIDName = new ArrayList<String>();
            int count = ruleNames.size()*2-1;
            for (int i = 0; i < count; i++) {
                patterIDName.add(ruleName + "_"+ i +"_new_stream");
            }

            // new newEventNames
            ArrayList<String> newEventNames = new ArrayList<String>();
            for (int i = 0; i < count; i++) {
                newEventNames.add(ruleName + "_"+ i +"_new_event_name");
            }

            // new outputNames
            ArrayList<String> outputNames = new ArrayList<String>();
            outputNames.add(ruleName + "_overshoot_output");
            outputNames.add(ruleName + "_undershoot_output");

            // new messages
            ArrayList<String> mess = new ArrayList<String>();
            mess.add("Einstiegsklausel");
            mess.add("Ausstiegsklausel");

            // pairwise: enter condition, exit condition
            patterIDs = new ArrayList<String>();

            // docBuilder
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();

            // list of eml documents
            ArrayList<Document> documents = new ArrayList<Document>();

            // load EML rule files 
            BufferedReader reader;
            for (int i = 0; i < ruleNames.size(); i++) {
                Object o = RULES.get(i);
                if (o instanceof BasicRule) {
                    BasicRule br = (BasicRule)o;
                  reader = new BufferedReader(new StringReader(br.getEml()));
                  documents.add(docBuilder.parse(new InputSource(reader)));
                } else if (o instanceof ComplexRule) {
                    ComplexRule cr = (ComplexRule)o;
                    reader = new BufferedReader(new StringReader(cr.getEml()));
                    documents.add(docBuilder.parse(new InputSource(reader)));
                }
            }

            // merge all SimplePatterns
            NodeList simplePatternsList = documents.get(0).getElementsByTagName(Constants.simplePatterns);
            Node simpleRoot = simplePatternsList.item(0);

            for (int i = 1; i < documents.size(); i++) {
                NodeList simplePatternsList2 = documents.get(i).getElementsByTagName(Constants.simplePattern);
                for (int j = 0; j < simplePatternsList2.getLength(); j++) {
                    Node simpleNode = simplePatternsList2.item(j);
                    Node nd = documents.get(0).importNode(simpleNode, true);
                    simpleRoot.appendChild(nd);
                }
            }

            // merge all ComplexPatterns
            NodeList complexPatternsList = documents.get(0).getElementsByTagName(Constants.complexPatterns);
            Node complexRoot = complexPatternsList.item(0);

            for (int i = 1; i < documents.size(); i++) {
                NodeList complexPatternsList2 = documents.get(i).getElementsByTagName(Constants.complexPattern);
                for (int j = 0; j < complexPatternsList2.getLength(); j++) {
                    Node complexNode = complexPatternsList2.item(j);
                    Node nd = documents.get(0).importNode(complexNode, true);
                    complexRoot.appendChild(nd);
                }
            }

            // add logicalPatterns: logicalCount = (ruleNames.size()*2)-2
            URL logicalURL = new URL(SesConfig.resLocation_logical);
            int logicalCount = (ruleNames.size()*2)-2;
            for (int i = 0; i < logicalCount; i++) {
                Document logDoc = docBuilder.parse(logicalURL.openStream());
                NodeList complexPatternsList2 = logDoc.getElementsByTagName(Constants.complexPattern);
                for (int j = 0; j < complexPatternsList2.getLength(); j++) {
                    Node complexNode = complexPatternsList2.item(j);
                    Node nd = documents.get(0).importNode(complexNode, true);
                    complexRoot.appendChild(nd);
                }
            }

            // add one structuralPattern
            URL structuralURL = new URL(SesConfig.resLocation_structural);
            Document structDoc = docBuilder.parse(structuralURL.openStream());
            NodeList complexPatternsList2 = structDoc.getElementsByTagName(Constants.complexPattern);
            for (int j = 0; j < complexPatternsList2.getLength(); j++) {
                Node complexNode = complexPatternsList2.item(j);
                Node nd = documents.get(0).importNode(complexNode, true);
                complexRoot.appendChild(nd);
            }

            // parse <ComplexPattern> to remove outputNames and save enter and exit condition patternIDs
            NodeList complexPatternList = documents.get(0).getElementsByTagName(Constants.complexPattern);
            for (int i = 0; i < complexPatternList.getLength(); i++) {
                Node fstNode = complexPatternList.item(i);
                Element fstElement = (Element) fstNode;

                // newEventName of SelectFunction
                NodeList selectFunctionList = fstElement.getElementsByTagName(Constants.selectFunction);
                Node selectFunctionNode = selectFunctionList.item(0);
                if (selectFunctionNode.getAttributes().getNamedItem(Constants.outputName) != null) {
                    // remove outputName
                    if (i<complexPatternList.getLength()) {
                        selectFunctionNode.getAttributes().removeNamedItem(Constants.outputName);
                    }

                    // save patternID
                    patterIDs.add(fstNode.getAttributes().getNamedItem(Constants.patternID).getTextContent());
                }
                // <Message> set message to EMPTY
                NodeList messageList = fstElement.getElementsByTagName(Constants.message);
                if (messageList.getLength() != 0) {
                    Node messageNode = messageList.item(0);
                    messageNode.setTextContent(messages.get(2));
                }
            }
            
            // NEW PATTERNS
            // set patternIDs to new patterns
            for (int j = complexPatternList.getLength()-(logicalCount+1); j < complexPatternList.getLength(); j++) {
                int value = j-(complexPatternList.getLength()-(logicalCount+1));

                Node fstNode = complexPatternList.item(j);
                Element fstElement = (Element) fstNode;

                // set patternID
                Node patternIdNode = complexPatternList.item(j);
                patternIdNode.getAttributes().getNamedItem(Constants.patternID).setTextContent(patterIDName.get(value));

                // newEventName of SelectFunction
                NodeList selectFunctionList = fstElement.getElementsByTagName(Constants.selectFunction);
                Node selectFunctionNode = selectFunctionList.item(0);
                selectFunctionNode.getAttributes().getNamedItem(Constants.newEventName).setTextContent(newEventNames.get(value));
            }

            // transformer
            Transformer transormer2 = TransformerFactory.newInstance().newTransformer();
            transormer2.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result2 = new StreamResult(new StringWriter());
            DOMSource source2 = new DOMSource(documents.get(0));
            transormer2.transform(source2, result2);

            finalDoc = docBuilder.parse(new InputSource(new StringReader(result2.getWriter().toString())));

            finalComplexPatternList = finalDoc.getElementsByTagName(Constants.complexPattern);

            // set patternCount to the new patterns
            patternCount = finalComplexPatternList.getLength()-(logicalCount+1);

            // parse tree content which contains the combination tree, traversed in postorder (RULE, RULE, OPERATOR, ...)
            size = treeContent.size()-1;
            for (int i = 0; i < treeContent.size(); i++) {
                treeCount = i;
                String content = treeContent.get(i);
                if ((!content.equals(Constants.AND) && !content.equals(Constants.OR) && !content.equals(Constants.AND_NOT))) {
                    //current entry is not an operator --> rule
                    ruleList.add(content);
                } else {
                    // operator
                    setEnter(content);
                    setExit(Constants.OR);
                }
            }

            // RESULT
            // transformer for final output
            Transformer transormer = TransformerFactory.newInstance().newTransformer();
            transormer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(finalDoc);
            transormer.transform(source, result);

            eml = result.getWriter().toString();
            finalEml = eml;
            finalEml = finalEml.substring(finalEml.indexOf("<EML"));

            LOGGER.debug(finalEml);

        } catch (Exception e) {
            throw new Exception("build complex rule failed!", e);
        }
        
        // rule names for quick rearch
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < treeContent.size(); i++) {
            sb.append(treeContent.get(i));
            sb.append("_T_");
        }
        
        // clear values
        clear();
        
        return new ComplexRule(rule.getTitle(), "K", rule.getDescription(), rule.isPublish(), rule.getUserID(), finalEml, false, "", "", sb.toString());
    }

    /**
     * set pattern condition data
     * 
     * @param operator
     */
    private static void setEnter(String operator){

        Node fstNode = finalComplexPatternList.item(patternCount);
        Element fstElement = (Element) fstNode;

        // get patternID of current ComplexPattern
        String patterID = fstNode.getAttributes().getNamedItem(Constants.patternID).getTextContent();

        // <LogicalOperator>
        NodeList logicalOperatorList = fstElement.getElementsByTagName(Constants.logicalOperator);
        if (logicalOperatorList.getLength() != 0) {
            Node logicalOperatorNode = logicalOperatorList.item(0);
            Node operatorNode = finalDoc.createElement(operator);

            logicalOperatorNode.appendChild(operatorNode);
        }

        // <PatternReference>
        // should be pairwise 
        NodeList patterReferenceList = fstElement.getElementsByTagName(Constants.patternReference);
        Node patterReferenceNode = patterReferenceList.item(0);
        Node patterReferenceNode2 = patterReferenceList.item(1);

        // combine "normal" pair of rules
        if (ruleList.size() == 2) {

            patterReferenceNode.setTextContent(patterIDs.get(patternIDCount));
            patterReferenceNode2.setTextContent(patterIDs.get(patternIDCount+2));

            // clear ruleList
            ruleList.clear();

            // add patternID to enterList
            enterList.add(patterID);
        } else if (ruleList.size() == 1 && enterList.size() == 1) {
            // create enter notification (odd rule count)
            patterReferenceNode.setTextContent(enterList.get(0));
            patterReferenceNode2.setTextContent(patterIDs.get(patterIDs.size()-2));

            // <Message> set messages
            NodeList messageList = fstElement.getElementsByTagName(Constants.message);
            if (messageList.getLength() != 0) {
                Node messageNode = messageList.item(0);
                messageNode.setTextContent(messages.get(0));
            }

            // set outputName
            NodeList selectFunctionList = fstElement.getElementsByTagName(Constants.selectFunction);
            Node selectFunctionNode = selectFunctionList.item(0);
            selectFunctionNode.getAttributes().setNamedItem(finalDoc.createAttribute(Constants.outputName));
            selectFunctionNode.getAttributes().getNamedItem(Constants.outputName).setTextContent(outputNames.get(0));

            // clear enterList
            enterList.clear();

            enterList.add(patterID);

            //        } else if (ruleList.size() == 1 && enterList.size() == 1 && treeCount != size) {
            //            // combine block with single rule
            //            // TODO eventuell löschen und diese kombination nicht erlauben (dann oben treecount == size löschen)
            //            patterReferenceNode.setTextContent(enterList.get(0));
            //            patterReferenceNode2.setTextContent(patterIDs.get(patternIDCount));
            //            
            //            // clear enterList
            //            enterList.clear();
            //
            //            enterList.add(patterID);

        } else if (ruleList.isEmpty() && enterList.size() == 2 && treeCount == size) {
            // create enter notification (even rule count)
            patterReferenceNode.setTextContent(enterList.get(0));
            patterReferenceNode2.setTextContent(enterList.get(1));

            // <Message> set messages
            NodeList messageList = fstElement.getElementsByTagName(Constants.message);
            if (messageList.getLength() != 0) {
                Node messageNode = messageList.item(0);
                messageNode.setTextContent(messages.get(0));
            }

            // set outputName
            NodeList selectFunctionList = fstElement.getElementsByTagName(Constants.selectFunction);
            Node selectFunctionNode = selectFunctionList.item(0);
            selectFunctionNode.getAttributes().setNamedItem(finalDoc.createAttribute(Constants.outputName));
            selectFunctionNode.getAttributes().getNamedItem(Constants.outputName).setTextContent(outputNames.get(0));

            // clear enterList
            enterList.clear();

            enterList.add(patterID);
        } else if (ruleList.isEmpty() && enterList.size() == 2 && treeCount != size) {
            patterReferenceNode.setTextContent(enterList.get(0));
            patterReferenceNode2.setTextContent(enterList.get(1));

            // clear enterList
            enterList.clear();

            // add patternID to enterList
            enterList.add(patterID);
        }
        // increment patternCount
        patternCount++;
    }

    /**
     * set pattern data
     * 
     * @param operator
     */
    private static void setExit(String operator){
        Node fstNode = finalComplexPatternList.item(patternCount);
        Element fstElement = (Element) fstNode;

        // patternID
        String patterID = fstNode.getAttributes().getNamedItem(Constants.patternID).getTextContent();


        // <LogicalOperator>
        NodeList logicalOperatorList = fstElement.getElementsByTagName(Constants.logicalOperator);
        if (logicalOperatorList.getLength() != 0) {

            Node logicalOperatorNode = logicalOperatorList.item(0);
            Node operatorNode = finalDoc.createElement(operator);

            logicalOperatorNode.appendChild(operatorNode);
        }

        // <PatternReference>
        // should be pairwise 
        NodeList patterReferenceList = fstElement.getElementsByTagName(Constants.patternReference);
        Node patterReferenceNode = patterReferenceList.item(0);
        Node patterReferenceNode2 = patterReferenceList.item(1);

        if (ruleList.isEmpty() && exitList.size() == 2 && treeCount == size) {
            // create last exit combination (even rule count)
            patterReferenceNode.setTextContent(exitList.get(0));
            patterReferenceNode2.setTextContent(exitList.get(1));

            //clear exitList
            exitList.clear();

            exitList.add(patterID);

            // increment patternCount
            patternCount++;

            setStructural();
        } else if (ruleList.isEmpty() && exitList.size() == 2 && treeCount != size) {
            // combine "normal" from exitList
            patterReferenceNode.setTextContent(exitList.get(0));
            patterReferenceNode2.setTextContent(exitList.get(1));

            //clear exitList
            exitList.clear();

            exitList.add(patterID);

            // increment patternCount
            patternCount++;
        } else if (exitList.size() == 1 && ruleList.size() == 1){
            // create last exit combination (odd rule count)
            patterReferenceNode.setTextContent(exitList.get(0));
            patterReferenceNode2.setTextContent(patterIDs.get(patterIDs.size()-1));

            //clear exitList
            exitList.clear();

            exitList.add(patterID);

            // increment patternCount
            patternCount++;

            setStructural();

        } else {
            // combine "normal" pair
            patternIDCount++;
            patterReferenceNode.setTextContent(patterIDs.get(patternIDCount));
            patternIDCount++;
            patternIDCount++;
            patterReferenceNode2.setTextContent(patterIDs.get(patternIDCount));
            patternIDCount++;

            // clear ruleList
            ruleList.clear();

            // add patternID to exitList
            exitList.add(patterID);

            // increment patternCount
            patternCount++;
        }
    }

    /**
     *  fill pattern with structural operator
     */
    private static void setStructural() {
        Node fstNode = finalComplexPatternList.item(patternCount);
        Element fstElement = (Element) fstNode;

        // <PatternReference>
        // should be pairwise 
        NodeList patterReferenceList = fstElement.getElementsByTagName(Constants.patternReference);
        Node patterReferenceNode = patterReferenceList.item(0);
        Node patterReferenceNode2 = patterReferenceList.item(1);

        patterReferenceNode.setTextContent(enterList.get(0));
        patterReferenceNode2.setTextContent(exitList.get(0));

        // <Message> set messages
        NodeList messageList = fstElement.getElementsByTagName(Constants.message);
        if (messageList.getLength() != 0) {
            Node messageNode = messageList.item(0);
            messageNode.setTextContent(messages.get(1));
        }

        NodeList selectFunctionList = fstElement.getElementsByTagName(Constants.selectFunction);
        Node selectFunctionNode = selectFunctionList.item(0);
        selectFunctionNode.getAttributes().setNamedItem(finalDoc.createAttribute(Constants.outputName));
        selectFunctionNode.getAttributes().getNamedItem(Constants.outputName).setTextContent(outputNames.get(1));
    }
    
    /**
     * This method clears all values and ArrayLists 
     */
    private static void clear(){
        patternCount = 0;
        size = 0;
        treeCount = 0;
        patternIDCount = 0;

        enterList.clear();
        exitList.clear();
        ruleList.clear();

        patterIDs.clear();
        messages.clear();
        outputNames.clear();
    }
}