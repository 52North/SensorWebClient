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
package org.n52.server.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Filter} to add cache control headers for GWT generated files to ensure
 * that the correct files get cached.
 * 
 * @see http 
 *      ://seewah.blogspot.com/2009/02/gwt-tips-2-nocachejs-getting-cached-in
 *      .html
 * 
 * @author See Wah Cheng
 * @created 24 Feb 2009
 */
public class GWTCacheControlFilterIE6 implements Filter {


    private static final Logger LOGGER = LoggerFactory.getLogger(GWTCacheControlFilterIE6.class);
    
    public void destroy() {
        // do nothing
    }

    public void init(FilterConfig config) throws ServletException {
        // do nothing
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        
        LOGGER.info("IE6");
        
        // TODO just for IE7
        Date now = new Date();
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setDateHeader("Date", now.getTime());
        httpResponse.setDateHeader("Expires", 0);
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setHeader("Cache-control", "no-cache, no-store, must-revalidate");

        filterChain.doFilter(request, response);
    }

}
