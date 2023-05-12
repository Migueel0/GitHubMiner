package aiss.githubminer.service;

import aiss.githubminer.model.Commit;
import aiss.githubminer.model.CommitData.Author;
import aiss.githubminer.model.CommitData.Committer;
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
public class CommitService {

    @Autowired
    RestTemplate restTemplate;
    final String baseUri = "https://api.github.com/";
    public List<Commit> sinceCommits(String owner, String repo, Integer days, Integer pages){
        String uri = baseUri + "/repos/" + owner + "/" + repo + "/commits";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<Commit[]> request = new HttpEntity<>(null, headers);
        ResponseEntity<Commit[]> response =  restTemplate.exchange(uri, HttpMethod.GET, request, Commit[].class);


        //TODO: DONT TAKE FIRST PAGE
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

            String id = commit.getSha();
            String title = commit.getCommit().getMessage();
            String authorName = author.getName();
            String authorEmail = author.getEmail();
            String authoredDate = author.getDate();
            String committerName = committer.getName();
            String committerEmail = committer.getEmail();
            String committedDate = committer.getDate();
            String webUrl = commit.getCommit().getUrl();

            commit.setId(id);
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
}
