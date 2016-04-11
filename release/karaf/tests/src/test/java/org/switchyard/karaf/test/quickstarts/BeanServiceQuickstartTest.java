/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.karaf.test.quickstarts;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class BeanServiceQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.bean.service";
    private static String featureName = "switchyard-quickstart-bean-service";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Test
    public void testOrders() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();

        httpMixIn.initialize();
        try {
            XMLUnit.setIgnoreWhitespace(true);
            String port = getSoapClientPort();
            String wsdl = httpMixIn.sendString("http://localhost:" + port + "/quickstart-bean/OrderService?wsdl", "", HTTPMixIn.HTTP_GET);
            compareWSDL(new InputStreamReader(Classes.getResourceAsStream("quickstarts/bean-service/OrderService.wsdl")), new StringReader(wsdl));
            String response = httpMixIn.postString("http://localhost:" + port + "/quickstart-bean/OrderService", SOAP_REQUEST);
            XMLAssert.assertXpathEvaluatesTo("PO-19838-XYZ", "//orderAck/orderId", response);
            XMLAssert.assertXpathEvaluatesTo("true", "//orderAck/accepted", response);
            XMLAssert.assertXpathEvaluatesTo("Order Accepted [intercepted]", "//orderAck/status", response);
        } finally {
            httpMixIn.uninitialize();
        }
    }

    /**
     * Due to the different behavior on Element ordering between JDK8 and older,
     * assertXMLEqual() doesn't work OOTB. Instead just verify some parts of WSDL using XPath.
     */
    private void compareWSDL(Reader expected, Reader actual) throws Exception {
        Schema wsdlSchema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                                         .newSchema(new URL("http://schemas.xmlsoap.org/wsdl/"));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setSchema(wsdlSchema);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document expectedDoc = builder.parse(new InputSource(expected));
        Document actualDoc = builder.parse(new InputSource(actual));

        XMLAssert.assertXpathsEqual(
                "//definitions/types", expectedDoc,
                "//definitions/types", actualDoc);
        XMLAssert.assertXpathsEqual(
                "//definitions/message[@name=submitOrder]", expectedDoc,
                "//definitions/message[@name=submitOrder]", actualDoc);
        XMLAssert.assertXpathsEqual(
                "//definitions/message[@name=submitOrderResponse]", expectedDoc,
                "//definitions/message[@name=submitOrderResponse]", actualDoc);
        XMLAssert.assertXpathsEqual(
                "//definitions/portType", expectedDoc,
                "//definitions/portType", actualDoc);
        XMLAssert.assertXpathsEqual(
                "//definitions/binding", expectedDoc,
                "//definitions/binding", actualDoc);
        XMLAssert.assertXpathsEqual(
                "//definitions/service", expectedDoc,
                "//definitions/service", actualDoc);
    }

    private static final String SOAP_REQUEST = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
            "    <soap:Body>\n" +
            "        <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart:bean-service:1.0\">\n" +
            "            <order>\n" +
            "                <orderId>PO-19838-XYZ</orderId>\n" +
            "                <itemId>BUTTER</itemId>\n" +
            "                <quantity>200</quantity>\n" +
            "            </order>\n" +
            "        </orders:submitOrder>\n" +
            "    </soap:Body>\n" +
            "</soap:Envelope>";
}
