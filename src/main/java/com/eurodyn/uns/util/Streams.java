/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Unified Notification System
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 * 
 * Contributors(s):
 *    Original code: Dusan Popovic (ED)
 *                   Nedeljko Pavlovic (ED) 
 */

package com.eurodyn.uns.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.io.InputStreamReader;

/**
 * Utility functions related to Streams.
 *
 */
public class Streams {

    private static final Logger LOGGER = LoggerFactory.getLogger(Streams.class);

    static final int BLOCK_SIZE = 4096;


    public static void drain(InputStream r, OutputStream w) throws IOException {
        byte[] bytes = new byte[BLOCK_SIZE];
        try {
            int length = r.read(bytes);
            while (length != -1) {
                if (length != 0) {
                    w.write(bytes, 0, length);
                }
                length = r.read(bytes);
            }
        } finally {
            bytes = null;
        }

    }


    public static void drain(Reader r, Writer w) throws IOException {
        char[] bytes = new char[BLOCK_SIZE];
        try {
            int length = r.read(bytes);
            while (length != -1) {
                if (length != 0) {
                    w.write(bytes, 0, length);
                }
                length = r.read(bytes);
            }
        } finally {
            bytes = null;
        }

    }


    public static void drain(Reader r, OutputStream os) throws IOException {
        Writer w = new OutputStreamWriter(os);
        drain(r, w);
        w.flush();
    }


    public static void drain(InputStream is, Writer w) throws IOException {
        Reader r = new InputStreamReader(is);
        drain(r, w);
        w.flush();
    }


    public static byte[] drain(InputStream r) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        drain(r, bytes);
        return bytes.toByteArray();
    }


    public static String getAsString(InputStream is) {
        int c = 0;
        char lineBuffer[] = new char[128], buf[] = lineBuffer;
        int room = buf.length, offset = 0;
        try {
            loop: while (true) {
                // read chars into a buffer which grows as needed
                switch (c = is.read()) {
                case -1:
                    break loop;

                default:
                    if (--room < 0) {
                        buf = new char[offset + 128];
                        room = buf.length - offset - 1;
                        System.arraycopy(lineBuffer, 0, buf, 0, offset);
                        lineBuffer = buf;
                    }
                    buf[offset++] = (char) c;
                    break;
                }
            }
        } catch (IOException ioe) {
            LOGGER.error(ioe.getMessage(), ioe);
        }
        if ((c == -1) && (offset == 0)) {
            return null;
        }
        return String.copyValueOf(buf, 0, offset);
    }

}
