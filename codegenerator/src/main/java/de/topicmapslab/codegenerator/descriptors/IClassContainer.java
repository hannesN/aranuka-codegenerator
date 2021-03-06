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

import java.util.List;

/**
 * Interface for classes and packages which may contain classes.
 * 
 * @author Hannes Niederhausen
 *
 */
public interface IClassContainer {

	/**
	 * Adds a child to the container
	 * 
	 * @param cd the {@link ClassDescriptor} to add
	 */
	public abstract void addChildClass(ClassDescriptor cd);

	/**
	 * Removes a child to the container
	 * 
	 * @param cd the {@link ClassDescriptor} to remove
	 */
	public abstract void removeChildClass(ClassDescriptor cd);

	/**
	 * @return the classes
	 */
	public abstract List<ClassDescriptor> getChildClasses();

	
	/**
	 * returns the name of the container
	 * @return
	 */
	public String getName();
	
	/**
	 * Returns the qualified name of the container.
	 * @return
	 */
	public String getQualifiedName();

}
