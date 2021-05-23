package app;

import IoC.context.Context;
import IoC.context.impl.AnnotationContext;
import app.service.UserCacheService;

public class Main {

    public static void main(String[] args) {
        Context context = new AnnotationContext("app.service");
        UserCacheService userCacheService = (UserCacheService) context.getBean("UserCacheService");
        userCacheService.updateCache();
        userCacheService.sendMessageToMQ();
    }
}
