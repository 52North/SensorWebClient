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

	public static synchronized void increaseSensorUse(TimeseriesMetadata timeseriesMetadata) {
		changeCounter(timeseriesMetadata, 1);
	}

	public static synchronized void decreaseSensorUse(TimeseriesMetadata timeseriesMetadata) {
		changeCounter(timeseriesMetadata, -1);
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

	private static void changeCounter(TimeseriesMetadata timeseriesMetadata, int changeBy) {
		Session session = getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();
		List<TimeseriesFeed> timeseriesFeed = findTimeseriesFeedsWith(timeseriesMetadata, session);
		for (TimeseriesFeed sensor : timeseriesFeed) {
			sensor.setUsedCounter(sensor.getUsedCounter() + changeBy);
			session.saveOrUpdate(sensor);
		}
		transaction.commit();
	}

	@SuppressWarnings("unchecked")
	private static List<TimeseriesFeed> findTimeseriesFeedsWith(TimeseriesMetadata timeseriesMetadata, Session session) {
		Criteria criteria = session.createCriteria(TimeseriesFeed.class);
		List<TimeseriesFeed> timeseriesFeeds = criteria
                .add(Restrictions.eq("serviceUrl", timeseriesMetadata.getServiceUrl()))
                .add(Restrictions.eq("phenomenon", timeseriesMetadata.getPhenomenon()))
				.add(Restrictions.eq("procedure", timeseriesMetadata.getProcedure()))
				.add(Restrictions.eq("offering", timeseriesMetadata.getOffering()))
				.add(Restrictions.eq("featureOfInterest", timeseriesMetadata.getFeatureOfInterest()))
				.list();
		return timeseriesFeeds;
	}
}
