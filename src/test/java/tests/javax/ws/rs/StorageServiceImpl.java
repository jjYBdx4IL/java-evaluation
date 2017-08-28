package tests.javax.ws.rs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/storage")
public class StorageServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(StorageServiceImpl.class);

    public StorageServiceImpl() {
        LOG.info("initialized");
    }

    private static final Map<String, String> storage = new ConcurrentHashMap<>();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{key}")
    public Response get(@PathParam("key") String key) {
        LOG.info("get() " + key);
        if (key == null || key.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("key required").build();
        }
        if (!storage.containsKey(key)) {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).entity("key not found").build();
        }
        return Response.ok().entity(storage.get(key)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{key}")
    public Response getJson(@PathParam("key") String key) {
        LOG.info("getJson() " + key);
        if (key == null || key.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("key required").build();
        }
        if (!storage.containsKey(key)) {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).entity("key not found").build();
        }
        DTO dto = new DTO();
        dto.setaString(storage.get(key));
        return Response.ok().entity(dto).build();
    }
    
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/{key}")
    public Response put(String value, @PathParam("key") String key) {
        LOG.info("put()");
        if (key == null || key.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("key required").build();
        }
        storage.put(key, value);
        return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/{key}")
    public Response postForm(
        @FormParam("payload") String payload,
        @PathParam("key") String key,
        @DefaultValue("true") @QueryParam("append") boolean append
        ) {
        LOG.info("postForm()");
        if (key == null || key.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("key required").build();
        }
        if (append && storage.containsKey(key)) {
            storage.put(key, storage.get(key) + payload);
        } else {
            storage.put(key, payload);
        }
        return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{key}")
    public Response postJson(
        DTO dto,
        @PathParam("key") String key,
        @DefaultValue("true") @QueryParam("append") boolean append
        ) {
        LOG.info("postJson()");
        if (key == null || key.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("key required").build();
        }
        if (append && storage.containsKey(key)) {
            storage.put(key, storage.get(key) + dto.getaString());
        } else {
            storage.put(key, dto.getaString());
        }
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{key}")
    public Response delete(@PathParam("key") String key) {
        LOG.info("delete()");
        if (key == null || key.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("key required").build();
        }
        if (!storage.containsKey(key)) {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
        storage.remove(key);
        return Response.noContent().build();
    }

}