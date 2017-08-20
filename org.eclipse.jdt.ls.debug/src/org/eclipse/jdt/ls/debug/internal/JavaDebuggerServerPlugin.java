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

package org.eclipse.jdt.ls.debug.internal;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.ls.debug.internal.DebugClientConnection.DebugClient;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class JavaDebuggerServerPlugin implements BundleActivator {

    public static final String PLUGIN_ID = "org.eclipse.jdt.ls.debug";
    
    private static BundleContext context;
    
    @Override
    public void start(BundleContext context) throws Exception {
        JavaDebuggerServerPlugin.context = context;
        startConnection();
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }
    
    private void startListening() {
    
    }

    
    private void startConnection() throws IOException {
		DebugServer protocol = new DebugServer();
		ConnectionStreamFactory connectionFactory = new ConnectionStreamFactory();
		
		Launcher<DebugClient> launcher = Launcher.createLauncher(protocol, DebugClient.class,
				connectionFactory.getInputStream(),
				connectionFactory.getOutputStream());
		protocol.connectClient(launcher.getRemoteProxy());
		launcher.startListening();
    }
    
    /**
     * Log the specific status with the plugin's logger.
     * @param status
     *              status to log
     */
    public static void log(IStatus status) {
        if (context != null) {
            Platform.getLog(JavaDebuggerServerPlugin.context.getBundle()).log(status);
        }
    }

    /**
     * Log the {@link CoreException} with the plugin's logger.
     * @param e
     *         {@link CoreException} to log
     */
    public static void log(CoreException e) {
        log(e.getStatus());
    }

    /**
     * Log the error message with the plugin's logger.
     * @param message
     *               error message to log
     */
    public static void logError(String message) {
        if (context != null) {
            log(new Status(IStatus.ERROR, context.getBundle().getSymbolicName(), message));
        }
    }

    /**
     * Log the info message with the plugin's logger.
     * @param message
     *               message to log
     */
    public static void logInfo(String message) {
        if (context != null) {
            log(new Status(IStatus.INFO, context.getBundle().getSymbolicName(), message));
        }
    }

    /**
     * Log the exception with the plugin's logger.
     * @param message
     *               message to log
     * @param ex
     *          Exception to log
     */
    public static void logException(String message, Throwable ex) {
        if (context != null) {
            log(new Status(IStatus.ERROR, context.getBundle().getSymbolicName(), message, ex));
        }
    }
}
