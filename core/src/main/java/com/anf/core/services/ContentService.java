package com.anf.core.services;

import org.apache.sling.api.resource.ResourceResolver;

import com.google.gson.JsonObject;

/**
 * @author kishore
 *
 */
public interface ContentService {
	
	/**
	 * @param resolver
	 * @param userJson
	 * @return
	 */
	String commitUserDetails(ResourceResolver resolver, JsonObject userJson);
	
}
