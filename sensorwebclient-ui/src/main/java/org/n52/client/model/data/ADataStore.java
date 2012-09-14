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
package org.n52.client.model.data;

import java.util.HashMap;

/**
 * TODO define interface and let implementing class hold the datastructure
 * 
 * @param <T>
 *            the data type to store
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public abstract class ADataStore<T> {

    protected HashMap<String, T> dataItems;

    protected ADataStore() {
        this.dataItems = new HashMap<String, T>();
    }

    public final void storeDataItem(String ID, T data) {
        this.dataItems.put(ID, data);
    }

    public final T getDataItem(String id) {
        return this.dataItems.get(id);
    }

    public final void deleteDataItem(String ID) {
        this.dataItems.remove(ID);
    }

    public final HashMap<String, T> getDataItems() {
        return this.dataItems;
    }
    
    public final T[] getDataAsArray(T[] array) {
        T[] dataArray = this.dataItems.values().toArray(array);
        return dataItems.values().toArray(dataArray);
    }
}