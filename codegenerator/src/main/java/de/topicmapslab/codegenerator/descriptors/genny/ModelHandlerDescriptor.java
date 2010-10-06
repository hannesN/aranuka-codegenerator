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
 
package de.topicmapslab.codegenerator.descriptors.genny;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

import de.topicmapslab.aranuka.Session;
import de.topicmapslab.codegenerator.descriptors.ClassDescriptor;
import de.topicmapslab.codegenerator.descriptors.FieldDescriptor;
import de.topicmapslab.codegenerator.descriptors.PackageDescriptor;

/**
 * @author Hannes Niederhausen
 * 
 */
public class ModelHandlerDescriptor extends ClassDescriptor {

	private List<ModelContainerDescriptor> modelContainerList;
	
	private List<ClassDescriptor> classes;

	private Map<String, String> names;

	/**
	 * @param parent
	 */
	public ModelHandlerDescriptor(PackageDescriptor parent) {
		super(parent);
		init();

	}

	/**
     * @return the modelContainer
     */
    public List<ModelContainerDescriptor> getModelContainer() {
    	if (modelContainerList==null)
    		return Collections.emptyList();
	    return modelContainerList;
    }
    
    public void addModelContainer(ModelContainerDescriptor mcd) {
    	if (modelContainerList==null)
    		modelContainerList = new ArrayList<ModelContainerDescriptor>();
    	modelContainerList.add(mcd);
    }
    
    public void removeModelContainer(ModelContainerDescriptor mcd) {
    	if (modelContainerList==null)
    		modelContainerList = new ArrayList<ModelContainerDescriptor>();
    	modelContainerList.add(mcd);
    }
	
	/**
	 * @return the classes
	 */
	public List<ClassDescriptor> getClassDescriptors() {
		if (this.classes == null)
			return Collections.emptyList();
		return this.classes;
	}

	/**
	 * Adds a class descriptor for a class which should be added to the CLASS_SET
	 * @param cd the added {@link ClassDescriptor}
	 */
	public void addClassDescriptor(ClassDescriptor cd) {
		if (this.classes == null)
			this.classes = new ArrayList<ClassDescriptor>();
		this.classes.add(cd);
	}

	public void removeClassDescriptor(ClassDescriptor cd) {
		if (this.classes != null)
			this.classes.add(cd);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addChildClass(ClassDescriptor cd) {
	    // TODO Auto-generated method stub
	    super.addChildClass(cd);
	}
	
	/**
	 * @return the names
	 */
	public Map<String, String> getNames() {
		if (this.names == null)
			return Collections.emptyMap();
		return this.names;
	}

	public void putName(String si, String name) {
		if (this.names == null)
			this.names = new HashMap<String, String>();
		this.names.put(si, name);
	}

	public void removeName(String si) {
		if (this.names != null)
			this.names.remove(si);
	}

	/**
	 * @param names
	 *            the names to set
	 */
	public void setNames(Map<String, String> names) {
		this.names = names;
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public void generateClass(JClassContainer pack, JCodeModel cm) throws Exception {
    	super.generateClass(pack, cm);
    
    	JDefinedClass c = cm._getClass(getQualifiedName());
    
    	// generate names map
    	generateStaticParts(c, cm);
    	// generate the methods for the
    	generateMethods(c, cm);
    }

	/**
     * 
     */
	private void init() {
		setName("ModelHandler");
		addImplementInterface("de.topicmapslab.genericeditor.application.model.IModelHandler");

		// generating configuration field
		new FieldDescriptor(this, false, "configuration", "de.topicmapslab.aranuka.Configuration", false);

		// generating session
		new FieldDescriptor(this, false, "session", "de.topicmapslab.aranuka.Session", false);

		// generating usingDatabase flag
		new FieldDescriptor(this, true, "usingDatabase", "boolean", false);

		// generating usingDatabase flag
		new FieldDescriptor(this, true, "properties", "java.util.Properties", false);

	}

	/**
	 * Generates the methods:
	 * 
	 * @param c
	 *            the JClass instance which will contain the methods
	 * @param cm
	 *            the {@link JCodeModel} instance which genrated the class
	 */
	private void generateMethods(JDefinedClass c, JCodeModel cm) {
		// helper types and fields
		// JClass properties =
		// cm.ref("de.topicmapslab.aranuka.connectors.IProperties");
		// JClass stringType = cm.ref(String.class);
		// JFieldVar conf = c.fields().get("configuration");
		// JFieldVar session = c.fields().get("session");

		// reset method
		generateResetMethod(c, cm);

		// clean session method
		generateCleanSessionMethod(c, cm);

		// return conf if not null else createConfiguration first
		generateGetConfigurationMethod(c, cm);

		// prefix methods
		generateAddPrefixMethod(c, cm);
		generateRemovePrefixMethod(c, cm);

		// filename methods
		generateSetFilenameMethod(c, cm);
		generateGetFilenameMethod(c, cm);

		// set base locator
		generateSetBaseLocatorMethod(c, cm);

		// getSession
		generateGetSessionMethod(c, cm);

		// getModelContainer
		generateGetModelContainerMethod(c, cm);

		// getContentProvider
		getContentProviderMethod(c, cm);

		// createConfiguration
		generateCreateConfigurationMethod(c, cm);
		
		// generate getCategories
		generateGetCategories(c, cm);
	}

	/**
     * @param c
     * @param cm
     */
    private void generateCreateConfigurationMethod(JDefinedClass c, JCodeModel cm) {
	    // helper types and fields
		JClass properties = cm.ref("de.topicmapslab.aranuka.connectors.IProperties");
		JClass classType = cm.ref(Class.class).narrow(cm.ref("?"));

		JFieldVar conf = c.fields().get("configuration");
		JFieldVar props = c.fields().get("properties");

		JFieldRef thisConf = JExpr._this().ref(conf);
		JFieldRef thisProperties = JExpr._this().ref(props);

		JMethod m = c.method(JMod.PRIVATE, cm.VOID, "createConfiguration");
		JBlock body = m.body();

		// Instantiate configuration and set properties
		body.assign(thisConf, JExpr._new(conf.type()));
		body._if(thisProperties.ne(JExpr._null()))._then().invoke(thisConf, "setProperties").arg(thisProperties);

		body.directStatement("// set connector for aranuka - using MaJorToM");
		body.invoke(thisConf, "setProperty").arg(properties.staticRef("CONNECTOR_CLASS"))
		        .arg("de.topicmapslab.aranuka.majortom.connector.MaJorToMEngineConnector");

		body.directStatement("// set classes with mapping annotations - ignoring the model container");
		// foreach block adding every class to the configuration
		JForEach forEach = body.forEach(classType, "c", JExpr.invoke("getClassSet"));
		JBlock forEachBody = forEach.body();
		
		// check if the current class in in the CONTAINER_MAP
		JFieldVar containerMap = c.fields().get("CONTAINER_MAP");
		
		JInvocation condition = containerMap.invoke("containsValue").arg(forEach.var());
		forEachBody._if(condition.not())._then().invoke(thisConf, "addClass")
		        .arg(forEach.var());

		// adding names map
		body.invoke(thisConf, "setNameMap").arg(JExpr.invoke("getNameMap"));
    }

	/**
	 * @param c
	 * @param cm
	 */
	private void getContentProviderMethod(JDefinedClass c, JCodeModel cm) {
		// helper
		JClass contentProviderType = cm.ref("de.topicmapslab.kuria.swtgenerator.edit.IContentProvider");
		JClass modelContentProviderType = cm.ref(getParent().getName() + ".ModelContentProvider");
		JClass modelContainerType = cm.ref(getParent().getName() + ".ModelContainer");
		// Class<?>
		JClass classType = cm.ref(Class.class).narrow(cm.ref("?"));

		JMethod m = c.method(JMod.PUBLIC, contentProviderType, "getContentProvider");
		m.param(classType, "clazz");
		JInvocation invoke = JExpr.invoke("getModelContainer").arg(getModelContainer().get(0).getCategory());
		JInvocation newInvoke = JExpr._new(modelContentProviderType).arg(JExpr.cast(modelContainerType, invoke));
		m.body()._return(newInvoke);
	}

	/**
	 * @param c
	 * @param cm
	 */
	private void generateGetModelContainerMethod(JDefinedClass c, JCodeModel cm) {
		// helper types and fields
		JFieldVar containerMap =  c.fields().get("CONTAINER_MAP");
		
		
		JMethod m = c.method(JMod.PUBLIC, cm.ref("de.topicmapslab.genericeditor.application.model.IModelContainer"), "getModelContainer");
		JVar p = m.param(String.class, "category");
		
		JInvocation arg = containerMap.invoke("get").arg(p).invoke("getConstructor").arg(JExpr.dotclass(cm.ref(Session.class)));
		JInvocation getSessionInvocation = JExpr.invoke("getSession");
		
		
		JTryBlock tryBlock = m.body()._try();
		tryBlock.body()._return(arg.invoke("newInstance").arg(getSessionInvocation));
		JBlock catchBody = tryBlock._catch(cm.ref(Exception.class)).body();
		catchBody._throw(JExpr._new(cm.ref(RuntimeException.class)).arg(JExpr.direct("\"Modelcontainer could not be created for category:\" +category")));
	}
	
	private void generateGetCategories(JDefinedClass c, JCodeModel cm) {
		
		JClass type = cm.ref(List.class).narrow(String.class);
		JMethod m = c.method(JMod.PUBLIC, type, "getCategories");
		
		JClass arraysRef = cm.ref(Arrays.class);

		JInvocation staticInvoke = arraysRef.staticInvoke("asList");
		for(ModelContainerDescriptor mcd : getModelContainer()) {
			staticInvoke.arg(mcd.getCategory());
		}
		
		m.body()._return(staticInvoke);
	}

	/**
	 * @param c
	 * @param cm
	 */
	private void generateGetSessionMethod(JDefinedClass c, JCodeModel cm) {
		// helpers
		JFieldVar session = c.fields().get("session");
		JFieldRef thisSession = JExpr._this().ref(session);

		JMethod m = c.method(JMod.PUBLIC, session.type(), "getSession");
		JTryBlock tryBlock = m.body()._try();

		tryBlock.body()._if(thisSession.eq(JExpr._null()))._then()
		        .assign(thisSession, JExpr.invoke("getConfiguration").invoke("getSession").arg(JExpr.FALSE));
		tryBlock.body()._return(thisSession);
		JCatchBlock catchBlock = tryBlock._catch(cm.ref(Exception.class));
		JVar p = catchBlock.param("e");
		catchBlock.body()._throw(JExpr._new(cm.ref(RuntimeException.class)).arg(p));
	}

	/**
	 * @param c
	 * @param cm
	 */
	private void generateSetBaseLocatorMethod(JDefinedClass c, JCodeModel cm) {
		// helper types and fields
		JClass properties = cm.ref("de.topicmapslab.aranuka.connectors.IProperties");
		JClass stringType = cm.ref(String.class);
		JFieldVar conf = c.fields().get("configuration");
		JFieldVar session = c.fields().get("session");
		JClass exc = cm.ref(IllegalArgumentException.class);

		JMethod m = c.method(JMod.PUBLIC, cm.VOID, "setBaseLocator");
		JVar p = m.param(stringType, "baseLocator");
		JBlock body = m.body();
		JBlock thenBlock = body._if(JExpr._this().ref(session).ne(JExpr._null()))._then();
		thenBlock._throw(JExpr._new(exc).arg("Session already created"));
		JExpression condition = JExpr._this().ref(conf).eq(JExpr._null());
		m.body()._if(condition)._then().invoke("createConfiguration");
		m.body().invoke(JExpr._this().ref(conf), "setProperty").arg(properties.staticRef("BASE_LOCATOR")).arg(p);
	}

	/**
	 * @param c
	 * @param stringType
	 * @param conf
	 */
	private void generateRemovePrefixMethod(JDefinedClass c, JCodeModel cm) {
		// helpers
		JClass stringType = cm.ref(String.class);
		JFieldVar conf = c.fields().get("configuration");

		// removePrefix
		JMethod m = c.method(JMod.PUBLIC, cm.VOID, "removePrefix");
		m.param(stringType, "prefix");
		m.param(stringType, "uri");
		JExpression condition2 = JExpr._this().ref(conf).eq(JExpr._null());
		m.body()._if(condition2)._then().invoke("createConfiguration");

		m.body().directStatement("// TODO prefix removal");
		// removePrefix.body().invoke(JExpr._this().ref(conf),
		// "removePrefix").arg(p1).arg(p2);
	}

	/**
	 * @param c
	 * @param properties
	 * @param stringType
	 * @param conf
	 */
	private void generateGetFilenameMethod(JDefinedClass c, JCodeModel cm) {
		// helpers
		JClass stringType = cm.ref(String.class);
		JFieldVar conf = c.fields().get("configuration");
		JClass properties = cm.ref("de.topicmapslab.aranuka.connectors.IProperties");

		// get filename
		JMethod m = c.method(JMod.PUBLIC, stringType, "getFilename");
		JExpression condition = JExpr._this().ref(conf).ne(JExpr._null());
		m.body()._if(condition)._then()
		        ._return(JExpr.invoke(JExpr._this().ref(conf), "getProperty").arg(properties.staticRef("FILENAME")));
		m.body()._return(JExpr._null());
	}

	/**
	 * @param c
	 * @param cm
	 * @param properties
	 * @param stringType
	 * @param conf
	 */
	private void generateSetFilenameMethod(JDefinedClass c, JCodeModel cm) {
		// helper types and fields
		JClass properties = cm.ref("de.topicmapslab.aranuka.connectors.IProperties");
		JClass stringType = cm.ref(String.class);
		JFieldVar conf = c.fields().get("configuration");
		// set the filename
		JMethod m = c.method(JMod.PUBLIC, cm.VOID, "setFilename");
		JVar param = m.param(stringType, "filename");
		JExpression condition = JExpr._this().ref(conf).eq(JExpr._null());
		m.body()._if(condition)._then().invoke("createConfiguration");
		m.body().invoke(JExpr._this().ref(conf), "setProperty").arg(properties.staticRef("FILENAME")).arg(param);
	}

	/**
	 * @param c
	 * @param condition
	 * @param conf
	 * @param stringType
	 */
	private void generateAddPrefixMethod(JDefinedClass c, JCodeModel cm) {
		// some helper
		JClass stringType = cm.ref(String.class);
		JFieldVar conf = c.fields().get("configuration");

		JMethod m = c.method(JMod.PUBLIC, cm.VOID, "addPrefix");
		JVar p1 = m.param(stringType, "prefix");
		JVar p2 = m.param(stringType, "uri");
		JExpression condition = JExpr._this().ref(conf).eq(JExpr._null());
		m.body()._if(condition)._then().invoke("createConfiguration");
		m.body().invoke(JExpr._this().ref(conf), "addPrefix").arg(p1).arg(p2);
	}

	/**
	 * @param c
	 * @param conf
	 * @return
	 */
	private JExpression generateGetConfigurationMethod(JDefinedClass c, JCodeModel cm) {
		// helper
		JFieldVar conf = c.fields().get("configuration");

		JMethod m = c.method(JMod.PUBLIC, conf.type(), "getConfiguration");
		JExpression condition = JExpr._this().ref(conf).eq(JExpr._null());
		m.body()._if(condition)._then().invoke("createConfiguration");
		m.body()._return(JExpr._this().ref(conf));
		return condition;
	}

	/**
	 * @param c
	 * @param cm
	 */
	private void generateCleanSessionMethod(JDefinedClass c, JCodeModel cm) {
		JMethod cleanSession = c.method(JMod.PUBLIC, cm.VOID, "cleanSession");
		cleanSession.body().assign(JExpr._this().ref("session"), JExpr._null());
	}

	/**
	 * @param c
	 * @param cm
	 */
	private void generateResetMethod(JDefinedClass c, JCodeModel cm) {
		JMethod reset = c.method(JMod.PUBLIC, cm.VOID, "reset");
		reset.body().assign(JExpr._this().ref("session"), JExpr._null());
		reset.body().assign(JExpr._this().ref("configuration"), JExpr._null());
	}

	/**
	 * Generates the static fields CLASS_SET and NAMES and creates the static
	 * 
	 * @param c
	 * @param cm
	 */
	private void generateStaticParts(JDefinedClass c, JCodeModel cm) {
		// generating class set
		JClass type = cm.ref(Set.class).narrow(cm.ref(Class.class).narrow(cm.ref("?")));
		JFieldVar classSetField = c.field(JMod.PRIVATE | JMod.STATIC, type, "CLASS_SET");

		// generating names map
		type = cm.ref(String.class); // get string ref
		// create a Map<String, String>
		type = cm.ref(Map.class).narrow(Arrays.asList(type, type));
		JFieldVar namesField = c.field(JMod.PRIVATE | JMod.STATIC, type, "NAMES");

		// generating modelcontainer map
		
		// to get import ;)
		cm.ref("de.topicmapslab.genericeditor.application.model.IModelContainer");
		JClass clazzType = cm.ref(Class.class).narrow(cm.ref("? extends IModelContainer"));
		type = cm.ref(Map.class).narrow(Arrays.asList(cm.ref(String.class), clazzType));
		JFieldVar containerMap = c.field(JMod.PRIVATE|JMod.STATIC, type, "CONTAINER_MAP");
		
		// generating static init block
		JBlock initBlock = c.init();

		initBlock.directStatement("// creating modelcontainer map and fill it with the categories and model container class");

		JClass mapType = cm.ref(HashMap.class).narrow(Arrays.asList(cm.ref(String.class), clazzType));
		initBlock.assign(containerMap, JExpr._new(mapType));
		for (ModelContainerDescriptor mcd : getModelContainer()) {
			JClass classRef = cm.ref(mcd.getQualifiedName());
			initBlock.invoke(containerMap, "put").arg(mcd.getCategory()).arg(classRef.dotclass());
		}
		
		
		initBlock.directStatement("// creating class set and fill it with the annotated model classes");

		// init class set and instantiate it
		JClass setType = cm.ref("java.util.HashSet").narrow(cm.ref(Class.class).narrow(cm.ref("?")));
		JExpression newHashSet = JExpr._new(setType);
		initBlock.assign(classSetField, newHashSet);
		
		// add all modelContainers
		initBlock.add(classSetField.invoke("addAll").arg(containerMap.invoke("values")));

		for (ClassDescriptor cd : getClassDescriptors()) {
			JClass classSetEntry = cm._getClass(cd.getQualifiedName());
			if (classSetEntry==null)
				classSetEntry = cm.ref(cd.getQualifiedName());
			initBlock.add(classSetField.invoke("add").arg(classSetEntry.dotclass()));
		}
		
		initBlock.directStatement("// creating name map and fill it with the names of the used topic types");

		// init names map
		mapType = cm.ref(String.class); // get string ref
		// create a Map<String, String>
		mapType = cm.ref(HashMap.class).narrow(Arrays.asList(mapType, mapType));
		initBlock.assign(namesField, JExpr._new(mapType));
		for (Entry<String, String> entrySet : getNames().entrySet()) {
			initBlock.add(namesField.invoke("put").arg(entrySet.getKey()).arg(entrySet.getValue()));
		}
		
		// getter for NAMES
		JMethod getNames = c.method(JMod.PUBLIC, c.fields().get("NAMES").type(), "getNameMap");
		getNames.body().directStatement("return ModelHandler.NAMES;");

		// getter for CLASS_SET
		JMethod getClassSet = c.method(JMod.PUBLIC, c.fields().get("CLASS_SET").type(), "getClassSet");
		getClassSet.body().directStatement("return ModelHandler.CLASS_SET;");

	}
}
