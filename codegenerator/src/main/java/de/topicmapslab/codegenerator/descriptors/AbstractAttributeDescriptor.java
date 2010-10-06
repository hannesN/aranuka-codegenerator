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
import com.sun.codemodel.JCodeModel;

import de.topicmapslab.codegenerator.CodeGeneratorException;

/**
 * @author Hannes Niederhausen
 *
 */
public abstract class AbstractAttributeDescriptor {

	
	private String name;
	
	private Object value;
	
	private AnnotationDescriptor parent;
	
	/**
     * @param parent
     */
    public AbstractAttributeDescriptor(AnnotationDescriptor parent) {
	    super();
	    this.parent = parent;
	    this.parent.addAttribute(this);
    }

	
	/**
     * @param name the name to set
     */
    public void setName(String name) {
	    this.name = name;
    }
    
    /**
     * @return the name
     */
    public String getName() {
	    return name;
    }
    
    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
	    this.value = value;
    }
    
    /**
     * @return the value
     */
    public Object getValue() {
	    return value;
    }

	/**
     * @param annotation
     * @param cm
     */
    public abstract void generateAttribute(JAnnotationUse annotation, JCodeModel cm) throws CodeGeneratorException;
}
