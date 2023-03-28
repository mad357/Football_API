package org.football.user;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/user")
@ApplicationScoped
@Named
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserRepository userRepository;

    @Inject
    UserService userService;
    
    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") long id) {
        return userRepository.findByIdOptional(id).orElse(null);
    }

//    @POST
//    public Response create(User user, @Context UriInfo uriInfo) {
//        try {
//            long id = userService.create(user);
//            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
//            uriBuilder.path(String.valueOf(id));
//            return Response.created(uriBuilder.build()).build();
//        } catch (RuntimeException e) {
//            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
//        }
//    }

//    @DELETE
//    @Path("/{id}")
//    public Response delete(@PathParam("id") Long id,  @Context UriInfo uriInfo) {
//        userService.delete(id);
//        return Response.ok().build();
//    }

    @POST
    @Path("/login")
    public String login(User user, @Context UriInfo uriInfo) {
        return userService.login(user.getLogin(), user.getPassword());
    }

    @POST
    @Path("/refresh-token")
    public String refreshToken(String refreshToken, @Context UriInfo uriInfo) {
        return userService.loginWithRefreshToken(refreshToken);
    }
}