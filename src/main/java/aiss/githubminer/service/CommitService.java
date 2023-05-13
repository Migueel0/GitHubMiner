package aiss.githubminer.service;

import aiss.githubminer.model.Commit;
import aiss.githubminer.model.CommitData.Author;
import aiss.githubminer.model.CommitData.CommitData;
import aiss.githubminer.model.CommitData.Committer;
import aiss.githubminer.model.FactoryModel;
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
public class CommitService {

    @Autowired
    RestTemplate restTemplate;
    final String baseUri = "https://api.github.com";
    public List<Commit> sinceCommits(String owner, String repo, Integer days, Integer pages){
        LocalDate date = LocalDate.now().minusDays(days);
        String uri = baseUri + "/repos/" + owner + "/" + repo + "/commits?page=1&since=" + date;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<CommitData[]> request = new HttpEntity<>(null, headers);
        ResponseEntity<CommitData[]> response =  restTemplate.exchange(uri, HttpMethod.GET, request, CommitData[].class);
        List<CommitData> commits = new ArrayList<>();
        int page = 1;
        while (page <= pages && uri!= null){
            System.out.println(uri);
            List<CommitData> commitPage = Arrays.stream(response.getBody()).toList();
            response =  restTemplate.exchange(uri,HttpMethod.GET,request,CommitData[].class);
            commits.addAll(commitPage);
            uri =  RESTUtil.getNextPageUrl(response.getHeaders());
            page++;
        }
        List<Commit> data = FactoryModel.formatCommits(commits);
        return data;
    }
}
