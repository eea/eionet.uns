/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Unified Notification System
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 * 
 * Contributors(s):
 *    Original code: Nedeljko Pavlovic (ED) 
 */

package com.eurodyn.uns.util.xml;

import java.io.*;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;


public class XmlSerialization implements IXmlSerializer {

	private XMLSerializer xmlSerializer;
	private IXmlCtx ctx = null;


	public XmlSerialization() {
		xmlSerializer = new XMLSerializer();
		OutputFormat outputFormat = new OutputFormat();
		outputFormat.setAllowJavaNames(true);
		outputFormat.setEncoding("UTF-8");
		outputFormat.setVersion("1.0");
		outputFormat.setIndenting(true);
		outputFormat.setIndent(5);
		xmlSerializer.setOutputFormat(outputFormat);
	}
	
	public XmlSerialization(IXmlCtx ctx) {
		this();
		this.ctx=ctx;
	}



	
	public ByteArrayOutputStream serializeToOutStream() throws XmlException {
		ByteArrayOutputStream byteOutputStream = null;
		try {
			byteOutputStream = new ByteArrayOutputStream();
			xmlSerializer.setOutputByteStream(byteOutputStream);
			xmlSerializer.serialize(ctx.getDocument());
		} catch (IOException ioe) {
			throw new XmlException("Error occurred while serializing XML document. Reason: " + ioe.getMessage());
		} finally {
		}
		return byteOutputStream;
	}
	
	
	public void serializeToFs(String fullFileName) throws XmlException {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(fullFileName);
			xmlSerializer.setOutputByteStream(fileOutputStream);
			xmlSerializer.serialize(ctx.getDocument());
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException ioe) {
			throw new XmlException("Error occurred while serializing XML document. Reason: " + ioe.getMessage());
		}
	}
	
	public ByteArrayInputStream serializeToInStream() throws XmlException {
		ByteArrayInputStream byteInputStream = null;
		try {
			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
			xmlSerializer.setOutputByteStream(byteOutputStream);
			xmlSerializer.serialize(ctx.getDocument());
			byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
			byteOutputStream.flush();
			byteOutputStream.close();
		} catch (IOException ioe) {
			throw new XmlException("Error occurred while serializing XML document . Reason: " + ioe.getMessage());
		} finally {
		}
		return byteInputStream;
	}

}