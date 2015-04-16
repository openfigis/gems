package org.fao.fi.gems.application;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fao.fi.gems.codelist.CodelistParser;
import org.fao.fi.gems.feature.FeatureTypeProperty;
import org.fao.fi.gems.feature.FeatureUtils;
import org.fao.fi.gems.feature.WfsFeatureClient;
import org.fao.fi.gems.feature.WfsFeatureClient.WfsVersion;
import org.fao.fi.gems.entity.EntityAddin;
import org.fao.fi.gems.entity.FigisGeographicEntityImpl;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.metaobject.FigisGeographicMetaObjectImpl;
import org.fao.fi.gems.metaobject.GeographicMetaObject;
import org.fao.fi.gems.metaobject.GeographicMetaObjectImpl;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.settings.data.GeoWorkerInstance;
import org.fao.fi.gems.model.settings.publication.EntityList;
import org.fao.fi.gems.publisher.Publisher;
import org.fao.fi.gems.util.Utils;
import org.opengis.feature.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main App to launch the batch data/metadata publication
 * 
 */
public class Gems {

	private static Logger LOGGER = LoggerFactory.getLogger(Gems.class);

	static Set<GeographicEntity> set = null;
	
	
	/**
	 * Main Gems application
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		//Read the configuration
		LOGGER.info("(1) Loading the configuration file");
		GemsConfig config = GemsConfig.fromXML(new File(args[0]));
	
		//read the codelists
		LOGGER.info("(2) Loading the reference list");
		String owner = Utils.whoIsOwner(config);
		
		LOGGER.info("Owner = "+owner);
		String collectionType = config.getSettings().getPublicationSettings().getCollectionType();
		LOGGER.info("Collection type = "+collectionType);
		String codelistUrl = config.getSettings().getPublicationSettings().getCodelistURL().replaceAll("&amp;","&");
		LOGGER.info("Codelist URL = "+codelistUrl);
		
		EntityList entities = config.getSettings().getPublicationSettings().getEntities();
		if(entities != null){
			List<String> include = entities.getInclude();
			List<String> exclude = entities.getExclude();
			
			if(include != null){
				if(include.size() > 0){
					LOGGER.info("Publication Scope = SUBSET");
					LOGGER.info("List of entities = "+include.toString());
				}else{
					if(exclude != null){
						if(exclude.size() == 0){
							LOGGER.info("Publication Scope = COMPLETE");
						}
					}
				}
			}
				
			if(exclude != null){
				if(exclude.size() > 0){
					LOGGER.info("Publication Scope = SUBSET");
					LOGGER.info("List of entities = "+exclude.toString());
				}else{
					if(include != null){
						if(include.size() == 0){
							LOGGER.info("Publication Scope = COMPLETE");
						}
					}
				}
			}
			
		}
		
		
		//load the codelist parser
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		Class<?> parserClass = loader.loadClass(config.getSettings().getPublicationSettings().getCodelistParser());
		CodelistParser codelistParser = (CodelistParser) parserClass.newInstance();
		set = codelistParser.getCodelist(config);
		LOGGER.info("Codelist parser = "+config.getSettings().getPublicationSettings().getCodelistParser());
			
		// configure the publisher
		Publisher publisher = new Publisher(config);

		int size = 0;

		List<String> failures = new ArrayList<String>();

		//action
		String action = config.getSettings().getPublicationSettings().getAction();
		LOGGER.info("== ACTION: "+action+" == ");
		
		// iteration on the entities
		LOGGER.info("(3) Start metadata creation & publication");
		LOGGER.info("Nb of expected publications = " + set.size());
		Iterator<GeographicEntity> entityIterator = set.iterator();
		try{
			while (entityIterator.hasNext()) {
	
				GeographicEntity entity = entityIterator.next();
				LOGGER.info("==============");
				LOGGER.info(action+" single layer & metadata for: "+entity.code()+ " ("+entity.refName()+")");
	
				Map<FeatureTypeProperty, Object> geoproperties = null;
				
				Integer featureCount = 0;
				
				// calculate geoproperties & feature count
				if (action.matches("PUBLISH")){
					int i = 1;
					while (geoproperties == null && i <= 3) {
						try{
							LOGGER.info("Calculating geoproperties - Attempt "+i);
							
							WfsFeatureClient wfs = new WfsFeatureClient(WfsVersion.v100, config, entity.codeStack());
							List<Feature> features = wfs.features();
							geoproperties = FeatureUtils.computeFeatureTypeProperties(
												features, config.getSettings().getPublicationSettings().getBuffer(),
												config.getSettings().getGeographicServerSettings().getTimeDimension());
						
						}catch(Exception e){
							LOGGER.info("Failed to calculate geoproperties at attempt "+i);
						}finally{
							if(geoproperties != null){
								featureCount = (Integer) geoproperties.get(FeatureTypeProperty.COUNT);
								break;
							}
						}
						i++;
					}
					
					if(geoproperties == null || featureCount == 0){
						continue;
					}
				}
					
				
				//pass specific properties to config
				Iterator<GeographicMetaObjectProperty> it = entity.properties().keySet().iterator();
				while(it.hasNext()){
					
					GeographicMetaObjectProperty property = it.next();
					
					Object obj = property.getObject();
					if(obj == EntityAddin.ABSTRACT){
						String abstractText = entity.properties().get(property).get(0);
						config.getContent().setAbstract(abstractText);
					
					} else if(obj == EntityAddin.BASETITLE) {
						String basetitleText = entity.properties().get(property).get(0);
						config.getContent().setBaseTitle(basetitleText);
					}
					
				}
				
				//configure entity
				GeographicMetaObject metaObject = null;
				if(entity instanceof FigisGeographicEntityImpl){
					metaObject = new FigisGeographicMetaObjectImpl(Arrays.asList(entity), null, geoproperties, config);
				}else{
					metaObject = new GeographicMetaObjectImpl(Arrays.asList(entity), null, geoproperties, config);
				}
				
				//style
				String style = config.getSettings().getPublicationSettings().getStyle();
				if(style == "") style = null;
				
				if(style == null){
					Iterator<GeographicMetaObjectProperty> it2 = entity.properties().keySet().iterator();
					while(it2.hasNext()){
						GeographicMetaObjectProperty property = it2.next();
						Object obj = property.getObject();
						if(obj == EntityAddin.STYLE){
							style = entity.properties().get(property).get(0);
							break;
						}
					}
				}
				
				if(style != null){
					LOGGER.debug(style);
				}else{
					LOGGER.warn("Applying default style");
					style = "polygon";
				}
				
				// PUBLISH ACTION
				//===============
				if (action.matches("PUBLISH") && featureCount > 0) {
					
					boolean published = false;
					try {
						published = publisher.publish(metaObject, style);
						if(published){
							size = size + 1;	
							LOGGER.info(size + " published metalayers");
						}
					}catch(Exception e){
						LOGGER.info("Failed to publish layer/metadata pair for " + entity.code());
						failures.add(entity.code());
					}
	
				// UNPUBLISH ACTION
				//=================
				}else if (action.matches("UNPUBLISH")) {
					
					boolean unpublished = false;
					try {
						unpublished = publisher.unpublish(metaObject, style);
						if(unpublished){
							size = size + 1;	
							LOGGER.info(size + " unpublished metalayers");
						}
					}catch(Exception e){
						LOGGER.info("Failed to unpublish layer/metadata pair for " + entity.code());
						failures.add(entity.code());
					}
				}
			}
		} finally {
			LOGGER.info("GEMS finished to "+action.toLowerCase()+ " layer/metadata pairs");
			List<GeoWorkerInstance> workers = config.getSettings().getGeographicServerSettings().getInstances().getWorkers();
			if(workers != null){
				if(workers.size() > 0){
					LOGGER.info("List of geoserver workers (cluster members) detected");
					LOGGER.info("Reloading geoserver worker's in-memory catalog");
					boolean reload = publisher.getDataPublisher().reloadWorkerCatalogs();
					if(reload){
						LOGGER.info("Successfull reload of geoserver worker in-memory catalogs");
					} else{
						LOGGER.info("Some catalog reload failed! check above log for details");
					}
				}
			}
			
			LOGGER.info("GEMS publication complete!");
		}
		
		if(failures.size() > 0) LOGGER.info("== GEMS FAILURES ==");
		Iterator<String> failuresIt = failures.iterator();
		while(failuresIt.hasNext()){
			LOGGER.info(failuresIt.next());
		}
		
	}
}
