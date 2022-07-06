package seongsic.corona.controller;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import seongsic.corona.elastic.Connector;
import seongsic.corona.service.CoronaService;
import seongsic.corona.service.IndexingService;


@RequestMapping("/api")
@Controller
@RequiredArgsConstructor
public class API {


    private final IndexingService indexingService;
    private final CoronaService coronaService;
    private final Connector connector;
    @RequestMapping("/index")
    public String indexing(String startDt,String endDt) throws Exception {
        startDt = startDt.replaceAll("-","");
        endDt = endDt.replaceAll("-","");
        RestClient restClient = connector.getRestClient();
        String text = coronaService.getData(startDt,endDt);
        indexingService.indexing(restClient,text);
        restClient.close();
        return "result";
    }

}



