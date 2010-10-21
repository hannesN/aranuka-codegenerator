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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;

import de.topicmapslab.codegenerator.CodeGenerator;

/**
 * @author Hannes Niederhausen
 *
 */
public class PackageDescriptor implements IClassContainer {

	private String name;
	
	private List<ClassDescriptor> classes;
	/**
     * @param codegen
     */
    public PackageDescriptor(CodeGenerator codegen) {
	    codegen.addPackage(this);
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the qualified name 
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
     * {@inheritDoc}
     */
	@Override
    public void addChildClass(ClassDescriptor cd) {
		if (classes==null)
			classes = new ArrayList<ClassDescriptor>();
		classes.add(cd);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
    public void removeChildClass(ClassDescriptor cd) {
		if (classes!=null)
			classes.remove(cd);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
    public List<ClassDescriptor> getChildClasses() {
		if (classes==null)
			return Collections.emptyList();
		return classes;
	}
	
	public void generatePackage(JCodeModel cm) throws Exception {
		JPackage pack = cm._package(name);
		
		for (ClassDescriptor cd : getChildClasses()) {
			cd.generateClass(pack, cm);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getQualifiedName() {
		return getName();
	}
}
