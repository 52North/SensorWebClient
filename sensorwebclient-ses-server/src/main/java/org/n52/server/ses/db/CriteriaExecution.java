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