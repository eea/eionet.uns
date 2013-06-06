package com.eurodyn.uns.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator {
    private PasswordAuthentication password_auth;

    public MailAuthenticator(String smtp_user, String smtp_password) {
        password_auth = new PasswordAuthentication(smtp_user, smtp_password);
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return password_auth;
    }
}
