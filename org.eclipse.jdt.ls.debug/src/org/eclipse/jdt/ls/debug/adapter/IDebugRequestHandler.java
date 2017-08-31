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

package org.eclipse.jdt.ls.debug.adapter;

import java.util.List;

public interface IDebugRequestHandler {
    List<Requests.Command> getTargetCommands();
    
    void handle(Requests.Command command, Requests.Arguments arguments, Messages.Response response, IDebugAdapterContext context);

}
