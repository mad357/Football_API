package org.league;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/league")
@ApplicationScoped
@Named
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LeagueResource {
    @Inject
    LeagueRepository leagueRepository;

    @Inject
    LeagueService leagueService;

    @GET
    @Path("/list")
    public List<League> list() {
        return leagueRepository.listAll();
    }
    @GET
    @Path("/{id}")
    public League getLeague(@PathParam("id") long id) {
        return leagueRepository.findByIdOptional(id).orElse(null);
    }

    @POST
    public Response create(League league, @Context UriInfo uriInfo) {
        try {
            long id = leagueService.create(league);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(String.valueOf(id));
            return Response.created(uriBuilder.build()).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response update(League league, @Context UriInfo uriInfo) {
        leagueService.update(league);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id,  @Context UriInfo uriInfo) {
        leagueService.delete(id);
        return Response.ok().build();
    }

}