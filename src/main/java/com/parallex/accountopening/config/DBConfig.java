package com.parallex.accountopening.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import com.parallex.accountopening.utils.Encrypter;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DBConfig {

	@Value("${encryptor.key}")
	private String phrase;

	@Autowired
	private Encrypter encrypter;

	@Primary
	@Bean
	@ConfigurationProperties("spring.datasource")
	public DataSourceProperties mssqlDataSourceProperties() {
		return new DataSourceProperties();
	}
	
	@Bean
	@ConfigurationProperties("spring.datasource.finacle")
	public DataSourceProperties finacleDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Primary
	@Bean
	@ConfigurationProperties("spring.datasource.configuration")
	public DataSource mssqlDataSource() {

		String p = encrypter.decrypt(mssqlDataSourceProperties().getPassword());
		DataSource ds = mssqlDataSourceProperties().initializeDataSourceBuilder().password(p).type(HikariDataSource.class)
				.build();

		return ds;
	}
	
	@Bean
	@ConfigurationProperties("spring.datasource.finacle.configuration")
	public DataSource finacleDataSource() {

		String p = encrypter.decrypt(finacleDataSourceProperties().getPassword());
		DataSource ds = finacleDataSourceProperties().initializeDataSourceBuilder().password(p)
				.type(HikariDataSource.class).build();

		return ds;
	}
	
	@Primary
	@Bean(name = "mssqlJDBCTemplate")
	@Autowired
	public JdbcTemplate mssqlJDBCTemplate(@Qualifier("mssqlDataSource") DataSource ds) {
		return new JdbcTemplate(ds);
	}
	
	@Bean(name = "finacleJDBCTemplate")
	@Autowired
	public JdbcTemplate finacleJDBCTemplate(@Qualifier("finacleDataSource") DataSource ds) {
		return new JdbcTemplate(ds);
	}

}
