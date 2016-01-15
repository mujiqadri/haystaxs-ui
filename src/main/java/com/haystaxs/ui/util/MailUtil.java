package com.haystaxs.ui.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {
    final static Logger logger = LoggerFactory.getLogger(MailUtil.class);
    @Qualifier("mailSender")
    @Autowired
    private MailSender mailSender;
    @Qualifier("templateMessage")
    @Autowired
    private SimpleMailMessage templateMailMessage;

    public void sendEmail(String subject, String body, String[] toAddresses) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage(this.templateMailMessage);
        // From is being set in the templateMessage
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setTo(toAddresses);
        simpleMailMessage.setText(body);

        try {
            this.mailSender.send(simpleMailMessage);
        }
        catch (Exception ex) {
            logger.error("Email sending failed.");
            // TODO: Must log this as system error
        }
    }
}
