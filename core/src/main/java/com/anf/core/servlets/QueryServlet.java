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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.google.gson.JsonObject;

/**
 * @author kishore
 * 
 * Servlet to Query Repository & Return Response based on Business Rules.
 *
 */
@Component(service = { Servlet.class })
@SlingServletPaths(
        value = "/bin/queryRepo"
)
public class QueryServlet extends SlingSafeMethodsServlet {

    /**
     * SerialVersion
     */
    private static final long serialVersionUID = 1L;

    /**
     * Dynamic References
     */
    @Reference
    private QueryBuilder queryBuilder;
    
	/**
	 * Constants
	 */
	private static final String ROOT_PATH = "/content/anf-code-challenge/us/en";


	private static final String PN_JCR_CREATED = "@jcr:content/jcr:created";
	private static final String PN_ANF_CC = "@jcr:content/anfCodeChallenge";

	private static final String PARAM_QUERY_TYPE = "queryType";
	private static final String KEY_MESSAGE = "message";
	private static final String TYPE_PAGE = "cq:Page";
	private static final String SORT_ASC = "asc";
	private static final int LIMIT = 10;
	
	private static final String VAL_EXISTS = "exists";
	private static final String VAL_JCR = "jcr";
	
	private static final String SQL_STMT = "SELECT * FROM [cq:Page] AS s WHERE ISDESCENDANTNODE([/content/anf-code-challenge/us/en]) AND [jcr:content/anfCodeChallenge] IS NOT NULL ORDER BY [jcr:content/jcr:created] ASC";


    /**
     * Implementation to Query Repository & Return Response based on Business Rules.
     */
    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
    	String response = "No Records Fetched!";
    	String queryType = req.getParameter(PARAM_QUERY_TYPE);
    	
    	if(queryType.equalsIgnoreCase(VAL_JCR)) {
    		response = getJcrSQLResponse(req.getResourceResolver().adaptTo(Session.class));
    	}else {
    		response = getSlingQBResponse(req.getResourceResolver().adaptTo(Session.class));
    	}
    	
    	resp.getWriter().write(response);
    }

    
	/**
	 * @param session
	 * @return
	 */
	private String getSlingQBResponse(Session session) {
    	JsonObject resultJson = new JsonObject();
    	
		 // Creating the predicates for the query using a map object
        Map<String, String> predicates = new HashMap<>();
        predicates.put("type", TYPE_PAGE);
        predicates.put("path", ROOT_PATH);
        predicates.put("property", PN_ANF_CC);
        predicates.put("property.operation", VAL_EXISTS);
        predicates.put("orderby", PN_JCR_CREATED);
        predicates.put("orderby.sort", SORT_ASC);
        predicates.put("p.limit", Integer.toString(LIMIT));

        // Creating the query instance
        com.day.cq.search.Query query = queryBuilder.createQuery(PredicateGroup.create(predicates), session);

        // Getting the results
        SearchResult searchResult = query.getResult();
        Iterator<Resource> resources = searchResult.getResources();
        
        resultJson.addProperty(KEY_MESSAGE, "Sourced by Sling QueryBuilder Predicates");
        while(resources.hasNext()) {
        	Resource resource = resources.next();
			resultJson.addProperty(resource.getName(), resource.getPath());
        }
		return resultJson.toString();
	}


	/**
	 * @param session
	 * @return
	 */
	private String getJcrSQLResponse(Session session) {
    	JsonObject resultJson = new JsonObject();
    	
		try {
    		QueryManager queryManager = session.getWorkspace().getQueryManager();
    		Query query = queryManager.createQuery(SQL_STMT, Query.JCR_SQL2);
    		query.setLimit(LIMIT);

    		QueryResult result = query.execute();
    		NodeIterator nodeIter = result.getNodes();
    		
    		resultJson.addProperty(KEY_MESSAGE, "Sourced by JCR SQL-2 Query");
    		while (nodeIter.hasNext() ) {
    			Node node = nodeIter.nextNode();
    			resultJson.addProperty(node.getName(), node.getPath());
    		}

    	} catch (RepositoryException e) {
    		resultJson.addProperty(KEY_MESSAGE, "Exception While Fetching Response from JCR SQL2 Query ::" + e.getMessage());
    	}
		
		return resultJson.toString();
	}

}
