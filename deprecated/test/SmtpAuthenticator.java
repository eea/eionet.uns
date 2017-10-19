import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 *
 */
class SmtpAuthenticator extends Authenticator {

    /**  */
    private PasswordAuthentication password_auth;

    /**
     * Class constructor.
     *
     * @param smtp_user
     * @param smtp_password
     */
    public SmtpAuthenticator(String smtp_user, String smtp_password) {
        password_auth = new PasswordAuthentication(smtp_user, smtp_password);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Authenticator#getPasswordAuthentication()
     */
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return password_auth;
    }
}
