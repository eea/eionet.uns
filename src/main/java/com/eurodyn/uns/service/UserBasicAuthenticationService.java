package com.eurodyn.uns.service;

import com.eurodyn.uns.web.exceptions.EndpointCallException;
import eionet.acl.AppUser;
import eionet.acl.SignOnException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class UserBasicAuthenticationService {

    @Autowired
    public UserBasicAuthenticationService() {
    }

    public String checkUserAuthentication(String basicAuthString) throws EndpointCallException {
        if (basicAuthString == null || !basicAuthString.startsWith("Basic ")) {
            throw new EndpointCallException("No Basic authentication received.");
        }

        String[] authenticationArray = basicAuthString.split(" ");
        if (authenticationArray.length != 2) {
            throw new EndpointCallException("Basic Authentication error.");
        }
        String encodedUsernamePassword = authenticationArray[1].trim();
        byte[] decodedBytes = Base64.getDecoder().decode(encodedUsernamePassword);
        String decodedString = new String(decodedBytes);
        String[] decodedUsernamePassword = decodedString.split(":");
        if (decodedUsernamePassword.length != 2) {
            throw new EndpointCallException("Credentials were provided incorrectly.");
        }
        String providedUserName = decodedUsernamePassword[0];
        String providedPassword = decodedUsernamePassword[1];

        AppUser user = new AppUser();
        try {
            user.authenticate(providedUserName, providedPassword);
        }
        catch(SignOnException soe){
            throw new EndpointCallException("User was not authenticated. Invalid credentials");
        }
        return providedUserName;
    }
}
