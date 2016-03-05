/**
 * Copyright (c) 2015 Xtivia, Inc. All rights reserved.
 *
 * This file is part of the Xtivia Services Framework (XSF) library.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.xtivia.xsf.core.commands;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.xtivia.xsf.core.auth.IAuthorized;

/**
 * class CommandChain: A special command instance which can chain an unlimited number 
 * of commands into a command sequence.
 *
 * When processing the commands, the first command failure results in a failure of the chain.
 *
 */
public class CommandChain implements ICommand, IAuthorized {
	private static final Logger logger = LoggerFactory.getLogger(CommandChain.class);

	/**
	 * applicationContext: The spring application context.
	 */
	@Autowired private ApplicationContext applicationContext;

	/**
	 * _commands: The list of registered command objects.
	 */
	private List<ICommand> _commands = new ArrayList<ICommand>();

	/**
	 * addCommand: Adds a command to the chain.
	 * @param command
	 */
	public void addCommand(ICommand command) {
		_commands.add(command);
	}

	/**
	 * addCommand: Adds a command to the chain using a proxy to a named bean.
	 * @param beanName Name of the spring bean to load as a command.
	 */
	public void addCommand(String beanName) {
		_commands.add(new CommandProxy(beanName));
	}

	/**
	 * setCommands: Sets the list of commands to the given command list.
	 * @param commands
	 */
	public void setCommands(List<ICommand> commands) {
		_commands.clear();
		_commands.addAll(commands);
	}
	
	/**
	 * execute: Loop through chain of commands executing each one.
	 * If a command fails stop the chain and return the failed result.
	 * Otherwise if all commands succeed return the result from the
	 * last command in the chain
	 *
	 * When processing is complete walk chain in reverse order and give any
	 * commands that implement the IFilter interface a chance to do post
	 * processing.
	 * @param ctx Context for the command.
	 * @return CommandResult The result of the command execution.
	 */
	public CommandResult execute(IContext ctx) throws Exception {

		// track the last successfully executed command in the command chain.
		int lastSuccess = 0;

		// create a command result
		CommandResult commandResult = new CommandResult().setSucceeded(false);

		// for each command in the list
		for (ICommand command : _commands) {
			try {
				// execute the command.
			    commandResult  = command.execute(ctx);

				lastSuccess ++;
			} catch (Exception e) {
				logger.warn("Exception encounted during command processing [" + 
			                command.getClass().getName() + "]: " + e.getMessage(), e);

				// walkback commands letting them unwind because of the failure.
				walkbackCommands(lastSuccess,commandResult,e);

				// rethrow the exception.
				throw e;
			}

			if (!commandResult.isSucceeded()) {
				// command execution failed, stop processing commands.
				break;
			} 
		}

		// if there was at least one success
		if (lastSuccess > 0) {
			// allow the commands to post process the command result.
			walkbackCommands(lastSuccess,commandResult,null);
		}
		
		return commandResult;
	}

	/**
	 * walkbackCommands: Walks back the commands so they can undo anything they might have done or 
	 * apply a transform on the final result.
	 * @param lastSuccess Index of the last successfully completed command.
	 * @param commandResult The current command result.
	 * @param exception Optional exception that was encountered during command execution.
	 */
	protected void walkbackCommands(int           lastSuccess,
			                      CommandResult commandResult, 
			                      Exception     exception) {

		// Start from the last completed command
		for (int i=lastSuccess-1; i>0; i--) {
			// get the command object from the array
			ICommand command = _commands.get(i);

			// if the command implements the IFilter interface.
			if (command instanceof IFilter) {
				try {
					// let the filter post-process the result.
					((IFilter) command).postProcess(commandResult, exception);
				} catch (Exception e) {
					logger.error("Error invoking filter on class [" + 
				                  command.getClass().getName() + "]: " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * authorize: Proxies the auth check to the commands in the chain in order to give them a chance to determine auth.
	 * @param context Context for the authorization check.
	 * @return boolean <code>true</code> if authorized.
	 */
	@Override
	public boolean authorize(IContext context) {

		// for each command in the list
		for (ICommand command : _commands) {
			if (command instanceof IAuthorized) {
				if (! ((IAuthorized) command).authorize(context)) {
					// this command said not authorized
					return false;
				}
			}
		}

		// if we get here no command said unauthorized, so give access.
		return true;
	}
	
	/**
	 * class CommandProxy: A proxy class to bridge a connection from the command chain to a Spring bean.
	 * This class is used as a proxy for commands that are Spring beans. It allows
	 * the command name to be used when creating the proxy and at run time the proxy
	 * will obtain the bean from Spring and invoke as appropriate. This permits
	 * beans to define themselves using Spring metaphors such as singleton/prototype
	 * scope and yet still be included in a command chain.
	 */
	public class CommandProxy implements ICommand, IFilter, IAuthorized {

		/**
		 * _commandName: The bean name the proxy is wrapping.
		 */
		private final String _commandName;
		/**
		 * isFilter: Flag indicating whether the bean supports a filter or not.
		 */
		private boolean isFilter = false;

		private ICommand loadedBean;

		/**
		 * CommandProxy: Constructor for the instance.
		 * @param commandName The bean name to proxy.
		 */
		public CommandProxy(String commandName) {
			_commandName = commandName;
		}

		/**
		 * getCommand: Utility method to retrieve the command, possibly from the Spring context.
		 * @return ICommand The retrieved command.
		 */
		protected ICommand getCommand() {
			// return the loaded bean if we have it already.
			if (loadedBean != null) return loadedBean;

			// get the bean being proxied.
			Object bean = applicationContext.getBean(_commandName);
			if (bean == null) {
				throw new IllegalArgumentException(String.format("Bean with name=%s not found in context",_commandName));
			}

			// if it does not implement ICommand, it's an error.
			if (!(bean instanceof ICommand)) {
				throw new IllegalArgumentException(String.format("Bean with name=%s is not a command ",_commandName));
			}

			loadedBean = (ICommand) bean;

			// check if the bean supports the IFilter interface and cache the result.
			if ((bean instanceof IFilter)) {
				isFilter = true;
			}

			return loadedBean;
		}

		/**
		 * execute: Proxies the execute method to the Spring bean.
		 * @param ctx
		 * @return CommandResult The command result.
		 */
        @Override
		public CommandResult execute (IContext ctx) throws Exception {

			ICommand bean = getCommand();

			// let the bean execute.
			return ((ICommand)bean).execute(ctx);
		}
		
 
		/**
		 * postProcess: Implementation for the iFilter interface.
		 * @param cr
		 * @param e
		 */
       @Override
		public void postProcess(CommandResult cr, Exception e) {
			if (isFilter) {
				((IFilter) getCommand()).postProcess(cr, e);
			}
		}

		/**
		 * authorize: Allows access to bean to check authorization.
		 * @param context Context for the authorization check.
		 * @return boolean <code>true</code> if authorized.
		 */
		@Override
		public boolean authorize(IContext context) {
			// get the bean being proxied.
			Object bean = getCommand();

			// if the bean implements the interface
			if (bean instanceof IAuthorized) {
				// let it check the auth result.
				return ((IAuthorized) bean).authorize(context);
			}

			// this bean doesn't have an auth override, allow access.
			return true;
		}
	}
}
