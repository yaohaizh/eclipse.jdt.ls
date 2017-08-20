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
package org.eclipse.jdt.ls.core.internal.handlers;

import java.util.concurrent.CompletableFuture;

import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.jdt.ls.core.internal.LoadBundleRequestParams;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;

public class LoadBundleHandler {

	public CompletableFuture<Long> loadBundle(LoadBundleRequestParams param) {
		return CompletableFutures.computeAsync(cm -> {
			Long result = JavaLanguageServerPlugin.getInstance().installBundle(param.getBundleLocation());
			return result;
		});
	}
}
