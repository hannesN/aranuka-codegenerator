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

package de.topicmapslab.codegenerator.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapix.io.TopicMapReader;
import org.tmapix.io.XTMTopicMapReader;

import de.topicmapslab.aranuka.enummerations.IdType;
import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.tmql4j.common.core.runtime.TMQLRuntimeFactory;
import de.topicmapslab.tmql4j.common.model.runtime.ITMQLRuntime;

/**
 * @author Hannes Niederhausen
 * 
 */
public class TMQLPreperator {

	/**
	 * 
	 * @param is
	 * @return
	 * @throws TMAPIException
	 * @throws IOException
	 */
	public static ITMQLRuntime createRuntime(InputStream is) throws TMAPIException, IOException {
		TopicMapSystemFactoryImpl tmsFac = new TopicMapSystemFactoryImpl();

		TopicMapSystem tms = tmsFac.newTopicMapSystem();

		TopicMap tm = tms.createTopicMap("http://psi.topicmapslab.de/codegen");

		TopicMapReader reader = null;
		reader = new XTMTopicMapReader(tm, is, "http://genny.codegen.de/");

		reader.read();

		ITMQLRuntime runtime = TMQLRuntimeFactory.newFactory().newRuntime(tms, tm);

		return runtime;
	}

	/**
	 * Returns a tmql query part which returns the given topic.
	 * 
	 * @param t
	 *            the topic which identifier should be used
	 * @return a string containing the identifier string
	 */
	public static String getTMQLIdentifierString(Topic t) {
		Set<Locator> siSet = t.getSubjectIdentifiers();
		if (!siSet.isEmpty())
			return "\"" + siSet.iterator().next().toExternalForm() + "\" << indicators";

		Set<Locator> slSet = t.getSubjectLocators();
		if (!slSet.isEmpty())
			return "\"" + slSet.iterator().next().toExternalForm() + "\" << locators";

		Set<Locator> iiSet = t.getItemIdentifiers();
		if (!iiSet.isEmpty())
			return "\"" + iiSet.iterator().next().toExternalForm() + "\" << item";

		throw new IllegalArgumentException("The given topic has no identifier!");
	}

	/**
	 * Returns a tmql query part which returns the given topic.
	 * 
	 * @param t
	 *            the topic which identifier should be used
	 * @return a string containing the identifier string
	 */
	public static String getIdentifierString(String id, IdType type) {
		switch (type) {
		case ITEM_IDENTIFIER:
			return "\"" + id + "\" << item";
		case SUBJECT_IDENTIFIER:
			return "\"" + id + "\" << indicators";
		case SUBJECT_LOCATOR:
			return "\"" + id + "\" << locators";
		}

		throw new IllegalArgumentException("The given topic has no identifier!");
	}
}
