package app.task;

import app.model.UserInfo;
import app.util.XmlReader;
import com.couchbase.client.java.Cluster;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.TimerTask;

public class UpdateCacheTask extends TimerTask {

    @Override
    public void run() {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/users?mediaType=xml");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/xml");
            InputStream responseStream = connection.getInputStream();

            XmlReader xmlReader = new XmlReader();
            List<UserInfo> userInfoList = xmlReader.getUserInfo(responseStream);
            Cluster cluster = Cluster.connect("127.0.0.1", "admin", "123456");
            cluster.query("DELETE FROM `userInfo`");
            for (UserInfo userInfo: userInfoList) {
                cluster.query("INSERT INTO `userInfo` (KEY, VALUE)\n" +
                        "VALUES (\"" + userInfo.getId() + "\", \"" + userInfo.getBalance() + "\")");
            }
            System.out.println("Cache has been updated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
