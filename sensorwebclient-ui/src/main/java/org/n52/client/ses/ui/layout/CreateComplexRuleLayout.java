/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.client.ses.ui.layout;

import static org.n52.client.ses.i18n.I18NStringsAccessor.i18n;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.n52.client.eventBus.EventBus;
import org.n52.client.model.communication.requestManager.SesRequestManager;
import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.CreateComplexRuleEvent;
import org.n52.client.ses.event.GetAllPublishedRulesEvent;
import org.n52.client.ses.ui.Layout;
import org.n52.shared.Constants;
import org.n52.shared.LogicalOperator;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.ComplexRuleData;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.Tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * The Class CreateComplexRuleLayout.
 * 
 * This layout alows user to combine rules to complex rules. This implementation
 * is limited to maximum 6 rules on one time. It is possible to use complex rules
 * as one of the six possible rules. So it is theoreticaly possible combine
 * infinit count of rules.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class CreateComplexRuleLayout extends Layout {

    private VLayout ruleBlockLayout;
    private ArrayList<String> treeContent = new ArrayList<String>();
    
    private ButtonItem createButtonItem;
    private ButtonItem cancelButton;
    
    private IButton blockButtonItem;
    private IButton singleButtonItem;
    private IButton resetButtonItem;
   
    private TextItem nameItem;
    private TextAreaItem descriptionItem;
    private RadioGroupItem publishRadioGroup;
    private RadioGroupItem filterRadioGroup;

    private int blockCount = 0;
    private int singleCount = 0;

    private Tree rootTree;
    private Tree tempTree;
    private boolean first = true;
    private boolean editCR = false;
    private String oldRuleName = "";
    
    private SelectItem operator;
    private SelectItem singleOperator;
    
    private String oldSelectedFilter = "";
    
    private int selectItemWidth = 200;
    private int operatorWidth = 90;
    
    private LinkedHashMap<String, String> rulesHashMap;
    
    /**
     * Instantiates a new creates the complex rule layout.
     */
    public CreateComplexRuleLayout() {
        super(i18n.createComplexRule());

        this.rulesHashMap = new LinkedHashMap<String, String>();

        DataSource dataSource = new DataSource();

        this.form.setDataSource(dataSource);

        // TitleItem
        this.nameItem = new TextItem();
        this.nameItem.setName("title");
        this.nameItem.setTitle(i18n.name());
        this.nameItem.setKeyPressFilter("[0-9a-zA-Z_]");
        this.nameItem.setHint(i18n.possibleChars() + " [0-9 a-z A-Z _]");
        //"</nobr>"
        this.nameItem.setShowHintInField(true);
        this.nameItem.setRequired(true);
        this.nameItem.setLength(70);
        this.nameItem.setWidth(250);
        
        this.form.setFields(this.headerItem, this.nameItem);

        // form2
        this.form2 = new DynamicForm();

        // DescriptionItem
        this.descriptionItem = new TextAreaItem();
        this.descriptionItem.setName("description");
        this.descriptionItem.setTitle(i18n.description());
        this.descriptionItem.setRequired(true);
        this.descriptionItem.setHeight(100);
        this.descriptionItem.setWidth(250);
        this.descriptionItem.setHint(i18n.possibleChars() + " [0-9 a-z A-Z _ -]");
        this.descriptionItem.setShowHintInField(true);
        this.descriptionItem.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                String key = event.getKeyName();
                String [] filter = {"["};
                for (int i = 0; i < filter.length; i++) {
                    if (key.equals(filter[i])) {
                        event.cancel();
                        break;
                    }
                }
            }
        });

        this.form2.setFields(this.descriptionItem);

        // ====================
        // radioButtons
        this.publishRadioGroup = new RadioGroupItem("publish", i18n.publish());
        this.publishRadioGroup.setValueMap(i18n.yes(), i18n.no());
        this.publishRadioGroup.setDefaultValue(i18n.yes());
        
        // filter to distinguish bestween own, other or all published rules
        this.filterRadioGroup = new RadioGroupItem("filter", i18n.rules());
        this.filterRadioGroup.setValueMap(i18n.filterOwn(), i18n.filterOther(), i18n.filterBoth());
        this.filterRadioGroup.setDefaultValue(i18n.filterOwn());
        this.filterRadioGroup.setVertical(false);
        this.filterRadioGroup.setTitleOrientation(TitleOrientation.LEFT);
        this.filterRadioGroup.addChangedHandler(new ChangedHandler() {
            public void onChanged(final ChangedEvent event) {
                SC.ask(i18n.filterQuestion(), new BooleanCallback() {
                    public void execute(Boolean value) {
                        if (value) {
                            if (event.getValue().equals(i18n.filterOwn())) {
                                oldSelectedFilter = i18n.filterOwn();
                                EventBus.getMainEventBus().fireEvent(new GetAllPublishedRulesEvent(1));
                                clearBlock();
                            } else if (event.getValue().equals(i18n.filterOther())) {
                                oldSelectedFilter = i18n.filterOther();
                                EventBus.getMainEventBus().fireEvent(new GetAllPublishedRulesEvent(2));
                                clearBlock();
                            } else if (event.getValue().equals(i18n.filterBoth())) {
                                oldSelectedFilter = i18n.filterBoth();
                                EventBus.getMainEventBus().fireEvent(new GetAllPublishedRulesEvent(3));
                                clearBlock();
                            }
                        } else {
                            filterRadioGroup.setValue(oldSelectedFilter);
                        }
                    }
                });
            }
        });
        this.oldSelectedFilter = i18n.filterOwn();
        
        DynamicForm filterRadioForm = new DynamicForm();
        filterRadioForm.setWidth("30%");
        filterRadioForm.setItems(new SpacerItem(), this.filterRadioGroup);
        
        // form for radioButtons
        DynamicForm publishRadioForm = new DynamicForm();
        publishRadioForm.setItems(this.publishRadioGroup);
        
        // form3
        DynamicForm form3 = new DynamicForm();
        form3.setUseAllDataSourceFields(true);

        this.createButtonItem = new ButtonItem();
        this.createButtonItem.setTitle(i18n.create());
        this.createButtonItem.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (CreateComplexRuleLayout.this.form.validate(false) 
                        && CreateComplexRuleLayout.this.form2.validate(false)) {
                    createCR();
                }
            }
        });
        
        this.cancelButton = new ButtonItem();
        this.cancelButton.setTitle(i18n.cancel());
        this.cancelButton.setVisible(false);
        this.cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String role = Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE);
                if (role.equals("ADMIN")) {
                    EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.RULELIST));
                } else {
                    EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.EDIT_RULES));
                }
            }
        });
        
        form3.setFields(this.createButtonItem, this.cancelButton);

        // buttons for the rule block
        this.blockButtonItem = new IButton(i18n.addBlock());
        this.blockButtonItem.setWidth("130px");
        this.blockButtonItem.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
            
            public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
                if ((blockCount <= 2) && (singleCount == 0)) {
                    addBlock(null, null, null, null);
                }
                if (singleCount > 0) {
                    singleButtonItem.setDisabled(false);
                }
                if (blockCount == 3) {
                    singleButtonItem.setDisabled(true);
                    blockButtonItem.setDisabled(true);
                }
            }
        });
        this.singleButtonItem = new IButton(i18n.addSingleRule());
        this.singleButtonItem.setWidth("130px");
        this.singleButtonItem.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
            public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
                if ((singleCount == 0) && (blockCount <= 2)) {
                    blockButtonItem.setDisabled(true);
                    singleButtonItem.setDisabled(true);
                    addSingle(null, null);
                }
            }
        });

        this.resetButtonItem = new IButton(i18n.reset());
        this.resetButtonItem.setWidth("130px");
        this.resetButtonItem.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
            public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
                resetBlock();
            }
        });
        
        // init MAIN layout
        this.ruleBlockLayout = new VLayout();
        this.ruleBlockLayout.setWidth(200);
        this.ruleBlockLayout.setHeight(80);
        this.ruleBlockLayout.addMember(createBlock(null, null, null));
        
        // Layout spacer
        LayoutSpacer spacer = new LayoutSpacer();
        spacer.setWidth(100);
        
        LayoutSpacer spacer2 = new LayoutSpacer();
        spacer2.setWidth(100);
        
        HLayout h = new HLayout();
        h.setHeight(80);
        h.addMember(spacer);
        h.addMember(this.ruleBlockLayout);
        
        // Buttons Layout
        HLayout buttonsLayout = new HLayout();
        buttonsLayout.setHeight(22);
        buttonsLayout.addMember(spacer2);
        buttonsLayout.addMember(this.blockButtonItem);
        buttonsLayout.addMember(this.singleButtonItem);
        buttonsLayout.addMember(this.resetButtonItem);

        // add elements to main layout
        addMember(this.spacer);
        addMember(this.form);
        addMember(this.spacer);
        addMember(filterRadioForm);
        addMember(this.spacer);
        addMember(h);
        addMember(this.spacer);
        addMember(buttonsLayout);
        addMember(this.spacer);
        addMember(this.form2);
        addMember(this.spacer);
        addMember(publishRadioForm);
        addMember(this.spacer);
        addMember(form3);
    }

    // add one rule field and a operator to the complex rule
    private HLayout createSingle(String ruleName){
        this.singleCount++;
        
        DynamicForm block1 = new DynamicForm();
        block1.setWidth("50%");
        block1.setNumCols(1);
        
        DynamicForm block2 = new DynamicForm();
        block2.setWidth("50%");
        block2.setNumCols(1);
        
        SelectItem rule1Item = new SelectItem();
        rule1Item.setShowTitle(false);
        rule1Item.setValueMap(this.rulesHashMap);
        rule1Item.setWidth(this.selectItemWidth);
        if (ruleName != null) {
            rule1Item.setValue(ruleName);
        }
        
        SpacerItem spacerItem = new SpacerItem();
        spacerItem.setShowTitle(false);
        spacerItem.setHeight(20);
        
        block2.setFields(rule1Item);

        HLayout layout = new HLayout();
        layout.setWidth(200);
        layout.setHeight(25);
        layout.addMember(block1);
        layout.addMember(block2);

        // save tree
        this.tempTree = this.rootTree;
        Tree right = new Tree(rule1Item, null, null);

        this.rootTree = new Tree(this.singleOperator, this.tempTree, right);

        return layout;
    }

    // add a block to the complex rule. A block consist of two rules with an operator
    private synchronized HLayout createBlock(String op, String ruleName1, String ruleName2){
        this.blockCount++;
        
        LinkedHashMap<String, String> operatorHashMap = getLogicalOperators();
        
        DynamicForm block1 = new DynamicForm();
        block1.setWidth("30%");
        block1.setNumCols(1);
        
        DynamicForm block2 = new DynamicForm();
        block2.setWidth("70%");
        block2.setNumCols(1);
        
        SelectItem operator1 = new SelectItem();
        operator1.setShowTitle(false);
        operator1.setWidth(this.operatorWidth);
        operator1.setValueMap(operatorHashMap);
        operator1.setTextAlign(Alignment.CENTER);
        if (op != null) {
            operator1.setValue(op);
        } else {
            operator1.setValue(LogicalOperator.AND.toString().toString());
        }
        
        SelectItem rule1Item = new SelectItem();
        rule1Item.setShowTitle(false);
        rule1Item.setValueMap(this.rulesHashMap);
//        rule1Item.setHint("--Please select a rule--");
//        rule1Item.setShowHintInField(true);
        rule1Item.setWidth(this.selectItemWidth);
        if (ruleName1 != null) {
            rule1Item.setValue(ruleName1);
        }
        SelectItem rule2Item = new SelectItem();
        rule2Item.setShowTitle(false);
        rule2Item.setValueMap(this.rulesHashMap);
//        rule2Item.setHint("Test");
//        rule2Item.setShowHintInField(true);
        rule2Item.setWidth(this.selectItemWidth);
        if (ruleName2 != null) {
            rule2Item.setValue(ruleName2);
        }
        
        
        SpacerItem spacerItem = new SpacerItem();
        spacerItem.setShowTitle(false);
        spacerItem.setHeight(20);
        
        block1.setAlign(Alignment.CENTER);
        
        block1.setFields(spacerItem, operator1, spacerItem);
        block2.setFields(rule1Item, spacerItem, rule2Item);
        
        // generate tree
        if (this.first) {
            Tree left = new Tree(rule1Item, null, null);
            Tree right = new Tree(rule2Item, null, null);
            Tree newTree = new Tree(operator1, left, right);

            this.rootTree = newTree;
            this.first = false;
        } else {
            this.tempTree = this.rootTree;

            Tree left = new Tree(rule1Item, null, null);
            Tree right = new Tree(rule2Item, null, null);
            Tree newTree = new Tree(operator1, left, right);

            this.rootTree = new Tree(this.operator, this.tempTree, newTree);
        }

        // BlockLayout
        HLayout block = new HLayout();
        block.setHeight(80);
        block.setEdgeMarginSize(1);
        block.setEdgeSize(2);
        block.setShowEdges(true);
        block.setWidth(200);
        block.setMargin(1);
        block.addMember(block1);
        block.addMember(block2);

        return block;
    }

    private LinkedHashMap<String, String> getLogicalOperators() {
        LinkedHashMap<String, String> operatorHashMap = new LinkedHashMap<String, String>();
        
        operatorHashMap.put(LogicalOperator.AND.toString(), LogicalOperator.AND.toString());
        operatorHashMap.put(LogicalOperator.OR.toString(), LogicalOperator.OR.toString());
        operatorHashMap.put(LogicalOperator.AND_NOT.toString(), LogicalOperator.AND_NOT.toString());
        return operatorHashMap;
    }

    private void addBlock(String op1, String op2, String ruleName1, String ruleName2){
        // get all members from mainLayout and clear the layout
        Canvas[] members = this.ruleBlockLayout.getMembers();
        this.ruleBlockLayout.removeMembers(this.ruleBlockLayout.getMembers());
        
        LinkedHashMap<String, String> operatorHashMap = getLogicalOperators();

        VLayout v2 = new VLayout();
        v2.setWidth("70%");
        v2.addMember(members[0]);
        if (op1 != null) {
            v2.addMember(createBlock(op1, ruleName1, ruleName2));
        } else {
            v2.addMember(createBlock(null, null, null));
        }
        
        DynamicForm block1 = new DynamicForm();
        block1.setWidth("30%");
        block1.setNumCols(1);
        
        DynamicForm block2 = new DynamicForm();
        block2.setWidth("70%");
        block2.setNumCols(1);
        
        this.operator = new SelectItem();
        this.operator.setShowTitle(false);
        this.operator.setWidth(this.operatorWidth);
        this.operator.setValueMap(operatorHashMap);
        this.operator.setTextAlign(Alignment.CENTER);
        if (op2 != null) {
            this.operator.setValue(op2);
        } else {
            this.operator.setValue(LogicalOperator.AND.toString());
        }
        
        
        SpacerItem spacerItem = new SpacerItem();
        spacerItem.setShowTitle(false);
        spacerItem.setHeight(20);
        
        FormItem[] form = null;
        if (this.blockCount == 2) {
            int count = 3;
            form = new FormItem[count+1];
            for (int i = 0; i < count; i++) {
                form[i] = spacerItem;
            }
            form[count] = this.operator;
        }
        
        if (this.blockCount == 3) {
            int count = 5;
            form = new FormItem[count+1];
            for (int i = 0; i < count; i++) {
                form[i] = spacerItem;
            }
            form[count] = this.operator;
        }
        
        block1.setFields(form);


        HLayout layout = new HLayout();
        layout.setWidth100();
        layout.setHeight(25);
        layout.setEdgeMarginSize(1);
        layout.setEdgeSize(2);
        layout.setShowEdges(true);
        layout.addMember(block1);
        layout.addMember(v2);

        this.ruleBlockLayout.addMember(layout);
    }

    private void addSingle(String op, String ruleName){
        // get all members from mainLayout and clear the layout
        Canvas[] members = this.ruleBlockLayout.getMembers();
        this.ruleBlockLayout.removeMembers(this.ruleBlockLayout.getMembers());
        
        LinkedHashMap<String, String> operatorHashMap = getLogicalOperators();

        VLayout v2 = new VLayout();
        v2.setWidth("70%");
        v2.addMember(members[0]);
        v2.addMember(createSingle(ruleName));
        
        DynamicForm block1 = new DynamicForm();
        block1.setWidth("30%");
        block1.setNumCols(1);
        
        this.singleOperator = new SelectItem();
        this.singleOperator.setShowTitle(false);
        this.singleOperator.setWidth(this.operatorWidth);
        this.singleOperator.setValueMap(operatorHashMap);
        this.singleOperator.setTextAlign(Alignment.CENTER);
        if (op != null) {
            this.singleOperator.setValue(op);
        } else {
            this.singleOperator.setValue(LogicalOperator.AND.toString());
        }
        
        SpacerItem spacerItem = new SpacerItem();
        spacerItem.setShowTitle(false);
        spacerItem.setHeight(20);
        
        block1.setFields(spacerItem, spacerItem, this.singleOperator);

        HLayout layout = new HLayout();
        layout.setWidth(200);
        layout.setHeight(25);
        layout.setEdgeMarginSize(1);
        layout.setEdgeSize(2);
        layout.setShowEdges(true);
        layout.addMember(block1);
        layout.addMember(v2);

        this.ruleBlockLayout.addMember(layout);
    }
    
    private void clearBlock(){
        this.blockCount = 0;
        this.singleCount = 0;
        if (this.rootTree != null) {
            this.rootTree.clear();
        }
        this.rootTree = null;

        if (this.tempTree != null) {
            this.tempTree.clear();
        }

        this.tempTree = null;
        this.first = true;
        
        // clear rules
        this.ruleBlockLayout.removeMembers(this.ruleBlockLayout.getMembers());
        this.ruleBlockLayout.addMember(createBlock(null, null, null));
    }

    /**
     * Clear fields
     */
    public void clearFields() {
        this.editCR = false;
        this.cancelButton.setVisible(false);
        this.oldRuleName = "";
        clearBlock();
        
        // change button text
        this.createButtonItem.setTitle(i18n.create());

        // clear title
        this.nameItem.clearValue();

        // clear description
        this.descriptionItem.clearValue();

        // set radio buttons to "yes"
        this.publishRadioGroup.setValue(i18n.yes());
        this.filterRadioGroup.setValue(i18n.filterOwn());
        
//        resetBlock();
    }
    
    /**
     * reset the block to initial configuration --> two rules which are
     * logicaly combined.
     */
    private void resetBlock(){
        this.ruleBlockLayout.removeMembers(this.ruleBlockLayout.getMembers());
        this.blockCount = 0;
        this.singleCount = 0;
        if (this.rootTree != null) {
            this.rootTree.clear();
            this.rootTree = null;
        }
        if (this.tempTree != null) {
            this.tempTree.clear();
            this.tempTree = null;
        }

        this.first = true;
        this.ruleBlockLayout.addMember(createBlock(null, null, null));
        this.blockButtonItem.setDisabled(false);
        this.singleButtonItem.setDisabled(false);
    }

    /**
     * 
     * @param rules
     */
    public synchronized void setRules(ArrayList<String> rules) {
        // clear hash map
        this.rulesHashMap.clear();

        // fill hash map
        for (int i = 0; i < rules.size(); i++) {
            this.rulesHashMap.put(rules.get(i), rules.get(i));
        }
        if (!this.editCR || !this.oldSelectedFilter.equals("")) {
            resetBlock();
        }
    }

    /**
     * create complex rule from 
     */
    private void createCR() {
        String title = (String) this.nameItem.getValue();
        String description = (String) this.descriptionItem.getValue();

        boolean publish = false;
        if (this.publishRadioGroup.getValue().toString().equals(i18n.yes())) {
            publish = true;
        }

        // not final, fast and dirty 
        if (this.blockCount == 1 && this.singleCount == 0) {
            SelectItem operatorBox = this.rootTree.value;
            SelectItem leftBox = this.rootTree.left.value;
            SelectItem rightBox = this.rootTree.right.value;

            if ((operatorBox != null && leftBox != null && rightBox != null) &&
                    (!operatorBox.equals("") && !leftBox.equals("") && !rightBox.equals(""))) {

                String operator = operatorBox.getValueAsString();
                String leftRuleName = leftBox.getValueAsString();
                String rightRuleName = rightBox.getValueAsString();

                ArrayList<String> finalList = new ArrayList<String>();
                finalList.add(operator);
                finalList.add(leftRuleName);
                finalList.add(rightRuleName);

                boolean valid = true;
                for (int i = 0; i < finalList.size(); i++) {
                    if (finalList.get(i).equals("") || finalList.get(i) == null) {
                        valid = false;
                    }
                }
                // check ruleName
                String ruleName = this.nameItem.getValueAsString();
                if (Character.isDigit(ruleName.charAt(0))) {
                    // if rulename starts with number, inform user
                    SC.say(i18n.ruleNameStartsWithDigit());
                    valid = false;
                }
                
                if (leftRuleName.contains(i18n.ruleNotFound())
                        || rightRuleName.contains(i18n.ruleNotFound())) {
                    SC.say(i18n.invalidInputs());
                    valid = false;
                }
                
                if (valid) {
                    int userID = Integer.parseInt(Cookies.getCookie(SesRequestManager.COOKIE_USER_ID));
                    ComplexRuleData data = new ComplexRuleData(finalList, title, description, publish, userID, null, null, null);

                    EventBus.getMainEventBus().fireEvent(new CreateComplexRuleEvent(data, this.editCR, this.oldRuleName));
                } else {
                    SC.say(i18n.invalidInputs());
                }
            }
        } else {
            // check user inputs
            
            this.treeContent.clear();
            printPostorder(this.rootTree);
            
            boolean valid = true;
            for (int i = 0; i < this.treeContent.size(); i++) {
                if (this.treeContent.get(i).equals("") 
                        || this.treeContent.get(i) == null 
                        || this.treeContent.get(i).contains(i18n.ruleNotFound())) {
                    valid = false;
                }
            }

            if (valid) {
                
                int userID = Integer.parseInt(Cookies.getCookie(SesRequestManager.COOKIE_USER_ID));

                ComplexRuleData data = new ComplexRuleData(null, title, description, publish, userID, this.treeContent, null, null);

                EventBus.getMainEventBus().fireEvent(new CreateComplexRuleEvent(data, this.editCR, this.oldRuleName));
            } else {
                SC.say(i18n.invalidInputs());
            }
        }
    }

    private void printPostorder(Tree tree) { 
        if (tree == null) return;

        try {
            // first recur on both subtrees
            if (tree.left != null) {
                printPostorder(tree.left);
            }
            if (tree.right != null) {
                printPostorder(tree.right);
            }

            // then deal with the node
            if (tree.value.getValue() != null) {
                this.treeContent.add(tree.value.getValueAsString());
            } else {
                this.treeContent.add("");
            }
        } catch (Exception e) {
            GWT.log("Error printing postorder", e);
        }
    }

    /**
     * 
     * @param response 
     */
    public synchronized void editCR(SesClientResponse response){
        this.editCR = true;
        this.oldSelectedFilter = "";
        this.cancelButton.setVisible(true);
        
        ArrayList<String> treeList = response.getList();
        Rule rule = response.getRule();
        
        this.oldRuleName = rule.getTitle();

        // set header
        this.headerItem.setValue(i18n.editComplexRule());
        
        // rename button
        this.createButtonItem.setTitle(i18n.saveChanges());

        // set title
        this.nameItem.setValue(rule.getTitle());

        // set description
        this.descriptionItem.setValue(rule.getDescription());

        // set published
        if (rule.isPublish()) {
            this.publishRadioGroup.setValue(i18n.yes());
        } else {
            this.publishRadioGroup.setValue(i18n.no());
        }

        ArrayList<String> ruleNames = new ArrayList<String>();
        ArrayList<String> operators = new ArrayList<String>();
        boolean firstLoop = true;

        //remove rule from hashmap
        this.rulesHashMap.remove(this.oldRuleName);
        
        // reset block
        resetBlock();
        
        for (int i = 0; i < treeList.size(); i++) {
            String content = treeList.get(i);

            if ((!content.equals(LogicalOperator.AND.toString()) && !content.equals(LogicalOperator.OR.toString()) && !content.equals(LogicalOperator.AND_NOT.toString()))) {
                // replace the placeholder with user warning
                if (content.contains(Constants.SES_OP_SEPARATOR)) {
                    content = content.replace(Constants.SES_OP_SEPARATOR, i18n.ruleNotFound()+": ");
                }
                // add rule names
                ruleNames.add(content);
            } else {
                operators.add(content);
            }

            // two ruless (block)
            if (ruleNames.size() == 2 && !operators.isEmpty()) {
                if (firstLoop) {
                    this.blockCount = 0;
                    this.singleCount = 0;
                    this.first = true;
                    this.ruleBlockLayout.removeMembers(this.ruleBlockLayout.getMembers());
                    this.ruleBlockLayout.addMember(createBlock(operators.get(0), ruleNames.get(0), ruleNames.get(1)));
                    ruleNames.clear();
                    operators.clear();
                    firstLoop = false;
                } else if (operators.size() == 2){
                    // add block
                    addBlock(operators.get(0), operators.get(1), ruleNames.get(0), ruleNames.get(1));
                    ruleNames.clear();
                    operators.clear();
                }
                //one rule (single)
            } else if (ruleNames.size() == 1 && !operators.isEmpty()) {
                addSingle(operators.get(0), ruleNames.get(0));
                ruleNames.clear();
                operators.clear();
            }
        }
        if (this.singleCount == 1 || this.blockCount == 3) {
            singleButtonItem.setDisabled(true);
            blockButtonItem.setDisabled(true);
        }
    }

    /**
     * @param editCR
     */
    public void setEditCR(boolean editCR) {
        this.editCR = editCR;
    }
}