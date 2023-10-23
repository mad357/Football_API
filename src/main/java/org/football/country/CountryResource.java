package org.football.country;

import exceptions.DtoValidationException;
import org.football.AppConfig;
import org.football.util.validationgroups.Create;
import org.football.util.validationgroups.Update;
import org.modelmapper.ModelMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Set;

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

    final ModelMapper modelMapper;
    public CountryResource() {
        modelMapper = new ModelMapper();
    }
    
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
    public Response create(CountryDto countryDto, @Context UriInfo uriInfo) {
        Set<ConstraintViolation<CountryDto>> errors = AppConfig.getValidator().validate(countryDto, Create.class);
        if (errors.size() > 0) {
            throw new DtoValidationException(errors.iterator().next().getMessage());
        }
        var country =  modelMapper.map(countryDto, Country.class);
        long id = countryService.create(country);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(String.valueOf(id));
        return Response.created(uriBuilder.build()).build();
    }

    @PUT
    public Response update(CountryDto countryDto, @Context UriInfo uriInfo) {
        Set<ConstraintViolation<CountryDto>> errors = AppConfig.getValidator().validate(countryDto, Update.class);
        if (errors.size() > 0) {
            throw new DtoValidationException(errors.iterator().next().getMessage());
        }
        var country =  modelMapper.map(countryDto, Country.class);
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