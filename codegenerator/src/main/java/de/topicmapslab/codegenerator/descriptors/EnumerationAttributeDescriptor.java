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
 
package de.topicmapslab.codegenerator.descriptors;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

import de.topicmapslab.codegenerator.CodeGeneratorException;

/**
 * @author Hannes Niederhausen
 *
 */
public class EnumerationAttributeDescriptor extends AbstractAttributeDescriptor {

	private String enumerationType;
	
	/**
     * @param parent
     */
    public EnumerationAttributeDescriptor(AnnotationDescriptor parent) {
	    super(parent);
    }

    /**
     * @return the enumerationType
     */
    public String getEnumerationType() {
	    return enumerationType;
    }
    
    /**
     * @param enumerationType the enumerationType to set
     */
    public void setEnumerationType(String enumerationType) {
	    this.enumerationType = enumerationType;
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generateAttribute(JAnnotationUse annotation, JCodeModel cm) throws CodeGeneratorException {
		JClass c = cm.ref(enumerationType);
		annotation.param(getName(),c.staticRef((String) getValue()));
	}

}
