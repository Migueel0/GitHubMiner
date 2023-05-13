package aiss.githubminer.model.CommitData;

import com.fasterxml.jackson.annotation.JsonProperty;



public class CommitData {

    @JsonProperty("sha")
    private String sha;
    @JsonProperty("commit")
    public CommitProperty commit;


    @JsonProperty("sha")
    public String getSha() {
        return sha;
    }
    @JsonProperty("sha")
    public void setSha(String sha) {
        this.sha = sha;
    }
    @JsonProperty("commit")
    public CommitProperty getCommit() {
        return commit;
    }
    @JsonProperty("commit")
    public void setCommit(CommitProperty commit) {
        this.commit = commit;
    }


}
