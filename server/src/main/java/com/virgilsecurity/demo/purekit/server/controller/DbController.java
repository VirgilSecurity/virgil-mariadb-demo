package com.virgilsecurity.demo.purekit.server.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/db")
@RestController
public class DbController {
	
	@Autowired
	private String webServiceUrl;

	@GetMapping
	public void view(HttpServletResponse response) throws IOException {
		response.sendRedirect(this.webServiceUrl);
	}
}
