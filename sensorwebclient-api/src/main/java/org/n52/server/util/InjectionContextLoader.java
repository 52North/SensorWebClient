package org.n52.server.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InjectionContextLoader {

    public static <T> T load(String bean, Class<T> clazz) {
        ApplicationContext context = new ClassPathXmlApplicationContext("/application-context.xml");
        return clazz.cast(context.getBean(bean));
    }
}
