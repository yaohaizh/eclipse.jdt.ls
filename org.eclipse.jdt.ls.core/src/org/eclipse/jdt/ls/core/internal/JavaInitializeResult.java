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

import java.util.HashMap;
import java.util.Map;

/**
 * Represent the Java language server initialization result
 */
import org.eclipse.lsp4j.InitializeResult;

/**
 * Java language server specific initialize result.
 */
public class JavaInitializeResult extends InitializeResult {

	/**
	 * Map object for initialize result data from the participants
	 */
	private Map<String, Object> participantData;

	/**
	 * Constructor
	 */
	public JavaInitializeResult() {
		this.participantData = new HashMap<>();
	}

	/**
	 * Return the participant initialization data in a map object
	 *
	 * @return the initialize result data from participants.
	 */
	public Map<String, Object> getParticipantData() {
		return this.participantData;
	}
}
