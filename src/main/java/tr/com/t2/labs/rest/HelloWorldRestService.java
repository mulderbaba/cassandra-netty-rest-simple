package tr.com.t2.labs.rest;

import com.datastax.driver.core.utils.UUIDs;
import info.archinnov.achilles.persistence.AsyncManager;
import info.archinnov.achilles.persistence.PersistenceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tr.com.t2.labs.domain.Person;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Created by mertcaliskan
 * on 13/07/15.
 */
@Controller
@Path("/")
public class HelloWorldRestService {

    @Autowired
    private PersistenceManager persistenceManager;

    @Autowired
    private AsyncManager asyncManager;

    @GET
    @Produces("application/json")
    public Response sayHello() {
        UUID id = UUIDs.timeBased();
        Person person = new Person(id, "John", "Doe");
        asyncManager.insert(person);
        return Response.status(200).entity(id.toString()).build();
    }
}
