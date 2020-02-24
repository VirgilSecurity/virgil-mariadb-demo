/*
 * Copyright (c) 2015-2020, Virgil Security, Inc.
 *
 * Lead Maintainer: Virgil Security Inc. <support@virgilsecurity.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     (1) Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *     (2) Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *     (3) Neither the name of virgil nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.virgilsecurity.demo.purekit.server;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.h2.server.web.WebServer;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.PureContext;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.storage.MariaDbPureStorage;

import lombok.extern.log4j.Log4j2;

/**
 * Spring Boot application.
 */
@SpringBootApplication
@Log4j2
public class ServerApplication {

	@Autowired
	private Environment env;

	private Server server;

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	/**
	 * Database console web server.
	 * 
	 * @return web server instance.
	 * @throws Exception
	 */
	@Bean
	@Scope("singleton")
	public WebServer webServer() throws Exception {
		this.server = Server.createWebServer("-webPort", env.getProperty("spring.dbviewer.port", "0"),
				"-webAllowOthers");
		WebServer service = (WebServer) server.getService();
		this.server.start();
		return service;
	}

	/**
	 * PureKit MariaDB storage.
	 * 
	 * @param dataSource the data source.
	 * @return {@code MariaDbPureStorage} instance.
	 */
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
				registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE");
			}
		};
	}

	@PreDestroy
	public void preDestroy() {
		// Stop DB Console web server
		this.server.stop();
	}

}
