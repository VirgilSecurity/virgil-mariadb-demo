package com.virgilsecurity.demo.purekit.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.model.http.SharingData;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.model.PureGrant;

@Service
public class SharingService {

	@Autowired
	private Pure pure;

	public void share(SharingData sharingData, PureGrant grant) {
		// TODO
		// patient: ssn
		// physician: license_no
		// labtest: test id
//		this.pure.share(grant, sharingData.getDataId(), sharingData.getOtherUserIds(), null);
	}

}
