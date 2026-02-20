/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.gtkcyber.sqlglot.dialects.redshift;

import com.gtkcyber.sqlglot.parser.Parser;
import com.gtkcyber.sqlglot.parser.ParserConfig;

/**
 * Redshift parser.
 * Extends base parser with Redshift-specific syntax:
 * - DISTKEY and SORTKEY specifications
 * - ENCODE keyword for compression
 * - DISTSTYLE clause
 * - COPY and UNLOAD commands
 * - Redshift-specific window functions
 * - Spectrum external table syntax
 * - VACUUM and ANALYZE commands
 */
public class RedshiftParser extends Parser {
    /**
     * Creates a new Redshift parser with default configuration.
     */
    public RedshiftParser() {
        this(ParserConfig.defaultConfig());
    }

    /**
     * Creates a new Redshift parser with custom configuration.
     *
     * @param config Parser configuration options
     */
    public RedshiftParser(ParserConfig config) {
        super(config);
    }
}
