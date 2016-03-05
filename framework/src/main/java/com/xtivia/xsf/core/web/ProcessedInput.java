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
package com.xtivia.xsf.core.web;

/**
 * class ProcessedInput: Container for the processed input parsed out of the incoming XSF request.
 */
public class ProcessedInput {

	/**
	 * canContinue: Flag indicating that the command can continue (allows for breaking out in case of command or parse failure).
	 */
	private boolean canContinue = false;
	/**
	 * inputData: The input data object parsed out of the incoming JSON using Jackson mapper.
	 */
	private Object  inputData   = null;

	/**
	 * ProcessedInput: Default no-arg constructor.
	 */
	public ProcessedInput() {
	}

	/**
	 * ProcessedInput: Constructor.
	 * @param canContinue
	 * @param inputData
	 */
	public ProcessedInput(boolean canContinue, Object inputData) {
		this.canContinue = canContinue;
		this.inputData = inputData;
	}

	/**
	 * isCanContinue: Returns the value for the can continue flag.
	 * @return boolean <code>true</code> if the input is okay and command can continue, otherwise it should not continue processing.
	 */
	public boolean isCanContinue() {
		return canContinue;
	}

	/**
	 * setCanContinue: Sets the value for the can continue flag.
	 * @param canContinue
	 */
	public void setCanContinue(boolean canContinue) {
		this.canContinue = canContinue;
	}

	/**
	 * getInputData: Returns the input data object parsed out of the request.
	 * @return Object The parsed object or <code>null</code>.
	 */
	public Object getInputData() {
		return inputData;
	}

	/**
	 * setInputData: Sets the input data object.
	 * @param inputData
	 */
	public void setInputData(Object inputData) {
		this.inputData = inputData;
	}
}
