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
package org.eclipse.jdt.ls.core.internal.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.manipulation.CoreASTProvider;
import org.eclipse.jdt.ls.core.internal.JDTUtils;
import org.eclipse.jdt.ls.core.internal.lsp.SelectionRange;
import org.eclipse.jdt.ls.core.internal.lsp.SelectionRangeParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class SelectionRangeHandler {

	SelectionRangeHandler() {
	}

	public List<SelectionRange> selectionRange(SelectionRangeParams params, IProgressMonitor monitor) {
		List<SelectionRange> $ = new ArrayList<>();
		ITypeRoot unit = JDTUtils.resolveTypeRoot(params.getTextDocument().getUri());
		if (unit != null && params.getPositions() != null && params.getPositions().size() > 0) {
			for (Position pos : params.getPositions()) {
				try {
					CompilationUnit ast = CoreASTProvider.getInstance().getAST(unit, CoreASTProvider.WAIT_YES, monitor);
					int offset = JsonRpcHelpers.toOffset(unit.getBuffer(), pos.getLine(), pos.getCharacter());

					ASTNode node = NodeFinder.perform(ast, offset, 0);
					Stack<ASTNode> nodePaths = new Stack<>();
					while (node != null) {
						nodePaths.push(node);
						node = node.getParent();
					}

					SelectionRange currentRange = null;
					while (!nodePaths.empty()) {
						node = nodePaths.pop();
						Range range = JDTUtils.toRange(unit, node.getStartPosition(), node.getLength());
						currentRange = new SelectionRange(range, currentRange);
					}
					if (currentRange != null) {
						$.add(currentRange);
					}
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return $;
	}
}
