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
package org.n52.server.ses.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;

/**
 * The Class EnumType.
 * 
 * @author <a href="mailto:j.schulte@52north.de">Jan Schulte</a>
 */
public class EnumType implements EnhancedUserType, ParameterizedType {

    /** The enum class. */
    @SuppressWarnings("rawtypes")
    private Class<Enum> enumClass;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hibernate.usertype.EnhancedUserType#fromXMLString(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Object fromXMLString(String xmlValue) {
        return Enum.valueOf(this.enumClass, xmlValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hibernate.usertype.EnhancedUserType#objectToSQLString(java.lang.Object
     * )
     */
    @SuppressWarnings("rawtypes")
    public String objectToSQLString(Object value) {
        return '\'' + ((Enum) value).name() + '\'';
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hibernate.usertype.EnhancedUserType#toXMLString(java.lang.Object)
     */
    @SuppressWarnings("rawtypes")
    public String toXMLString(Object value) {
        return ((Enum) value).name();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable,
     * java.lang.Object)
     */
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
     */
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
     */
    @SuppressWarnings("rawtypes")
    public Serializable disassemble(Object value) throws HibernateException {
        return (Enum) value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#equals(java.lang.Object,
     * java.lang.Object)
     */
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
     */
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    public boolean isMutable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet,
     * java.lang.String[], java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        String name = rs.getString(names[0]);
        return rs.wasNull() ? null : Enum.valueOf(this.enumClass, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement,
     * java.lang.Object, int)
     */
    @SuppressWarnings("rawtypes")
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, ((Enum) value).name());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#replace(java.lang.Object,
     * java.lang.Object, java.lang.Object)
     */
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    @SuppressWarnings("rawtypes")
    public Class returnedClass() {
        return this.enumClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hibernate.usertype.ParameterizedType#setParameterValues(java.util
     * .Properties)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClassName");
        try {
            this.enumClass = (Class<Enum>) Class.forName(enumClassName);
        } catch (ClassNotFoundException cnfe) {
            throw new HibernateException("Enum class not found", cnfe);
        }
    }
}
