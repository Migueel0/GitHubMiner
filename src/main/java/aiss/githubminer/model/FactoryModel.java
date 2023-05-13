package aiss.githubminer.model;

import aiss.githubminer.model.CommentData.CommentData;
import aiss.githubminer.model.CommitData.Author;
import aiss.githubminer.model.CommitData.CommitData;
import aiss.githubminer.model.CommitData.Committer;
import aiss.githubminer.model.IssueData.IssueData;
import aiss.githubminer.model.ProjectData.ProjectData;
import aiss.githubminer.model.UserData.UserData;

import java.util.ArrayList;
import java.util.List;


public class FactoryModel {

    public static List<Comment> formatComment(List<CommentData> commentData){
        List<Comment> comments = new ArrayList<>();
        commentData.stream().forEach(comment -> {
            String id = comment.getId();
            String body = comment.getBody();
            String createdAt = comment.getCreatedAt();
            String updatedAt = comment.getUpdatedAt();
            User author = FactoryModel.formatUser(comment.getUser());
            Comment comment1 =  new Comment(id,body,createdAt,updatedAt,author);
            comments.add(comment1);
        });

        return comments;
    }

    public static List<Commit> formatCommits(List<CommitData> commitData){
        List<Commit> commits = new ArrayList<>();

        commitData.stream().forEach(commit -> {
            Author author = commit.getCommit().getAuthor();
            Committer committer = commit.getCommit().getCommitter();
            String id = commit.getSha();
            String title = commit.getCommit().getMessage();
            if(title.length() > 20) {
                title = title.substring(0,20);
            }
            String authorName = author.getName();
            String authorEmail = author.getEmail();
            String authoredDate = author.getDate();
            String committerName = committer.getName();
            String committerEmail = committer.getEmail();
            String committedDate = committer.getDate();
            String webUrl = commit.getCommit().getUrl();

            Commit commit1 = new Commit(id,title,title,authorName,authorEmail,authoredDate,committerName,committerEmail,committedDate,webUrl);
            commits.add(commit1);

        });
        return commits;
    }

    public static List<Issue> formatIssues(List<IssueData> issuesData){
        List<Issue> data= new ArrayList<>();
      issuesData.stream().forEach(issue -> {
            String id =  issue.getId();
            String refId = issue.getNumber();
            String title = issue.getTitle();
            String description = issue.getDescription();
            String state = issue.getState();
            String createdAt = issue.getCreatedAt();
            String updatedAt= issue.getUpdatedAt();
            String closedAt= issue.getClosedAt();
            List<String> labels = issue.getLabels().stream().map(x->x.getName()).toList();
            User author = FactoryModel.formatUser(issue.getUser());
            User assignee = issue.getAssignee();
            Integer upvotes = issue.getReactions().getPlus1();
            Integer downvotes = issue.getReactions().getMinous1();
            String webUrl = issue.getHtmlUrl();

            Issue issue1 = new Issue(id,refId,title,description,state,createdAt,
                    updatedAt,closedAt,labels,author,assignee,upvotes,downvotes,webUrl);
            data.add(issue1);
        });
        return data;
    }

    public static User formatUser(UserData userData){
        String id = userData.getId();
        String username = userData.getLogin();
        String name = username;
        String avatarUrl = userData.getAvatarUrl();
        String webUrl = userData.getHtmlUrl();
        return new User(id,username,name,avatarUrl,webUrl);
    }

    public static Project formatProject(ProjectData projectData,List<Commit> commits, List<Issue> issues){
        String id = projectData.getId();
        String name = projectData.getName();
        String webUrl = projectData.getHtmlUrl();
        return new Project(id,name,webUrl,commits,issues);
    }
}
