package com.nic.PMGSYSurvey.utils;


/**
 * Created by Achanthi Sundar  on 21/03/16.
 */
public class UrlGenerator {



    public static String getLoginUrl() {
        return "https://www.tnrd.gov.in/project/webservices_forms/login_service/login_services.php";
    }

    public static String getServicesListUrl() {
        return "https://www.tnrd.gov.in/project/webservices_forms/master_services/master_services.php";
    }

    public static String getWorkListUrl() {
        return "https://www.tnrd.gov.in/project/webservices_forms/work_monitoring/work_monitoring_services_test.php";
    }

    public static String getTnrdHostName() {
        return "www.tnrd.gov.in";
    }



}
