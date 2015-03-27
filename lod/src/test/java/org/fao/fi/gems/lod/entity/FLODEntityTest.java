package org.fao.fi.gems.lod.entity;

import static org.junit.Assert.*;

import org.fao.fi.gems.lod.entity.common.FLODEezEntity;
import org.fao.fi.gems.lod.entity.common.FLODRfbEntity;
import org.fao.fi.gems.lod.entity.common.FLODSpeciesEntity;
import org.junit.Test;

/**
 * Unit tests for FLOD Species entity
 * 
 * @author Emmanuel Blondel <emmanuel.blondel@fao.org>
 *
 */
public class FLODEntityTest {
	
	
	@Test
	public void testSpecies() throws Exception {
		
		FLODSpeciesEntity species = new FLODSpeciesEntity("COD");
		
		assertEquals("COD", species.code());
		assertEquals("COD", species.authorityCode());
		assertEquals("Gadus morhua", species.authorityName());
		assertEquals("http://www.fao.org/figis/lod/flod/entities/codedentity/26c968f5b2f8a5fe635b1e75241cbe97a457a574", species.authorityUri());
		assertEquals("Gadus morhua", species.asfisScientificName());
		assertEquals("Atlantic cod", species.asfisEnglishName());
		
		assertEquals("Gadus morhua", species.wormsScientificName());
		assertEquals("urn:lsid:marinespecies.org:taxname:126436", species.wormsLsid());
		assertEquals("126436", species.wormsAphiaID());
		
	}	
	
	@Test
	public void testRfb() throws Exception {
		
		FLODRfbEntity rfb = new FLODRfbEntity("WECAFC");
		
		assertEquals("WECAFC", rfb.code());
		assertEquals("WECAFC", rfb.authorityCode());
		assertEquals("Western Central Atlantic Fishery Commission", rfb.authorityName());
		assertEquals("Western Central Atlantic Fishery Commission", rfb.englishName());
		assertEquals("http://www.fao.org/figis/lod/flod/entities/codedentity/19ffcf0a3eeac55d9f84ff68d9f53be6dee6c2e7", rfb.authorityUri());
		
	}
	
	@Test
	public void testEez() throws Exception {
		
		FLODEezEntity eez = new FLODEezEntity("8435");
		
		assertEquals("8435", eez.code());
		assertEquals("Faeroe Islands Exclusive Economic Zone", eez.authorityName());
		assertEquals("Faeroe Islands Exclusive Economic Zone", eez.englishName());
		assertEquals("http://www.fao.org/figis/lod/eez/entities/codedentity/48a4a155d9da8fa33415035e6b4ff721e1142869", eez.authorityUri());
		
	}

}
