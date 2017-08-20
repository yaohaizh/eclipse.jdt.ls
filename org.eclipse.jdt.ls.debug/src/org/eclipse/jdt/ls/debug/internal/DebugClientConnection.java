package org.eclipse.jdt.ls.debug.internal;

import org.eclipse.jdt.ls.core.internal.StatusReport;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;

public class DebugClientConnection {
	
	public interface DebugClient {
		/**
		 * The show message notification is sent from a server to a client to
		 * ask the client to display a particular message in the user interface.
		 */
		@JsonNotification("debug/status")
		void sendStatusReport(StatusReport report);
	}

	private DebugClient client;
	
	public DebugClientConnection(DebugClient client) {
		this.client = client;
	}
	
	/**
	 * Noting.
	 */
	public void sentTestStatus() {
		StatusReport status = new StatusReport();
		status.withMessage("Testing").withType("Starting");;;
		this.client.sendStatusReport(status);
	}
}
