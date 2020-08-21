package com.bms.kafka.config;

import java.util.HashMap;import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

/*import com.bms.model.MessagePayloadParam;
import com.bms.model.NtpcKafka;
import com.bms.model.PublishedDataPayloadParam;*/

//@Configuration
public class KakfaConfiguration {

	// kafka consumer configuation
	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> config = new HashMap<>();

//		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.170.164:9092");
//		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.170.165:9300");
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		return new DefaultKafkaConsumerFactory<>(config);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

	@Bean
	public NewTopic topic4() {
		return new NewTopic("FWA_exicom", 1000, (short) 3);
	}
	/*
	 * @Bean public ProducerFactory<String, String> producerFactoryClient() {
	 * Map<String, Object> config = new HashMap<>();
	 * 
	 * // config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
	 * "192.168.170.165:9300"); //
	 * config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.170.165:9300");
	 * config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	 * config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
	 * StringSerializer.class);
	 * config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
	 * StringSerializer.class);
	 * 
	 * return new DefaultKafkaProducerFactory<>(config); }
	 * 
	 * @Bean public KafkaTemplate<String, String> kafkaTemplateClient() { return new
	 * KafkaTemplate<>(producerFactoryClient()); }
	 */

	/*
	 * @Bean public ProducerFactory<String, MessagePayloadParam> producerFactoryDB()
	 * { Map<String, Object> config = new HashMap<>();
	 * config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); //
	 * config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.170.165:9092");
	 * // config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
	 * "192.168.170.165:9300");
	 * config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
	 * StringSerializer.class);
	 * config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
	 * JsonSerializer.class);
	 * 
	 * return new DefaultKafkaProducerFactory<>(config); }
	 * 
	 * @Bean public KafkaTemplate<String, MessagePayloadParam> kafkaTemplateDB() {
	 * return new KafkaTemplate<>(producerFactoryDB()); }
	 * 
	 * @Bean public ProducerFactory<String, PublishedDataPayloadParam>
	 * pushedproducerFactoryDB() { Map<String, Object> config = new HashMap<>();
	 * config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); //
	 * config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.170.165:9300");
	 * //
	 * config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.170.164:9300");
	 * config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
	 * StringSerializer.class);
	 * config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
	 * JsonSerializer.class);
	 * 
	 * return new DefaultKafkaProducerFactory<>(config); }
	 * 
	 * @Bean public KafkaTemplate<String, PublishedDataPayloadParam>
	 * pushedkafkaTemplateDB() { return new
	 * KafkaTemplate<>(pushedproducerFactoryDB()); }
	 * 
	 * @Bean public ProducerFactory<String, NtpcKafka> pushedproducerFactoryNtpc() {
	 * Map<String, Object> config = new HashMap<>();
	 * config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); //
	 * config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.170.165:9300");
	 * //
	 * config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.170.164:9300");
	 * config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
	 * StringSerializer.class);
	 * config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
	 * JsonSerializer.class);
	 * 
	 * return new DefaultKafkaProducerFactory<>(config); }
	 * 
	 * @Bean public KafkaTemplate<String, NtpcKafka> pushedkafkaTemplateDBNtpc() {
	 * return new KafkaTemplate<>(pushedproducerFactoryNtpc()); }
	 * 
	 * 
	 * @Bean public KafkaAdmin admin() { Map<String, Object> configs = new
	 * HashMap<>(); configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
	 * StringUtils.arrayToCommaDelimitedString(kafkaEmbedded().getBrokerAddresses())
	 * ); return new KafkaAdmin(configs); }
	 * 
	 * 
	 * @Bean public NewTopic topic1() { return new NewTopic("Save_master_data", 50,
	 * (short) 3); }
	 * 
	 * @Bean public NewTopic topic2() { return new NewTopic("Save_client_data", 50,
	 * (short) 3); }
	 * 
	 * @Bean public NewTopic topic3() { return new NewTopic("publish_client_data",
	 * 50, (short) 3); }
	 * 
	 * @Bean public NewTopic topic4() { return new
	 * NewTopic("mqtt_data_with_partitions", 1000, (short) 3); }
	 */

}
