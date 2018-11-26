package si.fri.rso.teamlj.rents.api.v1.resources;

import si.fri.rso.teamlj.rents.dtos.Bike;
import si.fri.rso.teamlj.rents.entities.BikeRent;
import si.fri.rso.teamlj.rents.services.beans.RentsBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.sql.Time;
import java.util.List;

@ApplicationScoped
@Path("/rents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RentsResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private RentsBean rentsBean;

    @GET
    public Response getRents() {

        List<BikeRent> rents = rentsBean.getRents(uriInfo);

        return Response.ok(rents).build();
    }
    
    @GET
    @Path("/test")
    public Response getSomething() {

        BikeRent rent = new BikeRent();
        rent.setLatitudeOfReturn((float) 46.050242);
        rent.setLongitudeOfReturn((float) 14.4691958);

        return Response.ok(rent).build();
    }

    @GET
    @Path("/{rentId}")
    public Response getRent(@PathParam("rentId") Integer rentId) {

        BikeRent rent = rentsBean.getRent(rentId);

        if (rent == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(rent).build();
    }

    @GET
    @Path("/filtered")
    public Response getRentsFiltered() {

        List<BikeRent> rents;

        rents = rentsBean.getRentsFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(rents).build();
    }

    @POST
    public Response createRent(BikeRent rent) {

        /** TODO **/ // TODO - kaj je s tem mišljeno, meni zgleda vse ok?
        if (rent.getUserId() == null || rent.getBikeId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            rent = rentsBean.createRent(rent);
        }

        if (rent.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(rent).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(rent).build();
        }
    }

    @POST
    @Path("/rentabike/{userId}/{bikeId}")
    public Response rentABike(@PathParam("userId") Integer userId, @PathParam("bikeId") Integer bikeId) {

        BikeRent rent = rentsBean.rentABike(userId, bikeId);

        if (rent.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(rent).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(rent).build();
        }
    }

    // TODO - razmisli, če ne bi tukaj raje dali bike kot param
    @PUT
    @Path("/returnabike/{userId}/{rentId}/{mapId}")
    public Response returnBike(@PathParam("userId") Integer userId, @PathParam("rentId") Integer rentId, @PathParam("rentId") Integer mapId) {

        BikeRent rent = rentsBean.returnBike(userId, rentId, mapId);

        if (rent == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (rent.getId() != null)
                return Response.status(Response.Status.OK).entity(rent).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @PUT
    @Path("/{rentId}")
    public Response putRent(@PathParam("rentId") Integer rentId, BikeRent rent) {

        rent = rentsBean.putRent(rentId, rent);

        if (rent == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (rent.getId() != null)
                return Response.status(Response.Status.OK).entity(rent).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @DELETE
    @Path("/{rentId}")
    public Response deleteRent(@PathParam("rentId") Integer rentId) {

        boolean deleted = rentsBean.deleteRent(rentId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
