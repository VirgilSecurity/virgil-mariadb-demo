package com.virgilsecurity.demo.purekit.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virgilsecurity.demo.purekit.server.model.http.SharingData;
import com.virgilsecurity.demo.purekit.server.service.SharingService;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
import com.virgilsecurity.purekit.pure.model.PureGrant;

@RequestMapping("/share")
@RestController
public class SharingController {

	@Autowired
	private SharingService sharingService;

	@PostMapping
	public void share(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant, @RequestBody SharingData sharingData) {
		this.sharingService.share(sharingData, grant);
	}

}
