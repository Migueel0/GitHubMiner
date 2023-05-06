package aiss.githubminer.service;

import aiss.githubminer.model.Comment;
import aiss.githubminer.model.Commit;
import aiss.githubminer.model.Issue;
import aiss.githubminer.model.Project;
import aiss.githubminer.model.CommitData.Author;
import aiss.githubminer.model.CommitData.Committer;
import aiss.githubminer.utils.RESTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.config.ProjectingArgumentResolverRegistrar;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RestController
@RequestMapping("githubminer")
public class GitHubService {
    @Autowired
    RestTemplate restTemplate;
    final String baseUri = "https://api.github.com/";
    final String gitMinerUri = "http://localhost:8080/gitminer";

    public Project getProjectByOwnerAndName(String owner,String repo){
        String uri = baseUri + "/repos/" +  owner + "/" + repo;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<Project> request = new HttpEntity<>(null,headers);
        ResponseEntity<Project> response = restTemplate.exchange(uri,HttpMethod.GET,request,Project.class);
        return response.getBody();
    }

    public Project allData(String owner, String repo, Integer sinceCommits, Integer sinceIssues,Integer maxPages){
        Project data = getProjectByOwnerAndName(owner,repo);
        data.setCommits(sinceCommits(owner,repo,sinceCommits,maxPages));
        data.setIssues(sinceIssues(owner,repo,sinceIssues,maxPages));
        return data;
    }

    public List<Commit> sinceCommits(String owner,String repo, Integer days, Integer pages){
        String uri = baseUri + "/repos/" + owner + "/" + repo + "/commits";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<Commit[]> request = new HttpEntity<>(null, headers);
        ResponseEntity<Commit[]> response =  restTemplate.exchange(uri, HttpMethod.GET, request, Commit[].class);
        //FIRST PAGE
        List<Commit> commits = new ArrayList<>();

        commits.addAll(Arrays.stream(response.getBody()).toList());
        mapCommitValues(commits);
        commits = commits.stream()
                .filter(x -> RESTUtil.StringToLocalDateTime(x.getCommittedDate())
                        .isAfter(LocalDateTime.now().minusDays(days))).collect(Collectors.toList());
        int page = 1;
        //ADDING REMAINING PAGES
        while (page <= pages && RESTUtil.getNextPageUrl(response.getHeaders())!= null){
            String url =  RESTUtil.getNextPageUrl(response.getHeaders());
            response =  restTemplate.exchange(url,HttpMethod.GET,request,Commit[].class);
            List<Commit> commitPage = Arrays.stream(response.getBody()).toList();
            mapCommitValues(commitPage);
            commitPage = commitPage.stream()
                    .filter(x -> RESTUtil.StringToLocalDateTime(x.getCommittedDate())
                            .isAfter(LocalDateTime.now().minusDays(days))).collect(Collectors.toList());
            commits.addAll(commitPage);
            page++;
        }
        return commits;
    }

    public void mapCommitValues(List<Commit> commits){
        for(Commit commit: commits){

            Author author = commit.getCommit().getAuthor();
            Committer committer = commit.getCommit().getCommitter();

            String title = commit.getCommit().getMessage();
            String authorName = author.getName();
            String authorEmail = author.getEmail();
            String authoredDate = author.getDate();
            String committerName = committer.getName();
            String committerEmail = committer.getEmail();
            String committedDate = committer.getDate();
            String webUrl = commit.getCommit().getUrl();

            commit.setTitle(title);
            commit.setMessage(title);
            commit.setAuthorName(authorName);
            commit.setAuthorEmail(authorEmail);
            commit.setAuthoredDate(authoredDate);
            commit.setCommitterName(committerName);
            commit.setCommitterEmail(committerEmail);
            commit.setCommittedDate(committedDate);
            commit.setWebUrl(webUrl);
        }

    }

    public List<Comment> getNotes(String owner, String repo, String iid){
        String uri = baseUri + "/repos/" + owner + "/" + repo + "/issues/" + iid + "/comments";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<String[]> request = new HttpEntity<>(null,headers);
        ResponseEntity<Comment[]> response = restTemplate.exchange(uri,HttpMethod.GET,request,Comment[].class);
        return Arrays.stream(response.getBody()).toList();
    }

    public List<Issue> sinceIssues(String owner, String repo, Integer days, Integer pages){
        String uri = baseUri + "/repos/" + owner + "/" + repo + "/issues?state=all";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<Issue[]> request = new HttpEntity<>(null, headers);
        ResponseEntity<Issue[]> response =  restTemplate.exchange(uri, HttpMethod.GET, request, Issue[].class);
        List<Issue> issues = new ArrayList<>();

        //FIRST PAGE
        int page = 1;
        issues.addAll(Arrays.stream(response.getBody()).filter(x -> RESTUtil
                .StringToLocalDateTime(x.getUpdatedAt())
                .isAfter(LocalDateTime.now().minusDays(days))).collect(Collectors.toList()));

        //ADDING REMAINING PAGES
        while (page <= pages && RESTUtil.getNextPageUrl(response.getHeaders())!= null){
            String url =  RESTUtil.getNextPageUrl(response.getHeaders());
            response =  restTemplate.exchange(url,HttpMethod.GET,request,Issue[].class);
            List<Issue> issuePage = Arrays.stream(response.getBody()).filter(x -> RESTUtil
                    .StringToLocalDateTime(x.getUpdatedAt())
                    .isAfter(LocalDateTime.now().minusDays(days))).collect(Collectors.toList());
            issues.addAll(issuePage);
            page++;
        }
        issues.forEach(x -> x.setComments(getNotes(owner,repo,x.getRefId())));
        return issues;
    }

    @GetMapping("/{owner}/{repo}")
    public Project getData(@PathVariable String owner,@PathVariable String repo, @RequestParam(defaultValue = "5") Integer sinceCommits,
                           @RequestParam(defaultValue = "20") Integer sinceIssues, @RequestParam(defaultValue = "2") Integer maxPages){
        return allData(owner,repo, sinceCommits, sinceIssues, maxPages);
    }


    @PostMapping("/{owner}/{repo}")
    public Project sendData(@PathVariable String owner,@PathVariable String repo, @RequestParam(defaultValue = "5") Integer sinceCommits,
                            @RequestParam(defaultValue = "20") Integer sinceIssues, @RequestParam(defaultValue = "2") Integer maxPages){
        Project newProject = restTemplate.postForObject(gitMinerUri + "/" + owner + "/" + repo,
                allData(owner,repo, sinceCommits, sinceIssues, maxPages),Project.class);
        return newProject;
    }



}