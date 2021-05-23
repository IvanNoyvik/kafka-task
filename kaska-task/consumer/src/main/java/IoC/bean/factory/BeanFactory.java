package IoC.bean.factory;

import IoC.annotation.Bean;
import IoC.annotation.Injection;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BeanFactory {

    private final Map<String, Object> beans = new HashMap<>();

    public BeanFactory(String packageName) {
        init(packageName);
        injectBeans();
    }

    public Object getBean(String beanName) {
        return beans.get(beanName);
    }

    private void init(String packageName) {
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            String path = packageName.replace(".", "/");
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                File file = new File(resource.toURI());
                for (File classFile : Objects.requireNonNull(file.listFiles())) {
                    String fileName = classFile.getName();
                    if (fileName.endsWith(".class")) {
                        String className = fileName.substring(0, fileName.lastIndexOf("."));
                        Class classObject = Class.forName(packageName + "." + className);
                        if (classObject.isAnnotationPresent(Bean.class)) {
                            System.out.println("Found bean: " + classObject);
                            Object instance = classObject.getDeclaredConstructor().newInstance();
                            beans.put(className, instance);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void injectBeans() {
        beans.forEach((beanName, beanInstance) -> {
            for (Field field : beanInstance.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Injection.class)) {
                    Object beanToInject = findBean(field.getType(), beanInstance);
                    try {
                        field.setAccessible(true);
                        field.set(beanInstance, beanToInject);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private Object findBean(Class injectClass, Object beanForInjection) {
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object bean = entry.getValue();
            if (bean.getClass().equals(injectClass) && !bean.equals(beanForInjection)) {
                return bean;
            }
            for (Class iFace : bean.getClass().getInterfaces()) {
                if (iFace.equals(injectClass)) {
                    return bean;
                }
            }
        }
        return null;
    }
}
