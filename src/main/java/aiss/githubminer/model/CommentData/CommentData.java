package aiss.githubminer.model.CommentData;

import aiss.githubminer.model.Comment;
import aiss.githubminer.model.User;
import aiss.githubminer.model.UserData.UserData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentData {
    @JsonProperty("id")
    private String id;
    @JsonProperty("body")
    private String body;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("user")
    private UserData user;
    @JsonProperty("id")
    public String getId() {
        return id;
    }
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }
    @JsonProperty("body")
    public String getBody() {
        return body;
    }
    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    @JsonProperty("user")
    public UserData getUser() {
        return user;
    }
    @JsonProperty("user")
    public void setUser(UserData user) {
        this.user = user;
    }

}
