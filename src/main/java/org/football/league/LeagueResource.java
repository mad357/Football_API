package org.football.league;

import annotations.Authorized;
import annotations.AllowedRoles;
import org.football.club.Club;
import org.football.club.ClubDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

    @Context
    UriInfo info;

    @Context
    HttpHeaders headers;

    final ModelMapper modelMapper;
    public LeagueResource() {
        modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Club, ClubDto>() {
            @Override
            protected void configure() {
                skip(destination.getLeague());
                //modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
                //modelMapper.getConfiguration().setSkipNullEnabled(true);
            }
        });
    }

    @GET
    @Path("/list")
    @Authorized()
    @AllowedRoles({"admin", "user"})
    public List<LeagueDto> list() {
        List<League> result = null;
        if (info.getQueryParameters().size() == 0) {
            result = leagueRepository.listAll();
        } else {
            StringBuilder query = new StringBuilder ();
            Map<String, Object> nonNullParams = new HashMap<>();
            addFilters(query, nonNullParams);

            result = leagueRepository.list(query.toString(), nonNullParams);
        }

        return result
                .stream()
                .map(element -> modelMapper.map(element, LeagueDto.class))
                .collect(Collectors.toList());

    }
    @GET
    @Path("/{id}")
    @Authorized()
    @AllowedRoles({"admin", "user"})
    public LeagueDto getLeague(@PathParam("id") long id) {
        var entity = leagueRepository.findByIdOptional(id).orElse(null);
        if (entity == null) {
            return null;
        }

        return modelMapper.map(entity, LeagueDto.class);
    }

    @POST
    @Authorized()
    @AllowedRoles({"admin", "user"})
    public Response create(LeagueDto leagueDto, @Context UriInfo uriInfo) {
        try {
            var league =  modelMapper.map(leagueDto, League.class);
            long id = leagueService.create(league);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(String.valueOf(id));
            return Response.created(uriBuilder.build()).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Authorized()
    @AllowedRoles({"admin", "user"})
    public Response update(LeagueDto leagueDto, @Context UriInfo uriInfo) {
        League league = modelMapper.map(leagueDto, League.class);
        leagueService.update(league);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    @Authorized()
    @AllowedRoles({"admin", "user"})
    public Response delete(@PathParam("id") Long id,  @Context UriInfo uriInfo) {
        leagueService.delete(id);
        return Response.ok().build();
    }

    private void addFilters(StringBuilder query, Map<String, Object> params) {
        MultivaluedMap<String, String> parameters = info.getQueryParameters();
        boolean firstParameter = true;
        query.append("select DISTINCT l from League l JOIN l.lowerLeagues ll JOIN l.higherLeagues hl where ");

        if (parameters.get("countryId") != null) {
            firstParameter = false;
            query.append("l.country.id = :countryId");
            params.put("countryId", Long.parseLong(parameters.get("countryId").get(0)));
        }

        if (parameters.get("name") != null) {
            if (!firstParameter){
                query.append(" AND ");
            }
            firstParameter = false;
            query.append("l.name like UPPER(:name)");
            params.put("name", "%" + parameters.get("name").get(0).toUpperCase() +"%");
        }

        if (parameters.get("clubNumber") != null) {
            if (!firstParameter){
                query.append(" AND ");
            }
            firstParameter = false;
            query.append("l.clubNumber = :clubNumber");
            params.put("clubNumber", Short.parseShort(parameters.get("clubNumber").get(0)));
        }

        if (parameters.get("lowerLeagueId") != null) {
            if (!firstParameter){
                query.append(" AND ");
            }
            firstParameter = false;
            query.append("ll.id IN (:lowerLeagueId)");
            params.put("lowerLeagueId", new HashSet<>(Arrays.asList( Long.parseLong(parameters.get("lowerLeagueId").get(0)))));
        }

        if (parameters.get("higherLeagueId") != null) {
            if (!firstParameter){
                query.append(" AND ");
            }
            firstParameter = false;
            query.append("hl.id IN (:higherLeagueId)");
            params.put("higherLeagueId", new HashSet<>(Arrays.asList( Long.parseLong(parameters.get("higherLeagueId").get(0)))));
        }
    }
}