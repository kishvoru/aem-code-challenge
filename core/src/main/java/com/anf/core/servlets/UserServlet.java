/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.anf.core.servlets;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.anf.core.services.ContentService;
import com.google.gson.JsonObject;

/**
 * @author kishore
 *
 */
@Component(service = { Servlet.class })
@SlingServletPaths(
        value = "/bin/saveUserDetails"
)
public class UserServlet extends SlingAllMethodsServlet {

    /**
     * Serial Version
     */
    private static final long serialVersionUID = 1L;

    /**
     * Dynamic References
     */
    @Reference
    private ContentService contentService;
    
	/**
	 * Constants
	 */
	private static final String PATH_AGE_RES = "/etc/age";

	private static final String PN_FIRST_NAME = "firstName";
	private static final String PN_LAST_NAME = "lastName";
	private static final String PN_COUNTRY = "country";
	private static final String PN_AGE = "age";
	
	private static final String PN_MIN_AGE = "minAge";
	private static final String PN_MAX_AGE = "maxAge";


    
    /**
     * Serves POST Request
     */
    @Override
    protected void doPost(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
    	
    	String response = "You are not eligible";
    	ResourceResolver resolver = req.getResourceResolver();
    	JsonObject userData = getRequestData(req);
    	
    	if(isUserEligible(resolver, userData)) {
			response = contentService.commitUserDetails(resolver, userData);
    	}
    	
    	resp.getWriter().write(response);
    }

    
	/**
	 * @param resolver
	 * @param userData
	 * @return
	 */
	private Boolean isUserEligible(ResourceResolver resolver, JsonObject userData) {
		Boolean isUserEligible = Boolean.FALSE;
		
		Resource ageResource = resolver.getResource(PATH_AGE_RES);
		if(Objects.isNull(ageResource)) 
			return isUserEligible;
		
		ValueMap ageProps = ageResource.getValueMap();
		
		Integer age = getAge(userData.get(PN_AGE).getAsString());
		Integer minAge = getAge(ageProps.get(PN_MIN_AGE, StringUtils.EMPTY));
		Integer maxAge = getAge(ageProps.get(PN_MAX_AGE, StringUtils.EMPTY));

		if(age < 0 || minAge < 0 || maxAge < 0 || age < minAge || age > maxAge)
			return isUserEligible;
		
		isUserEligible = Boolean.TRUE;
		return isUserEligible;
	}


	/**
	 * @param ageString
	 * @return
	 */
	private Integer getAge(String ageString) {
		if(StringUtils.isEmpty(ageString))
			return -1;
		else
			return Integer.valueOf(ageString);
	}
	
	/**
	 * @param req
	 * @return
	 */
	private JsonObject getRequestData(SlingHttpServletRequest req) {
		JsonObject userData = new JsonObject();
		
		userData.addProperty(PN_FIRST_NAME, req.getParameter(PN_FIRST_NAME));
		userData.addProperty(PN_LAST_NAME, req.getParameter(PN_LAST_NAME));
		userData.addProperty(PN_AGE, req.getParameter(PN_AGE));
		userData.addProperty(PN_COUNTRY, req.getParameter(PN_COUNTRY));
		
		return userData;
	}
}
