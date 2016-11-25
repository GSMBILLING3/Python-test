package uk.co.gsmbilling.smshubsidemod;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import uk.co.gsmbilling.sms2uk.utils.ParseUtility;

/**
 * Created by GSM-1 on 2016-05-21.
 */

@ParseClassName("Message")
public class Message extends ParseObject{

    public String getText() {
        return getString("text");
    }

    public String getSenderNumber() {
        return getString("senderNumber");
    }

    public String getRecipientNumber() {
        return getString("recipientNumber");
    }

    public void setText(String text) {
        put("text", text);
    }

    public void setSenderNumber(String senderNumber) {
        put("senderNumber", senderNumber);
    }

    public void setRecipientNumber(String recipientNumber) {
        put("recipientNumber", recipientNumber);
    }

    public boolean isFromCurrentUser() {
        return (getSenderNumber().equals(ParseUtility.getCurrentUserNumber()));
    }
}