package dst.ass2.service.api.auth.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;

/**
 * The IAuthenticationResource exposes parts of the {@code IAuthenticationService} as a RESTful interface.
 */
public interface IAuthenticationResource {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/authenticate")
    Response authenticate(@FormParam("email") String email, @FormParam("password") String password)
            throws NoSuchUserException, AuthenticationException;

}
