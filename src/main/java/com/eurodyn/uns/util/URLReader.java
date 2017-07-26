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
 *    Original code: Nedeljko Pavlovic (ED)
 */

package com.eurodyn.uns.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import com.eurodyn.uns.util.common.AppConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility responsible for reading data by using HTTP protocol.
 * It wraps Apache http-client library in order to provide WDS with capability of controling
 * connection and socket timeouts.
 *
 */
public class URLReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLReader.class);
    private static final int DEFAULT_SOCKET_TIMEOUT = 1000*10;
    private org.apache.commons.httpclient.HttpClient client;
    private static int socketTimeout;

    static {
        try {
            socketTimeout=1000*Integer.parseInt(AppConfigurator.getInstance().getBoundle("uns").getString("channel.socket_timeout"));
        } catch (Exception e) {
            socketTimeout = DEFAULT_SOCKET_TIMEOUT;
            LOGGER.info("Could not load socket timeout from configuration, assuming default: " + DEFAULT_SOCKET_TIMEOUT);
        }
    }


    public URLReader() {
        client = new org.apache.commons.httpclient.HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        client.getHttpConnectionManager().getParams().setSoTimeout(socketTimeout);
    }


    public ByteArrayOutputStream getContentAsOutStream(String url) throws Exception {
        ByteArrayOutputStream result = null;
        HttpMethod method = getMethod(url);
        try {
            client.executeMethod(method);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(method.getResponseBodyAsStream());
            result = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(b, 0, b.length)) != -1) {
                result.write(b, 0, bytesRead);
            }
            result.flush();
            result.close();
        } catch (HttpException he) {
            throw new Exception("Http error connecting to '" + url + "' " + he.getMessage());
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
        return result;
    }


    public ByteArrayInputStream getContentAsInStream(String url) throws Exception {
        byte[] content = null;
        if(url.startsWith("file:///")){
            String filename = url.substring(8);
            FileInputStream fis = new FileInputStream(new File(filename));
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            Streams.drain(fis, bytes);
            content = bytes.toByteArray();
        } else {
            content = getContentAsBytes(url);
        }

        return new ByteArrayInputStream(content);
    }


    public byte[] getContentAsBytes(String url) throws Exception {
        HttpMethod method = getMethod(url);
        byte[] responseBody;
        try {
            client.executeMethod(method);
            responseBody = method.getResponseBody();
        } catch (HttpException he) {
            throw new Exception("Http error connecting to '" + url + "' " + he.getMessage());
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
        return responseBody;
    }


    private HttpMethod getMethod(String url) {
        HttpMethod method = null;
        method = new GetMethod(url);
        method.setFollowRedirects(true);
        return method;
    }

}
