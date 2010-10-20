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

import static de.topicmapslab.codegenerator.utils.TMQLPreperator.getTMQLIdentifierString;

import java.math.BigInteger;
import java.util.Date;

import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.codegenerator.descriptors.AbstractAttributeDescriptor;
import de.topicmapslab.codegenerator.descriptors.AnnotationDescriptor;
import de.topicmapslab.codegenerator.descriptors.ClassDescriptor;
import de.topicmapslab.codegenerator.descriptors.FieldDescriptor;
import de.topicmapslab.codegenerator.descriptors.PrimitiveAttributeDescriptor;
import de.topicmapslab.codegenerator.utils.DescriptorUtil;
import de.topicmapslab.kuria.annotation.Text;
import de.topicmapslab.kuria.annotation.widgets.Check;
import de.topicmapslab.kuria.annotation.widgets.Combo;
import de.topicmapslab.kuria.annotation.widgets.Editable;
import de.topicmapslab.kuria.annotation.widgets.Hidden;
import de.topicmapslab.kuria.annotation.widgets.List;
import de.topicmapslab.kuria.annotation.widgets.TextField;
import de.topicmapslab.tmql4j.common.model.query.IQuery;
import de.topicmapslab.tmql4j.common.model.runtime.ITMQLRuntime;

/**
 * @author Hannes Niederhausen
 * 
 */
class KuriaDescriptorFactory implements IKuriaDescriptorFactory {

	private final ITMQLRuntime runtime;

	public KuriaDescriptorFactory(ITMQLRuntime runtime) {
		super();
		this.runtime = runtime;
	}

	/**
     * {@inheritDoc}
     */
	@Override
    public void addKuriaAnnotations(ClassDescriptor cd, Topic topic) {
		AnnotationDescriptor ad = new AnnotationDescriptor(cd);
		ad.setQualifiedName(Editable.class.getName());

	}

	/**
     * {@inheritDoc}
     */
	@Override
    public void addKuriaAnnotations(FieldDescriptor fd, Topic topic) {
		Class<?> annoClass = getClassByFieldType(fd);

		AnnotationDescriptor ad = new AnnotationDescriptor(fd);
		// check if we have a hidden annoation
		if ((topic == null) || (isHidden(topic))) {
			ad.setQualifiedName(Hidden.class.getName());
		} else {
			ad.setQualifiedName(annoClass.getName());

			if (annoClass == TextField.class) {
				int rows = getRows(topic);
				if (rows > 1) {
					PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
					pad.setName("rows");
					pad.setValue(rows);
				}
			}

			String label = getLabel(topic);
			if (label != null) {
				PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
				pad.setName("label");
				pad.setValue(label);
			}

			if (isOptional(topic)) {
				PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
				pad.setName("optional");
				pad.setValue(true);
			}

			try {
				if ((annoClass.getMethod("createNew") != null) && (isCreateNew(topic))) {
					PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
					pad.setName("createNew");
					pad.setValue(true);
				}
			} catch (Exception e) {
				// noop we just ignore the fact
			}
			try {
				if ((annoClass.getMethod("weight") != null)) {
					int weight = getWeight(topic);
					if (weight != Integer.MIN_VALUE) {
						PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
						pad.setName("weight");
						pad.setValue(weight);
					}
				}
			} catch (Exception e) {
				// noop we just ignore the fact
			}
			try {
				if ((annoClass.getMethod("readOnly") != null)) {
					boolean readOnly = isReadOnly(topic);
					if (readOnly) {
						PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
						pad.setName("readOnly");
						pad.setValue(readOnly);
					}
				}
			} catch (Exception e) {
				// noop we just ignore the fact
			}
			try {
				if ((annoClass.getMethod("showTime") != null)) {
					for (AnnotationDescriptor annod : fd.getAnnotations()) {
						if (annod.getQualifiedName().equals(Occurrence.class.getName())) {
							for (AbstractAttributeDescriptor aad : annod.getAttributes()) {
								if ("datatype".equals(aad.getName())
								        && ("http://www.w3.org/2001/XMLSchema#dateTime".equals(aad.getValue()))) {

									PrimitiveAttributeDescriptor pad = new PrimitiveAttributeDescriptor(ad);
									pad.setName("showTime");
									pad.setValue(true);

								}
							}
						}
					}
				}
			} catch (Exception e) {
				// noop we just ignore the fact
			}
		}

		if (isTypeLabel(topic)) {
			ad = new AnnotationDescriptor(fd);
			ad.setQualifiedName(Text.class.getName());
		}
	}

	/**
	 * @param topic
	 * @return
	 */
	private boolean isReadOnly(Topic topic) {
		String queryString = "RETURN " + getTMQLIdentifierString(topic)
		        + " / http://onotoa.topicmapslab.de/annotation/de/topicmapslab/kuria/read-only ";
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
	 * @param topic
	 * @return
	 */
	private int getWeight(Topic topic) {
		String queryString = "RETURN " + getTMQLIdentifierString(topic)
		        + " / http://onotoa.topicmapslab.de/annotation/de/topicmapslab/kuria/weight ";

		IQuery q = runtime.run(queryString);

		if (q.getResults().isEmpty())
			return Integer.MIN_VALUE;

		return Integer.parseInt((String) q.getResults().get(0, 0));
	}

	/**
	 * @param topic
	 * @return
	 */
	private boolean isHidden(Topic topic) {
		String queryString = "RETURN " + getTMQLIdentifierString(topic)
		        + " / http://onotoa.topicmapslab.de/annotation/de/topicmapslab/kuria/hidden ";
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
	 * @param topic
	 * @return
	 */
	private boolean isTypeLabel(Topic topic) {
		if (topic == null)
			return false;
		String queryString = "RETURN " + getTMQLIdentifierString(topic)
		        + " / http://onotoa.topicmapslab.de/annotation/de/topicmapslab/kuria/typelabel ";
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
	 * @param topic
	 * @return
	 */
	private boolean isOptional(Topic topic) {
		if ("0".equals(getCardMin(topic)))
			return true;
		
		String queryString = "RETURN " + getTMQLIdentifierString(topic)
		        + " / http://onotoa.topicmapslab.de/annotation/de/topicmapslab/kuria/optional ";
		IQuery q = runtime.run(queryString);

		if (q.getResults().isEmpty()) {
			return false;
		}

		Object hidden = q.getResults().get(0, 0);
		if (hidden instanceof Boolean)
			return (Boolean) hidden;
		else
			return Boolean.parseBoolean((String) hidden);
	}

	/**
	 * @param topic
	 * @return
	 */
	private boolean isCreateNew(Topic topic) {
		String queryString = "RETURN " + getTMQLIdentifierString(topic)
		        + " / http://onotoa.topicmapslab.de/annotation/de/topicmapslab/kuria/createnew ";
		IQuery q = runtime.run(queryString);

		if (q.getResults().isEmpty()) {
			return false;
		}

		Object hidden = q.getResults().get(0, 0);
		if (hidden instanceof Boolean)
			return (Boolean) hidden;
		else
			return Boolean.parseBoolean((String) hidden);
	}

	/**
	 * @param topic
	 * @return
	 */
	private int getRows(Topic topic) {
		String queryString = "RETURN " + getTMQLIdentifierString(topic)
		        + " / http://onotoa.topicmapslab.de/annotation/de/topicmapslab/kuria/rows ";
		IQuery q = runtime.run(queryString);

		if (q.getResults().isEmpty())
			return 1;

		Object hidden = q.getResults().get(0, 0);
		if (hidden instanceof BigInteger)
			return ((BigInteger) hidden).intValue();
		else {

			try {
				return Integer.parseInt((String) hidden);
			} catch (NumberFormatException e) {
				return 1;
			}
		}
	}

	/**
	 * @param topic
	 * @return
	 */
	private String getLabel(Topic topic) {
		String queryString = "RETURN " + getTMQLIdentifierString(topic)
		        + " / http://onotoa.topicmapslab.de/annotation/de/topicmapslab/kuria/label ";
		IQuery q = runtime.run(queryString);

		if (q.getResults().isEmpty())
			return null;

		return q.getResults().get(0, 0);
	}

	/**
	 * Returns the String value of the occurrence tmcl:card-min
	 * 
	 * @param topic
	 * @return the value of card-max or <code>"0"</code>
	 */
	private String getCardMin(Topic topic) {
		String queryString = "RETURN " + getTMQLIdentifierString(topic) + " / tmcl:card-min ";
		IQuery q = runtime.run(queryString);

		if (q.getResults().isEmpty())
			return "0";

		return q.getResults().get(0, 0);
	}

	/**
	 * @param fd
	 * @return
	 */
	private Class<?> getClassByFieldType(FieldDescriptor fd) {
		String type = fd.getType();
		if (type == null)
			throw new IllegalArgumentException("field descriptor has no type set!");

		if (fd.isMany()) {
			return List.class;
		}

		if (type.equals(Date.class.getName()))
			return de.topicmapslab.kuria.annotation.widgets.Date.class;

		if (DescriptorUtil.isBoolean(type))
			return Check.class;

		if ((DescriptorUtil.hasPrimitiveType(type)) || (String.class.getName().equals(type)))
			return de.topicmapslab.kuria.annotation.widgets.TextField.class;

		return Combo.class;
	}
}
