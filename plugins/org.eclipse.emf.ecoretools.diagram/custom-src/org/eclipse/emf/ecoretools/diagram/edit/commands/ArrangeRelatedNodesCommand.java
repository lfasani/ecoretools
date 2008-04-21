/**
 * Copyright (c) 2008 Anyware Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Anyware Technologies - initial API and implementation
 */
package org.eclipse.emf.ecoretools.diagram.edit.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecoretools.diagram.edit.parts.EReferenceUtils;
import org.eclipse.emf.ecoretools.diagram.part.EcoreLinkDescriptor;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.commands.DeferredLayoutCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.View;

/**
 * 
 * Restores the missing nodes of outgoing or incoming links for the selected
 * parts <br>
 * creation : 15 avr. 2008
 * 
 * @author <a href="mailto:gilles.cannenterre@anyware-tech.com">Gilles
 *         Cannenterre</a>
 */
public class ArrangeRelatedNodesCommand extends RestoreRelatedLinksCommand {

	public ArrangeRelatedNodesCommand(DiagramEditPart diagramEditPart, List selection) {
		super(diagramEditPart, selection);
	}

	/**
	 * @see org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand#doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		List<View> views = new ArrayList<View>();
		for (Iterator iter = adapters.iterator(); iter.hasNext();) {
			Object object = iter.next();
			if (object instanceof IAdaptable) {
				IAdaptable ad = (IAdaptable) object;
				View view = (View) ad.getAdapter(View.class);
				if (view != null) {
					views.add(view);
				}
			} else if (object instanceof View) {
				views.add((View) object);
			}

		}
		arrangeRelatedNodes(views);
		
		return CommandResult.newOKCommandResult();
	}

	/**
	 * Arranges related missing nodes for graphicalEditPart
	 * 
	 * @param graphicalEditPart
	 */
	protected void arrangeRelatedNodes(List<View> notationViews) {
		Map domain2NotationMap = new HashMap();

		// Collect all related link from semantic model
		List<View> relatedNodes = new ArrayList<View>();
		relatedNodes.addAll(notationViews);
		
		for (View notationView : notationViews) {
			Collection linkDescriptors = collectPartRelatedLinks(notationView, domain2NotationMap);
			relatedNodes.addAll(getRelatedMissingNodes(linkDescriptors, domain2NotationMap));
		}
		
		List<EObjectAdapter> adapters = new ArrayList<EObjectAdapter>();
		for (View view : relatedNodes) {
			adapters.add(new EObjectAdapter(view));
		}

		if (adapters.size() > 1) {
			// perform a layout of the container
			DeferredLayoutCommand cmd = new DeferredLayoutCommand(host.getEditingDomain(), adapters, host);
			if (cmd != null && cmd.canExecute()) {
				EReferenceUtils.executeCommand(new ICommandProxy(cmd), host);
			}
		}
	}

	/**
	 * Gets related nodes corresponding to linkDescriptions
	 * 
	 * @param part
	 * @param linkDescriptors
	 * @param domain2NotationMap
	 * 
	 * @return views
	 */
	protected List<View> getRelatedMissingNodes(Collection linkDescriptors, Map domain2NotationMap) {
		// map diagram
		mapModel(diagram, domain2NotationMap);

		List<View> relatedNodes = new ArrayList<View>();

		for (Iterator linkDescriptorsIterator = linkDescriptors.iterator(); linkDescriptorsIterator.hasNext();) {
			final EcoreLinkDescriptor nextLinkDescriptor = (EcoreLinkDescriptor) linkDescriptorsIterator.next();
			View sourceView = (View) domain2NotationMap.get(nextLinkDescriptor.getSource());
			View targetView = (View) domain2NotationMap.get(nextLinkDescriptor.getDestination());
			if (sourceView != null && false == relatedNodes.contains(sourceView) && targetView != null && false == relatedNodes.contains(targetView)) {
				relatedNodes.add(sourceView);
				relatedNodes.add(targetView);
			}
		}

		return relatedNodes;
	}

}