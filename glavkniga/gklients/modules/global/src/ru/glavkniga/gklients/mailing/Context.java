package ru.glavkniga.gklients.mailing;

import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.entity.MagazineIssue;

import java.io.Serializable;

/**
 * Created by vdergachev on 17.07.17.
 */
public class Context implements Serializable {

    private static final long serialVersionUID = 7346113366939442295L;

    Client client;
    ElverSubscription elverSubscription;
    MagazineIssue magazineIssueStart;
    MagazineIssue magazineIssueEnd;

    public Context withClient(final Client client) {
        this.client = client;
        return this;
    }

    public Context withElverSubscription(final ElverSubscription elverSubscription) {
        this.elverSubscription = elverSubscription;
        return this;
    }

    public Context withMagazineIssueStart(final MagazineIssue magazineIssue) {
        this.magazineIssueStart = magazineIssue;
        return this;
    }

    public Context withMagazineIssueEnd(final MagazineIssue magazineIssue) {
        this.magazineIssueEnd = magazineIssue;
        return this;
    }
}