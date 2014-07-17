package org.fao.fi.gems;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.fao.fi.gems.association.GeographicMetaObject;
import org.fao.fi.gems.association.GeographicMetaObjectProperty;
import org.fao.fi.gems.entity.EntityAuthority;
import org.fao.fi.gems.model.content.MetadataBiblioRef;
import org.fao.fi.gems.model.content.MetadataContact;
import org.fao.fi.gems.model.content.MetadataThesaurus;
import org.fao.fi.gems.util.Utils;
import org.geotoolkit.internal.jaxb.gmx.Anchor;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.citation.DefaultAddress;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultCitationDate;
import org.geotoolkit.metadata.iso.citation.DefaultContact;
import org.geotoolkit.metadata.iso.citation.DefaultOnlineResource;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.citation.DefaultTelephone;
import org.geotoolkit.metadata.iso.constraint.DefaultLegalConstraints;
import org.geotoolkit.metadata.iso.distribution.DefaultDigitalTransferOptions;
import org.geotoolkit.metadata.iso.distribution.DefaultDistribution;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.metadata.iso.extent.DefaultTemporalExtent;
import org.geotoolkit.metadata.iso.identification.DefaultBrowseGraphic;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultKeywords;
import org.geotoolkit.metadata.iso.lineage.DefaultLineage;
import org.geotoolkit.metadata.iso.maintenance.DefaultMaintenanceInformation;
import org.geotoolkit.metadata.iso.quality.DefaultDataQuality;
import org.geotoolkit.metadata.iso.quality.DefaultScope;
import org.geotoolkit.metadata.iso.spatial.DefaultGeometricObjects;
import org.geotoolkit.metadata.iso.spatial.DefaultVectorSpatialRepresentation;
import org.geotoolkit.temporal.object.DefaultTemporalPrimitive;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.Telephone;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.constraint.Restriction;
import org.opengis.metadata.distribution.DigitalTransferOptions;
import org.opengis.metadata.identification.CharacterSet;
import org.opengis.metadata.identification.KeywordType;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.maintenance.MaintenanceFrequency;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.spatial.GeometricObjectType;
import org.opengis.metadata.spatial.TopologyLevel;
import org.opengis.temporal.TemporalPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Envelope;

/**
 * A class aimed to handle generic geographicEntityMetadata that can be then
 * used for different collections: species, rfb, etc
 * 
 * We define here a geographic object a dataset that can be mapped one-to-one to
 * a domain object such as SPECIES, RFB, VME
 * 
 * 
 * @author eblondel
 * 
 */
public class GeographicEntityMetadata extends DefaultMetadata {

	private static Logger LOGGER = LoggerFactory.getLogger(GeographicEntityMetadata.class);
	
	private static final String INSPIRE_THESAURUS_CITATION = "GEMET - INSPIRE themes, version 1.0";

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6602734832572502929L;

	protected GeographicMetaObject object;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Calendar c = Calendar.getInstance();

	private Date lastRevisionDate;
	private String lastVersion;

	Collection<? extends Constraints> constraints;
	List<ResponsibleParty> ORGANIZATIONS;
	MetadataContact OWNER_CONTACT;
	MetadataContact BIBLIO_CONTACT;

	/**
	 * Constructs a GeographicEntity metadata
	 * 
	 * @throws URISyntaxException
	 * @throws ParseException
	 * 
	 */
	public GeographicEntityMetadata(GeographicMetaObject object, String version) throws URISyntaxException, ParseException {

		super();
		this.object = object;
		
		this.lastRevisionDate = sdf.parse(sdf.format(new Date()));
		this.lastVersion = version;

		// build the metadata
		this.setIdentifier(object.getMetaIdentifier()); // identifier
		this.setDateStamp(this.lastRevisionDate);

		this.setLocales(Arrays.asList(Locale.ENGLISH)); // Locales
		this.setLanguage(Locale.ENGLISH); // Language
		this.setCharacterSet(CharacterSet.UTF_8); // Encoding

		this.setMetadataStandardName("ISO 19115:2003/19139"); // standard
		this.setMetadataStandardVersion("1.0"); // version
		this.getHierarchyLevels().add(ScopeCode.DATASET); // hierarchical level

		this.setOrganizationContacts(); // organization contacts
		this.setIndividualContacts(); // individual contacts
		this.setDataQuality(); // methodology if existing
		this.setSpatialRepresentation(); // spatial representation
		this.setReferenceSystemInfo(Arrays.asList(object.getCRS())); // ReferenceSystem
		this.setMetadataConstraints(); // constraints
		this.setDistributionInfo();
		this.setIdentificationInfo();

	}

	protected GeographicMetaObject getMetaObject() {
		return this.object;
	}

	protected Date getRevisionDate() {
		return this.lastRevisionDate;
	}

	protected String getVersion() {
		return this.lastVersion;
	}

	protected List<ResponsibleParty> getOrganizationContacts(){
		return this.ORGANIZATIONS;
	}
	
	protected MetadataContact getOwnerOrganization() {
		return this.OWNER_CONTACT;
	}

	/**
	 * Set the metadata identifier
	 * 
	 * @param fileIdentifier
	 */
	private void setIdentifier(String fileIdentifier) {
		if (fileIdentifier != null) {
			this.setFileIdentifier(fileIdentifier);
		} else {
			UUID uuid = UUID.randomUUID();
			String fileId = uuid.toString();
			this.setFileIdentifier(fileId);
		}

	}

	/**
	 * set Organizations
	 * 
	 * @throws URISyntaxException
	 */
	private void setOrganizationContacts() throws URISyntaxException {
		
		final List<ResponsibleParty> contacts = new ArrayList<ResponsibleParty>();
		
		for(MetadataContact contact : object.getTemplate().getOrganizationContacts()){
			
			final DefaultResponsibleParty rp = new DefaultResponsibleParty();
			
			// contact info
			final DefaultContact contactORG = new DefaultContact();
			final DefaultOnlineResource resourceORG = new DefaultOnlineResource();
			resourceORG.setName(contact.getName());
			resourceORG.setLinkage(new URI(contact.getUrl()));
			contactORG.setOnlineResource(resourceORG);

			// Address
			final DefaultAddress addressORG = new DefaultAddress();
			addressORG.getDeliveryPoints().add(contact.getAddress()); // deliveryPoint
			addressORG.setCity(new SimpleInternationalString(contact.getCity())); // city
			addressORG.setPostalCode(contact.getPostalCode()); // postal code
			addressORG.setCountry(new SimpleInternationalString(contact.getCountry())); // country
			contactORG.setAddress(addressORG);

			rp.setContactInfo(contactORG);
			rp.setOrganisationName(new SimpleInternationalString(contact.getOrgName()));
			rp.setRole(Role.valueOf(contact.getRole()));
			contacts.add(rp);
			
			if(contact.getRole().matches("OWNER")) this.OWNER_CONTACT = contact;
			if(contact.isBiblioAuthor()) this.BIBLIO_CONTACT = contact;
		}
		
		this.ORGANIZATIONS = contacts;
	}

	/**
	 * A method to set the list of contacts
	 * 
	 * @throws URISyntaxException
	 * 
	 * 
	 */
	private void setIndividualContacts() throws URISyntaxException {
		
		final List<ResponsibleParty> contacts = new ArrayList<ResponsibleParty>();
		
		for(MetadataContact iContact : object.getTemplate().getIndividualContacts()){
		
			DefaultResponsibleParty rp = new DefaultResponsibleParty();
			
			// contact info
			final DefaultContact contact = new DefaultContact();
			final DefaultOnlineResource resource = new DefaultOnlineResource();
			resource.setName(iContact.getName());
			resource.setLinkage(new URI(iContact.getUrl()));
			contact.setOnlineResource(resource);
			
			// telephone
			DefaultTelephone tel = null;
			if(iContact.getMainPhone() != null){
				if(tel == null){
					tel = new DefaultTelephone();
				}
				tel.getVoices().add(iContact.getMainPhone());
			}
			if(iContact.getFax() != null){
				if(tel == null){
					tel = new DefaultTelephone();
				}
				tel.getVoices().add(iContact.getFax());
			}
			if(tel != null){
				contact.setPhone((Telephone) tel);
			}

			// Address
			DefaultAddress address = null;
			if(iContact.getAddress() != null){
				if(address == null){
					address = new DefaultAddress();
				}
				address.getDeliveryPoints().add(iContact.getAddress());
				address.setCity(new SimpleInternationalString(iContact.getCity()));
				address.setPostalCode(iContact.getPostalCode());
				address.setCountry(new SimpleInternationalString(iContact.getCountry()));
			}
			if(iContact.getMainEmail() != null){
				if(address == null){
					address = new DefaultAddress();
				}
				address.getElectronicMailAddresses().add(iContact.getMainEmail());
			}
			if(address != null){
				contact.setAddress(address);
			}

			rp.setContactInfo(contact);
			rp.setIndividualName(iContact.getIndividualName());
			rp.setOrganisationName(new SimpleInternationalString(iContact.getOrgName()));
			rp.setPositionName(new SimpleInternationalString(iContact.getPositionName()));
			rp.setRole(Role.POINT_OF_CONTACT);

			contacts.add(rp);
			this.setContacts(contacts);
		}

	}

	/**
	 * Data Quality / Lineage
	 * 
	 * 
	 */
	protected void setDataQuality() {
		DefaultDataQuality quality = new DefaultDataQuality();
		DefaultScope scope = new DefaultScope();
		scope.setLevel(ScopeCode.DATASET);
		quality.setScope(scope);

		DefaultLineage lineage = new DefaultLineage();
		lineage.setStatement(new SimpleInternationalString(object.getTemplate()
				.getMethodology()));
		quality.setLineage(lineage);
		this.setDataQualityInfo(Arrays.asList(quality));
	}

	/**
	 * spatial representation
	 * 
	 */
	protected void setSpatialRepresentation() {
		DefaultVectorSpatialRepresentation spatialRepresentation = new DefaultVectorSpatialRepresentation();

		// Geometry objects
		DefaultGeometricObjects geomObjects = new DefaultGeometricObjects();
		geomObjects.setGeometricObjectType(GeometricObjectType.SURFACE);

		// count
		int count = this.object.getFeaturesCount();

		geomObjects.setGeometricObjectCount(count);
		spatialRepresentation.setGeometricObjects(Arrays.asList(geomObjects));

		// topology level
		spatialRepresentation.setTopologyLevel(TopologyLevel.GEOMETRY_ONLY);

		this.setSpatialRepresentationInfo(Arrays.asList(spatialRepresentation));

	}

	/**
	 * A method to set the metadata constraints, e.g. legal use/access
	 * constraints such as license
	 * 
	 * LAST UPDATE: 2013/04/16
	 * 
	 */
	private void setMetadataConstraints() {

		// Legal constraints
		DefaultLegalConstraints legalConstraints = new DefaultLegalConstraints();
		legalConstraints.setUseConstraints(Arrays.asList(Restriction.COPYRIGHT,
				Restriction.LICENSE));
		
		//prepare bibliography item
		MetadataBiblioRef mdBiblioRef = object.getTemplate().getBiblioRef();
		String biblioRef = "Usage subject to mandatory citation: ";
		if(mdBiblioRef.isCopyright()) biblioRef += "© ";
		biblioRef += this.BIBLIO_CONTACT.getAcronym()+", ";
		biblioRef += c.get(Calendar.YEAR)+ ". ";
		biblioRef += object.getTemplate().getCollection()+ ". ";
		if(mdBiblioRef.getScope().matches("DATASET")){
			biblioRef += object.getMetaTitle()+ ". ";
		}
		
		biblioRef += "In: "+this.BIBLIO_CONTACT.getName()+" [online]. ";
		biblioRef += this.BIBLIO_CONTACT.getCity()+". ";
		biblioRef += "Updated "+sdf.format(this.lastRevisionDate)+" ";
		biblioRef += "[Cited <DATE>] ";
		
		if(mdBiblioRef.getScope().matches("DATASET")){
			biblioRef += Utils.getHTMLMetadataURL(object.getMetadataCatalogueSettings().getUrl(), object.getMetaIdentifier());
		}else if(mdBiblioRef.getScope().matches("COLLECTION")){
			biblioRef += object.getTemplate().getCollectionURL();
		}
	
		legalConstraints
				.setUseLimitations(Arrays
						.asList(

						// license to use
						new SimpleInternationalString(object.getTemplate()
								.getLicense()),

						// Usage for bibliography
						new SimpleInternationalString(biblioRef),

						// Disclaimer
						new SimpleInternationalString(object
										.getTemplate().getDisclaimer())));
		legalConstraints.setAccessConstraints(Arrays.asList(
				Restriction.COPYRIGHT, Restriction.LICENSE));

		// set constraints
		this.constraints = Arrays.asList(legalConstraints);
	}

	/**
	 * Species DistributionInfo
	 * 
	 */
	private void setDistributionInfo() {
		try {
			DefaultDistribution distribution = new DefaultDistribution();

			DefaultDigitalTransferOptions option = new DefaultDigitalTransferOptions();
			Set<OnlineResource> resources = new HashSet<OnlineResource>();

			// website main resource
			// ---------------------------
			DefaultOnlineResource fiweb = new DefaultOnlineResource();
			fiweb.setLinkage(new URI(object.getTemplate().getCollectionURL()));
			fiweb.setProtocol("WWW:LINK-1.0-http--link");
			fiweb.setDescription(new SimpleInternationalString(object
					.getTemplate().getCollection()));
			fiweb.setFunction(OnLineFunction.INFORMATION);
			resources.add(fiweb);

			// factsheet (if it exists)
			// ---------------------------
			if(object.isFromFigis()){
				DefaultOnlineResource factsheet = new DefaultOnlineResource();
				factsheet.setLinkage(new URI(object.getFigisFactsheet()));
				factsheet.setProtocol("WWW:LINK-1.0-http--link");
				factsheet.setDescription(new SimpleInternationalString(
						"Factsheet - Summary description"));
				factsheet.setFunction(OnLineFunction.INFORMATION);
				resources.add(factsheet);
			}

			// viewer Resource (if it exists)
			// -------------------------------
			if(object.isFromFigis() && object.getFigisViewerResource() != null){
				DefaultOnlineResource viewerResource = new DefaultOnlineResource();
				viewerResource.setLinkage(object.getFigisViewerResource());
				viewerResource.setProtocol("WWW:LINK-1.0-http--link");
				viewerResource.setDescription(new SimpleInternationalString(object
						.getTemplate().getCollection() + " (GIS Viewer)"));
				viewerResource.setFunction(OnLineFunction.INFORMATION);
				resources.add(viewerResource);
			}
			
			// OGC standard data protocols
			// ===========================
			// WMS resource
			// ----------------
			DefaultOnlineResource wmsResource = new DefaultOnlineResource();
			wmsResource.setLinkage(new URI(object.getGeographicServerSettings().getUrl() + "/"
					+ object.getGeographicServerSettings().getTargetWorkspace() + "/ows?SERVICE=WMS"));
			// "&srs="+object.getGisProperties().get(GisProperty.PROJECTION)+
			// "&styles="+object.getGisProperties().get(GisProperty.STYLE)));
			wmsResource.setProtocol("OGC:WMS-1.3.0-http-get-map");
			wmsResource.setName(object.getTargetLayerName());
			wmsResource.setDescription(new SimpleInternationalString(object.getMetaTitle()));
			resources.add(wmsResource);

			// WFS resource (both GML and SHP)
			// -------------------------------
			// GML
			DefaultOnlineResource wfsResource1 = new DefaultOnlineResource();
			wfsResource1.setLinkage(new URI(object.getGeographicServerSettings().getUrl() + "/"
					+ object.getGeographicServerSettings().getTargetWorkspace()
					+ "/ows?service=WFS&request=GetFeature&version=1.0.0"
					+ "&typeName=" + object.getTargetLayerName()));
			wfsResource1.setProtocol("OGC:WFS-1.0.0-http-get-feature");
			wfsResource1.setName(object.getTargetLayerName());
			wfsResource1.setDescription(new SimpleInternationalString(
					"GIS data (WFS - GML)"));
			wfsResource1.setFunction(OnLineFunction.DOWNLOAD);
			resources.add(wfsResource1);

			// SHP
			// note: in the future we should see to customize the SHAPE-ZIP so
			// it handles the metadata. This will require Geoserver
			// developements
			String shpFileName = this.getOwnerOrganization().getAcronym()+"_" + object.getTargetLayerName();
			DefaultOnlineResource wfsResource2 = new DefaultOnlineResource();
			wfsResource2.setLinkage(new URI(object.getGeographicServerSettings().getUrl() + "/"
					+ object.getGeographicServerSettings().getTargetWorkspace()
					+ "/ows?service=WFS&request=GetFeature&version=1.0.0"
					+ "&typeName=" + object.getTargetLayerName()
					+ "&outputFormat=SHAPE-ZIP" + "&format_options=filename:"
					+ shpFileName + ".zip"));

			wfsResource2.setProtocol("OGC:WFS-1.0.0-http-get-feature");
			wfsResource2.setName(object.getTargetLayerName());
			wfsResource2.setDescription(new SimpleInternationalString(
					"GIS data (WFS - Shapefile)"));
			wfsResource2.setFunction(OnLineFunction.DOWNLOAD);
			resources.add(wfsResource2);

			// Metadata formats
			// =================

			// Geonetwork - metadata as XML ISO 19115/19139
			// ----------------
			DefaultOnlineResource xmlResource = new DefaultOnlineResource();
			xmlResource.setLinkage(new URI(Utils.getXMLMetadataURL(
					object.getMetadataCatalogueSettings().getUrl(), this.getFileIdentifier())));
			xmlResource.setProtocol("WWW:LINK-1.0-http--link");
			xmlResource.setName("XML");
			xmlResource.setDescription(new SimpleInternationalString(
					"metadata (XML)"));
			resources.add(xmlResource);

			option.setOnLines(resources);
			Set<DigitalTransferOptions> options = new HashSet<DigitalTransferOptions>();
			options.add(option);
			distribution.setTransferOptions(options);
			this.setDistributionInfo(distribution);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setIdentificationInfo() throws URISyntaxException,
			ParseException {

		DefaultDataIdentification identification = new DefaultDataIdentification();

		// language
		// --------
		identification.getLanguages().add(Locale.ENGLISH);

		// citation
		// --------
		DefaultCitation citation = new DefaultCitation();
		citation.setCitedResponsibleParties(this.getOrganizationContacts());

		// Identifier
		DefaultIdentifier identifier = new DefaultIdentifier();
		identifier.setCode(this.getFileIdentifier());
		citation.setIdentifiers(Arrays.asList(identifier));

		// title
		citation.setTitle(new SimpleInternationalString(object.getMetaTitle()));
		DefaultCitationDate citationDate = new DefaultCitationDate();
		citationDate.setDate(this.getRevisionDate());
		citationDate.setDateType(DateType.REVISION);
		citation.setDates(Arrays.asList(citationDate));

		// edition
		citation.setEdition(new SimpleInternationalString(this.getVersion()));
		citation.setEditionDate(this.getRevisionDate());

		// presentation form
		citation.getPresentationForms().add(PresentationForm.MAP_DIGITAL);

		identification.setCitation(citation); // add to the identification info

		// point of contact
		identification.setPointOfContacts(this.getContacts());

		// resource constraints
		identification.setResourceConstraints(this.constraints);

		// extent
		// ------
		DefaultExtent extent = new DefaultExtent();
		
		//Geographic bounding box
		DefaultGeographicBoundingBox boundingBox = new DefaultGeographicBoundingBox();
		Envelope bbox = object.getActualBBOX();
		if (bbox != null) {
			boundingBox.setWestBoundLongitude(bbox.getMinX());
			boundingBox.setEastBoundLongitude(bbox.getMaxX());
			boundingBox.setSouthBoundLatitude(bbox.getMinY());
			boundingBox.setNorthBoundLatitude(bbox.getMaxY());
		} else {
			boundingBox.setWestBoundLongitude(-180);
			boundingBox.setEastBoundLongitude(180);
			boundingBox.setSouthBoundLatitude(-90);
			boundingBox.setNorthBoundLatitude(90);
		}
		extent.getGeographicElements().add(boundingBox);

		//Temporal extent
		TemporalPrimitive time = object.getTIME();
		if(time != null){
			DefaultTemporalExtent temporalExtent = new DefaultTemporalExtent();
			temporalExtent.setExtent(time);
			extent.getTemporalElements().add(temporalExtent);
		}
		
		//add extents (geograhic + eventual temporal)
		identification.getExtents().add(extent);
		
		// abstract
		// -------
		identification.setAbstract(new SimpleInternationalString(object.getMetaTitle()+ ". "+ object.getTemplate().getAbstract()));

		// purpose
		// -------
		identification.setPurpose(new SimpleInternationalString(object
				.getTemplate().getPurpose()));
		// maintenance information
		// -----------------------
		DefaultMaintenanceInformation info = new DefaultMaintenanceInformation();
		info.setMaintenanceAndUpdateFrequency(MaintenanceFrequency.AS_NEEDED);
		identification.getResourceMaintenances().add(info);

		// graphic overview
		// ----------------
		DefaultBrowseGraphic graphic = new DefaultBrowseGraphic();
		graphic.setFileDescription(new SimpleInternationalString("Map overview"));
		graphic.setFileName(object.getLayerGraphicOverview());
		graphic.setFileType("image/png");
		identification.setGraphicOverviews(Arrays.asList(graphic));

		// descriptive keywords
		// --------------------

		List<DefaultKeywords> keywordsList = new ArrayList<DefaultKeywords>();

		// add general thesaurus
		for (MetadataThesaurus thesaurus : object.getTemplate()
				.getThesaurusList()) {

			DefaultKeywords keywords = new DefaultKeywords();
			keywords.setType(KeywordType.THEME);
			DefaultCitation kwCitation = new DefaultCitation();
			DefaultCitationDate kwCitationDate = new DefaultCitationDate();

			if (thesaurus.getName().matches(INSPIRE_THESAURUS_CITATION)) {
				kwCitationDate.setDate(sdf.parse("2008-06-01"));
				kwCitationDate.setDateType(DateType.PUBLICATION);
			} else {
				kwCitationDate.setDate(this.getRevisionDate());
				kwCitationDate.setDateType(DateType.REVISION);
			}
			kwCitation.setDates(Arrays.asList(kwCitationDate));
			kwCitation.setTitle(new SimpleInternationalString(thesaurus
					.getName()));
			keywords.setThesaurusName(kwCitation);
			for (String kw : thesaurus.getKeywords()) {
				keywords.getKeywords().add(new SimpleInternationalString(kw));
			}

			keywordsList.add(keywords);
		}

		// add object-based thesaurus
		for (Entry<GeographicMetaObjectProperty, List<String>> objectType : object
				.getSpecificProperties().entrySet()) {
			if (objectType.getKey().isThesaurus()) {

				DefaultKeywords keywords = new DefaultKeywords();
				keywords.setType(KeywordType.THEME);
				DefaultCitation kwCitation = new DefaultCitation();

				DefaultCitationDate kwCitationDate = new DefaultCitationDate();
				kwCitationDate.setDate(this.getRevisionDate());
				kwCitationDate.setDateType(DateType.REVISION);
				kwCitation.setDates(Arrays.asList(kwCitationDate));
				SimpleInternationalString title = null;
				if(objectType.getKey().getObject() instanceof EntityAuthority){
					title = new SimpleInternationalString(((EntityAuthority) objectType.getKey().getObject()).name());
				}else if(objectType.getKey().getObject()instanceof String){
					title = new SimpleInternationalString((String) objectType.getKey().getObject());
				}
				kwCitation.setTitle(title);
				keywords.setThesaurusName(kwCitation);

				if (objectType.getKey().containsURIs()) {
					for (String kw : objectType.getValue()) {
						keywords.getKeywords().add(new Anchor(new URI(kw), kw));
					}
				} else {
					for (String kw : objectType.getValue()) {
						keywords.getKeywords().add(
								new SimpleInternationalString(kw));
					}
				}
				keywordsList.add(keywords);
			}
		}

		// add keywords to identification info
		identification.setDescriptiveKeywords(keywordsList);

		// character set
		// -------------
		identification.getCharacterSets().add(CharacterSet.UTF_8);

		// topic category
		// --------------
		List<TopicCategory> categories = new ArrayList<TopicCategory>();
		for(String cat : object.getTemplate().getTopicCategories()){
			categories.add(TopicCategory.valueOf(cat));
		}
		identification.setTopicCategories(categories);

		// additional information
		// ----------------------
		identification
				.setSupplementalInformation(new SimpleInternationalString(object.getTemplate().getSupplementaryInformation()));

		// add identification info
		this.getIdentificationInfo().add(identification);

	}

}
