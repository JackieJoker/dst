package dst.ass2.service.facade.impl;

import dst.ass2.service.auth.client.IAuthenticationClient;
import dst.ass2.service.facade.Authenticated;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Authenticated
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private IAuthenticationClient authenticationClient;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        MultivaluedMap<String, String> headers = containerRequestContext.getHeaders();
        if (headers.containsKey("Authorization")) {
            // authorization = Bearer <token>
            String authorization = headers.getFirst("Authorization");
            // token = <token>
            // skip the first 7 characters that are: "Bearer "
            String token = authorization.substring(7);
            if (!authenticationClient.isTokenValid(token)) {
                containerRequestContext.abortWith(Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity("AuthenticationFilter: The token " + token + " is invalid.")
                        .build());
            }
        } else {
            containerRequestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("AuthenticationFilter: Token not found in request header. Bearer token needed.")
                    .build());
        }
    }
}
