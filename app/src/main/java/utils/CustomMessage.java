package utils;

/**
 * Created by bunny on 13/07/17.
 */

public class CustomMessage  {

    private String customMessageText , customMessageDevice ,customMessageUserUID;
    int messageType ;
    long messageTime;


    public String getCustomMessageText() {
        return customMessageText;
    }

    public void setCustomMessageText(String customMessageText) {
        this.customMessageText = customMessageText;
    }

    public String getCustomMessageDevice() {
        return customMessageDevice;
    }

    public void setCustomMessageDevice(String customMessageDevice) {
        this.customMessageDevice = customMessageDevice;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getCustomMessageUserUID() {
        return customMessageUserUID;
    }

    public void setCustomMessageUserUID(String customMessageUserUID) {
        this.customMessageUserUID = customMessageUserUID;
    }

    public String resolveTimeStamp() {
        return "";
    }
}
