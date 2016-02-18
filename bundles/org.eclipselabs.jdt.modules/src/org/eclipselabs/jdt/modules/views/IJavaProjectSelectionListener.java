/*******************************************************************************
 * Copyright (c) 2015 The Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipselabs.jdt.modules.views;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

public class IJavaProjectSelectionListener implements ISelectionListener {
	private Viewer viewer;
	private IJavaProject javaProject;

	public IJavaProjectSelectionListener(Viewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection)
			handleSelection((IStructuredSelection)selection);
	}

	private void handleSelection(IStructuredSelection selection) {
		if (selection.isEmpty()) return;
		Object object = selection.getFirstElement();
		if (object instanceof IAdaptable)
			handleSelection((IJavaProject)((IAdaptable)object).getAdapter(IJavaProject.class));
	}

	private void handleSelection(IJavaProject javaProject) {
		if (javaProject == null) return;
		if (this.javaProject == javaProject) return;
		this.javaProject = javaProject;
		viewer.setInput(javaProject);
	}
}