package org.mySpring.web.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

public class XmlParser {

    public static String  getBasePackage(String xml){
        SAXReader reader = new SAXReader();
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xml);
        try {
            Document document = reader.read(stream);
            Element rootElement = document.getRootElement();
            Element componentScan = rootElement.element("component-scan");
            String text = componentScan.attribute("base-package").getText();
            return text;
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}
