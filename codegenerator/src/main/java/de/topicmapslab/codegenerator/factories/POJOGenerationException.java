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
package de.topicmapslab.codegenerator.factories;

/**
 * 
 * @author Sven Krosse
 *
 */
public class POJOGenerationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public POJOGenerationException() {
	}

	/**
	 * Constructor
	 * 
	 * @param message the message
	 */
	public POJOGenerationException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause the causing exception
	 */
	public POJOGenerationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor
	 * @param message the message
	 * @param cause the causing exception
	 */
	public POJOGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

}
