package org.fao.fi.gems.publisher;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore.DBType;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder21;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.authorityurl.GSAuthorityURLInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import it.geosolutions.geoserver.rest.encoder.identifier.GSIdentifierInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.Presentation;
import it.geosolutions.geoserver.rest.encoder.metadata.GSFeatureDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.virtualtable.GSVirtualTableEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.virtualtable.VTGeometryEncoder;
import it.geosolutions.geoserver.rest.encoder.metadatalink.GSMetadataLinkInfoEncoder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.fao.fi.gems.entity.EntityAuthority;
import org.fao.fi.gems.entity.EntityCode;
import org.fao.fi.gems.entity.GeographicEntity;
import org.fao.fi.gems.metaobject.GeographicMetaObject;
import org.fao.fi.gems.metaobject.GeographicMetaObjectProperty;
import org.fao.fi.gems.model.settings.data.GeoMasterInstance;
import org.fao.fi.gems.model.settings.data.GeoWorkerInstance;
import org.fao.fi.gems.model.settings.data.GeographicServerSettings;
import org.fao.fi.gems.model.settings.data.GemsMethod;
import org.fao.fi.gems.model.settings.data.dimension.TimeDimension;
import org.fao.fi.gems.model.settings.data.filter.DataObjectFilter;
import org.fao.fi.gems.model.settings.data.filter.ExtraDataFilter;
import org.fao.fi.gems.model.settings.data.filter.FilterList;
import org.fao.fi.gems.model.settings.metadata.MetadataCatalogueSettings;
import org.fao.fi.gems.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Envelope;

/**
 * DataPublisher
 * 
 * @author eblondel (FAO)
 * 
 */
public class DataPublisher {

	private static Logger LOGGER = LoggerFactory.getLogger(DataPublisher.class);
	
	GeoMasterInstance master;
	List<GeoWorkerInstance> workers;

	public GeoServerRESTReader GSReader;
	public GeoServerRESTPublisher GSPublisher;
	
	String srcLayer;
	FilterList srcFilters;
	
	String trgWorkspace;
	String trgDatastore;
	String trgLayerPrefix;
	
	TimeDimension timeDimension;
	
	List<String> existingLayers;
	
	String geonetworkBaseURL;


	/**
	 * Data publisher
	 * 
	 * @param settings
	 * @param catalogueSettings
	 * @throws MalformedURLException
	 */
	public DataPublisher(GeographicServerSettings settings, MetadataCatalogueSettings catalogueSettings)
			throws MalformedURLException {

		this.master = settings.getInstances().getMaster();
		this.workers = settings.getInstances().getWorkers();

		this.GSReader = new GeoServerRESTReader(master.getUrl(), master.getUser(), master.getPassword());
		this.GSPublisher = new GeoServerRESTPublisher(master.getUrl(), master.getUser(), master.getPassword());

		this.srcLayer = settings.getSourceLayer();
		this.srcFilters = settings.getFilters();
		
		this.trgWorkspace = settings.getTargetWorkspace();
		this.trgDatastore = settings.getTargetDatastore();
		this.trgLayerPrefix = settings.getTargetLayerPrefix();
		
		this.timeDimension = settings.getTimeDimension();
		
		this.existingLayers = GSReader.getLayers().getNames();

		this.geonetworkBaseURL = catalogueSettings.getUrl();
		
	}

	
	/**
	 * Get list of existing layers
	 * 
	 * @return a list of layer names
	 */
	public List<String> getExistingLayers(){
		return this.existingLayers;
	}
	

	/**
	 * Check layer existence
	 * 
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public boolean checkLayerExistence(GeographicMetaObject object) {
		
		String layername = object.targetLayerName();
		List<String> layers = this.getExistingLayers();
		
		boolean existingLayer = false;
		if(layers.contains(layername)) existingLayer = true;
		return existingLayer;
	}

	
	/**
	 * Delete the layer
	 * 
	 * @param object
	 * @throws Exception
	 */
	public boolean deleteLayer(GeographicMetaObject object) throws Exception {
		String layername = object.targetLayerName();
		boolean existingLayer = this.checkLayerExistence(object);
		
		boolean deleted = false;
		if(existingLayer){
			try{
				deleted = GSPublisher.unpublishFeatureType(trgWorkspace, trgDatastore, layername);
			}catch(Exception e){
				throw new Exception("Fail to delete unpublish Feature Type / Layer resources", e);
			}
		}else{
			try{
				deleted = this.deleteOnlyFeatureType(object);
			} catch (Exception e){
				throw new Exception("Fail to delete only feature type",e);
			}
		}
		
		return deleted;
	}

	
	/**
	 * Delete only the feature Type
	 * 
	 * @param object
	 * @return
	 * @throws MalformedURLException
	 */
	public boolean deleteOnlyFeatureType(GeographicMetaObject object)
			throws MalformedURLException {
		URL deleteFtUrl = new URL(this.master.getUrl() + "/rest/workspaces/"
				+ this.trgWorkspace + "/datastores/" + this.trgDatastore
				+ "/featuretypes/" + object.targetLayerName());

		boolean ftDeleted = HTTPUtils.delete(deleteFtUrl.toExternalForm(),
				this.master.getUser(), this.master.getPassword());
		return ftDeleted;
	}

	/**
	 * Publish a layer (as GeoServer SQL View layer)
	 * 
	 * @param object
	 * @throws IOException 
	 */
	public boolean publishLayer(GeographicMetaObject object, String style, GemsMethod method, String shapefile) throws IOException {

		
		// Using geoserver-manager
		// -----------------------
		final GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
		fte.setProjectionPolicy(ProjectionPolicy.REPROJECT_TO_DECLARED);
		fte.setNativeName(object.targetLayerName());
		fte.setName(object.targetLayerName());
		fte.setTitle(object.metaTitle());
		fte.setSRS("EPSG:4326");
		fte.setNativeCRS("EPSG:4326");
		fte.setEnabled(true);

		for (Entry<GeographicMetaObjectProperty, List<String>> entry : object
				.properties().entrySet()) {
			if (entry.getKey().isAuthority() && entry.getKey().isThesaurus()) {
				if (!entry.getKey().containsURIs()) {
					for (String kw : entry.getValue()) {
						String keyword = kw + " ("
								+ ((EntityAuthority) entry.getKey().getObject()).name() + ")";
						fte.addKeyword(keyword);
					}
				}
			}
		}

		Envelope bbox = object.geographicExtentActual();
		if (bbox != null) {
			fte.setNativeBoundingBox(bbox.getMinX(), bbox.getMinY(),
					bbox.getMaxX(), bbox.getMaxY(), "EPSG:4326");
			fte.setLatLonBoundingBox(bbox.getMinX(), bbox.getMinY(),
					bbox.getMaxX(), bbox.getMaxY(), "EPSG:4326");
		} else {
			fte.setNativeBoundingBox(-180.0, -90.0, 180.0, 90.0, "EPSG:4326");
			fte.setLatLonBoundingBox(-180.0, -90.0, 180.0, 90.0, "EPSG:4326");
		}
		
		// virtual table (sql view)
		if(method == GemsMethod.SQLVIEW){
			
			//determine the geometry name
			String geometryName = "the_geom";
			DBType storeType = this.GSReader.getDatastore(this.trgWorkspace, this.trgDatastore).getType();
			if(storeType.name().matches("ORACLE")){
				LOGGER.info("Oracle datastore - set the_geom to uppercase");
				geometryName = geometryName.toUpperCase();
			}
			
			//configure the sql view
			VTGeometryEncoder gte = new VTGeometryEncoder(geometryName,
					"MultiPolygon", "4326");
			
			String sql = "SELECT ";
			String pf = "";
			List<String> propertyFilters = this.srcFilters.getProperties();
			if(propertyFilters.size() > 0){
				for(int i = 0;i<propertyFilters.size();i++){
					String p = propertyFilters.get(i);
					pf += p;
					if(i < propertyFilters.size()-1) pf += ",";
				}
			}else{
				pf = "*";
			}			
			sql += pf+" FROM " + this.srcLayer + " WHERE ";
			
			Iterator<GeographicEntity> it = object.entities().iterator();
			while(it.hasNext()){
				GeographicEntity ge = it.next();
				List<EntityCode> codestack = ge.codeStack();
				for(int i = 0; i<codestack.size();i++){
					EntityCode ec = codestack.get(i);
					String filterCode = ec.getCode();
					DataObjectFilter ecFilter = ec.getFilter();
					if(ecFilter.getIsString()) filterCode = "'"+filterCode+"'";
					sql += ecFilter.getProperty() + " = " + filterCode;
					if(i<codestack.size()-1) sql += " AND ";
				}
			}
			
			List<ExtraDataFilter> extras = this.srcFilters.getExtras();
			if(extras.size() > 0){
				for(int i=0;i<extras.size();i++){
					ExtraDataFilter ef = extras.get(i);
					String efCode = ef.getValue();
					if(ef.getIsString()) efCode = "'"+efCode+"'";
					
					if(object.entities().size() > 0 || (object.entities().size() == 0 && i > 0)) sql += " AND ";
					sql += ef.getProperty() + " = " + efCode;
				}
			}
			
			GSVirtualTableEncoder vte = new GSVirtualTableEncoder(
					object.targetLayerName(), sql, null, Arrays.asList(gte),
					null);
			fte.setMetadataVirtualTable(vte);
		}
			
		// metadata
		final GSMetadataLinkInfoEncoder mde1 = new GSMetadataLinkInfoEncoder(
				"text/xml", "ISO19115:2003", Utils.getXMLMetadataURL(
						this.geonetworkBaseURL, object.metaIdentifier()));
		final GSMetadataLinkInfoEncoder mde2 = new GSMetadataLinkInfoEncoder(
				"text/html", "ISO19115:2003", Utils.getHTMLMetadataURL(
						this.geonetworkBaseURL, object.metaIdentifier()));
		fte.addMetadataLinkInfo(mde1);
		fte.addMetadataLinkInfo(mde2);
		
		//time dimension
		if(this.timeDimension.getStartTime() != null){
			GSFeatureDimensionInfoEncoder time = new GSFeatureDimensionInfoEncoder(this.timeDimension.getStartTime());
			if(this.timeDimension.getEndTime() != null) time.setEndAttribute(this.timeDimension.getEndTime());
			Presentation presentation = null;
			if(this.timeDimension.getEndTime() == null){
				presentation = Presentation.LIST;
			}else{
				presentation = Presentation.CONTINUOUS_INTERVAL;
			}
			time.setPresentation(presentation);
			time.setUnit("ISO8601");
			time.setEnabled(true);
			fte.setMetadataDimension("time", time);
		}

		// layer
		GSLayerEncoder layerEncoder = null;
		if(this.master.getVersion().startsWith("2.1") || this.master.getVersion().matches("2.1")){
			layerEncoder = new GSLayerEncoder21();
		}else{
			layerEncoder = new GSLayerEncoder();
		}
		
		layerEncoder.setDefaultStyle(style);

		// add authorityURL & identifiers
		for (Entry<GeographicMetaObjectProperty, List<String>> entry : object
				.properties().entrySet()) {
			if (entry.getKey().isThesaurus()) {
				if (entry.getKey().containsURIs()) {
					layerEncoder.addAuthorityURL(new GSAuthorityURLInfoEncoder(
							((EntityAuthority) entry.getKey().getObject()).name(),
							((EntityAuthority) entry.getKey().getObject()).href()));
					for (String identifier : entry.getValue()) {
						layerEncoder.addIdentifier(new GSIdentifierInfoEncoder(
								((EntityAuthority) entry.getKey().getObject()).name(), identifier));
								LOGGER.debug("add "+((EntityAuthority) entry.getKey().getObject()).name()+" identifier: "+identifier);
					}
				}
			}
		}
		
		// publication
		boolean publish = false;
		if(method == GemsMethod.SQLVIEW){
			publish = GSPublisher.publishDBLayer(trgWorkspace, trgDatastore, fte, layerEncoder);
			
		}
		
		/*else if(method == PublicationMethod.SHAPEFILE){
			
			//prepare shapefile
			URL url = new URL(shapefile);
			String tDir = System.getProperty("java.io.tmpdir");
			String inputpath = tDir + "tmp" + ".zip";
			File file = new File(inputpath);
			file.deleteOnExit();
			FileUtils.copyURLToFile(url, file);
			
			publish = GSPublisher.publishShp(trgWorkspace, trgDatastore,
					new NameValuePair[0], object.getTargetLayerName(),
					UploadMethod.FILE, file.toURI(), fte, layerEncoder);
		}*/
		return publish;

	}
	
	
	/**
	 * A method to reload in-memory catalogs of geoserver workers
	 * that are part of a cluster, in order to align these catalogs
	 * on the master one.
	 * 
	 * @return true if catalogs were reloaded, false otherwise
	 * 
	 */
	public boolean reloadWorkerCatalogs() {
		boolean response = false;
		boolean reloadFailed = false;
		
		Iterator<GeoWorkerInstance> it = this.workers.iterator();
		while (it.hasNext()) {
			GeoWorkerInstance worker = it.next();
			GeoServerRESTPublisher workerPublisher = new GeoServerRESTPublisher(
					worker.getUrl(), worker.getUser(), worker.getPassword());
			
			int i = 0;
			boolean success = false;
			try{
				while(i <= 2){
					int attempt = i+1;
					LOGGER.info("Attempt "+attempt+" to reload in-memory catalog "
								+ "for worker: "+worker.getUrl());
					success = workerPublisher.reload();
					if(success){
						LOGGER.info("Successfull in-memory catalog reload "
									+ "for worker: "+ worker.getUrl());
						break;
					}else{
						i++;
					}
				}
			} finally {
				if(!success){
					LOGGER.info("Failed to reload in-memory catalog "
								+ "for worker: "+worker.getUrl());
					reloadFailed = true;
					response = false;
				} else {
					if(!reloadFailed) {
						response = true;
					}
				}
			}
		}

		return response;
	}

}
