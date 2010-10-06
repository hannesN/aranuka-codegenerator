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

 /**
 /* Interface containing the qualified names of kuria and arnauka annotations.
 /* 
 /* @author Hannes Niederhausen
 /*
 */
public interface IAnnotationConstants {

	public static interface Kuria {
		public static final String TEXT = "de.topicmapslab.kuria.annotation.Text";
		
		// table annotations
		public static final String COLUMN = "de.topicmapslab.kuria.annotation.table.Column";
		public static final String TABLEELEMENT = "de.topicmapslab.kuria.annotation.table.TableElement";
		
		// tree annotations
		public static final String CHILDREN = "de.topicmapslab.kuria.annotation.tree.Children";
		public static final String TREENODE = "de.topicmapslab.kuria.annotation.tree.TreeNode";
		
		// widget annotations
		public static final String CHECK = "de.topicmapslab.kuria.annotation.widgets.Check";
		public static final String COMBO = "de.topicmapslab.kuria.annotation.widgets.Combo";
		public static final String DATE = "de.topicmapslab.kuria.annotation.widgets.Date";
		public static final String DIRECTORY = "de.topicmapslab.kuria.annotation.widgets.Directory";
		public static final String EDITABLE = "de.topicmapslab.kuria.annotation.widgets.Editable";
		public static final String FILE = "de.topicmapslab.kuria.annotation.widgets.File";
		public static final String GROUP = "de.topicmapslab.kuria.annotation.widgets.Group";
		public static final String HIDDEN = "de.topicmapslab.kuria.annotation.widgets.Hidden";
		public static final String LIST = "de.topicmapslab.kuria.annotation.widgets.List";
		public static final String TEXTFIELD = "de.topicmapslab.kuria.annotation.widgets.TextField";
		
	}
	
	public static interface Aranuka {
		public static final String ASSOCIATION = "de.topicmapslab.aranuka.annotations.Association";
		public static final String ASSOCIATION_CONTAINER = "de.topicmapslab.aranuka.annotations.AssociationContainer";
		public static final String GENERATED = "de.topicmapslab.aranuka.annotations.Generated";
		public static final String ID = "de.topicmapslab.aranuka.annotations.Id";
		public static final String NAME = "de.topicmapslab.aranuka.annotations.Name";
		public static final String OCCURRENCE = "de.topicmapslab.aranuka.annotations.Occurrence";
		public static final String ROLE = "de.topicmapslab.aranuka.annotations.Role";
		public static final String SCOPE = "de.topicmapslab.aranuka.annotations.Scope";
		public static final String TOPIC = "de.topicmapslab.aranuka.annotations.Topic";
	}
}
