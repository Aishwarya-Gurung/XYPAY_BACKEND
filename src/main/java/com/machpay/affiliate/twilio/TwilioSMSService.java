package com.machpay.affiliate.twilio;

import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class TwilioSMSService {
    private Logger logger = LoggerFactory.getLogger(TwilioSMSService.class);

    private String fromPhoneNumber;

    @Autowired
    TwilioSMSService(TwilioConfig twilioConfig) {
        fromPhoneNumber = twilioConfig.getFromPhoneNumber();
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    }

    public void sendVerificationCodeSynchronously(String toPhoneNumber, String smsContent) {
        try {
            sendSMS(toPhoneNumber, smsContent);
        } catch (ApiException ex) {
            logger.error("Error occurred while sending phone verification code to {}", toPhoneNumber, ex);
            throw new BadRequestException("Sorry, we are unable  to send verification code to your phone number.");
        }
    }

    @Async
    public void sendVerificationCodeAsynchronously(String toPhoneNumber, String smsContent) {
        try {
            sendSMS(toPhoneNumber, smsContent);
        } catch (ApiException ex) {
            logger.error("Error occurred while sending phone verification code to {}", toPhoneNumber, ex);
            throw new BadRequestException("Sorry, we are unable  to send verification code to your phone number.");
        }
    }

    public void sendSMS(String toPhoneNumber, String smsContent) {
        Message message = Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(fromPhoneNumber),
                smsContent).create();
        logger.info("Sending phone verification code to phone: {} with sid: {} ", toPhoneNumber, message.getSid());
    }
}
