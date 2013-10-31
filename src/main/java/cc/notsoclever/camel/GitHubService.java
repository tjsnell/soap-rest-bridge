package cc.notsoclever.camel;


import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface GitHubService {

   String getProjectTags(@WebParam(name="owner") String owner, @WebParam(name="project") String project);
   String getProjectBranches(@WebParam(name="owner") String owner, @WebParam(name="project") String project);
   String getRepos(@WebParam(name="owner") String owner);
}
