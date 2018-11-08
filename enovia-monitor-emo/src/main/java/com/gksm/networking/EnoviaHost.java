package com.gksm.networking;

/**
 * Created by VSDergachev on 11/18/2015.
 */
public enum EnoviaHost {

    ENOVIA_D("enovia-d.gksm.local"),
    ENOVIA_MCS_TEST("enovia-mcs-test.gksm.local"),
    ENOVIA_DEPLOY("enovia-deploy.gksm.local"),
    ENOVIA_SM_REF("sm-enovia-ref.gksm.local"),
    ENOVIA_TST("plm-enovia-tst.gksm.local"),
    ENOVIA_SM_DEV("sm-enovia-dev.gksm.local"),
    ENOVIA_MCS1("enovia-mcs1.gksm.local"),
    ENOVIA_MCS2("enovia-mcs2.gksm.local"),
    UNKNOWN("unknown");

    private String hostname;

    EnoviaHost(String hostname)
    {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }
}
