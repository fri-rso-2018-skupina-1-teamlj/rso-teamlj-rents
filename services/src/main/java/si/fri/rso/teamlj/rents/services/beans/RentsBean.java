package si.fri.rso.teamlj.rents.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Counted;
import si.fri.rso.teamlj.rents.dtos.Bike;
import si.fri.rso.teamlj.rents.dtos.MapEntity;
import si.fri.rso.teamlj.rents.entities.BikeRent;
import si.fri.rso.teamlj.rents.services.configuration.AppProperties;

import org.eclipse.microprofile.metrics.annotation.Timed;

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
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
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
    @DiscoverService("rso-map")
    private Optional<String> baseUrlMap;

    @Inject
    private EntityManager em;

    @Inject
    private AppProperties appProperties;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        //baseUrl = "http://localhost:8081"; // rents
    }

    @Timed(name = "get_rents_timed")
    @Counted(name = "get_rents_counter")
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getRentsFallback")
    public List<BikeRent> getRents(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, BikeRent.class, queryParameters);

    }

    public List<BikeRent> getRentsFallback(UriInfo uriInfo) {

        log.warning("get rents fallback called");
        return Collections.emptyList();

    }

    @Timed(name = "get_rent_timed")
    @Counted(name = "get_rent_counter")
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getRentFallback")
    public BikeRent getRent(Integer rentId) {

        BikeRent rent = em.find(BikeRent.class, rentId);

        if (rent == null) {
            throw new NotFoundException();
        }

        return rent;
    }

    public BikeRent getRentFallback(Integer rentId) {

        log.warning("get rent fallback called");
        return new BikeRent();
    }

    public List<BikeRent> getRentsFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, BikeRent.class, queryParameters);
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
            log.warning("rent is null");
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

    public BikeRent rentABike(Integer userId, Integer bikeId) {

        Bike bike = getBike(bikeId);

        log.warning("GET BIKE CALLED, BIKE ID: " + bikeId);

        if (bike.getStatus().equals("taken")) {
            return null;
        }

        log.warning("GET BIKE STATUS CALLED, BIKE STATUS: " + bike.getStatus());

        BikeRent rent = createRent(new BikeRent());

        log.warning("CREATE RENT CALLED, RENT ID: " + rent.getId());

        try {
            beginTx();

            rent.setDateOfRent(Instant.now());
            rent.setLongitudeOfRent(bike.getLongitude());
            rent.setLatitudeOfRent(bike.getLatitude());
            rent.setUserId(userId);
            rent.setBikeId(bikeId);

            commitTx();

            log.warning("COMMIT OK : " + rent.getId());

        } catch (Exception e) {
            rollbackTx();
        }

        takeBike(bikeId);

        log.warning("BIKE TAKEN CALLED");

        return rent;
    }

    public Bike getBike(Integer bikeId) {
        try {
            return httpClient
                    .target(baseUrl.get() + "/v1/bikes/" + bikeId)
//                    .target("http://localhost:8082/v1/bikes/" + bikeId)
                    .request().get(new GenericType<Bike>() {
                    });
        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    public MapEntity getMap(Integer mapId) {

        try {
            return httpClient
                    .target(baseUrlMap.get() + "/v1/map/" + mapId)
//                    .target("http://localhost:8084/v1/map/" + mapId)
                    .request().get(new GenericType<MapEntity>() {
                    });
        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }

    }

    public void takeBike(Integer bikeId) {

        try {
            httpClient
                    .target(baseUrl.get() + "/v1/bikes/" + bikeId + "/taken")
//                    .target("http://localhost:8082/v1/bikes/" + bikeId + "/taken")
                    .request()
                    .build("PATCH", Entity.json(""))
                    .invoke();
        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }


        log.warning("BIKE TAKEN PATCH CALLED");
    }

    public BikeRent returnBike(Integer userId, Integer rentId, Integer mapId) {

        BikeRent rent = getRent(rentId);

        if (rent == null) {
            throw new NotFoundException();
        }

        Integer bikeId = rent.getBikeId();
        if (bikeId == null) return rent;

        MapEntity mapEntity = getMap(mapId);

        if (mapEntity == null) return rent;


        try {
            beginTx();
            /** nastavimo datum in čas vrnitve kolesa
             *  posodobimo novo lokacijo kolesa
             */
            rent.setDateOfReturn(Instant.now());
            rent.setLongitudeOfReturn(mapEntity.getLatitude());
            rent.setLatitudeOfReturn(mapEntity.getLongitude());

            em.merge(rent);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        Bike b = new Bike();
        b.setId(rent.getBikeId());
        b.setLatitude(rent.getLatitudeOfReturn());
        b.setLongitude(rent.getLongitudeOfReturn());
        b.setStatus("free");
        b.setMapId(mapId);

        returnBike(b, mapEntity.getLatitude(), mapEntity.getLongitude());

        return rent;
    }

    public void returnBike(Bike bike, float latitude, float longitude) {


        try {
            httpClient
                    //TODO
                    .target(baseUrl.get() + "/v1/bikes/" + bike.getId() + "/free/" + latitude + "&" + longitude)
//                    .target("http://localhost:8082/v1/bikes/" + bike.getId() + "/free/" + latitude + "&" + longitude)
                    .request()
                    .build("PUT", Entity.json(bike))
                    .invoke();
        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }


    }

    public boolean deleteRent(Integer rentId) {

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
