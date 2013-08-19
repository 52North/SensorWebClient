
package org.n52.io;

import java.io.IOException;
import java.io.OutputStream;

import org.n52.io.v1.data.TimeseriesDataCollection;

public interface IOHandler {

    /**
     * @param data
     *        the input data collection to create an output for.
     * @throws TimeseriesIOException
     *         if ouput generation fails.
     */
    public void generateOutput(TimeseriesDataCollection data) throws TimeseriesIOException;

    /**
     * Encodes and writes previously generated output to the given stream. After handling the stream gets
     * flushed and closed.
     * 
     * @param stream
     *        the stream to write on the generated ouput.
     * @throws IOException
     *         if writing output to stream fails.
     */
    public void encodeAndWriteTo(OutputStream stream) throws TimeseriesIOException;
}
