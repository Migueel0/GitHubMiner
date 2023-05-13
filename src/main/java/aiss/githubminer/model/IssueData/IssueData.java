package aiss.githubminer.model.IssueData;

import aiss.githubminer.model.Comment;
import aiss.githubminer.model.User;
import aiss.githubminer.model.UserData.UserData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.stream.Collectors;
@JsonInclude(JsonInclude.Include.ALWAYS)
public class IssueData {
    @JsonProperty("id")
    private String id;
    @JsonProperty("number")
    private String number;
    @JsonProperty("title")
    private String title;
    @JsonProperty("body")
    private String body;
    @JsonProperty("description")
    private String description;
    @JsonProperty("state")
    private String state;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("closed_at")
    private String closedAt;
    @JsonProperty("user")
    private UserData user;
    @JsonProperty("assignee")
    private User assignee;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("labels")
    private List<Label> labels;
    @JsonProperty("reactions")
    private Reactions reactions;
    @JsonProperty("id")
    public String getId() {
        return id;
    }
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }
    @JsonProperty("number")
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }
    @JsonProperty("body")
    public String getBody() {
        return body;
    }
    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }
    @JsonProperty("state")
    public String getState() {
        return state;
    }
    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
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
    @JsonProperty("closed_at")
    public String getClosedAt() {
        return closedAt;
    }
    @JsonProperty("closed_at")
    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
    }
    @JsonProperty("user")
    public UserData getUser() {
        return user;
    }
    @JsonProperty("user")
    public void setUser(UserData user) {
        this.user = user;
    }
    @JsonProperty("assignee")
    public User getAssignee() {
        return assignee;
    }
    @JsonProperty("assignee")
    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    @JsonProperty("html_url")
    public String getHtmlUrl() {
        return htmlUrl;
    }

    @JsonProperty("html_url")
    public void setHtmlUrl(String webUrl) {
        this.htmlUrl = webUrl;
    }


    @JsonProperty("labels")
    public List<Label> getLabels() {
        return labels;
    }

    @JsonProperty("labels")
    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    @JsonProperty("reactions")
    public Reactions getReactions() {
        return reactions;
    }

    @JsonProperty("reactions")
    public void setReactions(Reactions reactions) {
        this.reactions = reactions;
    }

}
