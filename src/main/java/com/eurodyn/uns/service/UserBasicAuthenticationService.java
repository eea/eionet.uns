package com.eurodyn.uns.service;

import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.web.exceptions.EndpointCallException;
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

        UserFacade userFacade = new UserFacade();
        User user = userFacade.authenticate(providedUserName, providedPassword);
        if(user == null){
            throw new EndpointCallException("User was not authenticated. Invalid credentials");
        }
        if(!userFacade.hasPerm(providedUserName, "/" + "rest", "x")){
            throw new EndpointCallException("User was not authenticated. User" + providedUserName + " does not have permission.");
        }
        return providedUserName;
    }
}
