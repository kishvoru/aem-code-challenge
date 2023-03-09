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
package com.anf.core.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ResourcePath;

/**
 * @author kishore
 *
 */
@Model(adaptables = Resource.class)
public class NewsFeedModel {

	/**
	 * Dynamic References
	 */
	@ResourcePath(path="/var/commerce/products/anf-code-challenge/newsData")
	Resource newsFeedResource;
	
	/**
	 * Constants
	 */
	private static final String DATE_PATTERN = "dd.MM.yyyy";
	
	/**
	 * Variables
	 */
	Iterator<Resource> newsFeedList;
	String currentDate;

	/**
	 * Initializations
	 */
	@PostConstruct
	protected void init() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_PATTERN);  
		LocalDateTime now = LocalDateTime.now();  
		currentDate = dtf.format(now);
	}

	/**
	 * @return
	 */
	public Iterator<Resource> getNewsFeedList() {
		if(Objects.nonNull(newsFeedResource))
			newsFeedList = newsFeedResource.listChildren();
		
		return newsFeedList;
	}
	
	/**
	 * @return
	 */
	public String getCurrentDate() {
		return currentDate;
	}

}
