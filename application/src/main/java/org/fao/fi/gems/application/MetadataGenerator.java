package org.fao.fi.gems.application;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fao.fi.gems.codelist.CodelistParser;
import org.fao.fi.gems.feature.FeatureTypeProperty;
import org.fao.fi.gems.entity.EntityAddin;
import org.fao.fi.gems.entity.FigisGeographicEntityImpl;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.metaobject.FigisGeographicMetaObjectImpl;
import org.fao.fi.gems.metaobject.GeographicMetaObject;
import org.fao.fi.gems.metaobject.GeographicMetaObjectImpl;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.MetadataConfig;
import org.fao.fi.gems.model.content.MetadataContact;
import org.fao.fi.gems.publisher.Publisher;
import org.fao.fi.gems.util.FeatureTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main App to launch the batch data/metadata publication
 * 
 */
public class MetadataGenerator {

	private static Logger LOGGER = LoggerFactory.getLogger(MetadataGenerator.class);

	static Set<GeographicEntity> set = null;
	
	
	/**
	 * Main
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		//Read the configuration
		LOGGER.info("(1) Loading the configuration file");
		MetadataConfig config = MetadataConfig.fromXML(new File(args[0]));
		
		//read the codelists
		LOGGER.info("(2) Loading the reference list");
		String owner = null;
		for(MetadataContact organization : config.getContent().getOrganizationContacts()){
			if(organization.getRole().matches("OWNER")){
				owner = organization.getAcronym();
				break;
			}
		}
		LOGGER.info("Owner = "+owner);
		String collectionType = config.getSettings().getPublicationSettings().getCollectionType();
		LOGGER.info("Collection type = "+collectionType);
		String codelistUrl = config.getSettings().getPublicationSettings().getCodelistURL().replaceAll("&amp;","&");
		LOGGER.info("Codelist URL = "+codelistUrl);
		
		//load the codelist parser
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		Class<?> parserClass = loader.loadClass(config.getSettings().getPublicationSettings().getCodelistParser());
		CodelistParser codelistParser = (CodelistParser) parserClass.newInstance();
		set = codelistParser.getCodelist(owner, collectionType, codelistUrl);
		LOGGER.info("Codelist parser = "+config.getSettings().getPublicationSettings().getCodelistParser());
		
		//only for test (apply to the first element)
		if(config.getSettings().getPublicationSettings().isTest()){
			GeographicEntity testEntity = null;
			Iterator<GeographicEntity> it = set.iterator();
			while(it.hasNext()){
				GeographicEntity ent = it.next();
				String code = ent.getCode();
				if(code.matches(config.getSettings().getPublicationSettings().getTestCode())){
					testEntity = ent;
					break;
				}
			}
			set = new HashSet<GeographicEntity>();
			set.add(testEntity);
			LOGGER.info("Testing with "+testEntity.getCode());
		}
			
		// configure the publisher
		Publisher publisher = new Publisher(config);

		int size = 0;

		List<String> existingLayers = publisher.getDataPublisher().GSReader
				.getLayers().getNames(); // read once, improve performance

		// iteration on the entities
		LOGGER.info("(3) Start metadata creation & publication");
		Iterator<GeographicEntity> entityIterator = set.iterator();
		while (entityIterator.hasNext()) {

			GeographicEntity entity = entityIterator.next();
			LOGGER.info("==============");
			LOGGER.info("Publishing single layer & metadata for: "+entity.getCode()+ " ("+entity.getRefName()+")");

			Map<FeatureTypeProperty, Object> geoproperties = null;
			
			String targetLayer = config.getSettings().getGeographicServerSettings().getTargetLayerPrefix() + "_" + entity.getCode();
			boolean exist = existingLayers.contains(targetLayer);
			
			String action = config.getSettings().getPublicationSettings().getAction();
			LOGGER.info("== ACTION: "+action+" == ");
				
			// calculate geoproperties
			if (action.matches("PUBLISH")){
				while (geoproperties == null) {
					geoproperties = FeatureTypeUtils
							.computeFeatureTypeProperties(
									config.getSettings().getGeographicServerSettings(),
									entity.getCode(),
									config.getSettings().getPublicationSettings().getBuffer());
				}
			}
				
			Integer featureCount = 0;
			if(action.matches("PUBLISH")) {
				featureCount = (Integer) geoproperties.get(FeatureTypeProperty.COUNT);
			}
			if(featureCount == 0) geoproperties = null;
			
			//abstract
			Iterator<GeographicMetaObjectProperty> it = entity.getSpecificProperties().keySet().iterator();
			while(it.hasNext()){
				GeographicMetaObjectProperty property = it.next();
				Object obj = property.getObject();
				if(obj == EntityAddin.ABSTRACT){
					String abstractText = entity.getSpecificProperties().get(property).get(0);
					config.getContent().setAbstract(abstractText);
					break;
				}
			}
			
			//configure entity
			GeographicMetaObject metaObject = null;
			if(entity instanceof FigisGeographicEntityImpl){
				metaObject = new FigisGeographicMetaObjectImpl(Arrays.asList(entity), null, geoproperties, config);
			}else{
				metaObject = new GeographicMetaObjectImpl(Arrays.asList(entity), null, geoproperties, config);
			}
			
			// PUBLISH ACTION
			if (action.matches("PUBLISH") && featureCount > 0) {
				String style = config.getSettings().getPublicationSettings().getStyle();
				
				if(style == null){
					Iterator<GeographicMetaObjectProperty> it2 = entity.getSpecificProperties().keySet().iterator();
					while(it2.hasNext()){
						GeographicMetaObjectProperty property = it2.next();
						Object obj = property.getObject();
						if(obj == EntityAddin.STYLE){
							style = entity.getSpecificProperties().get(property).get(0);
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
				
				publisher.publish(metaObject, style, exist);
				size = size + 1;	
				LOGGER.info(size + " published metalayers");
			
			// UNPUBLISH ACTION
			}else if (action.matches("UNPUBLISH")) {
				publisher.unpublish(metaObject, exist);
			}
		}
	}
}
