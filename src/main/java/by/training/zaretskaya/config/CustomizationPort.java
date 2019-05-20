package by.training.zaretskaya.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomizationPort implements WebServerFactoryCustomizer< ConfigurableServletWebServerFactory > {

    private Node node;

    @Override
    public void customize(ConfigurableServletWebServerFactory server) {
        final String prefixLocalhost = "http://localhost:";
        server.setPort(Integer.valueOf(node.getHost().replace(prefixLocalhost, "")));
    }

    @Autowired
    public void setNode(Node node) {
        this.node = node;
    }
}
