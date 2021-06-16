package com.eurodyn.uns.service;

import com.eurodyn.uns.web.exceptions.EndpointCallException;

public interface UserBasicAuthenticationService {

    String checkUserAuthentication(String basicAuthString) throws EndpointCallException;
}
