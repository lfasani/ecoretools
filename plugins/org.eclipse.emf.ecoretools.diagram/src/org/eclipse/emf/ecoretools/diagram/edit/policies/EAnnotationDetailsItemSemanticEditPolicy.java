/***********************************************************************
 * Copyright (c) 2007 Anyware Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Anyware Technologies - initial API and implementation
 **********************************************************************/

package org.eclipse.emf.ecoretools.diagram.edit.policies;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecoretools.diagram.edit.commands.EStringToStringMapEntryCreateCommand;
import org.eclipse.emf.ecoretools.diagram.providers.EcoreElementTypes;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;

/**
 * @generated
 */
public class EAnnotationDetailsItemSemanticEditPolicy extends EcoreBaseItemSemanticEditPolicy {

	/**
	 * @generated
	 */
	protected Command getCreateCommand(CreateElementRequest req) {
		if (EcoreElementTypes.EStringToStringMapEntry_2007 == req.getElementType()) {
			if (req.getContainmentFeature() == null) {
				req.setContainmentFeature(EcorePackage.eINSTANCE.getEAnnotation_Details());
			}
			return getGEFWrapper(new EStringToStringMapEntryCreateCommand(req));
		}
		return super.getCreateCommand(req);
	}
}
