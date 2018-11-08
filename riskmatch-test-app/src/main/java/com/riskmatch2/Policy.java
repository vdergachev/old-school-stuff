package com.riskmatch2;

import java.util.Date;
import java.util.List;

/**
 * Created by Владимир on 25.08.2016.
 */
public class Policy {
    private int id;
    private int clientId;
    private int productId;
    private int carrierId;
    private Date startDate;
    private Date endDate;

    public int getClientId() {
        return clientId;
    }

    public int getProductId() {
        return productId;
    }

    public Date getStartDate() {
        return startDate;
    }

    ClientRel clientRel(Policy policy, List<Policy> allPolicies){
    return null;
    }


}
