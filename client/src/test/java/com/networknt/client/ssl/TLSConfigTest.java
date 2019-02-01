package com.networknt.client.ssl;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.networknt.client.ssl.TLSConfig.InvalidGroupKeyException;

public class TLSConfigTest {
	private static final Map<String, Object> tlsMap = new HashMap<>();
	private static final String LOCALHOST = "localhost";
	private static final String SOMEHOST = "somehost";
	private static final String EMPTY = "";
	
	
	@BeforeClass
	public static void fill_tls_map() {
		Map<String, Object> nameMap = new HashMap<>();
		Map<String, Object> groupMap = new HashMap<>();
		tlsMap.put("verifyHostname", Boolean.TRUE);
		tlsMap.put("trustedNames", nameMap);
		
		nameMap.put("default", LOCALHOST);
		nameMap.put("groups", groupMap);
		
		groupMap.put("group1", SOMEHOST);
		groupMap.put("group2", EMPTY);
		
	}

	@Test
	public void trusted_names_can_be_properly_resolved() {
		TLSConfig defaultConfig = TLSConfig.create(tlsMap);
		
		assertTrue(defaultConfig.getTrustedNameSet().size()==1 && defaultConfig.getTrustedNameSet().contains(LOCALHOST));
		assertTrue(EndpointIdentificationAlgorithm.APIS == defaultConfig.getEndpointIdentificationAlgorithm());
		
		TLSConfig group1Config = TLSConfig.create(tlsMap, "trustedNames.groups.group1");
		assertTrue(group1Config.getTrustedNameSet().size()==1 && group1Config.getTrustedNameSet().contains(SOMEHOST));
		assertTrue(EndpointIdentificationAlgorithm.APIS == group1Config.getEndpointIdentificationAlgorithm());	
		
		TLSConfig group2Config = TLSConfig.create(tlsMap, "trustedNames.groups.group2");
		assertTrue(group2Config.getTrustedNameSet().isEmpty());
		assertTrue(EndpointIdentificationAlgorithm.HTTPS == group2Config.getEndpointIdentificationAlgorithm());	
	}
	
	@Test(expected=InvalidGroupKeyException.class)
	public void incomplete_group_key_throws_exception() {
		TLSConfig.create(tlsMap, "trustedNames.groups");
	}
	
	@Test(expected=InvalidGroupKeyException.class)
	public void nonexisting_group_key_throws_exception() {
		TLSConfig.create(tlsMap, "trustedNames.something");
	}	
	
	@Test
	public void one_for_all_config_is_supported() {
		Map<String, Object> map = new HashMap<>();
		map.put("verifyHostname", Boolean.TRUE);
		map.put("trustedNames", LOCALHOST);
		
		TLSConfig config = TLSConfig.create(map, "trustedNames");
		
		assertTrue(config.getTrustedNameSet().size()==1 && config.getTrustedNameSet().contains(LOCALHOST));
		assertTrue(EndpointIdentificationAlgorithm.APIS == config.getEndpointIdentificationAlgorithm());		
		
		
	}
}
