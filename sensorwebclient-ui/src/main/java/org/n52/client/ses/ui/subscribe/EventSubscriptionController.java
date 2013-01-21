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

package org.n52.client.ses.ui.subscribe;

import org.n52.client.sos.legend.TimeSeries;

class EventSubscriptionController {

    private EventSubscriptionWindow eventSubscriptionWindow;

    private TimeSeries timeseries;

    private String abonnementName;

    private RuleTemplate selectedRuleTemplate;

    void setEventSubscription(EventSubscriptionWindow eventSubsciptionWindow) {
        this.eventSubscriptionWindow = eventSubsciptionWindow;
    }

    public void setTimeseries(TimeSeries timeseries) {
        this.timeseries = timeseries;
    }

    public TimeSeries getTimeSeries() {
        return timeseries;
    }

    public void setAbonnementName(String currentAbonnementName) {
        this.abonnementName = currentAbonnementName;
    }
    
    public String getAbonnementName() {
        return abonnementName;
    }

    /**
     * @return an abonnement name suggestion for current timeseries.
     */
    public String createSuggestedAbonnementName() {
        if (timeseries == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(timeseries.getProcedureId());
        // TODO adjust suggested name
        return sb.toString();
    }

    public void setSelectedRuleTemplate(RuleTemplate template) {
        selectedRuleTemplate = template;
        eventSubscriptionWindow.updateRuleEditCanvas(template);
        
        // TODO create abo subscription editor and set it in window
        
    }
    
}
