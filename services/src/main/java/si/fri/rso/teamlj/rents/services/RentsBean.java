package si.fri.rso.teamlj.rents.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.teamlj.rents.entities.BikeRent;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class RentsBean {

    private Logger log = Logger.getLogger(RentsBean.class.getName());

    @Inject
    private EntityManager em;

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

    public BikeRent returnBike(Integer rentId) {

        BikeRent rent = em.find(BikeRent.class, rentId);

        if (rent == null) {
            throw new NotFoundException();
        }

        /** TODO - location of return **/

        try {
            beginTx();
            rent.setDateOfReturn(Instant.now());
            commitTx();
        } catch (Exception e) {
            rollbackTx();
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
