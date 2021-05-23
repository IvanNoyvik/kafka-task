package app.task;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.TimerTask;

public class ProduceMessageTask extends TimerTask {

    @Override
    public void run() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "test");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);

        Cluster cluster = Cluster.connect("127.0.0.1", "admin", "123456");
        QueryResult queryRequest = cluster.query("SELECT META(`userInfo`).id, * FROM `userInfo`");
        for (JsonObject jsonObject : queryRequest.rowsAsObject()) {
            if (Integer.parseInt(jsonObject.get("userInfo").toString()) < 200) {
                kafkaProducer.send(new ProducerRecord<>("low balance", "userId: " + jsonObject.getString("id")));
            }
        }
        System.out.println("Message has been sent to MQ.");
    }
}
