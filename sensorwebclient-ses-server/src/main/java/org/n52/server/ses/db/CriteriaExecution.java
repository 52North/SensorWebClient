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
package org.n52.server.ses.db;

import org.hibernate.Session;

/**
 * Encapsulates an database execution on a given {@link Session}. Use it as leightweight execution
 * environment in combination with {@link #execute(Session)}, e.g. in a anonymous type implementation.<br>
 * <br>
 * A simple example for a no returning execution might be:
 * 
 * <pre>
 * public static Void updateTimeseriesFeed(final String timeseriesId, final boolean active) {
 *      return execute(new CriteriaExecution<Void>() {
 *          public Void execute(Session session) {
 *              Criteria criteria = session.createCriteria(TimeseriesFeed.class);
 *              criteria.add(Restrictions.eq(TIMESERIES_ID, timeseriesId));
 *              TimeseriesFeed uniqueResult = (TimeseriesFeed) criteria.uniqueResult();
 *              session.saveOrUpdate(uniqueResult);
 *              return null;
 *          }
 *      });
 *   }}
 * </pre>
 * 
 * @param <T>
 *        the expected result of the execution (can be a {@link Void} to indicate that nothing is
 *        expected)
 */
public interface CriteriaExecution<T> {
    public T execute(final Session session);
}