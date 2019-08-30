package com.vastserver;

import com.vastserver.classes.ScheduledTasks;
import com.vastserver.db.DBHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Calendar;

@SpringBootApplication
@EnableJms
public class ServerMain implements CommandLineRunner {
    private static final Logger log = LogManager.getLogger(ServerMain.class);

    @Autowired
    private DBHandler db;

    @Autowired
    private ScheduledTasks scheduledTasks;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ServerMain.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        this.db.initDBTables();
        this.scheduledTasks.start();
    }

    @Bean
    public JmsListenerContainerFactory<?> topicListenerFactory(CachingConnectionFactory connectionFactory,
                                                               DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory topicFactory = new DefaultJmsListenerContainerFactory();
        configurer.configure(topicFactory, connectionFactory);
        topicFactory.setPubSubDomain(true);
        return topicFactory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}