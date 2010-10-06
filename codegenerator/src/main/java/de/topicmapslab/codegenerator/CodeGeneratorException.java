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

/**
 * Exception thrown by the code generator.
 * 
 * @author Hannes Niederhausen
 *
 */
public class CodeGeneratorException extends Exception {
    private static final long serialVersionUID = -3742472216439273669L;

	/**
     * 
     */
    public CodeGeneratorException() {
	    super();
    }

	/**
     * @param message
     * @param cause
     */
    public CodeGeneratorException(String message, Throwable cause) {
	    super(message, cause);
    }

	/**
     * @param message
     */
    public CodeGeneratorException(String message) {
	    super(message);
    }

	/**
     * @param cause
     */
    public CodeGeneratorException(Throwable cause) {
	    super(cause);
    }

    
}
