package app.service;

import IoC.annotation.Bean;
import app.task.ProduceMessageTask;
import app.task.UpdateCacheTask;

import java.util.Timer;

@Bean
public class UserCacheService {

    public void updateCache() {
        Timer timer = new Timer();
        timer.schedule(new UpdateCacheTask(), 0, 10 * 1000);
    }

    public void sendMessageToMQ() {
        Timer timer = new Timer();
        timer.schedule(new ProduceMessageTask(), 5 * 1000, 10 * 1000);
    }
}
