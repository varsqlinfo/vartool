package com.vartool.web.configuration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.vartool.core.config.VartoolConfiguration;
import com.vartool.core.config.vo.DbConfig;
import com.vartool.web.constants.DBInitMode;
import com.vartool.web.constants.ResourceConfigConstants;

/**
 * jpa 설정.
* 
* @fileName	: JPAConfigurer.java
* @author	: ytkim
 */
@EnableTransactionManagement
@PropertySource({ "classpath:persistence-h2.properties" })
@EnableJpaAuditing(auditorAwareRef = "customAuditorAware")
@EnableJpaRepositories(basePackages = {"com.vartool.web.repository","com.vartool.web.security.repository"}
,includeFilters ={
		@ComponentScan.Filter(type = FilterType.ANNOTATION, value={Repository.class})
		,@ComponentScan.Filter(type = FilterType.REGEX, pattern="(service|controller|DAO|Repository)\\.\\.*")
})
public class JPAConfigurer {

	private final Logger logger = LoggerFactory.getLogger(JPAConfigurer.class);

    @Autowired
    private Environment env;

    public JPAConfigurer() {
        super();
    }
    
    private DataSource mainDataSource;
    
	@PostConstruct
	public void initialize() {
		
		DbConfig dbConfig = VartoolConfiguration.getInstance().getDbConfig();
		
		BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setDriverClassName(dbConfig.getDriverClass());
	    dataSource.setUrl(dbConfig.getUrl());
	    dataSource.setUsername(dbConfig.getUsername());
	    dataSource.setPassword(dbConfig.getPassword());
	    dataSource.setInitialSize(dbConfig.getInitialSize());
	    dataSource.setMaxTotal(dbConfig.getMaxTotal());
	    dataSource.setMinIdle(dbConfig.getMinIdle());
	    dataSource.setMaxIdle(dbConfig.getMaxIdle());
	    dataSource.setMaxWaitMillis(dbConfig.getMaxWaitMillis());
	    dataSource.setValidationQuery(dbConfig.getValidationQuery());
	    dataSource.setTestWhileIdle(dbConfig.isTestWhileIdle());
		
		logger.info("=================datasourceconfig info====================");
		logger.info(" datasourceconfig : {}", dbConfig);
		logger.info(" DBInitMode : {}", dbConfig.getInitMode());
		logger.info(" is initialized : {}", VartoolConfiguration.getInstance().isInitialized());
		logger.info("=================datasourceconfig info====================");
		
		
		this.mainDataSource = dataSource;
		
		// init schema, data
		if(!dbConfig.getInitMode().equals(DBInitMode.NONE)) {
			if(!VartoolConfiguration.getInstance().isInitialized()) {
				
				List<Resource> initResource = new LinkedList<>();
				
				if(dbConfig.getInitMode().equals(DBInitMode.DDL)){
					initResource.add(new ClassPathResource("config/db/vartool-app.sql"));
				}
		        
				initResource.add(new ClassPathResource("config/db/vartool-app-init-data.sql"));
		        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(initResource.toArray(new Resource[0]));
		        DatabasePopulatorUtils.execute(databasePopulator, this.mainDataSource);
			}
		}
	}

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mainDataSource);
        em.setPackagesToScan(new String[] {"com.vartool.web.model.entity" });
        
        
        
        

        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean(name = ResourceConfigConstants.APP_TRANSMANAGER)
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
    
    @Bean
	public JPAAuditorAware customAuditorAware() {
		return new JPAAuditorAware();
	}

    final Properties additionalProperties() {

        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        hibernateProperties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "false");
        hibernateProperties.setProperty("hibernate.default_batch_fetch_size", "10"); // join할때
        
        hibernateProperties.setProperty("org.hibernate.envers.audit_table_prefix", "ZAUD_");	// audit 테이블명 prefix
        hibernateProperties.setProperty("org.hibernate.envers.audit_table_suffix", "");		// suffix
        hibernateProperties.setProperty("org.hibernate.envers.store_data_at_delete", "true");	// delete 전에  모든 필드의 값을 쌓을때

        hibernateProperties.setProperty("hibernate.envers.autoRegisterListeners", "false");	// 감사 로그 등록 여부.

        return hibernateProperties;
    }



}