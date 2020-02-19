package com.virgilsecurity.demo.purekit.server.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.h2.server.web.WebServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public void view(HttpServletResponse response) throws IOException, SQLException {
		String url = webServer.addSession(dataSource.getConnection());
		log.debug("Database console url: {}", url);
		response.sendRedirect(url);
	}
}
