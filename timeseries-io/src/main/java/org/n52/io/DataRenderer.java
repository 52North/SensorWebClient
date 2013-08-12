package org.n52.io;

import javax.servlet.ServletOutputStream;

import org.n52.io.v1.data.in.StyleOptions;
import org.n52.io.v1.data.out.TimeseriesMetadata;

public abstract class DataRenderer {

	public abstract void renderToOutputStream(StyleOptions style, TimeseriesMetadata metadata, ServletOutputStream outputStream);

}
