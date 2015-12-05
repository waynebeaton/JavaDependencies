package org.eclipselabs.jdt.modules.views;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.zest.core.viewers.ISelfStyleProvider;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipselabs.jdt.modules.views.ModuleScanner.Module;
import org.eclipselabs.jdt.modules.views.ModuleScanner.ModuleSet;

public class ModuleDependenciesView extends ViewPart {
	public static final String ID = "org.eclipselabs.jdt.modules.views.ModuleDependenciesView";

	private GraphViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	static class ReadsContentProvider implements IGraphEntityContentProvider {

		public Object[] getConnectedTo(Object entity) {
			Module module = (Module)entity;
			
			return module.getReads().toArray();
		}

		public Object[] getElements(Object input) {
			if (input instanceof IJavaProject) {
				IJavaProject project = (IJavaProject)input;
				try {
					ModuleSet modules = new ModuleScanner().scan(project);
					return modules.getModules().toArray();
				} catch (CoreException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (input instanceof Module) {
				Set<Module> all = new HashSet<Module>();
				all.add((Module)input);
				all.addAll(((Module)input).getReads());
				return all.toArray();
			}
			
			return new Object[]{};
		}

		public double getWeight(Object entity1, Object entity2) {
			return 10;
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}
	}

	class ViewLabelProvider extends LabelProvider implements ISelfStyleProvider {
		final Image image = Display.getDefault().getSystemImage(SWT.ICON_WARNING);

		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {
			if (element instanceof Module) {
				return ((Module)element).getName();
			}
			return null;
		}

		@Override
		public void selfStyleConnection(Object element, GraphConnection connection) {
			connection.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		}

		@Override
		public void selfStyleNode(Object element, GraphNode node) {
		}

	}

	/**
	 * The constructor.
	 */
	public ModuleDependenciesView() {
	}
	
	ISelectionListener selectionListener;

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {

		viewer = new GraphViewer(parent, SWT.NONE);
		viewer.setContentProvider(new ReadsContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		viewer.setInput(null);
		getSite().setSelectionProvider(viewer);		
		selectionListener  = new IJavaProjectSelectionListener(viewer);
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(selectionListener);
		
		viewer.getControl().addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				viewer.getGraphControl().applyLayout();
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ModuleDependenciesView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				viewer.setInput(obj);
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Module Dependencies View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	@Override
	public void dispose() {
		getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(selectionListener);
		super.dispose();
	}
}
