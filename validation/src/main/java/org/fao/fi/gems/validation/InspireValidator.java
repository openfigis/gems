/**
 * (c) 2015 FAO / UN (project: gems-validation)
 */
package org.fao.fi.gems.validation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.annotation.Priority;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

/**
 * Inspire metadata validator
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class InspireValidator {

	static final String INSPIRE_METADATA_ENDPOINT = "http://inspire-geoportal.ec.europa.eu/GeoportalProxyWebServices/resources/INSPIREResourceTester";

	Client inspireResourceTester;
	
	/**
	 * Constructor for InspireValidator
	 * 
	 * @throws MalformedURLException
	 */
	public InspireValidator() throws MalformedURLException {		
		configure();
	}
	
	/**
	 * Validates an ISO/OGC metadata provided as string
	 * 
	 * @param resourceDescriptorText
	 * @return
	 */
	public Response validate(String resourceDescriptorText) {
		return validate(resourceDescriptorText, null);
	}

	/**
	 * Validates an ISO/OGC metadata provided as File
	 * 
	 * @param resourceDescriptorFile
	 * @return
	 */
	public Response validate(File resourceDescriptorFile) {
		return validate(null, resourceDescriptorFile);

	}

	/**
	 * Validates an ISO/OGC metadata provided as String or File
	 * 
	 * @param resourceDescriptorText
	 * @param resourceDescriptorFile
	 * @return
	 */
	private Response validate(String resourceDescriptorText, File resourceDescriptorFile) {
		
		Response response = null;
		
		WebTarget target = this.inspireResourceTester.target(INSPIRE_METADATA_ENDPOINT);

		Builder builder = target.request();
		
		if (resourceDescriptorFile != null) {
			response = builder.post(Entity.entity(resourceDescriptorFile, MediaType.TEXT_PLAIN));
		}

		if (resourceDescriptorText != null) {
			response = builder.post(Entity.entity(resourceDescriptorText, MediaType.TEXT_PLAIN));
		}
		
		return response;
	}
	
	/**
	 * Indicates the metadata is valid according to INSPIRE metadata specifications
	 * 
	 * @param response
	 * @return true if valid, false otherwise
	 */
	public boolean isValid(Response response){
		return response.getLocation().getPath().endsWith("resourceReport")? false : true;
	}
	
	/**
	 * Get the INSPIRE validation report
	 * 
	 * @param reponse
	 * @return the report
	 * @throws Exception 
	 */
	public InspireValidationReport getReport(Response response) throws Exception{
		return InspireValidationReport.fromXML((InputStream) response.getEntity());
	}

	////////////////////////////////////////////////////////////////////////

	/**
	 * Configures the Inspire Validator
	 * 
	 */
	private void configure(){
		inspireResourceTester = newClient()
								.register(ContentTypeFixer.instance);
	}
	
	//fixes ogc illegal content subtype
	@Priority(Integer.MIN_VALUE)
	private static class ContentTypeFixer implements ClientResponseFilter  {
		
		static final ContentTypeFixer instance = new ContentTypeFixer();
		
		@Override
		public void filter(ClientRequestContext requestContext,
				ClientResponseContext responseContext) throws IOException {
			responseContext.getHeaders().put(CONTENT_TYPE, asList("application/xml"));
			
		}
	}
}
