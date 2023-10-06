package org.football.club;

import annotations.AllowedRoles;
import annotations.Authorized;
import io.quarkus.panache.common.Page;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    @Context
    UriInfo info;

    @Context
    HttpHeaders headers;

    final ModelMapper modelMapper;
    public ClubResource() {
        modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Club, ClubDto>() {
            @Override
            protected void configure() {
                skip(destination.getLeague());
                map(source.getLeague(), destination.getLeagueShort());
            }
        });
        modelMapper.addMappings(new PropertyMap<ClubDto, Club>() {
            @Override
            protected void configure() {
                map(source.getLeagueShort(), destination.getLeague());
                map(source.getLeagueShort().getId(), destination.getLeague().getId());
            }
        });

    }


    @GET
    @Path("/list")
    @Authorized()
    @AllowedRoles({"admin", "user"})
    public List<ClubDto> list() {
        List<Club> result;
        if (info.getQueryParameters().size() == 0) {
            result = clubRepository.listAll();
        } else {
            MultivaluedMap<String, String> requestParameters = new MultivaluedHashMap<>(info.getQueryParameters());
            setDefaultParameters(requestParameters);

            StringBuilder query = new StringBuilder ();
            Map<String, Object> queryParams = new HashMap<>();
            addFilters(query, queryParams, requestParameters);
            appendOrderAndPagination(query, requestParameters);

            result = clubRepository.find(query.toString(), queryParams).page(Page.of( Integer.parseInt(requestParameters.get("page").get(0)),Integer.parseInt(requestParameters.get("limit").get(0)))).list();
        }

        return result
                .stream()
                .map(element -> modelMapper.map(element, ClubDto.class))
                .collect(Collectors.toList());

    }

    @GET
    @Path("/{id}")
    @Authorized()
    @AllowedRoles({"admin", "user"})
    public ClubDto findById(@PathParam("id") long id) {
        var entity = clubRepository.findByIdOptional(id).orElse(null);
        if (entity == null) {
            return null;
        }

        return modelMapper.map(entity, ClubDto.class);

    }

    @GET
    @Path("/list-size")
    @Authorized()
    @AllowedRoles({"admin", "user"})
    public int listSize() {
        int result;
        if (info.getQueryParameters().size() == 0) {
            result = clubRepository.listAll().size();
        } else {
            MultivaluedMap<String, String> requestParameters = new MultivaluedHashMap<>(info.getQueryParameters());
            setDefaultParameters(requestParameters);

            StringBuilder query = new StringBuilder ();
            Map<String, Object> nonNullParams = new HashMap<>();
            addFilters(query, nonNullParams, requestParameters);

            result = clubRepository.find(query.toString(), nonNullParams).list().size();
        }

        return result;
    }

    @PUT
    @Authorized()
    @AllowedRoles({"admin", "user"})
    public Response update(ClubDto clubDto, @Context UriInfo uriInfo) {

        Club club = modelMapper.map(clubDto, Club.class);
        clubService.update(club);
        return Response.ok().build();
    }

    @POST
    @Authorized()
    @AllowedRoles({"admin", "user"})
    public Response create(ClubDto clubDto, @Context UriInfo uriInfo) {
        try {
            var club =  modelMapper.map(clubDto, Club.class);
            long id = clubService.create(club);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(String.valueOf(id));
            return Response.created(uriBuilder.build()).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Authorized()
    @AllowedRoles({"admin", "user"})
    public Response delete(@PathParam("id") Long id,  @Context UriInfo uriInfo) {
        clubService.delete(id);
        return Response.ok().build();
    }

    private void addFilters(StringBuilder query, Map<String, Object> params, MultivaluedMap<String, String> requestParameters) {
        query.append("SELECT DISTINCT c from Club c WHERE ");

        if (requestParameters.get("countryId") != null) {
            query.append(" c.country.id = :countryId AND ");
            params.put("countryId", Long.parseLong(requestParameters.get("countryId").get(0)));
        }
        if (requestParameters.get("leagueId") != null) {
            query.append(" c.league.id = :leagueId AND ");
            params.put("leagueId", Long.parseLong(requestParameters.get("leagueId").get(0)));
        }
        if (requestParameters.get("clubName") != null) {
            query.append("( UPPER(c.name) like :name or UPPER(c.fullname) like :name) AND ");
            params.put("name", "%" + requestParameters.get("clubName").get(0).toUpperCase() +"%");
        }
        if (requestParameters.get("yearFound") != null) {
            query.append(" c.yearFounded = :yearFound AND ");
            params.put("yearFound", Short.valueOf(requestParameters.get("yearFound").get(0)));
        }

        if  (query.indexOf("WHERE ") == query.length() - 6) {
            query.delete(query.length() - 7, query.length() - 1);
            }
        else if  (query.indexOf("AND ") == query.length() - 4) {
            query.delete(query.length() - 5, query.length() - 1);
        }


    }

    private void appendOrderAndPagination(StringBuilder query, MultivaluedMap<String, String> requestParameters) {
            query.append(" order by c.");
            query.append(requestParameters.get("order").get(0));
            if (Boolean.parseBoolean(requestParameters.get("ascending").get(0))) {
                query.append(" ASC");
            } else {
                query.append(" DESC");
            }

    }
    private void setDefaultParameters(MultivaluedMap<String, String> requestParameters) {
        if (requestParameters.get("page") == null){
            requestParameters.put("page", new ArrayList<>() {{add("0");}});
        }
        if (requestParameters.get("order") == null){
            requestParameters.put("order", new ArrayList<>() {{add("id");}});
        }
        if (requestParameters.get("limit") == null){
            requestParameters.put("limit", new ArrayList<>() {{add("2147483647");}});
        }
        if (requestParameters.get("ascending") == null){
            requestParameters.put("ascending", new ArrayList<>() {{add("true");}});
        }

    }

}