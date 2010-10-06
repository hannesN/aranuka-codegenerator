/*******************************************************************************
 * Copyright 2010 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.topicmapslab.codegenerator.descriptors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;

import de.topicmapslab.codegenerator.CodeGeneratorException;

/**
 * @author Hannes Niederhausen
 * 
 */
public class AnnotationDescriptor {

	private String qualifiedName;

	private List<AbstractAttributeDescriptor> attributes;

	private final IAnnoationDescriptor parent;

	/**
	 * @param parent
	 */
	public AnnotationDescriptor(IAnnoationDescriptor parent) {
		this.parent = parent;
		this.parent.addAnnotations(this);
	}

	/**
	 * @param qualifiedName
	 *            the qualifiedName to set
	 */
	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

	/**
	 * @return the qualifiedName
	 */
	public String getQualifiedName() {
		return this.qualifiedName;
	}

	/**
	 * @return the attributes
	 */
	public List<AbstractAttributeDescriptor> getAttributes() {
		if (this.attributes == null)
			return Collections.emptyList();
		return this.attributes;
	}

	/**
	 * Adds an attribute to the list.
	 * 
	 * @param ad
	 *            the attribute to add
	 */
	public void addAttribute(AbstractAttributeDescriptor ad) {
		if (this.attributes == null)
			this.attributes = new ArrayList<AbstractAttributeDescriptor>();
		attributes.add(ad);
	}

	/**
	 * Removes an attribute from the list
	 * 
	 * @param ad
	 *            the attribute to remove
	 */
	public void removeAttribute(AbstractAttributeDescriptor ad) {
		if (this.attributes != null)
			attributes.remove(ad);
	}

	public void generateAnnotation(JAnnotatable annoatable, JCodeModel cm) throws JClassAlreadyExistsException, CodeGeneratorException {
		JClass annoType = cm._getClass(getQualifiedName());
		if (annoType==null) {
			annoType = cm.ref(getQualifiedName());
		}
		
		JAnnotationUse annotation = annoatable.annotate(annoType);
		
		for (AbstractAttributeDescriptor ad : getAttributes()) {
	        ad.generateAttribute(annotation, cm);
        }
	}
}
