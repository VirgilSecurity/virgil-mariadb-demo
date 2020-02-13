package com.virgilsecurity.demo.purekit.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virgilsecurity.demo.purekit.server.model.http.ResetData;
import com.virgilsecurity.demo.purekit.server.service.ResetService;

@RequestMapping("/reset")
@RestController
public class ResetController {

	@Autowired
	private ResetService resetService;

	@GetMapping
	public ResetData reset() {
		return this.resetService.reset();
	}

}
