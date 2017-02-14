/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.server.da.oxf;

public class TimePosition_OXFExtension implements ITimePosition_OXFExtension {

    public static final String GET_OBSERVATION_TIME_PARAM_FIRST = "getFirst";

    public static final String GET_OBSERVATION_TIME_PARAM_LAST = "latest";

    private final String timeParam;

    public TimePosition_OXFExtension(String timeParam) {
        this.timeParam = timeParam;
    }

    public String toISO8601Format() {
        return this.timeParam;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (timeParam == null) ? 0 : timeParam.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TimePosition_OXFExtension other = (TimePosition_OXFExtension) obj;
        if (timeParam == null) {
            if (other.timeParam != null)
                return false;
        }
        else if ( !timeParam.equals(other.timeParam))
            return false;
        return true;
    }

    

}
