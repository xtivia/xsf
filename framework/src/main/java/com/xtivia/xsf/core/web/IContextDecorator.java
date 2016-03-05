package com.xtivia.xsf.core.web;

import com.xtivia.xsf.core.commands.IContext;

/**
 * class IContextDecorator: An interface for a component which will decorate (inject objects or otherwise
 * extend/modify the context) the IContext instance at runtime.
 */
public interface IContextDecorator {

	/**
	 * decorateContext: Called to decorate the context.  When this is invoked the core elements (http request and response) have
	 * been populated, but no guarantee is made about what other decoration may also have been completed.
	 * @param context The context to decorateContext.
	 * @return IContext The decorated context.
	 */
	IContext decorateContext(IContext context);
}
