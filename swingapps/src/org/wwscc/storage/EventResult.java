/*
 * This software is licensed under the GPLv3 license, included as
 * ./GPLv3-LICENSE.txt in the source distribution.
 *
 * Portions created by Brett Wilson are Copyright 2008 Brett Wilson.
 * All rights reserved.
 */

package org.wwscc.storage;

public class EventResult implements Comparable<EventResult>
{
	//private static final Logger log = Logger.getLogger("org.wwscc.storage.EventResults");

	protected int id;
	protected int carid;
	protected String firstname;
	protected String lastname;
	protected String indexcode;
	protected int position;
	protected int courses; /* How many courses in the calculation */
	protected double sum;
	protected double diff;
	protected double diffpoints;
	protected int pospoints;
	protected SADateTime updated;
	protected int lastcourse; // the active course when last updated, more important for pros where you go back/forth
	
	private double indexvalue;

	public String getFullName() { return firstname + " " + lastname; }
	public int getCarId() { return carid; }
	public double getSum() { return sum; }
	public double getDiff() { return diff; }
	public String getIndexCode() { return indexcode; }
	public double getIndex() { return indexvalue; }
	public int getPosition() { return position; }
	public int getLastCourse() { return lastcourse; }
	
	protected void setIndex(double value) { indexvalue = value; }
	
	@Override
	public int compareTo(EventResult o) {
		return (int)(1000*(getSum() - o.getSum()));
	}
}
