import request.RequestType;
import response.Response;
import rest.Endpoint;
import rest.Path;
import rest.Rest;

@Rest(path = "some/long/path")
public class TestRest {

    @Endpoint(type = RequestType.GET)
    @Path(value = "data")
    public Response getTestData() {
        return null;
    }
}
