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