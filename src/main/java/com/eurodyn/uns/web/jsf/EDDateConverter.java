package com.eurodyn.uns.web.jsf;

import java.text.ParseException;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.eurodyn.uns.util.DateUtil;

public class EDDateConverter implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value)
			throws ConverterException {
		return value;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object value)
			throws ConverterException {
		String result;
		try {
			result = DateUtil.messageFormatWithTime((Date)value);
		} catch (ParseException e) {
			throw new ConverterException(e.getMessage());
		}
		return result;
	}

}
