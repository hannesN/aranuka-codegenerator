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

import com.sun.codemodel.JMod;

/**
 * @author bosso
 *
 */
public class AbstractModifiedDescriptor {

	protected boolean _abstract;
	protected boolean _public;
	protected boolean _protected;
	protected boolean _private;

	/**
	 * 
	 */
	public AbstractModifiedDescriptor() {
		super();
		this._abstract = false;
		this._private  = false;
		this._protected = false;
		this._public = true;
	}

	/**
     * @return the _abstract
     */
    public boolean isAbstract() {
    	return _abstract;
    }

	/**
     * @param _abstract
     *            the _abstract to set
     */
    public void setAbstract(boolean _abstract) {
    	this._abstract = _abstract;
    }

	/**
     * @return the _protected
     */
    public boolean isProtected() {
    	return _protected;
    }

	/**
     * @param _protected
     *            the _protected to set
     */
    public void setProtected(boolean _protected) {
    	this._protected = _protected;
    }

	/**
     * @return the _private
     */
    public boolean isPrivate() {
    	return _private;
    }

	/**
     * @param _private
     *            the _private to set
     */
    public void setPrivate(boolean _private) {
    	this._private = _private;
    }

	/**
     * @return the _public
     */
    public boolean isPublic() {
    	return _public;
    }

	/**
     * @param _public
     *            the _public to set
     */
    public void setPublic(boolean _public) {
    	this._public = _public;
    }

    /**
     * @return
     */
    protected int getMods() {
	    int mods = 0;
		if (isPrivate()) {
			mods |= JMod.PRIVATE;
		} else if (isPublic()) {
			mods |= JMod.PUBLIC;
		} else if (isProtected()) {
			mods |= JMod.PROTECTED;
		}
	    return mods;
    }
}
