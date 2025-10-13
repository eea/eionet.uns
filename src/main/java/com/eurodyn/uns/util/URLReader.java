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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.eurodyn.uns.Properties;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility responsible for reading data by using HTTP protocol.
 * It wraps Apache http-client library in order to provide WDS with the capability of controlling
 * connection and socket timeouts.
 *
 */
public class URLReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLReader.class);
    private static final int DEFAULT_SOCKET_TIMEOUT = 10;
    private final CloseableHttpClient client;
    private static int socketTimeout;

    static {
        try {
            socketTimeout = Integer.parseInt(Properties.getStringProperty("channel.socket_timeout"));
        } catch (Exception e) {
            socketTimeout = DEFAULT_SOCKET_TIMEOUT;
            LOGGER.info("Could not load socket timeout from configuration, assuming default: " + DEFAULT_SOCKET_TIMEOUT);
        }
    }

    public URLReader() {
        RequestConfig config = RequestConfig.custom()
                .setRedirectsEnabled(true)
                .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(socketTimeout))
                .build();

        client = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();
    }

    public ByteArrayInputStream getContentAsInStream(String url) throws Exception {
        return new ByteArrayInputStream(getContentAsBytes(url));
    }

    private byte[] getContentAsBytes(String url) throws Exception {
        if (url.startsWith("file:///")) {
            return readFile(url.substring(8));
        }

        HttpGet get = new HttpGet(url);
        try (CloseableHttpResponse response = client.execute(get)) {
            if (response.getEntity() != null) {
                return EntityUtils.toByteArray(response.getEntity());
            } else {
                return new byte[0];
            }
        }
    }

    private byte[] readFile(String path) throws IOException {
        File file = new File(path);
        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        }
    }

}
