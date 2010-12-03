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

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.codegenerator.CodeGenerator;
import de.topicmapslab.codegenerator.CodeGeneratorException;
import de.topicmapslab.codegenerator.descriptors.AbstractAttributeDescriptor;
import de.topicmapslab.codegenerator.descriptors.AnnotationDescriptor;
import de.topicmapslab.codegenerator.descriptors.ClassDescriptor;
import de.topicmapslab.codegenerator.descriptors.EnumerationAttributeDescriptor;
import de.topicmapslab.codegenerator.descriptors.FieldDescriptor;
import de.topicmapslab.codegenerator.descriptors.MethodDescriptor;
import de.topicmapslab.codegenerator.descriptors.PackageDescriptor;
import de.topicmapslab.codegenerator.descriptors.ParameterDescriptor;
import de.topicmapslab.codegenerator.descriptors.PrimitiveArrayAttributeDescriptor;
import de.topicmapslab.codegenerator.descriptors.PrimitiveAttributeDescriptor;
import de.topicmapslab.codegenerator.descriptors.genny.ModelContainerDescriptor;
import de.topicmapslab.codegenerator.descriptors.genny.ModelContentProviderDescriptor;
import de.topicmapslab.codegenerator.descriptors.genny.ModelHandlerDescriptor;
import de.topicmapslab.kuria.runtime.widget.ListStyle;

/**
 * @author Hannes Niederhausen
 *
 */
public class CodeGeneratorTest {

	private CodeGenerator fixture;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.fixture = new CodeGenerator();
		
		PackageDescriptor pd = new PackageDescriptor(this.fixture);
		pd.setName("de.topicmapslab.test");
		
		ModelContentProviderDescriptor mcp = new ModelContentProviderDescriptor(pd);
		
		ClassDescriptor cd = new ClassDescriptor(pd);
		cd.setName("TestClass");
		
		FieldDescriptor fd = new FieldDescriptor(cd);
		fd.setName("name");
		fd.setType(String.class.getName());
		
		cd.addIdField(fd);
		
		fd = new FieldDescriptor(cd);
		fd.setName("number");
		fd.setType("int");
		fd.setMany(true);

		fd = new FieldDescriptor(cd);
		fd.setName("age");
		fd.setType(Integer.TYPE.getName());
		cd.addIdField(fd);
		
		
		MethodDescriptor md = new MethodDescriptor(cd);
		md.setName("getName");
		md.setReturnType(String.class.getName());
		
		ParameterDescriptor paramdsc = new ParameterDescriptor(md);
		paramdsc.setType("de.topicmapslab.test.OtherTestClass");
		paramdsc.setName("arg0");
		
		fd = new FieldDescriptor(cd);
		fd.setName("otherName");
		fd.setType(pd.getName()+".OtherTestClass");
		mcp.addSupportedField(fd);
		
		cd = new ClassDescriptor(pd);
		cd.setName("OtherTestClass");
		
		AnnotationDescriptor ad = new AnnotationDescriptor(cd);
		ad.setQualifiedName(Topic.class.getName());
		
		AbstractAttributeDescriptor aad = new PrimitiveAttributeDescriptor(ad);
		aad.setName("hm");
		aad.setValue("haha");
		
		aad = new PrimitiveAttributeDescriptor(ad);
		aad.setName("rows");
		aad.setValue(10);
		
		aad = new EnumerationAttributeDescriptor(ad);
		aad.setName("type");
		((EnumerationAttributeDescriptor) aad).setEnumerationType(ListStyle.class.getName());
		aad.setValue("COMPACT");
		
		
		
		
		fd = new FieldDescriptor(cd);
		fd.setName("name");
		fd.setType(String.class.getName());
		fd.setMany(true);

		ad = new AnnotationDescriptor(fd);
		ad.setQualifiedName(Name.class.getName());
		
		aad = new PrimitiveArrayAttributeDescriptor(ad);
		aad.setName("subject_identifiers");
		aad.setValue(new String[]{"test:name1", "test:name2"});
		
		fd = new FieldDescriptor(cd);
		fd.setName("age");
		fd.setType(Integer.TYPE.getName());
		
		
		
		md = new MethodDescriptor(cd);
		md.setName("getName");
		md.setReturnType(String.class.getName());
		
		ad = new AnnotationDescriptor(md);
		ad.setQualifiedName(Name.class.getName());
		
		aad = new PrimitiveArrayAttributeDescriptor(ad);
		aad.setName("some_number_stuff");
		aad.setValue(new Integer[]{5, 8, 298});
		
		
		paramdsc = new ParameterDescriptor(md);
		paramdsc.setType("de.topicmapslab.test.TestClass");
		paramdsc.setName("arg0");
	
		
		ModelHandlerDescriptor mh = new ModelHandlerDescriptor(pd);
		mh.addClassDescriptor(cd);
		mh.putName("test:test1", "TestName1");
		mh.putName("test:test2", "TestName2");
		
		ModelContainerDescriptor modelContainerDesciptor = new ModelContainerDescriptor(pd);
		modelContainerDesciptor.addModelDescriptor(cd);
		modelContainerDesciptor.putTitle(cd, "Kleine Tests");
		
		mh.addModelContainer(modelContainerDesciptor);
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		this.fixture = null;
	}

	/**
	 * Test method for {@link de.topicmapslab.codegenerator.CodeGenerator#generateCode()}.
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
	 * Test method for {@link de.topicmapslab.codegenerator.CodeGenerator#getPackages()}.
	 */
	@Test
	public void testGetPackages() {
		assertEquals(1, this.fixture.getPackages().size());
	}

	/**
	 * Test to add a primitive attribute
	 * 
	 * @throws CodeGeneratorException
	 */
	@Test(expected=CodeGeneratorException.class)
	public void testAddPrimitiveAttributes() throws CodeGeneratorException {
		CodeGenerator codegen = new CodeGenerator();
		PackageDescriptor pd = new PackageDescriptor(codegen);
		ClassDescriptor cd = new ClassDescriptor(pd);
		cd.setName("Test1");
		
		AnnotationDescriptor ad = new AnnotationDescriptor(cd);
		ad.setQualifiedName(Topic.class.getName());
		
		AbstractAttributeDescriptor aad = new PrimitiveAttributeDescriptor(ad);
		aad.setName("hm");
		aad.setValue("haha");
		
		assertEquals(1, ad.getAttributes().size());
		
		aad = new PrimitiveAttributeDescriptor(ad);
		aad.setName("rows");
		aad.setValue(10);
		assertEquals(2, ad.getAttributes().size());
		
		aad = new PrimitiveAttributeDescriptor(ad);
		aad.setName("type");
		aad.setValue(ListStyle.COMPACT);
		assertEquals(3, ad.getAttributes().size());
		
		File td = new File("/tmp/test");
		if (!td.exists()) {
			td.mkdir();
		}
		codegen.generateCode(td);
	}
}
