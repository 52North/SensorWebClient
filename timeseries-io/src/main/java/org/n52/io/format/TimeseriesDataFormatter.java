package org.n52.io.format;



public interface TimeseriesDataFormatter<T> {

    public T format(TvpDataCollection toFormat);
}
