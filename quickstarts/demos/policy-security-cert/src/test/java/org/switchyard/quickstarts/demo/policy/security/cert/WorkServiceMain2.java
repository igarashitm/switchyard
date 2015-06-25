/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.quickstarts.demo.policy.security.cert;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.switchyard.common.io.pull.StringPuller;

/**
 * WorkServiceMain.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class WorkServiceMain2 {

    private static final Logger LOGGER = Logger.getLogger(WorkServiceMain2.class);

    public static void main(String... args) throws Exception {
        String endpoint = "https://localhost:8443/policy-security-cert/WorkService";
        String soapRequest = new StringPuller().pull("/xml/soap-request-no-wsse.xml").replaceAll("WORK_CMD", "CMD-" + System.currentTimeMillis());
        KeyStore connectorks = KeyStore.getInstance("JKS");
        connectorks.load(new FileInputStream("connector.jks"), "changeit".toCharArray());
        KeyStore usersks = KeyStore.getInstance("JKS");
        usersks.load(new FileInputStream("users.jks"), "changeit".toCharArray());
        SSLContext sslcontext = SSLContexts
                                .custom()
                                .loadTrustMaterial(usersks)
                                .loadKeyMaterial(usersks, "changeit".toCharArray())
                                .build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        CloseableHttpClient client = HttpClients
                                        .custom()
                                        .setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                                        .setSSLSocketFactory(sslsf)
                                        .build();
        HttpPost post = new HttpPost(endpoint);
        post.setEntity(new StringEntity(soapRequest));
        CloseableHttpResponse res = client.execute(post);
        String resstr = EntityUtils.toString(res.getEntity());
        LOGGER.info(resstr);
    }
}
