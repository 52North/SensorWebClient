package org.n52.server.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextLoader {
    
    private static ApplicationContext context = new ClassPathXmlApplicationContext("/application-context.xml");
    
    public static <T> T load(String bean, Class<T> clazz) {
        return clazz.cast(context.getBean(bean));
    }
}
