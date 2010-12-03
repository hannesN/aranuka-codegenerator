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

import static de.topicmapslab.codegenerator.utils.TMQLPreperator.getIdentifierString;
import static de.topicmapslab.codegenerator.utils.TMQLPreperator.getTMQLIdentifierString;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.AssociationContainer;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Role;
import de.topicmapslab.aranuka.enummerations.IdType;
import de.topicmapslab.codegenerator.CodeGenerator;
import de.topicmapslab.codegenerator.descriptors.AnnotationDescriptor;
import de.topicmapslab.codegenerator.descriptors.ClassDescriptor;
import de.topicmapslab.codegenerator.descriptors.EnumerationAttributeDescriptor;
import de.topicmapslab.codegenerator.descriptors.FieldDescriptor;
import de.topicmapslab.codegenerator.descriptors.PackageDescriptor;
import de.topicmapslab.codegenerator.descriptors.PrimitiveArrayAttributeDescriptor;
import de.topicmapslab.codegenerator.descriptors.PrimitiveAttributeDescriptor;
import de.topicmapslab.codegenerator.descriptors.genny.ModelContainerDescriptor;
import de.topicmapslab.codegenerator.descriptors.genny.ModelContentProviderDescriptor;
import de.topicmapslab.codegenerator.descriptors.genny.ModelHandlerDescriptor;
import de.topicmapslab.codegenerator.utils.TMQLPreperator;
import de.topicmapslab.codegenerator.utils.TypeUtility;
import de.topicmapslab.tmql4j.common.context.TMQLRuntimeProperties;
import de.topicmapslab.tmql4j.common.core.runtime.TMQLRuntimeFactory;
import de.topicmapslab.tmql4j.common.model.query.IQuery;
import de.topicmapslab.tmql4j.common.model.runtime.ITMQLRuntime;
import de.topicmapslab.tmql4j.resultprocessing.core.simple.SimpleResultSet;
import de.topicmapslab.tmql4j.resultprocessing.core.simple.SimpleTupleResult;
import de.topicmapslab.tmql4j.resultprocessing.model.IResult;
import de.topicmapslab.tmql4j.resultprocessing.model.IResultSet;

/**
 * @author Hannes Niederhausen
 * 
 */
public class AranukaDescriptorFactory {

	private static Logger logger = LoggerFactory.getLogger(AranukaDescriptorFactory.class);

	private final boolean createGennyClasses;
	private final boolean createKuriaAnnotation;

	private PackageDescriptor pkgDescriptor;
	private ITMQLRuntime runtime;
	private CodeGenerator codeGenerator;
	private IKuriaDescriptorFactory kuriaFactory;

	private ModelHandlerDescriptor modelHandlerDescriptor;
	private ModelContentProviderDescriptor modelContentProviderDescriptor;
	private ModelContainerDescriptor modelContainerDescriptor;
	private Map<String, ModelContainerDescriptor> modelContainerMap;
	private Map<String, ClassDescriptor> classesMap;

	private Map<Topic, ClassDescriptor> parsedTopics = new HashMap<Topic, ClassDescriptor>();

	/**
	 * Constructor
	 * 
	 * @param packageName name for the package to create. All generated classes will be added in this package
	 * @param filename the name of the xtm file containing the schema
	 * @throws TMAPIException
	 * @throws IOException
	 * @throws IllegalSchemaException
	 */
	public AranukaDescriptorFactory(String packageName, String filename) throws TMAPIException, IOException,
	        IllegalSchemaException {
		this(packageName, new FileInputStream(filename), true, true);
	}

	/**
	 * 
	 * Constructor
	 * 
	 * @param tms the {@link TopicMapSystem} used to create the topic map
	 * @param tm the topic map containing the schema
	 * @param packageName  packageName name for the package to create. All generated classes will be added in this package
	 * @param createGennyClasses flag wether to create genny classes
	 * @param createKuriaAnnotation flag wether to genrate Kuria annotations
	 * @throws TMAPIException
	 * @throws IOException
	 * @throws IllegalSchemaException
	 */
	public AranukaDescriptorFactory(TopicMapSystem tms, TopicMap tm, String packageName, boolean createGennyClasses,
	        boolean createKuriaAnnotation) throws TMAPIException, IOException, IllegalSchemaException {
		runtime = TMQLRuntimeFactory.newFactory().newRuntime(tms, tm);
		runtime.getProperties().setProperty(TMQLRuntimeProperties.RESULT_SET_IMPLEMENTATION_CLASS,
		        SimpleResultSet.class.getName());
		runtime.getProperties().setProperty(TMQLRuntimeProperties.RESULT_TUPLE_IMPLEMENTATION_CLASS,
		        SimpleTupleResult.class.getName());
		this.createGennyClasses = createGennyClasses;
		this.createKuriaAnnotation = createKuriaAnnotation;
		init(packageName);
	}

	/**
	 * @throws IOException
	 * @throws TMAPIException
	 * @throws IllegalSchemaException
	 * 
	 */
	public AranukaDescriptorFactory(String packageName, InputStream is, boolean createGennyClasses,
	        boolean createKuriaAnnotation) throws TMAPIException, IOException, IllegalSchemaException {
		runtime = TMQLPreperator.createRuntime(is);
		this.createGennyClasses = createGennyClasses;
		this.createKuriaAnnotation = createKuriaAnnotation;
		init(packageName);
	}

	/**
	 * @return the codeGenerator
	 */
	public CodeGenerator getCodeGenerator() {
		return codeGenerator;
	}

	protected ClassDescriptor parseTopicType(Topic t) throws IllegalSchemaException {

		if (parsedTopics.containsKey(t))
			return parsedTopics.get(t);

		
		String queryString = "FOR $t IN // tmcl:topic-type [ .  == " +getTMQLIdentifierString(t)+ " ] "
	        + "RETURN $t, $t >> characteristics  tm:name >> atomify[0], $t >> indicators >> atomify[0]";
		IQuery query = runtime.run(queryString);
		
		IResult r = query.getResults().get(0);
		
		if (r.size()!=3)
			throw new IllegalSchemaException("Topic has no subject identifier or name");
		addNameMapEntry((String) r.getResults().get(2), (String) r.getResults().get(1));
		

		// parse identifier
		if (t.getSubjectIdentifiers().size() == 0)
			throw new IllegalSchemaException("The topic: " + r.getResults().get(1) + " has no identifier");

		ClassDescriptor cd = generateClassDescriptor(t);

		kuriaFactory.addKuriaAnnotations(cd, t);

		return cd;
	}

	/**
     * @param packageName
     * @param is
     * @throws TMAPIException
     * @throws IOException
     * @throws IllegalSchemaException
     */
    private void init(String packageName) throws TMAPIException, IOException, IllegalSchemaException {
    	codeGenerator = new CodeGenerator();
    	
    	classesMap = new HashMap<String, ClassDescriptor>();
    	
    	pkgDescriptor = new PackageDescriptor(codeGenerator);
    	pkgDescriptor.setName(packageName);
    
    	if ((createKuriaAnnotation) || (createGennyClasses)) {
    		kuriaFactory = new KuriaDescriptorFactory(runtime);
    	} else {
    		kuriaFactory = new IKuriaDescriptorFactory() {
    
    			@Override
    			public void addKuriaAnnotations(FieldDescriptor fd, Topic topic) {
    				// does nothing
    			}
    
    			@Override
    			public void addKuriaAnnotations(ClassDescriptor cd, Topic topic) {
    				// does nothing
    			}

				@Override
                public void addKuriaAnnotations(FieldDescriptor fd, Topic topic, boolean optional) {
    				// does nothing
                }
    		};
    	}
    
    	if (createGennyClasses) {
    		modelHandlerDescriptor = new ModelHandlerDescriptor(pkgDescriptor);
    		modelContentProviderDescriptor = new ModelContentProviderDescriptor(pkgDescriptor);
    		createModelContainer();
    	}
    
    	parseTopicTypes();
    }

	private void parseTopicTypes() throws IllegalSchemaException {
    	String queryString = "FOR $t IN // tmcl:topic-type [ . != http://onotoa.topicmapslab.de/annotation ] "
    	        + "RETURN $t";
    	IQuery query = runtime.run(queryString);
    
    	for (IResult r : query.getResults()) {
    		Topic t = (Topic) r.get(0);
    		parseTopicType(t);
    	}
    
    }

	/**
     * 
     */
    private void createModelContainer() {
    	if (!createGennyClasses)
    		return;
    
    	String queryString = "RETURN   http://onotoa.topicmapslab.de/schemareifier /  http://onotoa.topicmapslab.de/annotation/de/topicmapslab/genny/category";
    
    	IQuery query = runtime.run(queryString);
    
    	String name = null;
    	if (query.getResults().isEmpty()) {
    		name = "Model";
    	} else {
    		name = query.getResults().get(0, 0);
    	}
    
    	modelContainerDescriptor = new ModelContainerDescriptor(pkgDescriptor);
    	modelContainerDescriptor.setCategory(name);
    	modelContainerDescriptor.setName("ModelContainer");
    
    	modelHandlerDescriptor.addModelContainer(modelContainerDescriptor);
    }

	private ClassDescriptor generateClassDescriptor(Topic topic)
	        throws IllegalSchemaException {
		
		Set<String> siSet = new HashSet<String>();
		for (Locator l : topic.getSubjectIdentifiers()) {
			siSet.add(l.toExternalForm());
		}
		String si = TMQLPreperator.getIdentifierString(siSet.iterator().next(), IdType.SUBJECT_IDENTIFIER);
		
		
		ClassDescriptor cd = new ClassDescriptor(pkgDescriptor);
		parsedTopics.put(topic, cd);
		

		// looking for class annotation

		String name = getClassName(si);

		if (name == null)
			name = (String) topic.getNames().iterator().next().getValue();

		cd.setAbstract(isAbstract(topic));
		cd.setName(TypeUtility.getJavaName(name));
		
		classesMap.put(cd.getName(), cd);

		// check supertype
		String queryString = "RETURN " + getTMQLIdentifierString(topic)
		        + " >> supertypes >> characteristics tm:name >> atomify[0], "
		        + getTMQLIdentifierString(topic)
		        + " >> supertypes [0]";
		IQuery q = runtime.run(queryString);
		if (!q.getResults().isEmpty()) {
			// XXX SVEN why  empty tuples??
			if (q.getResults().get(0, 0)  instanceof String) {
				String stName = q.getResults().get(0, 0);
				cd.setExtendsName(TypeUtility.getJavaName(stName));
				
				if (classesMap.get(cd.getExtendsName())==null) {
					Topic t = q.getResults().get(0, 1);
					generateClassDescriptor(t);
				}
			}
		}

		AnnotationDescriptor ad = new AnnotationDescriptor(cd);
		ad.setQualifiedName(de.topicmapslab.aranuka.annotations.Topic.class.getName());

		PrimitiveArrayAttributeDescriptor pad = new PrimitiveArrayAttributeDescriptor(ad);
		pad.setName("subject_identifier");
		pad.setValue(siSet.toArray(new String[siSet.size()]));

		generateIdentifierField(cd, si, IdType.SUBJECT_IDENTIFIER);
		generateIdentifierField(cd, si, IdType.SUBJECT_LOCATOR);
		generateIdentifierField(cd, si, IdType.ITEM_IDENTIFIER);

		// if we still have no id field in this or any super class: generate an item identifier
		if (getNumberOfIdFields(cd) == 0) {
			generateDefaultItemIdentifier(cd);
		}

		// generate names
		generateNameConstraints(cd, si);

		generateOccurrenceConstraints(cd, si);

		generateAssociationConstraints(cd, si);

		checkCategory(topic, cd, name);

		addAranukaMappingClass(cd);
		
		return cd;
	}

	/**
     * @param cd
     * @return
     */
    private int getNumberOfIdFields(ClassDescriptor cd) {
    	int counter = cd.getIdFields().size();
    	
    	if (cd.getExtendsName()!=null) {
    		return counter + getNumberOfIdFields(classesMap.get(cd.getExtendsName()));
    	}
    	
	    return counter;
    }

	private void generateDefaultItemIdentifier(ClassDescriptor cd) {
		
		// no need for a default item identifier in abstract classes 
		if (cd.isAbstract())
			return;
		
		FieldDescriptor fd = new FieldDescriptor(cd, true, "id", String.class.getName(), false);
		AnnotationDescriptor ad = new AnnotationDescriptor(fd);
		ad.setQualifiedName(Id.class.getName());
		EnumerationAttributeDescriptor ead = new EnumerationAttributeDescriptor(ad);
		ead.setName("type");
		ead.setEnumerationType(IdType.class.getName());
		ead.setValue("ITEM_IDENTIFIER");

		PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
		pad.setName("autogenerate");
		pad.setValue(true);
		cd.addIdField(fd);
		kuriaFactory.addKuriaAnnotations(fd, null);
	}

	/**
	 * @param cd
	 */
	private void generateIdentifierField(ClassDescriptor cd, String si, IdType type) {

		String constraintName = null;
		String fieldName = null;
		
		switch (type) {
		case ITEM_IDENTIFIER:
			constraintName = "tmcl:item-identifier-constraint";
			fieldName = "itemIdentifier";
			break;
		case SUBJECT_IDENTIFIER:
			constraintName = "tmcl:subject-identifier-constraint";
			fieldName = "subjectIdentifier";
			break;
		case SUBJECT_LOCATOR:
			constraintName = "tmcl:subject-locator-constraint";
			fieldName = "subjectLocator";
			break;
		}

		String queryString = "FOR $c IN // " + constraintName + " [ . >> traverse tmcl:constrained-topic-type == " + si
		        + " ] " + "RETURN $c / tmcl:card-max , $c";

		IQuery q = runtime.run(queryString);

		if (q.getResults().isEmpty())
			return;

		int cardMax = 0;
		for (IResult r : q.getResults()) {
			cardMax = getMax(cardMax, r);
		}

		FieldDescriptor fd = new FieldDescriptor(cd, true, fieldName, String.class.getName(), (cardMax > 1));
		String name = getFieldName((Topic) q.getResults().get(0, 1));
		if (name != null)
			fd.setName(name);

		// adding the id field to the idFiels for equals
		cd.addIdField(fd);

		Topic constraint = (Topic) q.getResults().get(0, 1);
		kuriaFactory.addKuriaAnnotations(fd, constraint);

		AnnotationDescriptor ad = new AnnotationDescriptor(fd);
		ad.setQualifiedName(Id.class.getName());
		EnumerationAttributeDescriptor ead = new EnumerationAttributeDescriptor(ad);
		ead.setName("type");
		ead.setEnumerationType(IdType.class.getName());
		ead.setValue(type.name());
		if (isGenerated(constraint)) {
			PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
			pad.setName("autogenerate");
			pad.setValue(true);
		}

	}

	private void generateNameConstraints(ClassDescriptor cd, String si) throws IllegalSchemaException {
		String queryString = "FOR $c IN //  tmcl:topic-name-constraint"
		        + " [ . >> traverse tmcl:constrained-topic-type == " + si + " ] "
		        + "ORDER BY $c >> traverse tmcl:constrained-statement >> characteristics tm:name >> atomify [0] "
		        + "RETURN ( $c / tmcl:card-max, "
		        + "$c >> traverse tmcl:constrained-statement >> characteristics tm:name >> atomify [0] || \"name\", "
		        + "$c >> traverse tmcl:constrained-statement >> indicators >> atomify [0] || \"none\", $c )";

		IQuery q = runtime.run(queryString);

		for (IResult r : q.getResults()) {
			if ("none".equals(r.get(2))) {
				throw new IllegalSchemaException("Name: " + r.get(1) + " has no subject identifier!");
			}
			Topic tnc = (Topic) r.get(3);

			if (!createField(tnc))
				continue;

			FieldDescriptor fd = new FieldDescriptor(cd);
			fd.setMany(getMax(0, r) > 1);
			fd.setType(String.class.getName());

			// getting name from annotation or name type

			String name = getFieldName(tnc);
			if (name != null)
				fd.setName(name);
			else
				fd.setName(TypeUtility.getFieldName((String) r.getResults().get(1)));

			kuriaFactory.addKuriaAnnotations(fd, tnc);

			AnnotationDescriptor ad = new AnnotationDescriptor(fd);
			ad.setQualifiedName(Name.class.getName());
			PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
			pad.setName("type");
			pad.setValue(r.get(2));
			addNameMapEntry((String) r.get(2), (String) r.get(1));
		}
	}

	private void generateOccurrenceConstraints(ClassDescriptor cd, String si) throws IllegalSchemaException {
		String queryString = "FOR $c IN //  tmcl:topic-occurrence-constraint"
		        + " [ . >> traverse tmcl:constrained-topic-type == " + si + " ] "
		        + "ORDER BY $c >> traverse tmcl:constrained-statement >> characteristics tm:name >> atomify [0] "
		        + "RETURN ( $c / tmcl:card-max, "
		        + "$c >> traverse tmcl:constrained-statement >> characteristics tm:name >> atomify [0] || \"name\", "
		        + "$c >> traverse tmcl:constrained-statement >> indicators >> atomify [0] , $c)";

		IQuery q = runtime.run(queryString);

		for (IResult r : q.getResults()) {
			Topic toc = (Topic) r.get(3);
			if ("none".equals(r.get(2))) {
				throw new IllegalSchemaException("Occurrence: " + r.get(1) + " has no subject identifier!");
			}
			if (!createField(toc))
				continue;

			FieldDescriptor fd = new FieldDescriptor(cd);
			fd.setMany(getMax(0, r) > 1);
			String occurrenceDatatype = getOccurrenceDatatype((String) r.getResults().get(2));
			fd.setType(TypeUtility.toJavaType(occurrenceDatatype).getName());

			// getting name from annotation or occ type
			String name = getFieldName(toc);
			if (name != null)
				fd.setName(name);
			else
				fd.setName(TypeUtility.getFieldName((String) r.getResults().get(1)));

			AnnotationDescriptor ad = new AnnotationDescriptor(fd);
			ad.setQualifiedName(Occurrence.class.getName());
			PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
			pad.setName("type");
			pad.setValue(r.get(2));

			pad = new PrimitiveAttributeDescriptor(ad);
			pad.setName("datatype");
			pad.setValue(occurrenceDatatype);
			addNameMapEntry((String) r.get(2), (String) r.get(1));

			kuriaFactory.addKuriaAnnotations(fd, toc);
		}
	}

	private String getOccurrenceDatatype(String occTypeSi) {
		String queryString = "FOR $c IN //  tmcl:occurrence-datatype-constraint "
		        + "[ . >> traverse tmcl:constrained-statement == " + occTypeSi + "] " + "RETURN $c / tmcl:datatype";

		IQuery q = runtime.run(queryString);

		for (IResult r : q.getResults()) {
			return (String) r.getResults().get(0);
		}
		return "http://www.w3.org/2001/XMLSchema#anyType";
	}

	/**
	 * @param cd
	 * @param si
	 * @throws IllegalSchemaException
	 */
	private void generateAssociationConstraints(ClassDescriptor cd, String si) throws IllegalSchemaException {
		// get
		String queryString = "%PREFIX ara http://onotoa.topicmapslab.de/annotation/de/topicmapslab/aranuka/\n"
		        + " FOR $c IN // tmcl:topic-role-constraint"
		        // +
		        // " [  ( . / ara:generateattribute =~ \"true\" ) OR  fn:count ( . / ara:generateattribute) == 0 ]\n"
		        + " [ . >> traverse tmcl:constrained-topic-type == " + si + " ] "
		        + " ORDER BY $c >> traverse tmcl:constrained-statement / tm:name [0]"
		        + " RETURN $c >> traverse tmcl:constrained-statement >> indicators >> atomify [0], "
		        + " $c >> traverse tmcl:constrained-role >> indicators >> atomify [0], " + " $c, "
		        + " $c >> traverse tmcl:constrained-statement / tm:name, "
		        + " $c >> traverse tmcl:constrained-role / tm:name";

		IQuery query = runtime.run(queryString);

		for (IResult result : query.getResults()) {
			String assocTypeSi = (String) result.get(0);
			String roleTypeSi = (String) result.get(1);
			Topic topicRoleConstraint = (Topic) result.get(2);

			addNameMapEntry(assocTypeSi, (String) result.get(3));
			addNameMapEntry(roleTypeSi, (String) result.get(4));

			queryString = "RETURN fn:count( // tmcl:topic-role-constraint "
			        + " [ . >>  traverse tmcl:constrained-statement == " + assocTypeSi + " ] )";
			IQuery query2 = runtime.run(queryString);

			BigInteger resultValue = query2.getResults().get(0, 0);

			switch (resultValue.intValue()) {
			case 1:
				createOneRoleTypeAssociationField(cd, assocTypeSi, roleTypeSi);
				break;
			case 2:
				createTwoRoleTypeAssociationField(cd, assocTypeSi, roleTypeSi, topicRoleConstraint);
				break;
			default:
				createNRoleTypeAssociationField(cd, assocTypeSi, roleTypeSi, si);
			}

		}
	}

	/**
	 * Creates a field for the association with only one role type
	 * 
	 * @param cd
	 *            the {@link ClassDescriptor} which gets the field
	 * @param assocTypeSi
	 *            the subject identifier of the association-type
	 * @param roleTypeSi
	 *            the subject identifier of the role-type
	 */
	private void createOneRoleTypeAssociationField(ClassDescriptor cd, String assocTypeSi, String roleTypeSi) {

		String queryString = "FOR $c IN // tmcl:association-role-constraint "
		        + "[ . >> traverse tmcl:constrained-statement == " + assocTypeSi + " ] " + "RETURN $c / tmcl:card-max ";

		IQuery query = runtime.run(queryString);
		int cardMax = getCardMax(query.getResults());

		queryString = "FOR $c IN // tmcl:topic-role-constraint " + "[ . >> traverse tmcl:constrained-statement == "
		        + assocTypeSi + " ] " + "RETURN $c / tmcl:card-max, $c";
		// second result is used to get annotations :)

		query = runtime.run(queryString);

		Topic topicRoleConstr = query.getResults().get(0, 1);
		if (!createField(topicRoleConstr))
			return;

		boolean isMany = isMany(query.getResults());

		String name = getFieldName((Topic) query.getResults().getResults().get(0).getResults().get(1));

		FieldDescriptor fd = new FieldDescriptor(cd);
		if (name == null)
			fd.setName(TypeUtility.getFieldName(getName(assocTypeSi)));
		else
			fd.setName(name);

		// create field according to role-type cardinalities
		switch (cardMax) {
		case 1:
			fd.setType("boolean");
			AnnotationDescriptor ad = new AnnotationDescriptor(fd);
			ad.setQualifiedName(Association.class.getName());

			PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
			pad.setName("type");
			pad.setValue(assocTypeSi);

			pad = new PrimitiveAttributeDescriptor(ad);
			pad.setName("played_role");
			pad.setValue(roleTypeSi);
			break;
		case 2:
			fd.setType(cd.getQualifiedName());
			fd.setMany(isMany);

			ad = new AnnotationDescriptor(fd);
			ad.setQualifiedName(Association.class.getName());

			pad = new PrimitiveAttributeDescriptor(ad);
			pad.setName("type");
			pad.setValue(assocTypeSi);

			pad = new PrimitiveAttributeDescriptor(ad);
			pad.setName("played_role");
			pad.setValue(roleTypeSi);

			pad = new PrimitiveAttributeDescriptor(ad);
			pad.setName("other_role");
			pad.setValue(roleTypeSi);
			addSupportedField(fd);
			break;
		default:

			// TODO create a container
			// fd.setType(cd.getQualifiedName());
			// fd.setMany(true);
			// ad = new AnnotationDescriptor(fd);
			// ad.setQualifiedName(Association.class.getName());
			//
			// pad = new PrimitiveAttributeDescriptor(ad);
			// pad.setName("type");
			// pad.setValue(assocTypeSi);
			//
			// pad = new PrimitiveAttributeDescriptor(ad);
			// pad.setName("played_role");
			// pad.setValue(roleTypeSi);
			//
			// pad = new PrimitiveAttributeDescriptor(ad);
			// pad.setName("other_role");
			// pad.setValue(roleTypeSi);
			break;
		}
		kuriaFactory.addKuriaAnnotations(fd, topicRoleConstr);
	}

	/**
	 * @param cd
	 * @param assocTypeSi
	 * @param roleTypeSi
	 * @throws IllegalSchemaException
	 */
	private void createTwoRoleTypeAssociationField(ClassDescriptor cd, String assocTypeSi, String roleTypeSi,
	        Topic topicRoleConstraint) throws IllegalSchemaException {
		String queryString = "FOR $c IN // tmcl:association-role-constraint "
		        + "[ . >> traverse tmcl:constrained-statement == " + assocTypeSi + "  "
		        + " AND NOT ( . >> traverse tmcl:constrained-role == " + roleTypeSi + " ) ] "
		        + "RETURN $c / tmcl:card-max, " + "$c >> traverse tmcl:constrained-role >> indicators >> atomify [0]";

		IQuery query = runtime.run(queryString);

		int cardMax = getCardMax(query.getResults());
		String otherPlayerRole = query.getResults().get(0, 1);

		Topic player = getOtherPlayer(assocTypeSi, otherPlayerRole);

		queryString = "RETURN " + getTMQLIdentifierString(topicRoleConstraint) + " " + " / tmcl:card-max";
		IQuery q2 = runtime.run(queryString);
		boolean isMany = isMany(q2.getResults());

		queryString = "FOR $c IN // tmcl:topic-role-constraint " + "[ . >> traverse tmcl:constrained-statement == "
		        + assocTypeSi + " AND NOT ( . >> traverse tmcl:constrained-role == " + roleTypeSi + " ) ] "
		        + "RETURN $c";
		// second result is used to get annotations s :)

		q2 = runtime.run(queryString);
		Topic otherTopicRoleConstr = (Topic) q2.getResults().get(0, 0);
		if (!createField(otherTopicRoleConstr))
			return;

		FieldDescriptor fd = new FieldDescriptor(cd);
		String name = getFieldName(otherTopicRoleConstr);
		if (name == null)
			name = TypeUtility.getFieldName(player.getNames().iterator().next().getValue());

		switch (cardMax) {
		case 1:
			fd.setType(parseTopicType(player).getQualifiedName());
			fd.setName(name);
			fd.setMany(isMany);

			AnnotationDescriptor ad = new AnnotationDescriptor(fd);
			ad.setQualifiedName(Association.class.getName());

			PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
			pad.setName("type");
			pad.setValue(assocTypeSi);

			pad = new PrimitiveAttributeDescriptor(ad);
			pad.setName("played_role");
			pad.setValue(roleTypeSi);

			pad = new PrimitiveAttributeDescriptor(ad);
			pad.setName("other_role");
			pad.setValue(otherPlayerRole);

			addSupportedField(fd);
			// TODO other cardinalities -> container
		}
		kuriaFactory.addKuriaAnnotations(fd, otherTopicRoleConstr);

	}

	/**
     * @param fd
     */
    protected void addSupportedField(FieldDescriptor fd) {
    	if (createGennyClasses)
    		modelContentProviderDescriptor.addSupportedField(fd);
    }

	/**
	 * 
	 * Creates n binary associtions using the rolecombination constraints.
	 * 
     * @param cd the class descriptor which contains the association fields
     * @param assocTypeSi the association type
     * @param roleTypeSi the role-type identifier of the topic represented by the class
     * @param topicSI the subject identifier of the topic represented by the class
     * @param query the query which contains the list of roles
     * @throws IllegalSchemaException
     */
	// TODO recode?
    protected void createBinaryAssociations(ClassDescriptor cd, String assocTypeSi, String roleTypeSi, String topicSI,
            IQuery query) throws IllegalSchemaException {
	    for (@SuppressWarnings("unused") IResult r : query.getResults()) {

	    	String queryString = "FOR $c IN // tmcl:topic-role-constraint "
	    	        + "[ . >> traverse tmcl:constrained-statement == " + getIdentifierString(assocTypeSi, IdType.SUBJECT_IDENTIFIER)
	    	        + " AND NOT ( . >> traverse tmcl:constrained-topic-type == " + topicSI + " ) ] "
	    	        + "RETURN $c / tmcl:card-max, "
	    	        + "$c >> traverse tmcl:constrained-role >> indicators >> atomify [0], "
	    	        + "$c >> traverse tmcl:constrained-topic-type >> indicators >> atomify [0], "
	    	        + "$c >> traverse tmcl:constrained-topic-type / tm:name [0], $c, "
	    	        + "$c >> traverse tmcl:constrained-topic-type [0] ";

	    	IQuery q2 = runtime.run(queryString);

	    	for (IResult r2 : q2.getResults()) {
	    		boolean isMany = isMany(q2.getResults());

	    		String roleSi = (String) r2.get(1);
	    		String playerSi = (String) r2.get(2);
	    		String playerName = (String) r2.get(3);
	    		Topic topicRoleConstr = (Topic) r2.get(4);
	    		Topic playerTopic = (Topic) r2.get(5);

	    		String name = getFieldName(topicRoleConstr);
	    		if (name == null) {
	    			name = TypeUtility.getFieldName(playerName);
	    		}

	    		if (!createField(topicRoleConstr))
	    			continue;

	    		if (roleCombinationConstraintExists(assocTypeSi, topicSI, roleTypeSi, playerSi, roleSi)) {
	    			FieldDescriptor fd = createField(cd, isMany, playerTopic, name);

	    			AnnotationDescriptor ad = new AnnotationDescriptor(fd);
	    			ad.setQualifiedName(Association.class.getName());

	    			PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
	    			pad.setName("type");
	    			pad.setValue(assocTypeSi);

	    			pad = new PrimitiveAttributeDescriptor(ad);
	    			pad.setName("played_role");
	    			pad.setValue(roleTypeSi);

	    			pad = new PrimitiveAttributeDescriptor(ad);
	    			pad.setName("other_role");
	    			pad.setValue(roleSi);
	    			kuriaFactory.addKuriaAnnotations(fd, topicRoleConstr);
	    			addSupportedField(fd);
	    		}
	    	}
	    }
    }

	/**
     * @param cd
     * @param isMany
     * @param playerTopic
     * @param name
     * @return
     * @throws IllegalSchemaException
     */
    protected FieldDescriptor createField(ClassDescriptor cd, boolean isMany, Topic playerTopic, String name)
            throws IllegalSchemaException {
	    FieldDescriptor fd = new FieldDescriptor(cd);
	    fd.setType(parseTopicType(playerTopic).getQualifiedName());
	    // TODO name set
	    fd.setName(name);
	    fd.setMany(isMany);
	    return fd;
    }

	/**
     * @param cd
     * @param assocTypeSi
     * @param roleTypeSi
     * @throws IllegalSchemaException
     */
    private void createNRoleTypeAssociationField(ClassDescriptor cd, String assocTypeSi, String roleTypeSi,
            String topicSI) throws IllegalSchemaException {
    	String queryString = "FOR $c IN // tmcl:association-role-constraint "
    	        + "[ . >> traverse tmcl:constrained-statement == " + assocTypeSi
    	        + " AND NOT ( . >> traverse tmcl:constrained-role == " + roleTypeSi + " ) ] "
    	        + "RETURN $c / tmcl:card-max, " 
    	        + "$c >> traverse tmcl:constrained-role >> indicators >> atomify [0], "
    	        + "$c / tmcl:card-min ";
    
    	IQuery query = runtime.run(queryString);
    
    	if (roleCombinationConstraintExists(assocTypeSi)) {
    		// create n binary fields
    		createBinaryAssociations(cd, assocTypeSi, roleTypeSi, topicSI, query);
    	} else {
    		// create association container
    		ClassDescriptor assocCD = new ClassDescriptor(cd);
    		assocCD.setName(TypeUtility.getJavaName(getName(assocTypeSi)));
    		assocCD.setStatic(true);
    		
    		AnnotationDescriptor ad = new AnnotationDescriptor(assocCD);
    		ad.setQualifiedName(AssociationContainer.class.getName());
    		
    		
    		for (IResult roleResult : query.getResults()) {
    			String roleSI = roleResult.get(1);
    			boolean isMany = getMax(0, roleResult)>1;
    			
    			String cardMin = roleResult.get(2);
    			
    			// create fields for the association container:
    			queryString = "FOR $c IN // tmcl:topic-role-constraint "
        	        + "[ . >> traverse tmcl:constrained-statement == " + getIdentifierString(assocTypeSi, IdType.SUBJECT_IDENTIFIER)
        	        + " AND (. >> traverse tmcl:constrained-role == " + getIdentifierString(roleSI, IdType.SUBJECT_IDENTIFIER) + " ) "
        	        + " AND NOT ( . >> traverse tmcl:constrained-topic-type == " + topicSI + " ) ] "
        	        + "RETURN $c / tmcl:card-max, "
        	        + "$c >> traverse tmcl:constrained-role >> indicators >> atomify [0], "
        	        + "$c >> traverse tmcl:constrained-topic-type >> indicators >> atomify [0], "
        	        + "$c >> traverse tmcl:constrained-topic-type / tm:name [0], "
        	        + "$c, "
        	        + "$c >> traverse tmcl:constrained-topic-type [0] ";
    			
    			IQuery q2 = runtime.run(queryString);
    			
    			for (IResult r : q2.getResults()) {
    				// check if we have the right amount of results
    				if (r.getResults().size()!=6) {
    					logger.warn("Invalid number of result columns for: "+assocTypeSi);
    					continue;
    				}
    				
    				// if annotation generate field is false we remove the container and return
    				if (!createField((Topic) r.get(4))) {
    					cd.removeChildClass(assocCD);
    					return;
    				}
    				
    				FieldDescriptor fd = createField(assocCD, isMany, (Topic) r.get(5), TypeUtility.getFieldName((String) r.get(3)));
    				ad = new AnnotationDescriptor(fd);
    				ad.setQualifiedName(Role.class.getName());
    				
    				assocCD.addIdField(fd);
    				
    				PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
    				pad.setName("type");
    				pad.setValue(r.get(1));
    				
    				kuriaFactory.addKuriaAnnotations(fd, (Topic) r.get(4), "0".equals(cardMin));
    				
    			}
    		}
    		
    		// adding the field to the class with the container as type
    		FieldDescriptor fd = new FieldDescriptor(cd);
    		fd.setType(assocCD.getQualifiedName());
    		fd.setName(TypeUtility.getFieldName(getName(assocTypeSi)));
    		
    		ad = new AnnotationDescriptor(fd);
    		ad.setQualifiedName(Association.class.getName());
    		
    		PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
    		pad.setName("type");
    		pad.setValue(assocTypeSi);
    		
    		pad = new PrimitiveAttributeDescriptor(ad);
    		pad.setName("persistOnCascade");
    		pad.setValue(true);
    		
    		pad = new PrimitiveAttributeDescriptor(ad);
    		pad.setName("played_role");
    		pad.setValue(roleTypeSi);
    		
    		// get cardinality of the topic-role constraint of the topic represented by this class descriptor
    		
    		// create fields for the association container:
    		queryString = "FOR $c IN // tmcl:topic-role-constraint "
    	        + "[ . >> traverse tmcl:constrained-statement ==  " + getIdentifierString(assocTypeSi, IdType.SUBJECT_IDENTIFIER)
    	        + " AND (. >> traverse tmcl:constrained-role ==  " + getIdentifierString(roleTypeSi, IdType.SUBJECT_IDENTIFIER) + " ) "
    	        + " AND ( . >> traverse tmcl:constrained-topic-type == " + topicSI + " ) ] "
    	        + "RETURN $c / tmcl:card-max, $c";
    		IQuery q2 = runtime.run(queryString);
    		fd.setMany(isMany(q2.getResults()));
    		
    		for (FieldDescriptor fieldDescriptor : assocCD.getFields()) {
    			addSupportedField(fieldDescriptor);
    		}
    		
    		kuriaFactory.addKuriaAnnotations(fd, (Topic) q2.getResults().get(0, 1));
    		kuriaFactory.addKuriaAnnotations(assocCD, getTopic(assocTypeSi));
    		addAranukaMappingClass(assocCD);
    	}
    
    }

	/**
	 * Checks if the topic map contains a role combination constraint for the
	 * players.
	 * 
	 * @param assocType
	 *            association type
	 * @param topicType
	 *            a topic type
	 * @param roleType
	 *            the role of the topic type
	 * @param otherPlayer
	 *            the topic type of the other player
	 * @param otherRole
	 *            the role of the other player
	 * @return <code>true</code> if a role combination constraint for this type
	 *         exists
	 */
	private boolean roleCombinationConstraintExists(String assocType, String topicType, String roleType,
	        String otherPlayer, String otherRole) {
		String queryString = "FOR $c IN // tmcl:role-combination-constraint \n"
		        + " [ . >> traverse tmcl:constrained-statement == "
		        + assocType
		        + " AND "
		        + " (.>> traverse tmcl:constrained-role == "
		        + roleType
		        + " AND\n"
		        + "  . >> traverse tmcl:constrained-topic-type == "
		        + topicType
		        + " AND\n"
		        + " . >> traverse tmcl:other-constrained-role == "
		        + otherRole
		        + " AND\n"
		        + " . >> traverse tmcl:other-constrained-topic-type == "
		        + otherPlayer
		        + ")"
		        + " OR"
		        + " (.>> traverse tmcl:constrained-role == "
		        + otherRole
		        + " AND\n"
		        + " . >> traverse tmcl:constrained-topic-type == "
		        + otherPlayer
		        + " AND\n"
		        + " . >> traverse tmcl:other-constrained-role == "
		        + roleType
		        + " AND\n"
		        + " . >> traverse tmcl:other-constrained-topic-type == " + topicType + ") ]" + "RETURN $c";

		IQuery q = runtime.run(queryString);

		// if we get a result we now theres is a constraint
		return !q.getResults().isEmpty();
	}

	/**
	 * Checks if a role combination constraint for this topic exists
	 * 
	 * @param assocType
	 *            the assoc type to check
	 * 
	 * @return <code>true</code> if their is at least one role combination
	 *         constraint; <code>false</code> else
	 */
	private boolean roleCombinationConstraintExists(String assocType) {
		String queryString = "FOR $c IN // tmcl:role-combination-constraint \n"
		        + " [ . >> traverse tmcl:constrained-statement == " + assocType + "]" + " RETURN $c";

		IQuery q = runtime.run(queryString);

		return !q.getResults().isEmpty();
	}

	private int getMax(int cardMax, IResult r) {
		Object o = r.get(0);
		if (o instanceof String) {
			if (o.equals("*")) {
				return Integer.MAX_VALUE;
			} else {
				return Math.max(cardMax, Integer.parseInt((String) o));
			}

		} else {
			return Math.max(cardMax, ((BigInteger) o).intValue());
		}
	}

	private int getCardMax(IResultSet<?> resultSet) {
		Object o = resultSet.get(0, 0);
		if (o instanceof String) {
			if (o.equals("*")) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.parseInt((String) o);
			}
		} else {
			return ((BigInteger) o).intValue();
		}
	}

	private boolean isMany(IResultSet<?> rs) {
		return getCardMax(rs) > 1;
	}

	/**
	 * Checks if an generate attribute exists and is set to <code>false</code>
	 * 
	 * @param constraint
	 *            the constraint to check
	 * @return <code>true</code> if the annotation is missing or its value is
	 *         <code>true</code>
	 */
	private boolean createField(Topic constraint) {
		String queryString = "RETURN " + getTMQLIdentifierString(constraint) + " "
		        + "/ http://onotoa.topicmapslab.de/annotation/de/topicmapslab/aranuka/generateattribute ";

		IQuery q = runtime.run(queryString);
		if (q.getResults().isEmpty())
			return true;
		else {
			Object val = q.getResults().get(0, 0);
			if (val instanceof String)
				return "true".equals(val);
			return true;
		}
	}

	/**
	 * 
	 * @param assocTypeSi
	 * @param otherPlayerRole
	 * @return
	 */

	private Topic getOtherPlayer(String assocTypeSi, String otherPlayerRole) {
		String queryString;
		IQuery query;
		queryString = "FOR $c IN  // tmcl:topic-role-constraint " + " [ . >>  traverse tmcl:constrained-statement == "
		        + assocTypeSi + " AND . >> traverse tmcl:constrained-role == " + getIdentifierString(otherPlayerRole, IdType.SUBJECT_IDENTIFIER) + " ] "
		        + "RETURN $c >> traverse tmcl:constrained-topic-type ";

		query = runtime.run(queryString);

		if (query.getResults().isEmpty()) {
			throw new IllegalArgumentException("assoc has no counter player");
		}

		return query.getResults().get(0, 0);
	}

	/**
	 * @param si
	 * @return
	 */
	private String getClassName(String si) {
		String queryString = "RETURN " + si
		        + " / http://onotoa.topicmapslab.de/annotation/de/topicmapslab/aranuka/name ";

		IQuery query = runtime.run(queryString);
		if (query.getResults().isEmpty())
			return null;

		return query.getResults().get(0, 0);
	}

	private String getFieldName(Topic constraint) {
		String queryString = "RETURN " + getTMQLIdentifierString(constraint)
		        + " / http://onotoa.topicmapslab.de/annotation/de/topicmapslab/aranuka/name ";

		IQuery query = runtime.run(queryString);
		if (query.getResults().isEmpty())
			return null;

		return query.getResults().get(0, 0);
	}

	/**
	 * Returns the topic instance with the given subject identifier
	 * 
     * @param si
     * @return
     */
    private Topic getTopic(String si) {
        return runtime.run(si).getResults().get(0, 0);
    }

	/**
	 * @param si
	 * @return
	 */
	private String getName(String si) {
		IQuery q = runtime.run("RETURN " + si + " / tm:name[0]");

		if (q.getResults().isEmpty())
			throw new RuntimeException("Topic with si: " + si + "has no name!");

		return (String) q.getResults().iterator().next().getResults().get(0);
	}

	private void addNameMapEntry(String si, String name) {
		if (!createGennyClasses)
			return;
		if (modelHandlerDescriptor != null) {
			modelHandlerDescriptor.putName(si, name);
		}
	}

	/**
	 * When generating the code classes represented by the
	 * {@link ClassDescriptor} will be added to the CLASS_SET.
	 * 
	 * @param cd
	 *            the {@link ClassDescriptor} of the mapped class
	 */
	private void addAranukaMappingClass(ClassDescriptor cd) {
		if (!createGennyClasses)
			return;
		if (modelHandlerDescriptor != null) {
			modelHandlerDescriptor.addClassDescriptor(cd);
		}
	}

	private void checkCategory(Topic t, ClassDescriptor cd, String name) {
		if (!createGennyClasses)
			return;
		
		if (isAbstract(t))
			return;
		
		IQuery q = runtime.run("RETURN " + getTMQLIdentifierString(t)
		        + " / http://onotoa.topicmapslab.de/annotation/de/topicmapslab/genny/category");

		if (!q.getResults().isEmpty()) {

			// get the name
			String catName = q.getResults().get(0, 0);

			if (modelContainerMap == null)
				modelContainerMap = new HashMap<String, ModelContainerDescriptor>();

			ModelContainerDescriptor mc = modelContainerMap.get(catName);
			if (mc == null) {
				String typeName = TypeUtility.getJavaName(catName) + "ModelContainer";
				mc = new ModelContainerDescriptor(pkgDescriptor, typeName, catName);
				modelContainerMap.put(catName, mc);
				modelHandlerDescriptor.addModelContainer(mc);
			}
			mc.addModelDescriptor(cd);
			mc.putTitle(cd, name);
		}
		modelContainerDescriptor.addModelDescriptor(cd);
		modelContainerDescriptor.putTitle(cd, name);

	}

	/**
	 * @param topic
	 * @return
	 */
	private boolean isGenerated(Topic topic) {
		String queryString = "RETURN " + getTMQLIdentifierString(topic)
		        + " / http://onotoa.topicmapslab.de/annotation/de/topicmapslab/aranuka/autogenerate ";
		IQuery q = runtime.run(queryString);

		if (q.getResults().isEmpty())
			return false;

		Object hidden = q.getResults().get(0, 0);
		if (hidden instanceof Boolean)
			return (Boolean) hidden;
		else
			return Boolean.parseBoolean((String) hidden);
	}
	
	/**
	 * Checksa if an abstract-constraint exists for the given topic type
	 * 
	 * @param topic
	 * @return
	 */
	private boolean isAbstract(Topic topic) {
		String queryString = " SELECT " +getTMQLIdentifierString(topic)+ " >> traverse  tmcl:constrained-topic-type [ . >> types == tmcl:abstract-constraint ] ";
		IQuery q = runtime.run(queryString);

		return !q.getResults().isEmpty();

		
	}
}
