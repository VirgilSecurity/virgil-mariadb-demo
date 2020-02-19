package com.virgilsecurity.demo.purekit.server.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.h2.server.web.WebServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.DefaultUriBuilderFactory;

import lombok.extern.log4j.Log4j2;

@RequestMapping("/db")
@RestController
@Log4j2
public class DbConsoleController {

	@Autowired
	private WebServer webServer;

	@Autowired
	private DataSource dataSource;

	@GetMapping
	public void view(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		URL url = new URL(webServer.addSession(dataSource.getConnection()));
		String location = new DefaultUriBuilderFactory().builder().scheme(url.getProtocol())
				.host(request.getServerName()).port(url.getPort()).path(url.getPath()).query(url.getQuery()).build()
				.toString();
		log.debug("Database console url: {}", location);
		response.sendRedirect(location);
	}
}
