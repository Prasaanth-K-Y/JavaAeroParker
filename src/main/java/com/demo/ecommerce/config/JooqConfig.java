package com.demo.ecommerce.config;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JooqConfig {

    @Bean
    public DSLContext dslContext(DataSource dataSource) {

        var settings = new org.jooq.conf.Settings()
                .withFetchSize(500)
                .withQueryTimeout(10);

        var config = new DefaultConfiguration()
                .set(dataSource)
                .set(SQLDialect.MYSQL)
                .set(settings);

        return DSL.using(config);
    }
}
