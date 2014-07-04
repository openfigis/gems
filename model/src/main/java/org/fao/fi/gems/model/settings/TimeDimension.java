package org.fao.fi.gems.model.settings;

/**
 * TimeDimension
 * 
 * 
 * @author eblondel
 *
 */
public class TimeDimension {

	private String startTime;
	private String endTime;
	
	public TimeDimension(){
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
}
