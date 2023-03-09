package com.anf.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class NewsFeedModelTest {

	private final AemContext context = new AemContext();

	@BeforeEach
	void setUp() throws Exception {
		context.addModelsForClasses(NewsFeedModel.class);
		context.load().json("/com/anf/core/models/NewsFeedModelTest.json", "/varProducts");
	}

	@Test
	void testInit() {
		NewsFeedModel newsFeedModel = context.registerService(new NewsFeedModel());
		newsFeedModel.init();
	}

	@Test
	void testGetNewsFeedList() {
		 Resource resource = context.currentResource("/varProducts/newsData");
		 NewsFeedModel newsFeedModel = context.registerService(new NewsFeedModel());
		 newsFeedModel.newsFeedResource = resource;
		 assertEquals(2, IteratorUtils.toList(newsFeedModel.getNewsFeedList()).size());
	}

	@Test
	void testGetCurrentDate() {
		NewsFeedModel newsFeedModel = context.registerService(new NewsFeedModel());
		assertEquals(newsFeedModel.currentDate, newsFeedModel.getCurrentDate());
	}

}
