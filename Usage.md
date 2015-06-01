# Usage #

Steps:

  * Create a schema with annotations using Onotoa ( see http://code.google.com/a/eclipselabs.org/p/onotoa/ )
  * Write a small java application
  * Use the following code line to generate the model:
```
// create a TMAPI Topic Map System
TopicMapSystem topicMapSystem = ...
// load topic map with an TMAPI2 engine
TopicMap topicMap = ...
// specify the package name where the model is located
String packageName = "de.test.model";
// generate util classes used by the generic editor SDK
boolean generateGennyClasses = false;
// generate annotations for Kuria in the model
boolean generateKuriaAnnotations = false;

// the path to the root source folder
String path = "/home/user/projects/myproject/src/main";

// the factory parses the topic map and generates a metadata describing the code to generate
AranukaDescriptorFactory fac = new AranukaDescriptorFactory(topicMapSystem(), 
                                                            topicMap,
                                                            packageName, 
                                                            generateGennyClasses, 
                                                            generateKuriaAnnotations);

// get a generator from the factory 
CodeGenerator gen = fac.getCodeGenerator();
// the generator creates the java files based on the meta model
gen.generateCode(new File(path));
```