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

import java.math.BigDecimal;
import java.util.Date;

public class TestResource {
	
	public static final String DEFAULT_TEXT = "no text set";
	public static final BigDecimal DEFAULT_RATE = new BigDecimal("0.00");
	public static final Date DEFAULT_TIMESTAMP = new Date();

	private String	    text;
	private BigDecimal	rate;
	private Date        timestamp;

	
	public TestResource(String text, BigDecimal rate, Date timestamp) {
		this.text = text;
		this.rate = rate;
		this.timestamp = timestamp;
	}

	public TestResource() {
    	this(DEFAULT_TEXT, DEFAULT_RATE, DEFAULT_TIMESTAMP);
    }
	
    public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getText() {
		return text;
	}

	public void setText(String message) {
		this.text = message;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
}
