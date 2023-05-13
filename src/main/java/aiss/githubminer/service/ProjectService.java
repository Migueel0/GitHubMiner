package aiss.githubminer.service;

import aiss.githubminer.model.Commit;
import aiss.githubminer.model.FactoryModel;
import aiss.githubminer.model.Issue;
import aiss.githubminer.model.Project;
import aiss.githubminer.model.ProjectData.ProjectData;
import aiss.githubminer.utils.RESTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class ProjectService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CommitService commitService;
    @Autowired
    IssueService issueService;
    final String baseUri = "https://api.github.com/";

    public ProjectData getProjectByOwnerAndName(String owner, String repo){
        String uri = baseUri + "/repos/" +  owner + "/" + repo;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<ProjectData> request = new HttpEntity<>(null,headers);
        ResponseEntity<ProjectData> response = restTemplate.exchange(uri, HttpMethod.GET,request,ProjectData.class);
        return response.getBody();
    }
    public Project allData(String owner, String repo, Integer sinceCommits, Integer sinceIssues,Integer maxPages){
        ProjectData project = getProjectByOwnerAndName(owner,repo);
        List<Commit> commits = commitService.sinceCommits(owner,repo,sinceCommits,maxPages);
        List<Issue> issues = issueService.sinceIssues(owner,repo,sinceIssues,maxPages);
        Project data = FactoryModel.formatProject(project,commits,issues);
        return data;
    }


}
