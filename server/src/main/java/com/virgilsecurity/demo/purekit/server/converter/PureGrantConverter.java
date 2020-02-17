package com.virgilsecurity.demo.purekit.server.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.virgilsecurity.demo.purekit.server.exception.BadRequestException;
import com.virgilsecurity.purekit.pure.Pure;
import com.virgilsecurity.purekit.pure.exception.PureException;
import com.virgilsecurity.purekit.pure.model.PureGrant;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class PureGrantConverter implements Converter<String, PureGrant> {

	@Autowired
	private Pure pure;

	@Override
	public PureGrant convert(String encryptedGrantString) {
		try {
			return pure.decryptGrantFromUser(encryptedGrantString);
		} catch (PureException e) {
			log.error("Pure grant can't be dectypted", e);
			throw new BadRequestException("Pure grant can't be dectypted", e);
		}
	}

}
