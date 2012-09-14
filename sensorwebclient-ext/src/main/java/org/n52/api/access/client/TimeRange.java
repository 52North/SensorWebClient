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

package org.n52.api.access.client;

import java.util.Date;

public class TimeRange {

	private long start;
	private long end;
	
	private TimeRange(long start, long end) {
		this.start = start;
		this.end = end;
	}
	
	public static TimeRange createTimeRangeByMillis(long start, long end) {
		return new TimeRange(start, end);
	}
	
	public static TimeRange createTimeRangeByDate(Date start, Date end) {
		return new TimeRange(start.getTime(), end.getTime());
	}

    public long getStart() {
        return this.start;
    }

    public long getEnd() {
        return this.end;
    }

	static TimeRange union(TimeRange first, TimeRange second) {
		long minStart = Math.min(first.getStart(), second.getStart());
		long maxEnd = Math.max(first.getEnd(), second.getEnd());
		return TimeRange.createTimeRangeByMillis(minStart, maxEnd);
	}
}
