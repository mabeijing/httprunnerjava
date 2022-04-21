package com.httprunnerjava.postman_echo.functionalVerification;

import com.httprunnerjava.HttpRunner;
import com.httprunnerjava.model.Config;
import com.httprunnerjava.model.Step;
import com.httprunnerjava.model.component.RunRequest;
import com.httprunnerjava.utils.JsonUtils;
import lombok.Getter;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-04-21-16:21
 * @Description:
 */
@Getter
public class JsonUtilTest extends HttpRunner{
    private Config config = new Config("config_name with variables,the viriables is $$var1: $var1")
            .variables("{'var1':'config_var1'}")
            .base_url("https://postman-echo.com")
            .verify(false)
            .export("['foo3']");

    private List<Step> teststeps = new ArrayList<Step>(){{
        add(new RunRequest("post form data using json 1")
                .withVariables(
                        "{'foo2': 'bar23','jsondata':[" +
                                "{'key1':'value1','key2':'value2'}," +
                                "{'key11':'value11','key22':'value22'}," +
                                "{'key111':'value111','key222':'value222'}" +
                                "]}"
                )
                .post("/post")
                .withHeaders(
                        "{'Content-Type': 'application/json'}"
                )
                .withJson("${loadResourcesFileAsString(/request_methods/requestBody)}")
                .validate()
                .assertEqual("status_code", 200)
                .jsonEqual("body.json","[{'key1':'value1','key2':'value2'},{'key11':'value11','key22':'value22'},{'key111':'value111','key222':'value222'}]")
        );
    }};

    public void compareJsonObject(){
        String str1 = "{\n" +
                "    \"c\": \"0\",\n" +
                "    \"m\": \"\",\n" +
                "    \"d\": {\n" +
                "        \"totalCount\": 212,\n" +
                "        \"count\": 10,\n" +
                "        \"hasMore\": true,\n" +
                "        \"result\": [\n" +
                "            {\n" +
                "                \"siCode\": \"DIAMOND_VIP\",\n" +
                "                \"instanceId\": \"3FO4K6EIDMD2\",\n" +
                "                \"sourceId\": 7,\n" +
                "                \"code\": \"SI0000kd4bj\",\n" +
                "                \"status\": 1,\n" +
                "                \"type\": 0,\n" +
                "                \"isBindUser\": false,\n" +
                "                \"validityPeriod\": {\n" +
                "                    \"startTime\": 1649666750000,\n" +
                "                    \"endTime\": 1662566399000,\n" +
                "                    \"validStartTime\": 1649666750000,\n" +
                "                    \"validEndTime\": 1662566399000,\n" +
                "                    \"startUsingTime\": 1649666750000,\n" +
                "                    \"expireUsingTime\": 1662566399000,\n" +
                "                    \"startHoldingTime\": 1649666558000,\n" +
                "                    \"expireHoldingTime\": 1655368958000,\n" +
                "                    \"validityUsingPeriod\": 149\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"siCode\": \"DIAMOND_VIP\",\n" +
                "                \"instanceId\": \"3FO4K6F0B4T9\",\n" +
                "                \"sourceId\": 7,\n" +
                "                \"code\": \"SI0000k7umr\",\n" +
                "                \"status\": 1,\n" +
                "                \"type\": 0,\n" +
                "                \"isBindUser\": false,\n" +
                "                \"validityPeriod\": {\n" +
                "                    \"startTime\": 1646814253000,\n" +
                "                    \"endTime\": 1662739199000,\n" +
                "                    \"validStartTime\": 1646814253000,\n" +
                "                    \"validEndTime\": 1662739199000,\n" +
                "                    \"startUsingTime\": 1646814253000,\n" +
                "                    \"expireUsingTime\": 1662739199000,\n" +
                "                    \"startHoldingTime\": 1646621869000,\n" +
                "                    \"expireHoldingTime\": 1647917869000,\n" +
                "                    \"validityUsingPeriod\": 184\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"siCode\": \"DIAMOND_VIP\",\n" +
                "                \"instanceId\": \"3FO4K6F0B86A\",\n" +
                "                \"sourceId\": 7,\n" +
                "                \"code\": \"SI0000k7unq\",\n" +
                "                \"status\": 1,\n" +
                "                \"type\": 0,\n" +
                "                \"isBindUser\": false,\n" +
                "                \"validityPeriod\": {\n" +
                "                    \"startTime\": 1646814253000,\n" +
                "                    \"endTime\": 1662739199000,\n" +
                "                    \"validStartTime\": 1646814253000,\n" +
                "                    \"validEndTime\": 1662739199000,\n" +
                "                    \"startUsingTime\": 1646814253000,\n" +
                "                    \"expireUsingTime\": 1662739199000,\n" +
                "                    \"startHoldingTime\": 1646621869000,\n" +
                "                    \"expireHoldingTime\": 1647917869000,\n" +
                "                    \"validityUsingPeriod\": 184\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"siCode\": \"DIAMOND_VIP\",\n" +
                "                \"instanceId\": \"3FO4K6F09EMQ\",\n" +
                "                \"sourceId\": 7,\n" +
                "                \"code\": \"SI0000k7upp\",\n" +
                "                \"status\": 1,\n" +
                "                \"type\": 0,\n" +
                "                \"isBindUser\": false,\n" +
                "                \"validityPeriod\": {\n" +
                "                    \"startTime\": 1646814253000,\n" +
                "                    \"endTime\": 1662739199000,\n" +
                "                    \"validStartTime\": 1646814253000,\n" +
                "                    \"validEndTime\": 1662739199000,\n" +
                "                    \"startUsingTime\": 1646814253000,\n" +
                "                    \"expireUsingTime\": 1662739199000,\n" +
                "                    \"startHoldingTime\": 1646621869000,\n" +
                "                    \"expireHoldingTime\": 1647917869000,\n" +
                "                    \"validityUsingPeriod\": 184\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"siCode\": \"DIAMOND_VIP\",\n" +
                "                \"instanceId\": \"3FO4K6F09HYR\",\n" +
                "                \"sourceId\": 7,\n" +
                "                \"code\": \"SI0000k7uqn\",\n" +
                "                \"status\": 1,\n" +
                "                \"type\": 0,\n" +
                "                \"isBindUser\": false,\n" +
                "                \"validityPeriod\": {\n" +
                "                    \"startTime\": 1646814253000,\n" +
                "                    \"endTime\": 1662739199000,\n" +
                "                    \"validStartTime\": 1646814253000,\n" +
                "                    \"validEndTime\": 1662739199000,\n" +
                "                    \"startUsingTime\": 1646814253000,\n" +
                "                    \"expireUsingTime\": 1662739199000,\n" +
                "                    \"startHoldingTime\": 1646621869000,\n" +
                "                    \"expireHoldingTime\": 1647917869000,\n" +
                "                    \"validityUsingPeriod\": 184\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"siCode\": \"DIAMOND_VIP\",\n" +
                "                \"instanceId\": \"3FO4K6F097XO\",\n" +
                "                \"sourceId\": 7,\n" +
                "                \"code\": \"SI0000k7urm\",\n" +
                "                \"status\": 1,\n" +
                "                \"type\": 0,\n" +
                "                \"isBindUser\": false,\n" +
                "                \"validityPeriod\": {\n" +
                "                    \"startTime\": 1646814253000,\n" +
                "                    \"endTime\": 1662739199000,\n" +
                "                    \"validStartTime\": 1646814253000,\n" +
                "                    \"validEndTime\": 1662739199000,\n" +
                "                    \"startUsingTime\": 1646814253000,\n" +
                "                    \"expireUsingTime\": 1662739199000,\n" +
                "                    \"startHoldingTime\": 1646621869000,\n" +
                "                    \"expireHoldingTime\": 1647917869000,\n" +
                "                    \"validityUsingPeriod\": 184\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"siCode\": \"DIAMOND_VIP\",\n" +
                "                \"instanceId\": \"3FO4K6F08WWL\",\n" +
                "                \"sourceId\": 7,\n" +
                "                \"code\": \"SI0000k7uwf\",\n" +
                "                \"status\": 1,\n" +
                "                \"type\": 0,\n" +
                "                \"isBindUser\": false,\n" +
                "                \"validityPeriod\": {\n" +
                "                    \"startTime\": 1646814253000,\n" +
                "                    \"endTime\": 1662739199000,\n" +
                "                    \"validStartTime\": 1646814253000,\n" +
                "                    \"validEndTime\": 1662739199000,\n" +
                "                    \"startUsingTime\": 1646814253000,\n" +
                "                    \"expireUsingTime\": 1662739199000,\n" +
                "                    \"startHoldingTime\": 1646621869000,\n" +
                "                    \"expireHoldingTime\": 1647917869000,\n" +
                "                    \"validityUsingPeriod\": 184\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"siCode\": \"DIAMOND_VIP\",\n" +
                "                \"instanceId\": \"3FO4K6F0919M\",\n" +
                "                \"sourceId\": 7,\n" +
                "                \"code\": \"SI0000k7utj\",\n" +
                "                \"status\": 1,\n" +
                "                \"type\": 0,\n" +
                "                \"isBindUser\": false,\n" +
                "                \"validityPeriod\": {\n" +
                "                    \"startTime\": 1646814253000,\n" +
                "                    \"endTime\": 1662739199000,\n" +
                "                    \"validStartTime\": 1646814253000,\n" +
                "                    \"validEndTime\": 1662739199000,\n" +
                "                    \"startUsingTime\": 1646814253000,\n" +
                "                    \"expireUsingTime\": 1662739199000,\n" +
                "                    \"startHoldingTime\": 1646621869000,\n" +
                "                    \"expireHoldingTime\": 1647917869000,\n" +
                "                    \"validityUsingPeriod\": 184\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"siCode\": \"DIAMOND_VIP\",\n" +
                "                \"instanceId\": \"3FO4K6F08TKK\",\n" +
                "                \"sourceId\": 7,\n" +
                "                \"code\": \"SI0000k7uvg\",\n" +
                "                \"status\": 1,\n" +
                "                \"type\": 0,\n" +
                "                \"isBindUser\": false,\n" +
                "                \"validityPeriod\": {\n" +
                "                    \"startTime\": 1646814253000,\n" +
                "                    \"endTime\": 1662739199000,\n" +
                "                    \"validStartTime\": 1646814253000,\n" +
                "                    \"validEndTime\": 1662739199000,\n" +
                "                    \"startUsingTime\": 1646814253000,\n" +
                "                    \"expireUsingTime\": 1662739199000,\n" +
                "                    \"startHoldingTime\": 1646621869000,\n" +
                "                    \"expireHoldingTime\": 1647917869000,\n" +
                "                    \"validityUsingPeriod\": 184\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"siCode\": \"DIAMOND_VIP\",\n" +
                "                \"instanceId\": \"3FO4K6F094LN\",\n" +
                "                \"sourceId\": 7,\n" +
                "                \"code\": \"SI0000k7uuh\",\n" +
                "                \"status\": 1,\n" +
                "                \"type\": 0,\n" +
                "                \"isBindUser\": false,\n" +
                "                \"validityPeriod\": {\n" +
                "                    \"startTime\": 1646814253000,\n" +
                "                    \"endTime\": 1662739199000,\n" +
                "                    \"validStartTime\": 1646814253000,\n" +
                "                    \"validEndTime\": 1662739199000,\n" +
                "                    \"startUsingTime\": 1646814253000,\n" +
                "                    \"expireUsingTime\": 1662739199000,\n" +
                "                    \"startHoldingTime\": 1646621869000,\n" +
                "                    \"expireHoldingTime\": 1647917869000,\n" +
                "                    \"validityUsingPeriod\": 184\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

    }
}
