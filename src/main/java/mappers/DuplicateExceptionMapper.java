package mappers;

import exceptions.DuplicateException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DuplicateExceptionMapper implements ExceptionMapper<DuplicateException> {

    @Override
    public Response toResponse(DuplicateException e) {
        return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
    }
}