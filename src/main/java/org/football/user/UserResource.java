package org.football.user;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.security.NoSuchAlgorithmException;

@Path("/user")
@ApplicationScoped
@Named
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    @Path("/register")
    public Response register(UserDto user, @Context UriInfo uriInfo) {
        try {
            userService.registerUser(user);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            return Response.created(uriBuilder.build()).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/login")
    public String login(UserDto user, @Context UriInfo uriInfo) {
        return userService.login(user.getLogin(), user.getPassword());
    }

    @POST
    @Path("/refresh-token")
    public String refreshToken(String refreshToken, @Context UriInfo uriInfo) {
        return userService.loginWithRefreshToken(refreshToken);
    }
}