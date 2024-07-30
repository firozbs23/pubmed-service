package com.omnizia.pubmedservice.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.constant.DbSelectorConstants;
import com.omnizia.pubmedservice.util.HostnameUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EntityScan(basePackages = "com.omnizia.pubmedservice.entity")
@EnableJpaRepositories(basePackages = "com.omnizia.pubmedservice.repository")
public class DataSourceConfig {

  @Bean(name = DbSelectorConstants.MCD)
  public DataSource dataSource1() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setUrl(
        "jdbc:postgresql://ls-e7500498eb7cca7cb3501ee037e5841634bfe0d3.cngn4h200lce.eu-central-1.rds.amazonaws.com:5432/brainstation_crdlp_dev");
    dataSource.setUsername("cdp_dev_user");
    dataSource.setPassword("PhAL5XwyWm");
    dataSource.setSchema("gds");
    dataSource.setDriverClassName("org.postgresql.Driver");
    return dataSource;
  }

  @Bean(name = DbSelectorConstants.OLAM)
  public DataSource dataSource2() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setUrl(
        "jdbc:postgresql://ls-e7500498eb7cca7cb3501ee037e5841634bfe0d3.cngn4h200lce.eu-central-1.rds.amazonaws.com:5432/crdlp_dev");
    dataSource.setUsername("cdp_dev_user");
    dataSource.setPassword("PhAL5XwyWm");
    dataSource.setSchema("gds");
    dataSource.setDriverClassName("org.postgresql.Driver");
    return dataSource;
  }

  @Bean(name = DbSelectorConstants.JOB_CONFIG)
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    String dbUrl = "jdbc:postgresql://" + HostnameUtil.getDBConfigHostname() + "/spring_batch_db";
    dataSource.setUrl(dbUrl);
    dataSource.setUsername("username");
    dataSource.setPassword("password");
    dataSource.setDriverClassName("org.postgresql.Driver");

    // Execute SQL script
    DatabasePopulatorUtils.execute(createDatabasePopulator(), dataSource);

    return dataSource;
  }

  private DatabasePopulator createDatabasePopulator() {
    ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
    databasePopulator.addScript(new ClassPathResource("db/batch_job.sql"));
    return databasePopulator;
  }

  @Primary
  @Bean(name = "dynamicDataSource")
  public DataSource routingDataSource(
      @Qualifier("mcd") DataSource dataSource1,
      @Qualifier("olam") DataSource dataSource2,
      @Qualifier("dataSource") DataSource batchJob) {
    AbstractRoutingDataSource routingDataSource =
        new AbstractRoutingDataSource() {
          @Override
          protected Object determineCurrentLookupKey() {
            return DataSourceContextHolder.getDataSourceType();
          }
        };

    Map<Object, Object> dataSourceMap = new HashMap<>();
    dataSourceMap.put("mcd", dataSource1);
    dataSourceMap.put("olam", dataSource2);
    dataSourceMap.put("dataSource", batchJob);

    routingDataSource.setTargetDataSources(dataSourceMap);
    routingDataSource.setDefaultTargetDataSource(batchJob);

    return routingDataSource;
  }

  @Primary
  @Bean(name = "entityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      EntityManagerFactoryBuilder builder, @Qualifier("dynamicDataSource") DataSource dataSource) {
    Map<String, String> properties = new HashMap<>();
    properties.put("hibernate.hbm2ddl.auto", "none");

    return builder
        .dataSource(dataSource)
        .packages("com.omnizia.pubmedservice.entity")
        .persistenceUnit("default")
        .properties(properties)
        .build();
  }

  @Bean(name = "transactionManager")
  public PlatformTransactionManager transactionManager(
      @Qualifier("entityManagerFactory")
          LocalContainerEntityManagerFactoryBean entityManagerFactory) {
    return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
  }
}
