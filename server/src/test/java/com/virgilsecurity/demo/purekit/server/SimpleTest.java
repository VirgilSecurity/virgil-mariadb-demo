package com.virgilsecurity.demo.purekit.server;

import org.junit.jupiter.api.Test;

import com.virgilsecurity.common.util.HexUtils;

public class SimpleTest {
	
	@Test
	public void xxx() {
		System.out.println(HexUtils.toHexString("Pills 2 tablet per day".getBytes()));
	}

}
