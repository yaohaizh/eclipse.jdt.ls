package org.eclipse.jdt.ls.debug.internal;


import static org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin.logInfo;

import java.util.concurrent.CompletableFuture;

import org.eclipse.jdt.ls.debug.internal.DebugClientConnection.DebugClient;
import org.eclipse.jdt.ls.debug.internal.handlers.ResolveClasspathsHandler;
import org.eclipse.jdt.ls.debug.internal.handlers.StartDebugSessionHandler;
import org.eclipse.lsp4j.jsonrpc.services.JsonDelegate;

public class DebugServer implements DebugProtocolExtension {
	
	
	private DebugClientConnection client;
	
	
	public void connectClient(DebugClient client) {
		this.client = new DebugClientConnection(client);
	}
	
	@JsonDelegate
	public DebugProtocolExtension getJavaExtensions() {
		return this;
	}

	@Override
	public CompletableFuture<String> startSession(String type) {
		// DebugServer		logInfo(">> java/startDebugSession");
		this.client.sentTestStatus();
		StartDebugSessionHandler handler = new StartDebugSessionHandler();
		return handler.startDebugServer(type);
	}
	
	@Override
	public CompletableFuture<String> resolveClasspaths(ClasspathResolveRequestParams param) {
		logInfo(">> java/resolveClasspath");
		ResolveClasspathsHandler handler = new ResolveClasspathsHandler();
		return handler.resolveClasspaths(param);
	}
}
