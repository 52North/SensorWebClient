package org.n52.io.generalize;

import org.n52.io.v1.data.TimeseriesDataCollection;

public interface Generalizer {

    public TimeseriesDataCollection generalize() throws GeneralizerException;
}
