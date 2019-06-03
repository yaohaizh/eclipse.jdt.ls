/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Renaud Waldura &lt;renaud+eclipse@waldura.com&gt;
 *     IBM Corporation - updates
 *******************************************************************************/

package org.eclipse.jdt.ls.core.internal.corrections.proposals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.internal.core.manipulation.CodeTemplateContext;
import org.eclipse.jdt.internal.core.manipulation.CodeTemplateContextType;
import org.eclipse.jdt.internal.core.manipulation.util.BasicElementLabels;
import org.eclipse.jdt.internal.core.manipulation.util.Strings;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.ls.core.internal.corext.refactoring.nls.changes.CreateTextFileChange;
import org.eclipse.jdt.ls.core.internal.corrections.CorrectionMessages;
import org.eclipse.jdt.ls.core.internal.corrections.IInvocationContext;
import org.eclipse.jdt.ls.core.internal.preferences.CodeGenerationTemplate;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.ltk.core.refactoring.Change;

import com.google.common.collect.Sets;

/**
 * This proposal is listed in the corrections list for a "type not found"
 * problem. It offers to create a new type by running the class/interface
 * wizard. If selected, this proposal will open a
 * {@link NewClassCreationWizard}, {@link NewInterfaceCreationWizard},
 * {@link NewEnumCreationWizard} or {@link NewAnnotationCreationWizard}.
 *
 * @see UnresolvedElementsSubProcessor#getTypeProposals(IInvocationContext,
 *      IProblemLocation, Collection)
 */
public class NewCUProposal extends CUCorrectionProposal {

	private static final String CLASS_SNIPPET_LABEL = "class";
	private static final String INTERFACE_SNIPPET_LABEL = "interface";
	private static final String CLASS_KEYWORD = "class";
	private static final String INTERFACE_KEYWORD = "interface";
	private static final Set<String> UNSUPPORTED_RESOURCES = Sets.newHashSet("module-info.java", "package-info.java");

	private static String PACKAGEHEADER = "package_header";
	private static String CURSOR = "cursor";

	public static final int K_CLASS = 1;
	public static final int K_INTERFACE = 2;
	public static final int K_ENUM = 3;
	public static final int K_ANNOTATION = 4;

	private Name fNode;
	private ICompilationUnit fCompilationUnit;
	private int fTypeKind;
	private IJavaElement fTypeContainer; // IType or IPackageFragment
	private String fTypeNameWithParameters;

	/**
	 * @param name
	 * @param kind
	 * @param cu
	 * @param change
	 * @param relevance
	 */
	public NewCUProposal(ICompilationUnit cu, Name node, int typeKind, IJavaElement typeContainer, int relevance) {
		super("", CodeActionKind.QuickFix, cu, NewCUProposal.createFileChange(node, cu), relevance);
		fCompilationUnit = cu;
		fNode = node;
		fTypeKind = typeKind;
		fTypeContainer = typeContainer;
		if (fNode != null) {
			fTypeNameWithParameters = getTypeName(typeKind, node);
		}

		setDisplayName();
	}

	private void setDisplayName() {
		String containerName;
		if (fNode != null) {
			containerName = ASTNodes.getQualifier(fNode);
		} else {
			if (fTypeContainer instanceof IPackageFragment) {
				containerName = ((IPackageFragment) fTypeContainer).getElementName();
			} else {
				containerName = ""; //$NON-NLS-1$
			}
		}
		String typeName = fTypeNameWithParameters;
		String containerLabel = BasicElementLabels.getJavaElementName(containerName);
		String typeLabel = BasicElementLabels.getJavaElementName(typeName);
		boolean isInnerType = fTypeContainer instanceof IType;
		switch (fTypeKind) {
			case K_CLASS:
				if (fNode != null) {
					if (isInnerType) {
						if (containerName.length() == 0) {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createinnerclass_description, typeLabel));
						} else {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createinnerclass_intype_description, new String[] { typeLabel, containerLabel }));
						}
					} else {
						if (containerName.length() == 0) {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createclass_description, typeLabel));
						} else {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createclass_inpackage_description, new String[] { typeLabel, containerLabel }));
						}
					}
				} else {
					setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createnewclass_inpackage_description, containerLabel));
				}
				break;
			case K_INTERFACE:
				if (fNode != null) {
					if (isInnerType) {
						if (containerName.length() == 0) {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createinnerinterface_description, typeLabel));
						} else {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createinnerinterface_intype_description, new String[] { typeLabel, containerLabel }));
						}
					} else {
						if (containerName.length() == 0) {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createinterface_description, typeLabel));
						} else {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createinterface_inpackage_description, new String[] { typeLabel, containerLabel }));
						}
					}
				} else {
					setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createnewinterface_inpackage_description, containerLabel));
				}
				break;
			case K_ENUM:
				if (fNode != null) {
					if (isInnerType) {
						if (containerName.length() == 0) {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createinnerenum_description, typeLabel));
						} else {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createinnerenum_intype_description, new String[] { typeLabel, containerLabel }));
						}
					} else {
						if (containerName.length() == 0) {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createenum_description, typeLabel));
						} else {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createenum_inpackage_description, new String[] { typeLabel, containerLabel }));
						}
					}
				} else {
					setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createnewenum_inpackage_description, containerLabel));
				}
				break;
			case K_ANNOTATION:
				if (fNode != null) {
					if (isInnerType) {
						if (containerName.length() == 0) {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createinnerannotation_description, typeLabel));
						} else {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createinnerannotation_intype_description, new String[] { typeLabel, containerLabel }));
						}
					} else {
						if (containerName.length() == 0) {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createannotation_description, typeLabel));
						} else {
							setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createannotation_inpackage_description, new String[] { typeLabel, containerLabel }));
						}
					}
				} else {
					setDisplayName(Messages.format(CorrectionMessages.NewCUCompletionUsingWizardProposal_createnewannotation_inpackage_description, containerLabel));
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown type kind"); //$NON-NLS-1$
		}

	}

	private static String getTypeName(int typeKind, Name node) {
		String name = ASTNodes.getSimpleNameIdentifier(node);

		if (typeKind == K_CLASS || typeKind == K_INTERFACE) {
			ASTNode parent = node.getParent();
			if (parent.getLocationInParent() == ParameterizedType.TYPE_PROPERTY) {
				String typeArgBaseName = name.startsWith(String.valueOf('T')) ? String.valueOf('S') : String.valueOf('T'); // use 'S' or 'T'

				int nTypeArgs = ((ParameterizedType) parent.getParent()).typeArguments().size();
				StringBuilder buf = new StringBuilder(name);
				buf.append('<');
				if (nTypeArgs == 1) {
					buf.append(typeArgBaseName);
				} else {
					for (int i = 0; i < nTypeArgs; i++) {
						if (i != 0) {
							buf.append(", "); //$NON-NLS-1$
						}
						buf.append(typeArgBaseName).append(i + 1);
					}
				}
				buf.append('>');
				return buf.toString();
			}
		}
		return name;
	}

	private static String getSnippetContent(ICompilationUnit cu, CodeGenerationTemplate templateSetting, String lineDelimiter, boolean snippetStringSupport) throws CoreException {
		Template template = templateSetting.createTemplate(cu.getJavaProject());
		if (template == null) {
			return null;
		}
		CodeTemplateContext context = new CodeTemplateContext(template.getContextTypeId(), cu.getJavaProject(), lineDelimiter);

		IPackageDeclaration[] packageDeclarations = cu.getPackageDeclarations();
		String packageName = cu.getParent().getElementName();
		String packageHeader = ((packageName != null && !packageName.isEmpty()) && (packageDeclarations == null || packageDeclarations.length == 0)) ? "package " + packageName + ";\n\n" : "";
		context.setVariable(PACKAGEHEADER, packageHeader);
		String typeName = JavaCore.removeJavaLikeExtension(cu.getElementName());
		List<IType> types = Arrays.asList(cu.getAllTypes());
		int postfix = 0;
		while (types != null && !types.isEmpty() && types.stream().filter(isTypeExists(typeName)).findFirst().isPresent()) {
			typeName = "Inner" + JavaCore.removeJavaLikeExtension(cu.getElementName()) + (postfix == 0 ? "" : "_" + postfix);
			postfix++;
		}
		if (postfix > 0 && snippetStringSupport) {
			context.setVariable(CodeTemplateContextType.TYPENAME, "${1:" + typeName + "}");
		} else {
			context.setVariable(CodeTemplateContextType.TYPENAME, typeName);
		}
		context.setVariable(CURSOR, snippetStringSupport ? "${0}" : "");

		// TODO Consider making evaluateTemplate public in StubUtility
		TemplateBuffer buffer;
		try {
			buffer = context.evaluate(template);
		} catch (BadLocationException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		} catch (TemplateException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		}
		if (buffer == null) {
			return null;
		}
		String str = buffer.getString();
		if (Strings.containsOnlyWhitespaces(str)) {
			return null;
		}
		return str;
	}

	public static Change createFileChange(Name fNode, ICompilationUnit cu) {
		IType targetType = createType(fNode.getFullyQualifiedName(), cu);

		return new CreateTextFileChange(targetType.getResource().getRawLocation(), "", "", "");
	}

	private static IType createType(String typeName, ICompilationUnit cu) {
		IPackageFragment pack = getPackageFragment(cu);
		ICompilationUnit createdCU = pack.getCompilationUnit(getCompilationUnitName(typeName));
		return createdCU.getType(typeName);
	}

	private static IPackageFragment getPackageFragment(ICompilationUnit cu) {
		IJavaElement curr = cu;
		while (!(curr instanceof IPackageFragment)) {
			curr = curr.getParent();
		}
		return (IPackageFragment) curr;
	}

	private static String getCompilationUnitName(String typeName) {
		return typeName + JavaModelUtil.DEFAULT_CU_SUFFIX;
	}
}
