package org.fao.fi.gems.model.content;

/**
 * Metadata Contact
 * 
 * @author eblondel
 *
 */
public class MetadataContact {
	
	private String name;
	private String url;
	private String acronym;
	private String address;
	private String city;
	private String postalCode;
	private String country;
	
	private String mainPhone;
	private String fax;
	private String mainEmail;
	private String individualName;
	private String positionName;
	private String organizationName;
	private String role;
	
	private boolean copyrightOwner = false;
	
	/**
	 * 
	 * Default Constructor
	 * 
	 */
	public MetadataContact(){
		
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the acronym
	 */
	public String getAcronym() {
		return acronym;
	}

	/**
	 * @param acronym the acronym to set
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the mainPhone
	 */
	public String getMainPhone() {
		return mainPhone;
	}

	/**
	 * @param mainPhone the mainPhone to set
	 */
	public void setMainPhone(String mainPhone) {
		this.mainPhone = mainPhone;
	}

	/**
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

	/**
	 * @return the mainEmail
	 */
	public String getMainEmail() {
		return mainEmail;
	}

	/**
	 * @param mainEmail the mainEmail to set
	 */
	public void setMainEmail(String mainEmail) {
		this.mainEmail = mainEmail;
	}

	/**
	 * @return the individualName
	 */
	public String getIndividualName() {
		return individualName;
	}

	/**
	 * @param individualName the individualName to set
	 */
	public void setIndividualName(String individualName) {
		this.individualName = individualName;
	}

	/**
	 * @return the positionName
	 */
	public String getPositionName() {
		return positionName;
	}

	/**
	 * @param positionName the positionName to set
	 */
	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return organizationName;
	}

	/**
	 * @param orgName the orgName to set
	 */
	public void setOrgName(String orgName) {
		this.organizationName = orgName;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the copyrightOwner
	 */
	public boolean isCopyrightOwner() {
		return copyrightOwner;
	}

	/**
	 * @param copyrightOwner the copyrightOwner to set
	 */
	public void setCopyrightOwner(boolean copyrightOwner) {
		this.copyrightOwner = copyrightOwner;
	}
	
}
