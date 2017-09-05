/*******************************************************************************
 * Copyright (c) 2017 Microsoft Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Microsoft Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.ls.core.internal;

/**
 * Interface for the language server participant
 */
public interface IParticipant {

	public static final String EXTENSIONPOINT_ID = "org.eclipse.jdt.ls.core.participant";

	/**
	 * Initialize a language server participant and save the initialize result
	 * into the JavaInitializeResult instance
	 *
	 * @param result
	 *            The JavaInitializeResult object instance for the participant
	 *            to save
	 */
	void initialize(JavaInitializeResult result);
}
