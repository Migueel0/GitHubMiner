package aiss.githubminer.service;

import aiss.githubminer.model.Issue;
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
    @Autowired
    RestTemplate restTemplate;
    final String baseUri = "https://api.github.com/";

    public List<Issue> sinceIssues(String owner, String repo, Integer days, Integer pages) {
        String uri = baseUri + "/repos/" + owner + "/" + repo + "/issues?state=all";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<Issue[]> request = new HttpEntity<>(null, headers);
        ResponseEntity<Issue[]> response = restTemplate.exchange(uri, HttpMethod.GET, request, Issue[].class);
        List<Issue> issues = new ArrayList<>();

        //FIRST PAGE
        int page = 1;
        issues.addAll(Arrays.stream(response.getBody()).filter(x -> RESTUtil
                .StringToLocalDateTime(x.getUpdatedAt())
                .isAfter(LocalDateTime.now().minusDays(days))).collect(Collectors.toList()));

        //ADDING REMAINING PAGES
        while (page <= pages && RESTUtil.getNextPageUrl(response.getHeaders()) != null) {
            String url = RESTUtil.getNextPageUrl(response.getHeaders());
            response = restTemplate.exchange(url, HttpMethod.GET, request, Issue[].class);
            List<Issue> issuePage = Arrays.stream(response.getBody()).filter(x -> RESTUtil
                    .StringToLocalDateTime(x.getUpdatedAt())
                    .isAfter(LocalDateTime.now().minusDays(days))).collect(Collectors.toList());
            issues.addAll(issuePage);
            page++;
        }
        mapIssuesValues(issues);
        issues.forEach(x -> x.setComments(commentService.getNotes(owner, repo, x.getRefId())));
        return issues;
    }

    public void mapIssuesValues(List<Issue> issues) {
        List<User> users = new ArrayList<>();
        for (Issue issue : issues) {

            String refId = issue.getNumber();
            Integer upvotes = issue.getReactions().getPlus1();
            Integer downvotes = issue.getReactions().getMinous1();
            String webUrl = issue.getHtmlUrl();
            User author = issue.getUser();
            String userWebUrl = issue.getUser().getHtmlUrl();

            issue.setAuthor(author);
            issue.setRefId(refId);
            issue.setUpvotes(upvotes);
            issue.setDownvotes(downvotes);
            issue.setWebUrl(webUrl);

            issue.getAuthor().setWebUrl(userWebUrl);
        }


    }
}
