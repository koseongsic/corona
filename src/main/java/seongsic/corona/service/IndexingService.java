package seongsic.corona.service;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.StringJoiner;

@Service
public class IndexingService {

    public void indexing(RestClient restClient, String text){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;
        String rAttribute = "";
        try {
            // XML
            InputSource is = new InputSource(new StringReader(text));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);

            Element root = doc.getDocumentElement();  //Get Root Node
            NodeList childeren = root.getChildNodes();
            NodeList bodyNodeList = null;
            NodeList NextNodeList = null;
            int size = childeren.item(1).getChildNodes().item(0).getChildNodes().getLength();
            for (int index = size - 1; index > 0; index--) {
                String key = "";
                bodyNodeList = childeren.item(1).getChildNodes().item(0).getChildNodes().item(index).getChildNodes();
                if (index != 0) {
                    NextNodeList = childeren.item(1).getChildNodes().item(0).getChildNodes().item(index - 1).getChildNodes();
                }
                StringJoiner joiner = new StringJoiner(",");
                for (int j = 0; j < 9; j++) {
                    if (bodyNodeList.item(j) == null) {
                        continue;
                    }
                    if (bodyNodeList.item(j) != null) {
                        String columnName = bodyNodeList.item(j).getNodeName();
                        String value = bodyNodeList.item(j).getChildNodes().item(0).getNodeValue();
                        if (columnName.equals("stateDt")) {
                            key = value;
                        }
                        if (columnName.equals("stateDt")) {
                            value = bodyNodeList.item(j).getChildNodes().item(0).getNodeValue().substring(0, 4) + "-" + bodyNodeList.item(j).getChildNodes().item(0).getNodeValue().substring(4, 6)
                                    + "-" + bodyNodeList.item(j).getChildNodes().item(0).getNodeValue().substring(6, 8);
                        }
                        if (columnName.equals("createDt") || columnName.equals("updateDt")) {
                            value = bodyNodeList.item(j).getChildNodes().item(0).getNodeValue().substring(0, 10);
                        }
                        joiner.add("\"" + columnName + "\"" + ":" + "\"" + value + "\"");
                        if (columnName.equals("deathCnt") && index > 0) {
                            columnName = "dailyDeathCnt";
                            String compareValue = "";
                            for (int k = 0; k < 9; k++) {
                                if (NextNodeList.item(k) != null) {
                                    String nextCol = NextNodeList.item(k).getNodeName();
                                    if (nextCol.equals("deathCnt")) {
                                        compareValue = NextNodeList.item(k).getChildNodes().item(0).getNodeValue();
                                    }
                                }
                            }
                            if(!compareValue.equals("")&&!value.equals("")){
                                int dailyDeathCnt = Integer.parseInt(compareValue) - Integer.parseInt(value);
                                value = dailyDeathCnt + "";
                                joiner.add("\"" + columnName + "\"" + ":" + "\"" + value + "\"");
                            }
                        }
                        if (columnName.equals("decideCnt") && index > 0) {
                            columnName = "dailyDecideCnt";
                            String compareValue = "";
                            for (int k = 0; k < 9; k++) {
                                if (NextNodeList.item(k) != null) {
                                    String nextCol = NextNodeList.item(k).getNodeName();
                                    if (nextCol.equals("decideCnt")) {
                                        compareValue = NextNodeList.item(k).getChildNodes().item(0).getNodeValue();
                                    }
                                }
                            }
                            if(!compareValue.equals("")&&!value.equals("")){
                                int dailyDecideCnt = Integer.parseInt(compareValue) - Integer.parseInt(value);
                                value = dailyDecideCnt + "";
                                joiner.add("\"" + columnName + "\"" + ":" + "\"" + value + "\"");
                            }
                        }
                        if (columnName.equals("accExamCnt") && index > 0) {
                            String compareValue = "";
                            columnName = "dailyAccExamCnt";
                            for (int k = 0; k < 9; k++) {
                                if (NextNodeList.item(k) != null) {
                                    String nextCol = NextNodeList.item(k).getNodeName();
                                    if (nextCol.equals("accExamCnt")) {
                                        compareValue = NextNodeList.item(k).getChildNodes().item(0).getNodeValue();
                                    }
                                }
                            }
                            if(!compareValue.equals("")&&!value.equals("")){
                                int dailyAccExamCnt = Integer.parseInt(compareValue) - Integer.parseInt(value);
                                value = dailyAccExamCnt + "";
                                joiner.add("\"" + columnName + "\"" + ":" + "\"" + value + "\"");
                            }
                        }
                    }
                }
                Request request = new Request("PUT", "corona/_doc/" + key);
                request.addParameter("pretty", "true");
                request.setJsonEntity("{" + joiner.toString() + "}");
                restClient.performRequest(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
