package com.eurodyn.uns.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Send notifications by email
*/
public class SendMail implements java.io.Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMail.class);

  /**
   * Creates a new instance of SendMail.
   */
  public SendMail() {
  }

  /**
   * Send mail method.
   *
   * @param mailTo  Recipient.
   * @param subject Subject of e-mail.
   * @param body    Body of e-mail.
   */
  public static synchronized void sendMail (
          final String mailTo,
          final String subject,
          final String body,
          final String html,
          final String id,
          final String SMTP_SERVER,
          final Integer smtp_port,
          final String smtp_username,
          final String smtp_password,
          final String smtp_accountFrom) throws Exception
    {

    if (StringUtils.isBlank(SMTP_SERVER)) {
        LOGGER.debug("Emulating e-mail to <" + mailTo + ">, subject: " + subject);
        return;
    }

    // JavaMail API
    Properties sysProps = System.getProperties();
    sysProps.put( "mail.smtp.host", SMTP_SERVER );
    sysProps.put("mail.smtp.port", smtp_port);

    MailAuthenticator auth = null;
    if ( smtp_username != null && smtp_password != null ){
        auth = new MailAuthenticator( smtp_username, smtp_password );
    }
    Session s = Session.getDefaultInstance( sysProps, auth );
    s.setDebug( true );
    MimeMessage msg = new MimeMessage( s );
    try
    {
        msg.setFrom( new InternetAddress( smtp_accountFrom ) );
        msg.setRecipients( Message.RecipientType.TO, InternetAddress.parse( mailTo, false ) );
        msg.setSubject( subject );

        if(html != null && html.length() > 0){

            Multipart mp = new MimeMultipart("alternative");

            BodyPart textPart = new MimeBodyPart();
            textPart.setText(body); // sets type to "text/plain"
            textPart.setHeader("Content-Type","text/plain; charset=\"utf-8\"");
            textPart.setHeader("Content-Transfer-Encoding", "quoted-printable");

            BodyPart pixPart = new MimeBodyPart();
            pixPart.setContent(html, "text/html");
            pixPart.setHeader("Content-Type","text/html; charset=\"utf-8\"");
            pixPart.setHeader("Content-Transfer-Encoding", "quoted-printable");

            // Collect the Parts into the MultiPart
            mp.addBodyPart(textPart);
            mp.addBodyPart(pixPart);

            // Put the MultiPart into the Message
            msg.setContent(mp);
        }else{
            msg.setText(body);
        }

        msg.setSentDate( new Date() );
        msg.setContentID(id);
        LOGGER.info("Sending e-mail, id=" + id);
        Transport.send( msg );
        LOGGER.info("E-mail sent, id=" + id);
    }
    catch ( Exception ex )
    {
        LOGGER.error(ex.getMessage(), ex);
        throw new Exception("Error occured when trying to send an e-mail: "+ex.toString());
    }
  }
}
