package com.viabill.graphqltest;

import com.viabill.graphqltest.criipto.CriiptoTestOperation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {GraphqlTestApplication.class})
public class GraphqlTestApplication implements InitializingBean {
    @Autowired
    private CriiptoTestOperation testOperation;

    public static void main(String[] args) {
//        for proxying calls to Fiddler
//        System.setProperty("http.proxyHost", "127.0.0.1");
//        System.setProperty("https.proxyHost", "127.0.0.1");
//        System.setProperty("http.proxyPort", "8866");
//        System.setProperty("https.proxyPort", "8866");
        SpringApplication.run(GraphqlTestApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        testOperation.submitDocumentForSignature();
    }
}
