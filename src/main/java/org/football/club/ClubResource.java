package org.football.club;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/club")
@ApplicationScoped
@Named
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClubResource {
    @Inject
    ClubRepository clubRepository;

    @Inject
    ClubService clubService;

    @GET
    @Path("/list")
    public List<Club> list() {
        return clubRepository.listAll();
    }
    @GET
    @Path("/{id}")
    public Club getClub(@PathParam("id") long id) {
        return clubRepository.findByIdOptional(id).orElse(null);
    }

    @POST
    public Response create(Club club, @Context UriInfo uriInfo) {
        try {
            long id = clubService.create(club);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(String.valueOf(id));
            return Response.created(uriBuilder.build()).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response update(Club club, @Context UriInfo uriInfo) {
        clubService.update(club);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id,  @Context UriInfo uriInfo) {
        clubService.delete(id);
        return Response.ok().build();
    }

}