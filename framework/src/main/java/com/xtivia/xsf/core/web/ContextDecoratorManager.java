package com.xtivia.xsf.core.web;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.xtivia.xsf.core.commands.IContext;

/**
 * class ContextDecoratorManager: Manages the decoration of the context at runtime.
 */
@Component("contextDecoratorMgr")
public class ContextDecoratorManager implements ApplicationContextAware {
	private static final Logger _logger = LoggerFactory.getLogger(ContextDecoratorManager.class);

	/**
	 * applicationContext: The spring application context
	 */
	@Autowired private ApplicationContext applicationContext;

	/**
	 * contextDecorators: List of context decorators that may adjust the context at request time.
	 */
	@Autowired(required = false)
	private List<IContextDecorator> contextDecorators = null;

	/**
	 * decorateContext: Processes the context decorators.
	 * @param context The context to decorateContext
	 * @return IContext The decorated context.
	 */
	public IContext decorateContext(IContext context) {

		// get the list of additional context decorators.
		List<IContextDecorator> decorators = getContextDecorators();

		// if the list has been given
		if ((decorators != null) && (! decorators.isEmpty())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("Invoking " + decorators + " context decorators.");
			}

			// for each decorator in the list
			for (IContextDecorator decorator : decorators) {
				// invoke the decorator to decorate the context.
				context = decorator.decorateContext(context);
			}
		}

		return context;
	}

	/**
	 * getContextDecorators: Returns the list of context decorators.  If the list has not already been defined and/or
	 * populated, the method will check the Spring context to see if any context decorators have been added.
	 * @return List The list of context decorator instances.
	 */
	protected List<IContextDecorator> getContextDecorators() {

		// if the list is not populated, use spring to get it only once and then cache it.
		if (contextDecorators == null) {
			if (_logger.isDebugEnabled()) _logger.debug("Decorator list has not been populated, checking spring context.");

			// get the map of beans that implement the IContextPopulator interface.
			Map<String, IContextDecorator> decoratorMap = applicationContext.getBeansOfType(IContextDecorator.class, true, true);

			// create a new list
			contextDecorators = new LinkedList<IContextDecorator>();

			// if there are decorator beans
			if ((decoratorMap != null) && (! decoratorMap.isEmpty())) {
				if (_logger.isDebugEnabled()) _logger.debug("Have " + decoratorMap.size() + " context decorators to process.");

				// for each bean name
				for (String beanName : decoratorMap.keySet()) {
					// add the context decorator to the list.
					contextDecorators.add(decoratorMap.get(beanName));
				}
			} else if (_logger.isDebugEnabled()) _logger.debug("No context decorators found.");

			if (_logger.isDebugEnabled()) _logger.debug("Have a list of " + contextDecorators.size() + " decorators.");
		}

		return contextDecorators;
	}

	/**
	 * setContextDecorators: Setter to update the context decorators list.
	 * @param decorators The list of context decorators.
	 */
	public void setContextDecorators(final List<IContextDecorator> decorators) {
		contextDecorators = decorators;
	}

	/**
	 * setApplicationContext: Setter for the applicationContext member.
	 *
	 * @param applicationContext New value for applicationContext.
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
