package com.virgilsecurity.demo.purekit.server.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.virgilsecurity.demo.purekit.server.exception.EncryptionException;
import com.virgilsecurity.demo.purekit.server.model.http.SharingData;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.model.PureGrant;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SharingService {

	@Autowired
	private Pure pure;

	public void share(SharingData sharingData, PureGrant grant) {
		// TODO
		// patient: ssn
		// physician: license_no
		// labtest: test id
		try {
			this.pure.share(grant, sharingData.getDataId(), sharingData.getOtherUserIds(), Collections.emptySet());
		} catch (PureException e) {
			log.error("Sharing data '{}' with users '{}' failed", sharingData.getDataId(),
					sharingData.getOtherUserIds(), e);
			throw new EncryptionException();
		}
	}

}
