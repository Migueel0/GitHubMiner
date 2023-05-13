package aiss.githubminer.service;

import aiss.githubminer.model.Comment;
import aiss.githubminer.model.CommentData.CommentData;
import aiss.githubminer.model.FactoryModel;
import aiss.githubminer.model.User;
import aiss.githubminer.utils.RESTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


@Service
public class CommentService {

    @Autowired
    RestTemplate restTemplate;
    final String baseUri = "https://api.github.com/";

    public List<Comment> getNotes(String owner, String repo, String iid){
        String uri = baseUri + "/repos/" + owner + "/" + repo + "/issues/" + iid + "/comments";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<String[]> request = new HttpEntity<>(null,headers);
        ResponseEntity<CommentData[]> response = restTemplate.exchange(uri, HttpMethod.GET,request,CommentData[].class);
        List<CommentData> comments = Arrays.stream(response.getBody()).toList();
        List<Comment> data = FactoryModel.formatComment(comments);
        return data;
    }

}
