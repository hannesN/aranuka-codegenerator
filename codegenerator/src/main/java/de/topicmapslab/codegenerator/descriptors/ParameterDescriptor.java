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

import static de.topicmapslab.codegenerator.utils.DescriptorUtil.getCodeModeltype;
import static de.topicmapslab.codegenerator.utils.DescriptorUtil.getPrimitiveType;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JMethod;
/**
 * @author Hannes Niederhausen
 *
 */
public class ParameterDescriptor {

	private String name;
	private String type;
	
	private MethodDescriptor parent;
	
	private boolean many;
	
	public ParameterDescriptor(MethodDescriptor parent) {
		this.parent = parent;
		this.parent.addParameter(this);
    }

	/**
     * @return the name
     */
    public String getName() {
    	return name;
    }

	/**
     * @param name the name to set
     */
    public void setName(String name) {
    	this.name = name;
    }
    
    /**
     * @return the many
     */
    public boolean isMany() {
	    return many;
    }
    
    /**
     * @param many the many to set
     */
    public void setMany(boolean many) {
	    this.many = many;
    }

	/**
     * @return the type
     */
    public String getType() {
    	return type;
    }

	/**
     * @param type the type to set
     */
    public void setType(String type) {
    	this.type = type;
    }
	
	public void generateParameter(JMethod method, JCodeModel cm) {
		JClass typeClass = getCodeModeltype(cm, getType(), isMany());
		
		if (typeClass==null) {
			method.param(getPrimitiveType(getType()), getName());
		} else {
			method.param(typeClass, getName());
		}
		
	}

}
