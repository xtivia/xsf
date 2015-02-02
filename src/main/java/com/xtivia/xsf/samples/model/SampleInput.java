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
package com.xtivia.xsf.samples.model;

import java.util.Date;

public class SampleInput {
	
	private String inputText;
	private int    inputNumber;
	private Date   inputDate;
	
	public String getInputText() {
		return inputText;
	}
	
	public void setInputText(String inputText) {
		this.inputText = inputText;
	}
	
	public int getInputNumber() {
		return inputNumber;
	}
	
	public void setInputNumber(int inputNumber) {
		this.inputNumber = inputNumber;
	}
	
	public Date getInputDate() {
		return inputDate;
	}
	
	public void setInputDate(Date inputDate) {
		this.inputDate = inputDate;
	}

}
