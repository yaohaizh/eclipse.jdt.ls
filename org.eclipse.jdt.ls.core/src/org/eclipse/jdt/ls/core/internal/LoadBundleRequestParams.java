/*******************************************************************************
 * Copyright (c) 2017 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Microsoft Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ls.core.internal;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/*
 * Representation of a request that client send to server to resolve class path
 */
public class LoadBundleRequestParams {
	/*
	 *  the fully qualified name of startupClass
	 */
	@NonNull
	private String bundleLocation;

	public LoadBundleRequestParams(final String startupClass) {
		this.setBundleLocation(startupClass);
	}

	/**
	 * @return the bundleLocation
	 */
	public String getBundleLocation() {
		return bundleLocation;
	}

	/**
	 * @param bundleLocation
	 *            the bundleLocation to set
	 */
	public void setBundleLocation(String bundleLocation) {
		this.bundleLocation = bundleLocation;
	}

	@Override
	@Pure
	public String toString() {
		ToStringBuilder b = new ToStringBuilder(this);
		b.add("bundleLocation", this.getBundleLocation());
		return b.toString();
	}

	@Override
	@Pure
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		LoadBundleRequestParams other = (LoadBundleRequestParams) obj;
		if (this.getBundleLocation() == null) {
			if (other.getBundleLocation() != null) {
				return false;
			}
		} else if (!this.getBundleLocation().equals(other.getBundleLocation())) {
			return false;
		}
		return true;
	}

	@Override
	@Pure
	public int hashCode() {
		return 31 + ((this.getBundleLocation() == null) ? 0 : this.getBundleLocation().hashCode());
	}
}
