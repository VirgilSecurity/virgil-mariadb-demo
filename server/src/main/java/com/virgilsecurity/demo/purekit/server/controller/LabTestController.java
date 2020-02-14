package com.virgilsecurity.demo.purekit.server.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virgilsecurity.demo.purekit.server.exception.BadRequestException;
import com.virgilsecurity.demo.purekit.server.model.http.LabTest;
import com.virgilsecurity.demo.purekit.server.service.LabTestService;
import com.virgilsecurity.demo.purekit.server.utils.Constants;
import com.virgilsecurity.purekit.pure.model.PureGrant;

@RequestMapping("/lab-tests")
@RestController
public class LabTestController {

	@Autowired
	private LabTestService labTestService;

	@GetMapping
	public List<LabTest> list(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant) {
		return this.labTestService.findAll(grant);
	}

	@PostMapping
	public String create(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant, @RequestBody LabTest labTest) {
		return this.labTestService.create(labTest, grant);
	}

	@GetMapping("/{id}")
	public LabTest get(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant, @PathVariable("id") String labTestId) {
		return this.labTestService.get(labTestId, grant);
	}

	@PutMapping("/{id}/share/{userId}")
	public void share(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant, @PathVariable("id") String id,
			@PathVariable("userId") String userId) {
		this.labTestService.shareResults(id, userId, grant);
	}

	@PutMapping("/{id}")
	public void update(@RequestHeader(Constants.GRANT_HEADER) PureGrant grant, @PathVariable("id") String labTestId,
			@RequestBody LabTest labTest) {
		if (StringUtils.isNotBlank(labTest.getId()) && !StringUtils.equals(labTestId, labTest.getId())) {
			throw new BadRequestException("LabTest id is not valid");
		}
		this.labTestService.update(labTest, grant);
	}

}
