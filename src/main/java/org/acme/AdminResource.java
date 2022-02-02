package org.acme;

import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.annotations.SseElementType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.apache.commons.codec.binary.Base64;

import org.reactivestreams.Publisher;

@Path("/onboarding")
@ApplicationScoped
public class AdminResource {
    @Inject
    @RestClient
    ProcessService processService;


    @GET
    @Path("/entity/{entityId}/country/{country}/subProductType/{subProductType}/annualSpend/{annualSpend}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getQuestionnaireRules(@javax.ws.rs.PathParam("entityId") String entityId, @PathParam("country") String country,
                                        @PathParam("subProductType") String subProductType, @PathParam("annualSpend") String annualSpend) {

        try {

            String jsonReq = "{\"Country\":\"" + country + "\",\"Entity Type\":\"" + entityId + "\",\"Sub Product Type\":\""+subProductType+"\",\"Annual Spend\":"+annualSpend+"}";
            System.out.println(jsonReq);
            String response = processService.getQuestionnaireRules(jsonReq);


            HashMap mapResponse = new ObjectMapper().readValue(response, HashMap.class);
            List results = (List) mapResponse.get("decisionResults");

            Map resultListVal = (Map) results.get(0);
            Map result = null;
            System.out.println(resultListVal.get("result").getClass());
            if(resultListVal.get("result").getClass().equals(LinkedHashMap.class)) {

                 result = (Map) resultListVal.get("result");
            } else {
                System.out.println(resultListVal);
                List fields = (List)resultListVal.get("result");
                result = (Map)fields.get(0);
            }


            System.out.println(result);

            QuestionnaireObject questionnaireObject = new QuestionnaireObject();
            questionnaireObject.setGeneralPartnerName((Boolean) result.get("General Partner Name"));
            questionnaireObject.setGeneralPartnerTaxId((Boolean) result.get("General Partner Tax ID"));
            questionnaireObject.setStateOfIncorporation((Boolean) result.get("State Of Incorporation"));
            questionnaireObject.setAnnualSpend((Boolean)result.get("annualSpend"));
            questionnaireObject.setCardPurchaseType((Boolean)result.get("cardPurchaseType"));

            return new ObjectMapper().writeValueAsString(questionnaireObject);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @GET
    @Path("/productType/{productType}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSubProductTypes(@javax.ws.rs.PathParam("productType") String productType) {

        try {

            String jsonReq = "{\"Product Type\":\"" + productType + "\"}";
            System.out.println(jsonReq);
            String response = processService.getSubProductTypes(jsonReq);


            HashMap mapResponse = new ObjectMapper().readValue(response, HashMap.class);
            List results = (List) mapResponse.get("decisionResults");

            Map resultListVal = (Map) results.get(0);

            List fields = (List) resultListVal.get("result");

            System.out.println(fields);



            return new ObjectMapper().writeValueAsString(fields);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @GET
    @Path("/steps/productType/{productType}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSteps(@javax.ws.rs.PathParam("productType") String productType) {

        try {

            String jsonReq = "{\"Product Type\":\"" + productType + "\"}";
            System.out.println(jsonReq);
            String response = processService.getSteps(jsonReq);


            HashMap mapResponse = new ObjectMapper().readValue(response, HashMap.class);
            List results = (List) mapResponse.get("decisionResults");

            Map resultListVal = (Map) results.get(0);

            List fields = (List) resultListVal.get("result");

            System.out.println(fields);



            return new ObjectMapper().writeValueAsString(fields);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postCase(String body) {
        System.out.println("here"+"{\"case-data\":"+body+"}");
        String caseId = processService.postCase("{\"case-data\":"+body+"}");
        System.out.println("id"+caseId);
        return caseId;
    }
    @POST
    @Path("/admin/comment/{caseId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postCaseComment(String body, @PathParam("caseId") String caseId) {
        try {
            String correlationId = null;
            if (Integer.parseInt(caseId) > 10) {
                correlationId = "CASE-00000000" + caseId;
            } else {
                correlationId = "CASE-000000000" + caseId;
            }
            System.out.println(correlationId+"::"+body);
            String comment = processService.addCaseComments(correlationId, "legal",new ObjectMapper().writeValueAsString(body));
            return caseId;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @POST
    @Path("/admin/close/{caseId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String closeCase(@PathParam("caseId") String caseId) {
        try {
            String correlationId = null;
            if (Integer.parseInt(caseId) >= 10) {
                correlationId = "CASE-00000000" + caseId;
            } else {
                correlationId = "CASE-000000000" + caseId;
            }

            String comment = processService.closeCase(correlationId);
            return caseId;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @POST
    @Path("/admin/trigger/{caseId}/{nodeName}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String triggerAdhoc(@PathParam("caseId") String caseId,@PathParam("nodeName") String nodeNam) {
        try {
            System.out.println(caseId+nodeNam);
            String correlationId = null;
            if (Integer.parseInt(caseId) >= 10) {
                correlationId = "CASE-00000000" + caseId;
            } else {
                correlationId = "CASE-000000000" + caseId;
            }

            String comment = processService.triggerAdhoc(correlationId,nodeNam,"{}");
            return caseId;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @GET
    @Path("/tasks/{processId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTasks(@PathParam("processId") String caseId) throws Exception{
        String response = processService.getTasks("legal");

        Map map = new ObjectMapper().readValue(response,Map.class);
        List<Map> taskList = (List) map.get("task-summary");
        List<TaskSummary> taskSummaries = new ArrayList<>();
        TaskSummary taskSummary = null;
        for(Map taskMap:taskList) {
            if((int)taskMap.get("task-proc-inst-id") == Integer.parseInt(caseId)) {
                taskSummary = new TaskSummary();
                taskSummary.setTaskName((String) taskMap.get("task-name"));
                taskSummary.setTaskStatus((String) taskMap.get("task-status"));
                taskSummary.setTaskId((Integer) taskMap.get("task-id"));
                taskSummary.setActualOwner((String)taskMap.get("task-actual-owner"));
                taskSummaries.add(taskSummary);
            }
        }
        return new ObjectMapper().writeValueAsString(taskSummaries);

    }

    @GET
    @Path("/docReq/{caseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDocReq(@PathParam("caseId") String caseId) throws Exception{
        String response = processService.getDocRequirements(caseId);
        List lst= new ObjectMapper().readValue(response,List.class);
        List<DocReq> docs = new ArrayList<>();
        DocReq docReq = null;
        int i= 0;
        for(Object str:lst) {
            i++;
            docReq = new DocReq();
            docReq.setDocId(i);
            docReq.setDocName((String) str);
            docs.add(docReq);
        }
        return new ObjectMapper().writeValueAsString(docs);

    }

    @POST
    @Path("/admin/approve/{caseId}/task/{taskId}/{approve}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void approveOrReject(@PathParam("approve") boolean approve, @PathParam("caseId") String caseId, @PathParam("taskId") String taskId) throws Exception {

        try {
            processService.startTask(taskId, "legal");
            processService.completeTask(taskId, null, "legal");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    private String getFileName(MultivaluedMap<String, String> header) {


        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {

            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

    @GET
    @Path("/admin/cases")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCases() throws Exception{

        String response = processService.getCaseDetails(1,2,3);

        List<CaseDetails> cases = new ArrayList<>();
        CaseDetails caseDetails =null;
        Map caseMap = null;
        Map map = new ObjectMapper().readValue(response,Map.class);
        List caseList = (List) map.get("process-instance");
        for(Object str: caseList) {

          caseMap = (Map) str;
          if((int)caseMap.get("process-instance-state")!= 3) {
              caseDetails = new CaseDetails();
              caseDetails.setCaseId((String) caseMap.get("correlation-key"));
              if ((int) caseMap.get("process-instance-state") == 1) {
                  caseDetails.setCaseStatus("Active");
              } else if ((int) caseMap.get("process-instance-state") == 2) {
                  caseDetails.setCaseStatus("Completed");
              }

              if ((int) caseMap.get("sla-compliance") == 1) {
                  caseDetails.setSlaCompliance("Active");
              } else if ((int) caseMap.get("sla-compliance") == 2) {
                  caseDetails.setSlaCompliance("Completed");
              } else if ((int) caseMap.get("sla-compliance") == 3) {
                  caseDetails.setSlaCompliance("Violated");
              }
              Map dateMap = (Map) caseMap.get("start-date");
              Date date = new Date((long) dateMap.get("java.util.Date"));
              DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
              String requiredDate = df.format(date).toString();
              caseDetails.setCaseStartedAt(requiredDate);
              caseDetails.setProcessId(String.valueOf(caseMap.get("process-instance-id")));
              cases.add(caseDetails);
          }

        }
        return new ObjectMapper().writeValueAsString(cases);

    }
    @GET
    @Path("/admin/caseFile/{caseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCaseData(@PathParam("caseId") String caseId) {
        try {
            String response = processService.getProcessData(caseId);

            Map map = new ObjectMapper().readValue(response, Map.class);
            List<ProcessData> processDataList = new ArrayList<>();
            ProcessData processData = null;

            List<Map> varList = (ArrayList) map.get("variable-instance");
            for(Map varMap:varList) {
                if (!varMap.get("name").equals("caseFile_cardholderRiskRating") && !varMap.get("name").equals("caseFile_disputeRiskRating") && !varMap.get("name").equals("caseFile_automated")) {
                    processData = new ProcessData();
                    processData.setName((String) varMap.get("name"));
                    processData.setValue((String) varMap.get("value"));
                    Map dateMap = (Map) varMap.get("modification-date");
                    Date date = new Date((long) dateMap.get("java.util.Date"));
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    String requiredDate = df.format(date).toString();
                    processData.setModifiedDate(requiredDate);
                    processDataList.add(processData);
                }

            }
            return new ObjectMapper().writeValueAsString(processDataList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

        @GET
        @Path("/admin/svg/{caseId}")
        @Produces(MediaType.APPLICATION_JSON)
        public String getSvg(@PathParam("caseId") String caseId) {
            try {
                String response = processService.getSvg(caseId);

                SVG svg = new SVG();
                svg.setSvg(response);
               return new ObjectMapper().writeValueAsString(svg);
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }

        }

    @GET
    @Path("/admin/decisionNodeData/{caseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDecisionNodeData(@PathParam("caseId") String caseId) {
        try {
            String response = processService.getProcessData(caseId);

            Map map = new ObjectMapper().readValue(response, Map.class);
            List<ProcessData> processDataList = new ArrayList<>();
            ProcessData processData = null;

            List<Map> varList = (ArrayList) map.get("variable-instance");
            for(Map varMap:varList) {
                if (varMap.get("name").equals("caseFile_DocumentChecklist") || varMap.get("name").equals("caseFile_entityType") || varMap.get("name").equals("caseFile_productType")) {
                    processData = new ProcessData();
                    processData.setName((String) varMap.get("name"));
                    processData.setValue((String) varMap.get("value"));
                    Map dateMap = (Map) varMap.get("modification-date");
                    Date date = new Date((long) dateMap.get("java.util.Date"));
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    String requiredDate = df.format(date).toString();
                    processData.setModifiedDate(requiredDate);
                    processDataList.add(processData);
                }

            }           System.out.println(map);
            return new ObjectMapper().writeValueAsString(processDataList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GET
    @Path("/admin/docs/{caseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDocs(@PathParam("caseId") String caseId) {
        try {
            String response = processService.getProcessData(caseId);

            Map map = new ObjectMapper().readValue(response, Map.class);
            List<ProcessData> processDataList = new ArrayList<>();
            ProcessData processData = null;

            List<Map> varList = (ArrayList) map.get("variable-instance");
            for(Map varMap:varList) {
                if (varMap.get("name").equals("caseFile_docIds")) {
                    String[] docMap = varMap.get("value").toString().substring(1,varMap.get("value").toString().length()-1).split(",");
                    List<String> docIds = Arrays.asList(docMap);
                    return new ObjectMapper().writeValueAsString(docIds);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }



    @GET
    @Path("/admin/casecomments/{caseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCaseComments(@PathParam("caseId") String caseId) {
        try {
            System.out.println(caseId);
            ObjectMapper mapper = new ObjectMapper();
            String correlationId = null;
            if(Integer.parseInt(caseId) > 10) {
                correlationId = "CASE-00000000"+caseId;
            } else {
                correlationId = "CASE-000000000"+caseId;
            }
            System.out.println(correlationId);
            String response = processService.getCaseComments(correlationId);
            Map mapValue = mapper.readValue(response, Map.class);

            List<Comment> commentList = new ArrayList<>();

            Comment comment = null;

            List<Map> commentMap = (List<Map>) mapValue.get("comments");

            for(Map commentMapValue:commentMap) {
                comment = new Comment();
                comment.setComment(commentMapValue.get("author").toString());
                comment.setAuthor(commentMapValue.get("text").toString());
                commentList.add(comment);
            }
            System.out.println(commentList);

            return new ObjectMapper().writeValueAsString(commentList);



        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    @GET
    @Path("/content/{docId}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getCaseFileDocs(@javax.ws.rs.PathParam("docId") String docId) throws IOException {

        try {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("docId" + docId.replace("\"","").trim());
            String document = processService.getCaseDoc(docId.replace("\"","").trim());

            Map<String, String> mapValue = mapper.readValue(document, Map.class);


            byte dearr[] = Base64.decodeBase64(mapValue.get("document-content"));
            System.out.println(mapValue.get("document-content"));
            java.nio.file.Path dir = Files.createTempDirectory("my-dir");
            java.nio.file.Path fileToCreatePath = dir.resolve(mapValue.get("document-name"));
            java.nio.file.Path newFilePath = Files.createFile(fileToCreatePath);
            Files.write(newFilePath,dearr);
            Response.ResponseBuilder response = Response.ok((Object) Files.readAllBytes(newFilePath));
            response.header("Content-Disposition", "attachment; filename=\"" + mapValue.get("document-name") + "\"");
            System.out.println("call end");
            return response.build();
        }catch (Exception e) {
            e.printStackTrace();

        }
        return null;



    }

}
