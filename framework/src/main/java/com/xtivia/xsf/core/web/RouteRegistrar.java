package com.xtivia.xsf.core.web;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

/**
 * class RouteRegistrar: A spring class to handle bean definitions and processing routes after beans have been
 * created.
 */
@Component
public class RouteRegistrar implements BeanDefinitionRegistryPostProcessor {
	
	private static final Logger _logger = LoggerFactory.getLogger(RouteRegistrar.class);

	/**
	 * postProcessBeanFactory: Empty method here to satisfy the interface.
	 * @param bf
	 * @throws BeansException
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
	}

	/**
	 * postProcessBeanDefinitionRegistry:
	 * @param bdr
	 * @throws BeansException
	 */
	@SuppressWarnings("unchecked")
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry bdr) throws BeansException {
		// scan the classpath for an optional file generated during the Maven
		// build process. This provides automated information on classes that
		// are annotated with IRoute

		// get the input stream to our file...
		InputStream is = ServicesController.class.getResourceAsStream("/META-INF/xsf-reflections.xml");

		// if the stream is valid
		if (is != null) {
			try {
				_logger.info("Loading routes from build-generated metadata ...");

				// Next lines parse the XML into a DOM doc model object.
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		        org.w3c.dom.Document doc = dBuilder.parse(is);
		        DOMBuilder domBuilder = new DOMBuilder();
		        Document xmldoc = domBuilder.build(doc);

				// get the annotation element
	            Element annoElement = xmldoc.getRootElement().getChild("TypeAnnotationsScanner");

				// if it is found
	            if (annoElement != null) {
		            // get the list of entry children.
	            	List<Element> entries = annoElement.getChildren("entry");

		            // for each child entry
	            	for (Element entry : entries) {
			            // get the key for the entry
	            		Element key = entry.getChild("key");

			            // if it is good
	            		if (key != null) {
				            // extract the annotation text
	            			String anno = key.getText();

				            // if it is a route annotation
	            			if (anno != null && anno.equals("com.xtivia.xsf.core.annotation.Route")) {
					            // extract the values from the entry
	            				Element valuesElement =entry.getChild("values");

					            // if the values were found
	            				if (valuesElement != null) {
									// get the value children
	            					List<Element> values = valuesElement.getChildren("value");

						            // for each value found
	            					for (Element value: values) {
							            // extract the route class name
	            						String routeClassName = value.getText();

	            						_logger.info("Loading route class="+routeClassName);
	            						loadClass(bdr,routeClassName);
	            					}
	            				}
	            			}
	            		}
	            	}
	            }
			} catch (Exception e) {
				_logger.error("Error processing the route annotations: " + e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
	}

	/**
	 * loadClass: Loads the route class into the bean defn registry.
	 * @param bdr
	 * @param className
	 */
	private void loadClass(BeanDefinitionRegistry bdr, String className) {
		
		try {
			// try using the class loader to load the given route class
			Class<?> clazz = Class.forName(className);

			// define it in the registry under it's own class name.
			bdr.registerBeanDefinition(className, new AnnotatedGenericBeanDefinition(clazz));
		} catch (ClassNotFoundException e) {
			_logger.error("XSF Route Registrator could not locate class="+className, e);
		}
	}
}
