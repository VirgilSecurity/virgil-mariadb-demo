package com.virgilsecurity.demo.purekit.server;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.h2.server.web.WebServer;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.PureContext;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.storage.MariaDbPureStorage;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class ServerApplication {

	@Autowired
	private Environment env;

	private Server server;

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

//	@Bean(name = "dataSource")
//	public DataSource dataSource() {
//		@SuppressWarnings("rawtypes")
//		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//		dataSourceBuilder.driverClassName(this.env.getProperty("spring.datasource.driverClassName"));
//		dataSourceBuilder.url(this.env.getProperty("spring.datasource.url"));
//		if (StringUtils.isNotBlank(this.env.getProperty("spring.datasource.username"))) {
//			dataSourceBuilder.username(this.env.getProperty("spring.datasource.username"));
//		}
//		if (StringUtils.isNotBlank(this.env.getProperty("spring.datasource.password"))) {
//			dataSourceBuilder.password(this.env.getProperty("spring.datasource.password"));
//		}
//		DataSource dataSource = dataSourceBuilder.build();
//		return dataSource;
//	}

	@Bean(name = "webServiceUrl")
	public String webServiceUrl(DataSource dataSource) throws Exception {
		this.server = Server.createWebServer("-webPort", env.getProperty("db.viewer.port", "0"), "-webAllowOthers");
		WebServer service = (WebServer) server.getService();
		String url = service.addSession(dataSource.getConnection());
		log.debug("Database console url: {}", url);
		this.server.start();
		return url;
	}

	@Bean
	MariaDbPureStorage pureStorage(DataSource dataSource) {
		return new MariaDbPureStorage(dataSource);
	}

	@Bean
	Pure pure(MariaDbPureStorage pureStorage) {
		String appToken = this.env.getProperty("virgil.at");
		String nms = this.env.getProperty("virgil.nms");
		String bu = this.env.getProperty("virgil.bu");
		String secretKey = this.env.getProperty("virgil.sk");
		String publicKey = this.env.getProperty("virgil.pk");
		String pheServiceAddress = this.env.getProperty("virgil.pheServiceAddress",
				"https://api.virgilsecurity.com/phe/v1");
		String kmsServiceAddress = this.env.getProperty("virgil.kmsServiceAddress",
				"https://api.virgilsecurity.com/kms/v1");
		try {
			PureContext context = PureContext.createContext(appToken, nms, bu, secretKey, publicKey, pureStorage, null,
					pheServiceAddress, kmsServiceAddress);
			return new Pure(context);
		} catch (PureException e) {
			log.fatal("Can't initialize Virgil Pure", e);
			throw new RuntimeException("Virgil Pure initialization error");
		}
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
				registry.addMapping("/**").allowedMethods("GET", "POST", "PUT");
			}
		};
	}

	@PreDestroy
	public void preDestroy() {
		this.server.stop();
	}

}
