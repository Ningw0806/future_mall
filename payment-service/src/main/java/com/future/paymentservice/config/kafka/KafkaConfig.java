package com.future.paymentservice.config.kafka;

import com.future.futurecommon.payload.OrderInfoDTO;
import com.future.futurecommon.payload.PaymentResultDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    public String bootstrapServers;

    @Value("${kafka.consumer-config.group-id}")
    public String groupId;

    @Value("${kafka.consumer-config.auto-offset-rest-config}")
    public String autoOffsetReset;

    @Value("${kafka.producer-config.acks-config}")
    public String acksConfig;

    @Value("${kafka.producer-config.retries-config}")
    public String retriesConfig;

    @Bean
    public ProducerFactory<String, OrderInfoDTO> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, acksConfig);         // 确保消息持久化
        configProps.put(ProducerConfig.RETRIES_CONFIG, retriesConfig);          // 重试次数
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 幂等性
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, OrderInfoDTO> orderKafkaTemplate() {
        KafkaTemplate<String, OrderInfoDTO> template = new KafkaTemplate<>(producerFactory());
        template.setObservationEnabled(true);  // 开启监控
        return template;
    }

    // 消费者配置（接收支付结果）
    @Bean
    public ConsumerFactory<String, PaymentResultDTO> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 手动提交偏移量
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // 信任所有包（仅测试环境）
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentResultDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentResultDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // 手动提交偏移量
        return factory;
    }

}
