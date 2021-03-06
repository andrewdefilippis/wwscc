/*
 * This software is licensed under the GPLv3 license, included as
 * ./GPLv3-LICENSE.txt in the source distribution.
 *
 * Portions created by Brett Wilson are Copyright 2012 Brett Wilson.
 * All rights reserved.
 */

package org.wwscc.services;

import java.net.InetAddress;
import java.util.Comparator;

/**
 *
 * @author bwilson
 */
public final class FoundService 
{
	final InetAddress host;
	final ServiceMessage description;

	public FoundService(InetAddress addr, ServiceMessage msg) {
		host = addr;
		description = msg;
	}

	public InetAddress getHost() { return host; }
	public String getService() { return description.getService(); }
	public String getId() { return description.getId(); }
	public int getPort() { return description.getPort(); }
	
	@Override
	public String toString() {
		return host.getHostAddress() + " => " + description;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final FoundService other = (FoundService) obj;
		if (host != other.host && (host == null || !host.equals(other.host))) {
			return false;
		}
		if (description != other.description && (description == null || !description.equals(other.description))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + (this.host != null ? this.host.hashCode() : 0);
		hash = 67 * hash + (this.description != null ? this.description.hashCode() : 0);
		return hash;
	}
	
	public static class Compare implements Comparator<FoundService>
	{
		@Override
		public int compare(FoundService lhs, FoundService rhs)
		{
			return lhs.description.getId().compareTo(rhs.description.getId());
		}
	}
}
