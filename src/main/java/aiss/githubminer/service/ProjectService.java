package aiss.githubminer.service;

import aiss.githubminer.model.Project;
import aiss.githubminer.utils.RESTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ProjectService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CommitService commitService;
    @Autowired
    IssueService issueService;
    final String baseUri = "https://api.github.com/";

    public Project getProjectByOwnerAndName(String owner, String repo){
        String uri = baseUri + "/repos/" +  owner + "/" + repo;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<Project> request = new HttpEntity<>(null,headers);
        ResponseEntity<Project> response = restTemplate.exchange(uri, HttpMethod.GET,request,Project.class);
        return response.getBody();
    }
    public Project allData(String owner, String repo, Integer sinceCommits, Integer sinceIssues,Integer maxPages){
        Project data = getProjectByOwnerAndName(owner,repo);
        data.setCommits(commitService.sinceCommits(owner,repo,sinceCommits,maxPages));
        data.setIssues(issueService.sinceIssues(owner,repo,sinceIssues,maxPages));
        return data;
    }


}
