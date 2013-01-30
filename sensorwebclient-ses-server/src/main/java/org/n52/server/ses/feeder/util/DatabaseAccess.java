package org.n52.server.ses.feeder.util;

import static org.n52.server.ses.hibernate.HibernateUtil.getSessionFactory;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;
import org.n52.shared.serializable.pojos.TimeseriesFeed;

public class DatabaseAccess {

    // TODO close sessions after committing transaction 
    
	public static synchronized boolean isKnownTimeseriesFeed(TimeseriesMetadata timeseriesMetadata) {
		Session session = getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();
		boolean isKnownTimeseriesFeed = false;
		List<TimeseriesFeed> timeseriesFeeds = findTimeseriesFeedsWith(timeseriesMetadata, session);
		if (timeseriesFeeds.size() > 0) {
			isKnownTimeseriesFeed = true;
		}
        transaction.commit();
        return isKnownTimeseriesFeed;
	}

	public static synchronized void saveTimeseriesFeed(TimeseriesFeed timeseriesFeed) {
		Session session = getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(timeseriesFeed);
        transaction.commit();
	}

	public static synchronized void increaseUsageCount(TimeseriesFeed timeseriesFeed) {
		changeCounter(timeseriesFeed, 1);
	}

	public static synchronized void decreaseUsageCount(TimeseriesFeed timeseriesFeed) {
		changeCounter(timeseriesFeed, -1);
	}
	
	private static void changeCounter(TimeseriesFeed timeseriesFeed, int changeBy) {
	    Session session = getSessionFactory().getCurrentSession();
	    Transaction transaction = session.beginTransaction();
	    timeseriesFeed.setUsedCounter(timeseriesFeed.getUsedCounter() + changeBy);
	    session.saveOrUpdate(timeseriesFeed);
	    transaction.commit();
	}

	@SuppressWarnings("unchecked")
	public static synchronized List<TimeseriesFeed> getUsedTimeseriesFeeds() {
		Session session = getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();
		Criteria criteria = session.createCriteria(TimeseriesFeed.class);
		List<TimeseriesFeed> timeseriesFeeds = criteria.add(Restrictions.gt("usedCounter", 0L)).list();
		transaction.commit();
		return timeseriesFeeds;
	}

	@SuppressWarnings("unchecked")
	private static List<TimeseriesFeed> findTimeseriesFeedsWith(TimeseriesMetadata timeseriesMetadata, Session session) {
		Criteria criteria = session.createCriteria(TimeseriesFeed.class);
		List<TimeseriesFeed> timeseriesFeeds = criteria
                .add(Restrictions.eq("timeseriesId", timeseriesMetadata.getTimeseriesId()))
//                .add(Restrictions.eq("serviceUrl", timeseriesMetadata.getServiceUrl()))
//                .add(Restrictions.eq("phenomenon", timeseriesMetadata.getPhenomenon()))
//				.add(Restrictions.eq("procedure", timeseriesMetadata.getProcedure()))
//				.add(Restrictions.eq("offering", timeseriesMetadata.getOffering()))
//				.add(Restrictions.eq("featureOfInterest", timeseriesMetadata.getFeatureOfInterest()))
				.list();
		return timeseriesFeeds;
	}
}
