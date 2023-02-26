package com.push.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author Yan XinYu
 **/
@Component
public class SpringBeanLocator implements BeanFactoryAware {

    private static BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        SpringBeanLocator.beanFactory = beanFactory;
    }

    public static Object getBean(String beanName) {
        try {
            return beanFactory.getBean(beanName);
        } catch (BeansException ignored) {}
        return null;
    }

    public static <T> T getBean(Class<T> beanType) {
        try {
            return (T) beanFactory.getBean(beanType);
        } catch (BeansException ignored) {}
        return null;
    }

}
