package com.anf.core.services.impl;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anf.core.services.ContentService;
import com.google.gson.JsonObject;

/**
 * @author kishore
 *
 */
@Component(immediate = true, service = ContentService.class)
public class ContentServiceImpl implements ContentService {
	
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentServiceImpl.class);

	/**
	 * Constants
	 */
	private static final String PATH_USER_DATA = "/var/anf-code-challenge";

	private static final String PN_FIRST_NAME = "firstName";
	private static final String PN_LAST_NAME = "lastName";
	private static final String PN_COUNTRY = "country";
	private static final String PN_AGE = "age";

	
	/**
	 * Service Implementation
	 */
	@Override
	public String commitUserDetails(ResourceResolver resolver, JsonObject userJson) {
		String status = StringUtils.EMPTY;
		
		try {
			Resource resource = ResourceUtil.getOrCreateResource(resolver, PATH_USER_DATA, new HashMap<String, Object>(), null, true);
			
			ModifiableValueMap mValueMap = resource.adaptTo(ModifiableValueMap.class);
			mValueMap.put(PN_FIRST_NAME, userJson.get(PN_FIRST_NAME).getAsString());
			mValueMap.put(PN_LAST_NAME, userJson.get(PN_LAST_NAME).getAsString());
			mValueMap.put(PN_AGE, userJson.get(PN_AGE).getAsString());
			mValueMap.put(PN_COUNTRY, userJson.get(PN_COUNTRY).getAsString());
			resolver.commit();
			
			status = "User Details Persisted Successfully!!!";
			LOGGER.info(status);
		} catch (PersistenceException e) {
			status = "Exception while Persisting User Data :: " + e.getMessage();
			LOGGER.error(status);
		}
		
		return status;
	}
}
