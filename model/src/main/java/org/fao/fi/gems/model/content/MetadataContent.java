package org.fao.fi.gems.model.content;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * A metadata Content template class
 * 
 * @author eblondel
 *
 */
public class MetadataContent {

	private String collection;
	private String collectionURL;
	private boolean hasBaseTitle;
	private String baseTitle;
	private String abstractText;
	private String purpose;
	private String methodology;
	private String supplementaryInfo;
	private String license;
	private String disclaimer;
	private List<MetadataThesaurus> thesaurusList;
	private List<String> topicCategories;
	private List<MetadataContact> organizationContacts;
	private List<MetadataContact> individualContacts;
	
	
	public MetadataContent(){
	}	
	
	public void setCollection(String collection){
		this.collection = collection;
	}
	
	public String getCollection(){
		return this.collection;
	}
	
	public void setCollectionURL(String url){
		this.collectionURL = url;
	}
	
	public String getCollectionURL(){
		return this.collectionURL;
	}
	
	public void setHasBaseTitle(boolean hasbasetitle){
		this.hasBaseTitle = hasbasetitle;
	}
	
	public boolean getHasBaseTitle(){
		return this.hasBaseTitle;
	}
	
	public void setBaseTitle(String basetitle){
		this.baseTitle = basetitle;
	}
	
	public String getBaseTitle(){
		return this.baseTitle;
	}	
	
	public void setAbstract(String abstractText){
		this.abstractText = abstractText;
	}

	public String getAbstract(){
		return this.abstractText;
	}
	
	
	public void setPurpose(String purpose){
		this.purpose = purpose;
	}

	public String getPurpose(){
		return this.purpose;
	}
	
	public void setMethodology(String methodology){
		this.methodology = methodology;
	}
	
	public String getMethodology(){
		return this.methodology;
	}
	
	public void setLicense(String license){
		this.license = license;
	}

	public String getLicense(){
		return this.license;
	}
	
	public void setDisclaimer(String disclaimer){
		this.disclaimer = disclaimer;
	}
	
	public String getDisclaimer(){
		return this.disclaimer;
	}
	
	public void addMetadataThesaurus(MetadataThesaurus thesaurus){
		if(this.thesaurusList == null){
			this.thesaurusList = new ArrayList<MetadataThesaurus>();
		}
		this.thesaurusList.add(thesaurus);
	}

	public List<MetadataThesaurus> getThesaurusList(){
		return this.thesaurusList;
	}
	
	public void addTopicCategory(String category){
		
		if(this.topicCategories == null){
			this.topicCategories = new ArrayList<String>();
		}
		this.topicCategories.add(category);
	}
	
	public List<String> getTopicCategories(){
		return this.topicCategories;
	}
	
	public void setSupplementaryInformation(String info){
		this.supplementaryInfo = info;
	}
	
	public String getSupplementaryInformation(){
		return this.supplementaryInfo;
	}
	
	public void addOrganizationContact(MetadataContact contact){
		if(this.organizationContacts == null){
			this.organizationContacts = new ArrayList<MetadataContact>();
		}
		this.organizationContacts.add(contact);
	}
	
	public List<MetadataContact> getOrganizationContacts(){
		return this.organizationContacts;
	}
	
	public void addIndividualContact(MetadataContact contact){
		if(this.individualContacts == null){
			this.individualContacts = new ArrayList<MetadataContact>();
		}
		this.individualContacts.add(contact);
	}
	
	public List<MetadataContact> getIndividualContacts(){
		return this.individualContacts;
	}
	
	/**
	 * Parsing from XML
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static MetadataContent fromXML(File file){
			
		XStream xstream = new XStream(new StaxDriver());
		xstream.alias("content", MetadataContent.class);
		xstream.alias("thesaurus", MetadataThesaurus.class);
		xstream.alias("contact", MetadataContact.class);
		
		MetadataContent content = (MetadataContent) xstream.fromXML(file);
			
        return content;
	}
}

