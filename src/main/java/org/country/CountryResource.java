package org.country;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/country")
@ApplicationScoped
@Named
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CountryResource {

    @Inject
    CountryRepository countryRepository;

    @Inject
    CountryService countryService;
    
    @GET
    @Path("/list")
    public List<Country> list() {
        return countryRepository.listAll();
    }
    @GET
    @Path("/{id}")
    public Country getCountry(@PathParam("id") long id) {
        return countryRepository.findByIdOptional(id).orElse(null);
    }

    @POST
    public Response create(Country country, @Context UriInfo uriInfo) {
        try {
            long id = countryService.create(country);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(String.valueOf(id));
            return Response.created(uriBuilder.build()).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response update(Country country, @Context UriInfo uriInfo) {
        countryService.update(country);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id,  @Context UriInfo uriInfo) {
        countryService.delete(id);
        return Response.ok().build();
    }

}