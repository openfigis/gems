package org.fao.fi.gems.feature;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

import javax.annotation.Priority;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.settings.data.GeographicServerSettings;
import org.fao.fi.gems.model.settings.data.filter.ExtraDataFilter;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.message.GZipEncoder;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * A WFS Feature Client to use as basis for calculating geo-properties of future
 * GEMS products. Such client implements the GeoAPI and allows to retrieve data
 * as list of {@link org.opengis.feature.Feature}
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class WfsFeatureClient extends FeatureClientImpl{

	public static enum WfsVersion {
	
		v100("1.0.0"),
		v110("1.1.0");
	
		private final String value;
	
		WfsVersion(String value) {
			this.value=value;
		}

		public String value() {
			return value;
		}
	
		public boolean before(WfsVersion v) {
			return this.ordinal()<v.ordinal();
		}
	}
	
	private String typeName;
	
	private WfsVersion serviceVersion;

	private Client client;
	
	private List<Feature> features;
	
	/**
	 * Constructs a WfsFeatureClient
	 * 
	 * @param version
	 * @param config
	 * @param codeStack
	 * @throws Exception
	 */
	public WfsFeatureClient(WfsVersion version, GemsConfig config,
			List<EntityCode> codeStack) throws Exception {
		
		super(config, codeStack);

		configure(version, config, codeStack);
	}
	

	@Override
	public List<Feature> features() {
		return features;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Configures the WFSFeatureClient and executes the request to get the list
	 * of Feature
	 * 
	 * @param config
	 * @param method
	 * @param codeStack
	 * @throws Exception 
	 */
	private void configure(WfsVersion version, GemsConfig config, List<EntityCode> codeStack) throws Exception{
		
		serviceVersion = version;
		typeName = config.getSettings().getGeographicServerSettings().getSourceLayer();
		client = configureClient();
		
		FeatureType type = describeFeatureType();
		
		if(type != null){
			features = getFeatures(type);
		}
		
	}
	
	/**
	 * Constructs a basic client
	 * 
	 * @return
	 */
	private Client configureClient(){
		Client client = newClient()
				.register(ContentTypeFixer.instance)
				.register(GZipEncoder.class)
				.register(EncodingFilter.class);
		
		Logger.getLogger("org.geotoolkit.feature.xml").setLevel(Level.OFF);

		return client;
	}
	
	/**
	 * Builds a WFS request
	 * 
	 * @param request
	 * @return
	 */
	private WebTarget configureRequest(String request){
		
		GeographicServerSettings settings = config.getSettings().getGeographicServerSettings();
		
		String serviceUrl = settings.getPublicUrl() + "/" + settings.getSourceWorkspace() + "/ows";
		
		WebTarget target = client.target(serviceUrl)
								.queryParam("service","wfs")
								.queryParam("version",serviceVersion.value())
								.queryParam("request", request)
								.queryParam("typeName", typeName);

		if(request.equals("GetFeature")){
			//add cql filter
			String cqlFilter = null;
			if(codeStack.size() > 0){
			
				cqlFilter = "";
				for(int i=0;i<codeStack.size();i++){
					EntityCode ec = codeStack.get(i);
					String filterCode = ec.getCode();
					if(ec.getFilter().getIsString()) filterCode = "'"+filterCode+"'";
					cqlFilter += ec.getFilter().getProperty() + "=" + filterCode;
					if(i<codeStack.size()-1) cqlFilter += " AND ";
				}
				cqlFilter = cqlFilter.replaceAll(" ", "%20");
			}
			
			List<ExtraDataFilter> extraFilters = settings.getFilters().getExtras();
			if(extraFilters.size() > 0){
				boolean hasFilter = cqlFilter != null;
				if(!hasFilter){
					cqlFilter = "";
				}
				for(int i=0;i<extraFilters.size();i++){
					ExtraDataFilter ef = extraFilters.get(i);
					String efCode = ef.getValue();
					if(ef.getIsString()) efCode = "'"+efCode+"'";
					
					if(hasFilter || (!hasFilter && i > 0)) cqlFilter += " AND ";
					cqlFilter += ef.getProperty()+"="+efCode;
				}
				cqlFilter = cqlFilter.replaceAll(" ", "%20");
			}
			
			if(cqlFilter != null) target = target.queryParam("cql_filter", cqlFilter);
		}
		
		return target;
	}
	
	/**
	 * Describes the feature type for which data will be retrieved
	 * 
	 * @return the feature type
	 * @throws Exception
	 */
	private FeatureType describeFeatureType() throws Exception{
		
		FeatureType featureType = null;
		
		Builder builder = configureRequest("DescribeFeatureType").request();
		
		Map<String,List<? extends FeatureType>> map = new HashMap<>();
		List<? extends FeatureType> types = map.get(typeName);
		
		JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
		if (types==null) {
			
			try {
				
				String schema = builder.get(String.class);
			
				types = reader.read(schema);
				
			}
			catch(Exception e) {
				throw new Exception("cannot acquire schemas for "+typeName,e);
			}
			
			map.put(typeName, types);
		}

		for(FeatureType type : types){
			if(typeName.contains(type.getName().toString())){
				featureType = type;
				break;
			}
		}
		
		return featureType;
		
	}
	
	/**
	 * Get the list of features
	 * 
	 * @param type a {org.opengis.feature.FeatureType}
	 * @return a list of {@link org.opengis.feature.Feature}
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Feature> getFeatures(FeatureType type) throws Exception{
		
		List<Feature> features;
		
		try{
			Builder request = configureRequest("GetFeature").request();
			
			JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader((List) Arrays.asList(type));
			
			InputStream stream =  request.get(InputStream.class);
	
			Collection<Feature> collection = (Collection<Feature>) reader.read(stream);
		
			features = new ArrayList<Feature>(collection);
			
		}catch(Exception e){
			throw new Exception("Impossible to get features for type "+typeName, e);
		}
		
		return features;
	}

	
	//fixes ogc illegal content subtype
	@Priority(Integer.MIN_VALUE)
	private static class ContentTypeFixer implements ClientResponseFilter  {
		
		static final ContentTypeFixer instance = new ContentTypeFixer();
		
		@Override
		public void filter(ClientRequestContext requestContext,
				ClientResponseContext responseContext) throws IOException {
			
			if (responseContext.getHeaderString(CONTENT_TYPE).contains("subtype=gml"))
				responseContext.getHeaders().put(CONTENT_TYPE, asList("text/xml"));
			
		}
	}

}
