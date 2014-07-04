package org.fao.fi.gems.entity;
/**
 * An enum of Authority Entity
 * 
 * @author eblondel
 *
 */
public enum EntityAuthority {

	ISO("ISO", "http://www.iso.org"),
	FAO("FAO", "http://www.fao.org/fi"),
	FIGIS("FIGIS", "http://www.fao.org/figis"),
	FLOD("FLOD", "http://www.fao.org/figis/flod"),
	ASFIS("ASFIS", "http://www.fao.org/fishery/collection/asfis"),
	VLIZ("VLIZ", "http://www.vliz.be"),
	MARINEREGIONS("MARINEREGIONS", "http://www.marineregions.org/"),
	WORMS("WORMS", "http://www.marinespecies.org");

	private final String propertyName;
	private final String href;

	EntityAuthority(String propertyName, String href) {
		this.propertyName = propertyName;
		this.href = href;
	}

	public String propertyName() {
		return this.propertyName;
	}

	public String href() {
		return this.href;
	}
}
