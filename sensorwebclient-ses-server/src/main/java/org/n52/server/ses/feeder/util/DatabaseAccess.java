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
package org.n52.server.ses.feeder.util;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.n52.server.ses.db.CriteriaExecution;
import org.n52.server.ses.db.HibernateDaoUtil;
import org.n52.shared.serializable.pojos.TimeseriesFeed;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;

public class DatabaseAccess extends HibernateDaoUtil {

    // TODO use HibernateDaoUtil's execution encapsulation 
    
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

	public static void increaseSubscriptionCountFor(TimeseriesFeed timeseriesFeed) {
		updateSubscriptionCount(timeseriesFeed, 1);
	}

	public static void decreaseSubscriptionCountFor(TimeseriesFeed timeseriesFeed) {
		updateSubscriptionCount(timeseriesFeed, -1);
	}
	
	private static void updateSubscriptionCount(final TimeseriesFeed timeseriesFeed, final int changeBy) {
	    execute(new CriteriaExecution<Void>() {
            @Override
            public Void execute(Session session) {
                timeseriesFeed.setUsedCounter(timeseriesFeed.getUsedCounter() + changeBy);
                session.saveOrUpdate(timeseriesFeed);
                return null;
            }
	    });
	}

	@SuppressWarnings("unchecked")
	public static synchronized List<TimeseriesFeed> getSubscribedTimeseriesFeeds() {
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
