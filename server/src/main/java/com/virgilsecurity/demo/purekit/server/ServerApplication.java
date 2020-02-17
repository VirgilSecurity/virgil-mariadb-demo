package com.virgilsecurity.demo.purekit.server;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

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

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	MariaDbPureStorage pureStorage() {
		return new MariaDbPureStorage(this.dataSource);
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

}