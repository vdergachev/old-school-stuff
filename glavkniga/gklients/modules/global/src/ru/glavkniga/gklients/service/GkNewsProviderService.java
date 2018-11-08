package ru.glavkniga.gklients.service;

import ru.glavkniga.gklients.crudentity.GKNews;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientDistributionSettings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public interface GkNewsProviderService {

    String NAME = "gklients_GkNewsProviderService";

    List<GkNewsSet> provide(List<GKNews> news);

    public class GkNewsSet implements Serializable{

        private static final long serialVersionUID = -3219245570201094363L;

        private final List<GKNews> news;
        private final List<Client> clients;
        //private final ClientDistributionSettings clientSettings;

        public GkNewsSet(final List<GKNews> news, final Client client) {
            this.news = news;
            final List<Client> clients = new ArrayList<>();
            clients.add(client);
            this.clients = clients;
        }

        public boolean addClient(final Client client) {
            if(clients.contains(client)){
               return false;
            }
            clients.add(client);
            return true;
        }

        public List<GKNews> getNews() {
            return news;
        }

        public List<Client> getClients() {
            return clients;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GkNewsSet gkNewsSet = (GkNewsSet) o;

            if (news != null ? !news.equals(gkNewsSet.news) : gkNewsSet.news != null) return false;
            return clients != null ? clients.equals(gkNewsSet.clients) : gkNewsSet.clients == null;
        }

        @Override
        public int hashCode() {
            int result = news != null ? news.hashCode() : 0;
            result = 31 * result + (clients != null ? clients.hashCode() : 0);
            return result;
        }
    }
}
