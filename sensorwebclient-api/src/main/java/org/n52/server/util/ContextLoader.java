
package org.n52.server.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Provides mechanism to load bean instances declared within the application context file.
 */
public class ContextLoader {

    private static ApplicationContext context = new ClassPathXmlApplicationContext("/application-context.xml");

    /**
     * Loads a bean from the {@link ApplicationContext}.
     * 
     * @param bean
     *        the bean id as declared in the application context file.
     * @param clazz
     *        the expected type of the bean.
     * @return the bean of type <code>T</code>.
     */
    public static <T> T load(String bean, Class<T> clazz) {
        return clazz.cast(context.getBean(bean));
    }
}
