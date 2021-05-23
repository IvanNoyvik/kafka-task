package com.demo.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
//@ComponentScan("com.demo.repository")
@PropertySource("classpath:application.properties")
@EnableJpaRepositories(basePackages = "com.demo.repository")
public class PersistenceConfig {

    @Autowired
    private Environment environment;

    @SneakyThrows
    @Bean
    // jdbc:h2:tcp://localhost:9092/mem:spring
    public Server tcpServer() {
        return Server.createTcpServer().start();
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setPackagesToScan("com.demo.model");

        Properties properties = new Properties();
        properties.setProperty("javax.persistence.schema-generation.database.action", "create");
        properties.setProperty("javax.persistence.schema-generation.create-script-source", "script/DDL-initialization.sql");
        properties.setProperty("javax.persistence.sql-load-script-source", "script/DML-initialization.sql");

        properties.setProperty("hibernate.use_sql_comments", "true");
        properties.setProperty("hibernate.show_sql", "true");

        em.setJpaProperties(properties);

        return em;
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(environment.getProperty("datasource.jdbc.url"));
        config.setDriverClassName(environment.getProperty("datasource.jdbc.driverClassName"));
        config.setUsername(environment.getProperty("datasource.jdbc.username"));
        config.setPassword(environment.getProperty("datasource.jdbc.password"));

        config.setMaximumPoolSize(environment.getProperty("hikari.config.mximumPoolSize", Integer.class));
        config.setMinimumIdle(environment.getProperty("hikari.config.minimumIdle", Integer.class));
        config.setIdleTimeout(environment.getProperty("hikari.config.idleTimeout", Integer.class));

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }


}
