package com.eurodyn.uns.web.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.eurodyn.uns.model.DeliveryType;

public class DeliveryTypes implements Converter {

    public Object getAsObject(FacesContext arg0, UIComponent arg1, String value)throws ConverterException {
        return new DeliveryType(new Integer(value));
    }

    public String getAsString(FacesContext arg0, UIComponent arg1, Object value)
            throws ConverterException {
        if (value instanceof String) { // when conrols immediate attribute is set to "true" 
            return (String) value;
        }       
        return ((DeliveryType) value).getId().toString();
    }

}
