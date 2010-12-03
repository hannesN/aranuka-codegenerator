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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

import de.topicmapslab.aranuka.Session;
import de.topicmapslab.aranuka.exception.AranukaException;
import de.topicmapslab.codegenerator.descriptors.ClassDescriptor;
import de.topicmapslab.codegenerator.descriptors.PackageDescriptor;
import de.topicmapslab.kuria.annotation.Text;
import de.topicmapslab.kuria.annotation.tree.Children;
import de.topicmapslab.kuria.annotation.tree.TreeNode;

/**
 * Class which represents a model container
 * 
 * @author Hannes Niederhausen
 * 
 */
public class ModelContainerDescriptor extends ClassDescriptor {

	private String category;
	private List<ClassDescriptor> modelClasses;
	private Map<ClassDescriptor, String> titleMap;

	/**
	 * @param parent
	 */
	public ModelContainerDescriptor(PackageDescriptor parent) {
		this(parent, "ModelContainer", "Model");
	}

	/**
	 * @param parent
	 * @param category
	 * @param modelClasses
	 */
	public ModelContainerDescriptor(PackageDescriptor parent, String name, String category) {
		super(parent);
		setName(name);
		this.category = category;
		addImplementInterface("de.topicmapslab.genericeditor.application.model.IModelContainer");
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the modelClasses
	 */
	public List<ClassDescriptor> getModelClasses() {
		if (modelClasses == null)
			return Collections.emptyList();
		return modelClasses;
	}

	public void addModelDescriptor(ClassDescriptor cd) {
		if (modelClasses == null)
			modelClasses = new ArrayList<ClassDescriptor>();
		modelClasses.add(cd);
	}

	public void removeModelDescriptor(ClassDescriptor cd) {
		if (modelClasses == null)
			modelClasses.remove(cd);
	}
	
	/**
     * @return the titleMap
     */
    public Map<ClassDescriptor, String> getTitleMap() {
    	if (titleMap==null)
    		return Collections.emptyMap();
    	return titleMap;
    }
    
    public void putTitle(ClassDescriptor key, String title) {
    	if (titleMap == null)
    		titleMap = new HashMap<ClassDescriptor, String>();
    	titleMap.put(key, title);
    }
    
    public void removeTitle(ClassDescriptor key) {
    	if (titleMap != null)
    		titleMap.remove(key);
    }
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generateClass(JClassContainer pack, JCodeModel cm) throws Exception {
		super.generateClass(pack, cm);
		JDefinedClass c = cm._getClass(getQualifiedName());

		c.annotate(cm.ref(TreeNode.class));
		
		// generating session field
		JFieldVar s = c.field(JMod.TRANSIENT | JMod.FINAL | JMod.PRIVATE, cm.ref(Session.class),
		        "session");
		JFieldRef sessionRef = JExpr._this().ref(s);

		// generating ArrayList Type
		JClass arrayListType = cm.ref(ArrayList.class);
		
		// generating constructor
		JMethod constructor = c.constructor(JMod.PUBLIC);
		JVar p = constructor.param(s.type(), "session");
		constructor.body().assign(sessionRef, p);
		
		// generating general get method
		JClass retType = cm.ref(List.class).narrow(cm.ref("?"));
		JMethod getMethod = c.method(JMod.PUBLIC, retType, "get");
		p = getMethod.param(cm.ref(Class.class).narrow(cm.ref("?")), "type");
		
		// set body (session.getAll)
		JExpression retrieveExpr = sessionRef.invoke("getAll").arg(p); 
		JExpression returnExpr = JExpr._new(arrayListType).arg(retrieveExpr);

		JTryBlock tryBlock = getMethod.body()._try();
		tryBlock.body()._return(returnExpr);
		
		
		JClass aranukaExc = cm.directClass(AranukaException.class.getName());
		JCatchBlock catchBlock = tryBlock._catch(aranukaExc);
		JVar catchParam = catchBlock.param("e");
		JExpression runtimeException = JExpr._new(cm._ref(RuntimeException.class)).arg(catchParam);
		catchBlock.body()._throw(runtimeException);
	    
		JAnnotationUse annotate = getMethod.annotate(SuppressWarnings.class);
		JAnnotationArrayMember param = annotate.paramArray("value");
		param.param("unchecked");
		param.param("rawtypes");
		
		
		
		// generating method for every type
		
		for (ClassDescriptor cd : getModelClasses()) {
			JClass classType = cm.ref(cd.getQualifiedName());
			JClass type = cm.ref(List.class).narrow(classType);
			JMethod m = c.method(JMod.PUBLIC, type, getMethodName(cd.getName()));
			
			// set body (session.getAll)
			JInvocation retExpr = JExpr.invoke(getMethod).arg(classType.staticRef("class"));
			m.body()._return(JExpr.cast(type, retExpr));
			
			// add children annotation
			m.annotate(Children.class).param("title", getTitle(cd));
		    annotate = m.annotate(SuppressWarnings.class);
			annotate.param("value", "unchecked");
			
		}
		
		// adding text method :-) use category as models
		JMethod m = c.method(JMod.PUBLIC, cm.ref(String.class), "getText");
		m.body()._return(JExpr.direct("\""+getCategory()+"\""));
		m.annotate(Text.class);
		m.annotate(Override.class);
		
		
		
	}

	/**
     * @param cd
     * @return
     */
    private String getTitle(ClassDescriptor cd) {
    	String title = getTitleMap().get(cd);
    	if (title==null)
    		return Character.toUpperCase(cd.getName().charAt(0)) + cd.getName().substring(1);
	    return title;
    }

	/**
	 * @param name
	 * @return
	 */
	private String getMethodName(String name) {
		return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

}
