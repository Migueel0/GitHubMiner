package aiss.githubminer.service;

import static org.junit.jupiter.api.Assertions.*;

import aiss.githubminer.model.Comment;
import aiss.githubminer.model.Commit;
import aiss.githubminer.model.Issue;
import aiss.githubminer.model.Project;
import aiss.githubminer.utils.RESTUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GitHubServiceTest {
    @Autowired
    CommitService commitService;
    @Autowired
    IssueService issueService;
    @Autowired
    ProjectService projectService;
    @Autowired
    CommentService commentService;
    @Autowired
    RestTemplate restTemplate;
    final String baseUri = "https://api.github.com/";

    @Test
    @DisplayName("Testing get project by id")
    void getProjectByIdTest(){

        //Define some parameters
        String owner = "Mastercard";
        String repo = "client-encryption-java";
        String name = "client-encryption-java";
        String uri = baseUri + "/repos/" + owner + "/" + repo;

        //Consuming API in order to get the status code
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<Project> request = new HttpEntity<>(null,headers);
        ResponseEntity<Project> response = restTemplate.exchange(uri, HttpMethod.GET,request,Project.class);
        HttpStatus status = response.getStatusCode();

        //Checking the status code
        assertEquals(HttpStatus.OK, status,"Status code must be OK");

        //Checking response fields
        assertTrue(response.hasBody());
        Project project = projectService.getProjectByOwnerAndName(owner,repo);
        assertNotNull(project.getId(), "Id cannot be null");
        assertNotNull(project.getName(), "Name cannot be null");
        assertEquals(name,project.getName(),"Provided name must be equal the project name");

        //Issues and commits must be empty because this method doesn't set both arrays
        assertTrue(project.getCommits().isEmpty());
        assertTrue(project.getIssues().isEmpty());

        System.out.println("Test passed");
        System.out.println(project);

    }

    @Test
    @DisplayName("Testing commits")
    void sinceCommitsTest(){
        String owner = "Mastercard";
        String repo = "client-encryption-java";
        Integer days = 7;
        Integer pages = 30;
        List<Commit> commits =  commitService.sinceCommits(owner,repo,days,pages);

        String uri = baseUri + "/repos/" + owner + "/" + repo + "/commits";

        //Consuming API in order to get the status code
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<Project> request = new HttpEntity<>(null,headers);
        ResponseEntity<Commit[]> response = restTemplate.exchange(uri, HttpMethod.GET,request, Commit[].class);
        HttpStatus status = response.getStatusCode();

        //Checking the status code
        assertEquals(HttpStatus.OK, status,"Status code must be OK");


        //Checking response fields
        for(Commit commit: commits){

            assertNotNull(commit.getId(),"Id cannot be null");

            assertEquals(40,commit.getId().length(),"Id length must be 40");

            assertTrue(commit.getWebUrl().contains(commit.getId()),"Web Url must contain commit id");

            assertTrue(RESTUtil.StringToLocalDateTime(commit.getCommittedDate()).isAfter(LocalDateTime.now().minusDays(days)),
                    "The committed date cannot be before the days specified in the parameters");

        }

        System.out.println("Test passed");
        System.out.println(commits);


    }

    @Test
    @DisplayName("Testing issues")
    void sinceIssuesTest(){
        String owner = "Mastercard";
        String repo = "client-encryption-java";
        Integer days = 20;
        Integer pages = 1;
        List<Issue> issues =  issueService.sinceIssues(owner,repo,days,pages);

        String uri = baseUri + "/repos/" + owner + "/" + repo + "/issues";

        //Consuming API in order to get the status code
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<Project> request = new HttpEntity<>(null,headers);
        ResponseEntity<Commit[]> response = restTemplate.exchange(uri, HttpMethod.GET,request, Commit[].class);
        HttpStatus status = response.getStatusCode();

        //Checking the status code
        assertEquals(HttpStatus.OK, status,"Status code must be OK");

        for(Issue issue: issues){

            assertNotNull(issue.getId(),"Id cannot be null");

            assertNotNull(issue.getRefId(),"Iid cannot be null");

            assertTrue(issue.getWebUrl().contains(issue.getRefId()),"Web Url must contain commit iid");

            assertTrue(RESTUtil.StringToLocalDateTime(issue.getUpdatedAt()).isAfter(LocalDateTime.now().minusDays(days)),
                    "The issue update date cannot be before the days specified in the parameters");

            //An opened issue cannot have close date
            assertTrue(issue.getState() == "opened"?issue.getClosedAt() == null:true);

            //A closed issue must have close date
            assertTrue(issue.getState() == "closed"?issue.getClosedAt() != null:true);

        }

        System.out.println("Test passed");
    }

    @Test
    @DisplayName("Testing all data")
    void allDataTest(){

        String owner = "Mastercard";
        String repo = "client-encryption-java";
        String name = "client-encryption-java";

        Project data =  projectService.allData(owner,repo,5,20,1);
        assertNotNull(data.getId(), "Id cannot be null");
        assertNotNull(data.getName(), "Name cannot be null");
        assertEquals(name,data.getName(),"Provided name must be equal the project name");


        System.out.println("Test passed");
    }

    @Test
    @DisplayName("Testing authorization")

    void authorizationTest(){



        //Checking response with correct token authorization
        String uri = "https://api.github.com/user/orgs";
        HttpHeaders headersCorrectToken  = new HttpHeaders();
        headersCorrectToken.set("Authorization", "Bearer " + RESTUtil.tokenReader("src/test/java/aiss/githubminer/token.txt"));
        HttpEntity<String[]> requestValidToken = new HttpEntity<>(null,headersCorrectToken);
        //Checking the status code
        try{
            ResponseEntity<Comment[]> responseValidToken = restTemplate.exchange(uri,HttpMethod.GET,requestValidToken,Comment[].class);
            HttpStatus status = responseValidToken.getStatusCode();
            assertEquals(HttpStatus.OK,status,"Status must be OK");
        }catch (HttpClientErrorException e){
            HttpStatus errorStatus = e.getStatusCode();
            assertNotEquals(HttpStatus.UNAUTHORIZED, errorStatus,"Status cannot be unauthorized");
        }



        ///Checking response with no token authorization
        HttpHeaders headersNoToken  = new HttpHeaders();
        HttpEntity<String[]> requestNoToken = new HttpEntity<>(null,headersNoToken);

        try {
            restTemplate.exchange(uri, HttpMethod.GET, requestNoToken, Comment[].class);
        }catch (HttpClientErrorException e) {
            //Checking the status code
            HttpStatus errorStatus = e.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, errorStatus, "Status code must be unauthorized");
        }


        //Checking response with not valid token
        HttpHeaders headersNotValidToken  = new HttpHeaders();
        headersNotValidToken.set("Authorization", "This is an invalid token");
        HttpEntity<String[]> requestNotValidToken = new HttpEntity<>(null,headersNotValidToken);
        try {
            restTemplate.exchange(uri, HttpMethod.GET, requestNotValidToken, Comment[].class);
        }catch (HttpClientErrorException e) {
            //Checking the status code
            HttpStatus errorStatus = e.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, errorStatus, "Status code must be unauthorized");
        }

        System.out.println("Test passed");

    }

    @Test
    @DisplayName("Display commit data")
    void commitData(){
        String owner = "Mastercard";
        String repo = "client-encryption-java";
        Integer days = 7;
        Integer pages = 30;
        List<Commit> commits =  commitService.sinceCommits(owner,repo,days,pages);
        System.out.println(commits);


    }

    @Test
    @DisplayName("Display issues data")
    void issueData(){
        String owner = "Mastercard";
        String repo = "client-encryption-java";
        Integer days = 20;
        Integer pages = 1;
        List<Issue> issues = issueService.sinceIssues(owner,repo,days,pages);
        System.out.println(issues);


    }

    @Test
    @DisplayName("Display all data")
    void allData(){
        String owner = "Mastercard";
        String repo = "client-encryption-java";
        Integer sinceCommits = 20;
        Integer sinceIssues = 50;
        Integer pages = 1;
        Project project = projectService.allData(owner,repo,sinceCommits,sinceIssues,pages);
        System.out.println(project);


    }
}