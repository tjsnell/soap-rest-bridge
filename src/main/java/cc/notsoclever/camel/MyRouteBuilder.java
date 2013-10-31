package cc.notsoclever.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class MyRouteBuilder extends RouteBuilder {


   public void configure() {

      from("cxf:bean:gitHubEndpoint")
            .to("log:input")
            // send the request to the route to handle the operation
            // the name of the operation is in that header
            .recipientList(simple("direct:${header.operationName}"));

      from("direct:getProjectTags")
            .process(new Processor() {
               @Override
               public void process(Exchange exchange) throws Exception {
                  Message in = exchange.getIn();
                  String owner = (String) in.getBody(List.class).get(0);
                  String repo = (String) in.getBody(List.class).get(1);
                  String url = "https://api.github.com/repos/" + owner + "/" + repo + "/tags";
                  in.setBody(getResponse(url));
               }
            });

      from("direct:getProjectBranches")
            .process(new Processor() {
               @Override
               public void process(Exchange exchange) throws Exception {
                  Message in = exchange.getIn();
                  String owner = (String) in.getBody(List.class).get(0);
                  String repo = (String) in.getBody(List.class).get(1);
                  String url = "https://api.github.com/repos/" + owner + "/" + repo + "/branches";
                  in.setBody(getResponse(url));
               }
            });


      from("direct:getRepos")
            .process(new Processor() {
               @Override
               public void process(Exchange exchange) throws Exception {
                  Message in = exchange.getIn();
                  String owner = (String) in.getBody(List.class).get(0);
                  String url = "https://api.github.com/orgs/" + owner + "/repos";
                  System.out.println("url = " + url);
                  in.setBody(getResponse(url));
               }
            });
   }

   public String getResponse(String urlString) throws Exception {
      URL url = new URL(urlString);
      HttpURLConnection c = (HttpURLConnection) url.openConnection();
      c.setRequestMethod("GET");
      c.setRequestProperty("Content-length", "0");
      c.setUseCaches(false);
      c.setAllowUserInteraction(false);
      c.connect();
      int status = c.getResponseCode();

      switch (status) {
         case 200:
         case 201:
            BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
               sb.append(line).append("\n");
            }
            br.close();
            return sb.toString();
      }
      return "error";
   }
}
