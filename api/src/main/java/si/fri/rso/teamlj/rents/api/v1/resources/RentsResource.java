package si.fri.rso.teamlj.rents.api.v1.resources;

import si.fri.rso.teamlj.rents.entities.BikeRent;
import si.fri.rso.teamlj.rents.services.RentsBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
        rent.setLatitudeOfReturn("46.078018");
        rent.setLongitudeOfReturn("14.496590");

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

    @POST
    public Response createRent(BikeRent rent) {

        /** TODO **/
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

    @PUT
    @Path("{rentId}")
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

    @PATCH
    @Path("{rentId}/return")
    public Response returnBike(@PathParam("rentId") Integer rentId, BikeRent bikeRent) {

        BikeRent rent = rentsBean.returnBike(rentId, bikeRent);

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
    @Path("{rentId}")
    public Response deleteRent(@PathParam("rentId") String rentId) {

        boolean deleted = rentsBean.deleteRent(rentId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
