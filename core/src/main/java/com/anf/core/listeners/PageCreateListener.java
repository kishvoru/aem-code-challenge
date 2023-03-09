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
package com.anf.core.listeners;

import java.util.Objects;

import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Event Listener that listens to page creation & updates custom property.
 */
@Component(service = EventHandler.class,
           immediate = true,
           property = {
                   EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/ADDED",
                   EventConstants.EVENT_FILTER + "=(&(path=/content/anf-code-challenge/us/en/*/jcr:content)(resourceType=anf-code-challenge/components/page))"
                   
           })
@ServiceDescription("Listens Page Creation Event")
public class PageCreateListener implements EventHandler {

	/**
     * Loggers
     */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Dynamic References
     */
    @Reference
    ResourceResolverFactory resourceResolverFactory;
    
    /**
     * Constants
     */
    private static final String PN_PAGE_CREATED = "pageCreated";

    
    /**
     * Business Logic on Event Trigger.
     */
    public void handleEvent(final Event event) {
    	LOGGER.debug("Resource event: {} at: {}", event.getTopic(), event.getProperty(SlingConstants.PROPERTY_PATH));
    	ResourceResolver resourceResolver = null;
    	try {
    		//Using this to avoid creating System User & its dependencies, as its just an excercise & not used at multiple places.
			resourceResolver = this.resourceResolverFactory.getAdministrativeResourceResolver(null);
			Resource resource = resourceResolver.getResource(event.getProperty(SlingConstants.PROPERTY_PATH).toString());
			
			if(Objects.nonNull(resource)) {
				ModifiableValueMap mValueMap = resource.adaptTo(ModifiableValueMap.class);
				mValueMap.put(PN_PAGE_CREATED, Boolean.TRUE);
				
				resourceResolver.commit();
				LOGGER.info("Property Updated at :: {}", event.getProperty(SlingConstants.PROPERTY_PATH));
			}
			
		} catch (LoginException | PersistenceException e) {
			LOGGER.error("Exception while Fetching Resource ::  {}", e.getMessage(), e);
		} finally {
			resourceResolver.close();
		}
    }
    
}

