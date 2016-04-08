package com.github.hackerwin7.jlib.utils.test;

import net.sf.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/03/11
 * Time: 6:44 PM
 * Desc:
 * Tips:
 */
public class StringTest {
    public static void main(String[] args) throws Exception {
        String json = "\n" +
                "{\n" +
                "\"web-app\": {\n" +
                "\"servlet\": [\n" +
                "{\n" +
                "\"servlet-name\": \"cofaxCDS\",\n" +
                "\"servlet-class\": \"org.cofax.cds.CDSServlet\",\n" +
                "\"init-param\": {\n" +
                "\"configGlossary:installationAt\": \"Philadelphia, PA\",\n" +
                "\"configGlossary:adminEmail\": \"ksm@pobox.com\",\n" +
                "\"configGlossary:poweredBy\": \"Cofax\",\n" +
                "\"configGlossary:poweredByIcon\": \"/images/cofax.gif\",\n" +
                "\"configGlossary:staticPath\": \"/content/static\",\n" +
                "\"templateProcessorClass\": \"org.cofax.WysiwygTemplate\",\n" +
                "\"templateLoaderClass\": \"org.cofax.FilesTemplateLoader\",\n" +
                "\"templatePath\": \"templates\",\n" +
                "\"templateOverridePath\": \"\",\n" +
                "\"defaultListTemplate\": \"listTemplate.htm\",\n" +
                "\"defaultFileTemplate\": \"articleTemplate.htm\",\n" +
                "\"useJSP\": false,\n" +
                "\"jspListTemplate\": \"listTemplate.jsp\",\n" +
                "\"jspFileTemplate\": \"articleTemplate.jsp\",\n" +
                "\"cachePackageTagsTrack\": 200,\n" +
                "\"cachePackageTagsStore\": 200,\n" +
                "\"cachePackageTagsRefresh\": 60,\n" +
                "\"cacheTemplatesTrack\": 100,\n" +
                "\"cacheTemplatesStore\": 50,\n" +
                "\"cacheTemplatesRefresh\": 15,\n" +
                "\"cachePagesTrack\": 200,\n" +
                "\"cachePagesStore\": 100,\n" +
                "\"cachePagesRefresh\": 10,\n" +
                "\"cachePagesDirtyRead\": 10,\n" +
                "\"searchEngineListTemplate\": \"forSearchEnginesList.htm\",\n" +
                "\"searchEngineFileTemplate\": \"forSearchEngines.htm\",\n" +
                "\"searchEngineRobotsDb\": \"WEB-INF/robots.db\",\n" +
                "\"useDataStore\": true,\n" +
                "\"dataStoreClass\": \"org.cofax.SqlDataStore\",\n" +
                "\"redirectionClass\": \"org.cofax.SqlRedirection\",\n" +
                "\"dataStoreName\": \"cofax\",\n" +
                "\"dataStoreDriver\": \"com.microsoft.jdbc.sqlserver.SQLServerDriver\",\n" +
                "\"dataStoreUrl\": \"jdbc:microsoft:sqlserver://LOCALHOST:1433;DatabaseName=goon\",\n" +
                "\"dataStoreUser\": \"sa\",\n" +
                "\"dataStorePassword\": \"dataStoreTestQuery\",\n" +
                "\"dataStoreTestQuery\": \"SET NOCOUNT ON;select test='test';\",\n" +
                "\"dataStoreLogFile\": \"/usr/local/tomcat/logs/datastore.log\",\n" +
                "\"dataStoreInitConns\": 10,\n" +
                "\"dataStoreMaxConns\": 100,\n" +
                "\"dataStoreConnUsageLimit\": 100,\n" +
                "\"dataStoreLogLevel\": \"debug\",\n" +
                "\"maxUrlLength\": 500\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "\"servlet-name\": \"cofaxEmail\",\n" +
                "\"servlet-class\": \"org.cofax.cds.EmailServlet\",\n" +
                "\"init-param\": {\n" +
                "\"mailHost\": \"mail1\",\n" +
                "\"mailHostOverride\": \"mail2\"\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "\"servlet-name\": \"cofaxAdmin\",\n" +
                "\"servlet-class\": \"org.cofax.cds.AdminServlet\"\n" +
                "},\n" +
                "{\n" +
                "\"servlet-name\": \"fileServlet\",\n" +
                "\"servlet-class\": \"org.cofax.cds.FileServlet\"\n" +
                "},\n" +
                "{\n" +
                "\"servlet-name\": \"cofaxTools\",\n" +
                "\"servlet-class\": \"org.cofax.cms.CofaxToolsServlet\",\n" +
                "\"init-param\": {\n" +
                "\"templatePath\": \"toolstemplates/\",\n" +
                "\"log\": 1,\n" +
                "\"logLocation\": \"/usr/local/tomcat/logs/CofaxTools.log\",\n" +
                "\"logMaxSize\": \"\",\n" +
                "\"dataLog\": 1,\n" +
                "\"dataLogLocation\": \"/usr/local/tomcat/logs/dataLog.log\",\n" +
                "\"dataLogMaxSize\": \"\",\n" +
                "\"removePageCache\": \"/content/admin/remove?cache=pages&id=\",\n" +
                "\"removeTemplateCache\": \"/content/admin/remove?cache=templates&id=\",\n" +
                "\"fileTransferFolder\": \"/usr/local/tomcat/webapps/content/fileTransferFolder\",\n" +
                "\"lookInContext\": 1,\n" +
                "\"adminGroupID\": 4,\n" +
                "\"betaServer\": true\n" +
                "}\n" +
                "}\n" +
                "],\n" +
                "\"servlet-mapping\": {\n" +
                "\"cofaxCDS\": \"/\",\n" +
                "\"cofaxEmail\": \"/cofaxutil/aemail/*\",\n" +
                "\"cofaxAdmin\": \"/admin/*\",\n" +
                "\"fileServlet\": \"/static/*\",\n" +
                "\"cofaxTools\": \"/tools/*\"\n" +
                "},\n" +
                "\"taglib\": {\n" +
                "\"taglib-uri\": \"cofax.tld\",\n" +
                "\"taglib-location\": \"/WEB-INF/tlds/cofax.tld\"\n" +
                "}\n" +
                "}\n" +
                "}";
        JSONObject js = JSONObject.fromObject(json);
        System.out.println(js);
        String line  = "{\"web-app\":{\"servlet\":[{\"servlet-name\":\"cofaxCDS\",\"servlet-class\":\"org.cofax.cds.CDSServlet\",\"init-param\":{\"configGlossary:installationAt\":\"Philadelphia, PA\",\"configGlossary:adminEmail\":\"ksm@pobox.com\",\"configGlossary:poweredBy\":\"Cofax\",\"configGlossary:poweredByIcon\":\"/images/cofax.gif\",\"configGlossary:staticPath\":\"/content/static\",\"templateProcessorClass\":\"org.cofax.WysiwygTemplate\",\"templateLoaderClass\":\"org.cofax.FilesTemplateLoader\",\"templatePath\":\"templates\",\"templateOverridePath\":\"\",\"defaultListTemplate\":\"listTemplate.htm\",\"defaultFileTemplate\":\"articleTemplate.htm\",\"useJSP\":false,\"jspListTemplate\":\"listTemplate.jsp\",\"jspFileTemplate\":\"articleTemplate.jsp\",\"cachePackageTagsTrack\":200,\"cachePackageTagsStore\":200,\"cachePackageTagsRefresh\":60,\"cacheTemplatesTrack\":100,\"cacheTemplatesStore\":50,\"cacheTemplatesRefresh\":15,\"cachePagesTrack\":200,\"cachePagesStore\":100,\"cachePagesRefresh\":10,\"cachePagesDirtyRead\":10,\"searchEngineListTemplate\":\"forSearchEnginesList.htm\",\"searchEngineFileTemplate\":\"forSearchEngines.htm\",\"searchEngineRobotsDb\":\"WEB-INF/robots.db\",\"useDataStore\":true,\"dataStoreClass\":\"org.cofax.SqlDataStore\",\"redirectionClass\":\"org.cofax.SqlRedirection\",\"dataStoreName\":\"cofax\",\"dataStoreDriver\":\"com.microsoft.jdbc.sqlserver.SQLServerDriver\",\"dataStoreUrl\":\"jdbc:microsoft:sqlserver://LOCALHOST:1433;DatabaseName=goon\",\"dataStoreUser\":\"sa\",\"dataStorePassword\":\"dataStoreTestQuery\",\"dataStoreTestQuery\":\"SET NOCOUNT ON;select test='test';\",\"dataStoreLogFile\":\"/usr/local/tomcat/logs/datastore.log\",\"dataStoreInitConns\":10,\"dataStoreMaxConns\":100,\"dataStoreConnUsageLimit\":100,\"dataStoreLogLevel\":\"debug\",\"maxUrlLength\":500}},{\"servlet-name\":\"cofaxEmail\",\"servlet-class\":\"org.cofax.cds.EmailServlet\",\"init-param\":{\"mailHost\":\"mail1\",\"mailHostOverride\":\"mail2\"}},{\"servlet-name\":\"cofaxAdmin\",\"servlet-class\":\"org.cofax.cds.AdminServlet\"},{\"servlet-name\":\"fileServlet\",\"servlet-class\":\"org.cofax.cds.FileServlet\"},{\"servlet-name\":\"cofaxTools\",\"servlet-class\":\"org.cofax.cms.CofaxToolsServlet\",\"init-param\":{\"templatePath\":\"toolstemplates/\",\"log\":1,\"logLocation\":\"/usr/local/tomcat/logs/CofaxTools.log\",\"logMaxSize\":\"\",\"dataLog\":1,\"dataLogLocation\":\"/usr/local/tomcat/logs/dataLog.log\",\"dataLogMaxSize\":\"\",\"removePageCache\":\"/content/admin/remove?cache=pages&id=\",\"removeTemplateCache\":\"/content/admin/remove?cache=templates&id=\",\"fileTransferFolder\":\"/usr/local/tomcat/webapps/content/fileTransferFolder\",\"lookInContext\":1,\"adminGroupID\":4,\"betaServer\":true}}],\"servlet-mapping\":{\"cofaxCDS\":\"/\",\"cofaxEmail\":\"/cofaxutil/aemail/*\",\"cofaxAdmin\":\"/admin/*\",\"fileServlet\":\"/static/*\",\"cofaxTools\":\"/tools/*\"},\"taglib\":{\"taglib-uri\":\"cofax.tld\",\"taglib-location\":\"/WEB-INF/tlds/cofax.tld\"}}}";
    }
}
