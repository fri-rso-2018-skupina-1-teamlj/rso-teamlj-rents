package si.fri.rso.teamlj.rents.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.teamlj.rents.dtos.Bike;
import si.fri.rso.teamlj.rents.entities.BikeRent;
import si.fri.rso.teamlj.rents.services.configuration.AppProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class RentsBean {

    private Logger log = Logger.getLogger(RentsBean.class.getName());

    private Client httpClient;

    //private String baseUrl;

    @Inject
    @DiscoverService("rso-bikes")
    private Optional<String> baseUrl;

    @Inject
    private EntityManager em;

    @Inject
    private AppProperties appProperties;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        //baseUrl = "http://localhost:8082"; // bikes
    }

    public List<BikeRent> getRents(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, BikeRent.class, queryParameters);

    }

    public BikeRent getRent(Integer rentId) {

        BikeRent rent = em.find(BikeRent.class, rentId);

        if (rent == null) {
            throw new NotFoundException();
        }

        return rent;
    }

    public BikeRent createRent(BikeRent rent) {

        try {
            beginTx();
            em.persist(rent);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return rent;
    }

    public BikeRent putRent(Integer rentId, BikeRent rent) {

        BikeRent r = em.find(BikeRent.class, rentId);

        if (r == null) {
            return null;
        }

        try {
            beginTx();
            rent.setId(r.getId());
            rent = em.merge(rent);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return rent;
    }

    public BikeRent rentBike(Integer rentId, BikeRent bikeReturn) {

        BikeRent rent = em.find(BikeRent.class, rentId);

        if (rent == null) {
            throw new NotFoundException();
        }

        try {
            beginTx();
            /** nastavimo datum in čas izposoje kolesa
             *  posodobimo novo lokacijo kolesa
             */
            rent.setDateOfRent(Instant.now());
            rent = em.merge(bikeReturn);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        try {
            httpClient
                    .target(baseUrl + "/v1/bikes/" + rent.getBikeId() + "/taken")
                    .request()
                    .build("PATCH", Entity.json(""))
                    .invoke();
        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }

        return rent;
    }

    public BikeRent returnBike(Integer rentId, BikeRent bikeReturn) {

        BikeRent rent = em.find(BikeRent.class, rentId);

        if (rent == null) {
            throw new NotFoundException();
        }

        try {
            beginTx();
            /** nastavimo datum in čas vrnitve kolesa
             *  posodobimo novo lokacijo kolesa
             */
            rent.setDateOfReturn(Instant.now());
            rent = em.merge(bikeReturn);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        Bike b = new Bike();
        b.setId(rent.getBikeId());
        b.setLatitude(rent.getLatitudeOfReturn());
        b.setLongitude(rent.getLongitudeOfReturn());

        /** TODO to ne vem če dela **/
        try {
            httpClient
                    .target(baseUrl + "/v1/bikes/" + rent.getBikeId() + "/free")
                    .request()
                    .build("PUT", Entity.json(b))
                    .invoke();
        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }

        return rent;
    }

    public boolean deleteRent(String rentId) {

        BikeRent rent = em.find(BikeRent.class, rentId);

        if (rent != null) {
            try {
                beginTx();
                em.remove(rent);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}
