/**
 * (c) 2015 FAO / UN (project: gems-collection)
 */
package org.fao.fi.gems.collection.parsers;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fao.fi.gems.codelist.CodelistParser;
import org.fao.fi.gems.collection.properties.VmeProperty;
import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.entity.FigisGeographicEntityImpl;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.GemsConfig;
import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;
import org.fao.fi.gems.util.Utils;

/**
 * Vme global type codelist parser
 * (reference for publishing Metadata at global type resolution)
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class VmeGlobalCodelistParser implements CodelistParser{

	@Override
	public Set<GeographicEntity> getCodelist(GemsConfig config) {
		
		Set<GeographicEntity> vmeCodelist = new HashSet<GeographicEntity>();
		
		String owner = Utils.whoIsOwner(config);
		
		Map<String,String> globalTypes = new HashMap<String,String>();
		globalTypes.put("VME", "VME closed areas");
		globalTypes.put("BTM_FISH","Bottom fishing areas");
		globalTypes.put("OTHER", "Other areas");
		
		Iterator<String> it = globalTypes.keySet().iterator();
		while(it.hasNext()){
			
			//configure CodeStack
			String globalType = it.next();
			List<DataObjectFilter> filters = config.getSettings().getGeographicServerSettings().getFilters().getData();
			
			DataObjectFilter typeFilter = filters.get(0);
			EntityCode typeEntityCode = new EntityCode(typeFilter, globalType);
			
			DataObjectFilter ownerFilter = filters.get(1);
			EntityCode rfmoEntityCode = new EntityCode(ownerFilter, owner);
			
			List<EntityCode> vmeCodeStack = Arrays.asList(typeEntityCode, rfmoEntityCode);
			
			//wrapEntity by default is true
			//if there is a list of subset then wrap entity only for those ones
			boolean wrapEntity = Utils.wrapEntity(config, globalType);
			
			if(wrapEntity){
				
				//title
				String globalName = globalTypes.get(globalType);
				String title = globalName + " related to UNGA Res. 61-105 (" + owner +")";
				
				//style
				String style = "MEASURES_" + globalType + "_for_" + owner;
				
				//configure geographic entity	
				FigisGeographicEntityImpl entity = null;
				try {
					boolean addEntity = true;
					for(GeographicEntity vmeEntity : vmeCodelist){
						if(vmeEntity.code().matches(globalType)){
							addEntity = false;
							break;
						}
					}
					
					if(addEntity){
						Map<GeographicMetaObjectProperty, List<String>> properties = new HashMap<GeographicMetaObjectProperty, List<String>>();
						properties.put(VmeProperty.VME, Arrays.asList(globalType, globalName));
						
						//add global type (used for the mapviewer link)
						properties.put(VmeProperty.GLOBALTYPE, Arrays.asList(globalType));
						
						//add style
						properties.put(VmeProperty.STYLE, Arrays.asList(style));
						
						String collection = config.getSettings().getPublicationSettings().getCollectionType();
						entity = new FigisGeographicEntityImpl(owner, collection, vmeCodeStack, title, properties);
						entity.setFigisDomain("vme");
						vmeCodelist.add(entity);
					}
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				
			}
			
		}	

		return vmeCodelist;
		
	}

}
