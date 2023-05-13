package aiss.githubminer.service;

import aiss.githubminer.model.Comment;
import aiss.githubminer.model.Commit;
import aiss.githubminer.model.Issue;
import aiss.githubminer.model.IssueData.IssueData;
import aiss.githubminer.model.User;
import aiss.githubminer.utils.RESTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class IssueService {

    @Autowired
    CommentService commentService;


    IssueData data = new IssueData();
    @Autowired
    RestTemplate restTemplate;
    final String baseUri = "https://api.github.com/";

    public List<Issue> sinceIssues(String owner, String repo, Integer days, Integer pages) {
        String uri = baseUri + "/repos/" + owner + "/" + repo + "/issues?state=all";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<Issue[]> request = new HttpEntity<>(null, headers);
        ResponseEntity<IssueData[]> response = restTemplate.exchange(uri, HttpMethod.GET, request, IssueData[].class);
        List<IssueData> issues = new ArrayList<>();

        //FIRST PAGE
        int page = 1;
        issues.addAll(Arrays.stream(response.getBody()).filter(x -> RESTUtil
                .StringToLocalDateTime(x.getUpdatedAt())
                .isAfter(LocalDateTime.now().minusDays(days))).collect(Collectors.toList()));

        //ADDING REMAINING PAGES
        while (page <= pages && RESTUtil.getNextPageUrl(response.getHeaders()) != null) {
            String url = RESTUtil.getNextPageUrl(response.getHeaders());
            response = restTemplate.exchange(url, HttpMethod.GET, request, IssueData[].class);
            List<IssueData> issuePage = Arrays.stream(response.getBody()).filter(x -> RESTUtil
                    .StringToLocalDateTime(x.getUpdatedAt())
                    .isAfter(LocalDateTime.now().minusDays(days))).collect(Collectors.toList());
            issues.addAll(issuePage);
            page++;
        }

        List<Issue> data = mapIssuesValues(issues);
        data.forEach(x -> x.setComments(commentService.getNotes(owner, repo, x.getRefId())));
        mapAuthorValues(data);
        return data;
    }

    public List<Issue> mapIssuesValues(List<IssueData> issues) {
        List<Issue> data= new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (IssueData issue : issues) {
            Issue issue1 = new Issue(issue.getId(), issue.getNumber(),issue.getTitle(),issue.getBody(),issue.getState(),issue.getCreatedAt(),issue.getUpdatedAt(),issue.getClosedAt(),
            labels,issue.getUser(),issue.getAssignee(),issue.getReactions().getPlus1(),issue.getReactions().getMinous1(),issue.getHtmlUrl());
            issue1.setLabels(issue.getLabels());
            data.add(issue1);
        }
        return data;
    }

    public void mapAuthorValues(List<Issue> issues){
        for(Issue issue: issues){
            User commentAuthor = issue.getAuthor();
            String commentAuthorUserName = commentAuthor.getLogin();
            String commentAuthorName = commentAuthor.getLogin();
            String commentAuthorWebUrl = commentAuthor.getHtmlUrl();

            issue.setAuthor(commentAuthor);
            commentAuthor.setUsername(commentAuthorUserName);
            commentAuthor.setName(commentAuthorName);
            commentAuthor.setWebUrl(commentAuthorWebUrl);

        }

    }
}
