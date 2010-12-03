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

package de.topicmapslab.codegenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.codemodel.JCodeModel;

import de.topicmapslab.codegenerator.descriptors.IClassContainer;
import de.topicmapslab.codegenerator.descriptors.PackageDescriptor;

/**
 * The codegenerator gets a set of package descriptions and generstes the code using JCodeModel.
 * 
 * @author Hannes Niederhausen
 *
 */
public class CodeGenerator {

	List<PackageDescriptor> packages;
	
	/**
	 * Generates the code using the configured {@link PackageDescriptor}.
	 * @param targetDirectory the target directory
	 * @throws CodeGeneratorException the exception if an error occurs
	 */
	public void generateCode(File targetDirectory) throws CodeGeneratorException {
		try {
			
	        JCodeModel codeModel = new JCodeModel();
	        
	        for (PackageDescriptor pd : getPackages()) {
	        	pd.generatePackage(codeModel);
	        }
	        
	        if (!targetDirectory.exists())
	        	targetDirectory.mkdirs();
	        codeModel.build(targetDirectory);
	        
        } catch (Exception e) {
	        throw new CodeGeneratorException(e);
        }
		
	}
	
	/**
     * @return the packages
     */
    public List<PackageDescriptor> getPackages() {
    	if (this.packages == null)
    		return Collections.emptyList();
    	return this.packages;
    }
    
    /**
     * 
     * @param pd
     */
    public void addPackage(PackageDescriptor pd) {
    	if (this.packages == null)
    		this.packages = new ArrayList<PackageDescriptor>();
    	this.packages.add(pd);
    }
    
    /**
     * 
     * @param pd
     */
    public void removePackage(IClassContainer pd) {
    	if (this.packages == null)
    		this.packages = new ArrayList<PackageDescriptor>();
    	this.packages.remove(pd);
    }
}
