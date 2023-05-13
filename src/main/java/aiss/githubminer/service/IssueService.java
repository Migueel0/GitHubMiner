package aiss.githubminer.service;

import aiss.githubminer.model.*;
import aiss.githubminer.model.IssueData.IssueData;
import aiss.githubminer.utils.RESTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
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
    final String baseUri = "https://api.github.com";

    public List<Issue> sinceIssues(String owner, String repo, Integer days, Integer pages) {
        LocalDate date = LocalDate.now().minusDays(days);
        String uri = baseUri + "/repos/" + owner + "/" + repo + "/issues?state=all&page=1&since=" + date;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<IssueData[]> request = new HttpEntity<>(null, headers);
        List<IssueData> issues = new ArrayList<>();
        int page = 1;
        while (page <= pages && uri != null) {
            System.out.println(uri);
            ResponseEntity<IssueData[]> response = restTemplate.exchange(uri, HttpMethod.GET, request, IssueData[].class);
            List<IssueData> issuePage = Arrays.stream(response.getBody()).collect(Collectors.toList());
            issues.addAll(issuePage);
            uri = RESTUtil.getNextPageUrl(response.getHeaders());
            page++;
        }
        List<Issue> data = FactoryModel.formatIssues(issues);
        data.forEach(x -> x.setComments(commentService.getNotes(owner, repo, x.getRefId())));
        return data;
    }

}
