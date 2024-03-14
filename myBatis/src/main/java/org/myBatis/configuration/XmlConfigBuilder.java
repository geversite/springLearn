package org.myBatis.configuration;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.myCP.DataSource;
import org.myCP.DataSourceConfig;
import org.myBatis.io.Resources;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XmlConfigBuilder {

    private final Configuration configuration;

    public XmlConfigBuilder(){
        this.configuration = new Configuration();
    }

    public XmlConfigBuilder(Configuration configuration){
        this.configuration = configuration;
    }


    public Configuration parse(InputStream stream) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(stream);
        Element root = document.getRootElement();
        List<Element> elements = root.selectNodes("//datasource//property");
        Properties properties = new Properties();
        for (Element element : elements) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.put(name,value);
        }

        DataSourceConfig config = new DataSourceConfig();

        config.setUrl(properties.getProperty("jdbcUrl"));
        config.setUsername(properties.getProperty("username"));
        config.setPassword(properties.getProperty("password"));
        config.setDriver(properties.getProperty("driverClass"));

        configuration.setDataSource(new DataSource(config));

        List<Element> listMapper = root.selectNodes("//mapper");
        for (Element element : listMapper) {
            InputStream inputStream = Resources.getResourceAsStream(element.attributeValue("resource"));
            XmlMapperBuilder builder = new XmlMapperBuilder(configuration);
            builder.parse(inputStream);
        }
        return configuration;
    }
}
