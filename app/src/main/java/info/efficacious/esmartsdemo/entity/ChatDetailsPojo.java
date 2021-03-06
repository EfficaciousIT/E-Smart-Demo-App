package info.efficacious.esmartsdemo.entity;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatDetailsPojo {

    @SerializedName("ChatDetails")
    @Expose
    private List<ChatDetail> chatDetails = null;

    public List<ChatDetail> getChatDetails() {
        return chatDetails;
    }

    public void setChatDetails(List<ChatDetail> chatDetails) {
        this.chatDetails = chatDetails;
    }

}