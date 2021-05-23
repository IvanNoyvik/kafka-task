package IoC.context.impl;

import IoC.bean.factory.BeanFactory;
import IoC.context.Context;

public class AnnotationContext implements Context {

    private BeanFactory beanFactory;

    public AnnotationContext(String packageName) {
        beanFactory = new BeanFactory(packageName);
    }

    @Override
    public Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

}
