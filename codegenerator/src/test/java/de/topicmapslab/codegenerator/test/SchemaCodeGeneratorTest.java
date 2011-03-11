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
 
package de.topicmapslab.codegenerator.test;

import java.io.File;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import de.topicmapslab.codegenerator.CodeGenerator;
import de.topicmapslab.codegenerator.CodeGeneratorException;
import de.topicmapslab.codegenerator.factories.AranukaDescriptorFactory;

/**
 * @author Hannes Niederhausen
 * 
 */
@Ignore
public class SchemaCodeGeneratorTest {

	private CodeGenerator fixture;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		InputStream is = getClass().getResourceAsStream("testschema.xtm");
		if (is == null)
			throw new RuntimeException("Could not open testschema.xtm!");
		AranukaDescriptorFactory fac = new AranukaDescriptorFactory("de.schematest", is, true, true);
		this.fixture = fac.getCodeGenerator(); 
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.codegenerator.CodeGenerator#generateCode()}.
	 * 
	 * @throws CodeGeneratorException
	 */
	@Test
	public void testGenerateCode() throws CodeGeneratorException {
		File td = new File("/tmp/test");
		if (!td.exists()) {
			td.mkdir();
		}
		this.fixture.generateCode(td);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

}
