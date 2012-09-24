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
package org.n52.shared.serializable.pojos;

import java.io.Serializable;

import com.smartgwt.client.widgets.form.fields.SelectItem;

/**
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 * 
 */
public class Tree implements Serializable {

    private static final long serialVersionUID = -5600032722797892946L;

    /** The value */
    public SelectItem value;

    /** left tree */
    public Tree left;

    /** right tree */
    public Tree right;

    /**
     * 
     */
    public Tree() {
        // empty constructor
    }

    /**
     * @param value
     * @param left
     * @param right
     */
    public Tree(SelectItem value, Tree left, Tree right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    /**
     * @return {@link Boolean}
     */
    public boolean hasLeftChild() {
        return !(this.left == null);
    }

    /**
     * @return {@link Boolean}
     */
    public boolean hasRightChild() {
        return !(this.right == null);
    }

    /**
     * clear tree
     */
    public void clear() {
        this.value = null;
        this.left = null;
        this.right = null;
    }

    /**
     * @param tree
     */
    public void printPostorder(Tree tree) {
        if (tree == null) {
            return;
        }

        // first recur on both subtrees
        printPostorder(tree.left);
        printPostorder(tree.right);

        // then deal with the node
        System.out.println(tree.value);
    }
}