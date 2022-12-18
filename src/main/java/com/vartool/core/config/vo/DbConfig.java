package com.vartool.core.config.vo;

import java.io.File;

import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.vartech.common.utils.StringUtils;
import com.vartool.core.config.VartoolConfiguration;
import com.vartool.core.config.vo.DbConfig.DbConfigBuilder;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * db conection config
 * 
* @fileName	: DbConfig.java
* @author	: ytkim
 */
@Getter
@NoArgsConstructor
public class DbConfig {
	private String type;
	private String url;
	private String username;
	private String password;
	private String driverClass;
	
	private int maxTotal = 10;
	private int minIdle = 2;
    private int maxIdle = 10;
    private int initialSize = 3;
    private long maxWaitMillis = 50000;
    
    private String validationQuery ="select 1";
    private boolean testWhileIdle = true;

	@Builder
	public DbConfig(String type, String url, String username, String password, String driverClass, 
			int maxTotal, int minIdle, int maxIdle, int initialSize, long maxWaitMillis, String validationQuery, boolean testWhileIdle) {
		
		this.type= type;
		this.url= url;
		this.username= username;
		this.password= password;
		this.driverClass= driverClass;
		
		this.maxTotal = maxTotal;
		this.minIdle =minIdle;
		this.maxIdle = maxIdle;
		this.initialSize =initialSize;
		this.maxWaitMillis = maxWaitMillis;
		this.validationQuery = validationQuery;
		this.testWhileIdle = testWhileIdle;
	}
	
	@Override
	public String toString() {
		
		return new StringBuilder()
			.append("type : ").append(type).append("\n")
			.append("url : ").append(url).append("\n")
			.append("username : ").append(username).append("\n")
			.append("driverClass : ").append(driverClass).append("\n")
			.append("maxTotal : ").append(maxTotal).append("\n")
			.append("minIdle : ").append(minIdle).append("\n")
			.append("maxIdle : ").append(maxIdle).append("\n")
			.append("initialSize : ").append(initialSize).append("\n")
			.append("maxWaitMillis : ").append(maxWaitMillis).append("\n")
			.append("validationQuery : ").append(validationQuery).append("\n")
			.append("testWhileIdle : ").append(testWhileIdle).append("\n")
			.toString();
	}

	public static DbConfig initDbConfig(DbConfig dbConfig) {
		if(dbConfig == null) {
			return DbConfig.builder()
				.driverClass("org.h2.Driver")
				.url("jdbc:h2:file:" + VartoolConfiguration.getConfigRootPath() + File.separator + "db" + File.separator + "vartool;AUTO_SERVER=TRUE;CACHE_SIZE=131072;")
				.username("sa")
				.password("sa")
				.type("h2")
			.build();
		}else {
			DbConfigBuilder builder = DbConfig.builder();
			
			builder.type(dbConfig.getType());
			builder.username(dbConfig.getUsername());
			builder.password(dbConfig.getPassword());
			builder.driverClass(dbConfig.getDriverClass());
			
			if("h2".equalsIgnoreCase(dbConfig.getType())){
				if(!StringUtils.isBlank(dbConfig.getUrl())) {
					builder.url(dbConfig.getUrl().replace("#resourePath#", VartoolConfiguration.getConfigRootPath()));
				}
			}
			
			builder.initialSize(dbConfig.getInitialSize() < 1 ? 3 : dbConfig.getInitialSize());
			builder.maxTotal(dbConfig.getMaxTotal() < 1 ? 10 : dbConfig.getMaxTotal());
			builder.minIdle(dbConfig.getMinIdle() < 1 ? 2 : dbConfig.getMinIdle());
			builder.maxIdle(dbConfig.getMaxIdle() < 1 ? 10 : dbConfig.getMaxIdle());
			builder.maxWaitMillis(dbConfig.getMaxWaitMillis() == 0 ? 50000 : dbConfig.getMaxWaitMillis());
			builder.validationQuery(StringUtils.isBlank(dbConfig.getValidationQuery()) ? "select 1" : dbConfig.getValidationQuery());
			
			return builder.build();
		}
	}
	
}
