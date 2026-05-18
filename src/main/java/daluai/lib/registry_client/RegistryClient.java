package daluai.lib.registry_client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import daluai.lib.network_utils.HttpMethod;
import daluai.lib.network_utils.RequestResult;
import daluai.lib.registry_api.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static daluai.lib.network_utils.HttpRequestUtils.queryHttpRequest;
import static daluai.lib.network_utils.HttpRequestUtils.sendHttpRequest;
import static daluai.lib.registry_api.Coms.ENDPOINT_DEREGISTER;
import static daluai.lib.registry_api.Coms.ENDPOINT_REGISTER;
import static daluai.lib.registry_api.Coms.ENDPOINT_RESET;
import static daluai.lib.registry_api.Coms.ENDPOINT_RETRIEVE;
import static daluai.lib.registry_api.Coms.ENDPOINT_RETRIEVE_ALL;

/**
 * Client for accessing registry through the network.
 */
public class RegistryClient {

    private static final Logger LOG = LoggerFactory.getLogger(RegistryClient.class);

    public static final RegistryClient INSTANCE = new RegistryClient();

    private final String registryUrl;

    RegistryClient() {
        this(System.getenv("registry.url"));
    }

    /**
     * For test purposes
     */
    RegistryClient(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public static RegistryClient get() {
        return INSTANCE;
    }

    public RequestResult register(Service service) {
        String serviceJson;
        try {
            serviceJson = new ObjectMapper().writeValueAsString(service);
        } catch (JsonProcessingException e) {
            LOG.error("Error during service json creation.", e);
            return RequestResult.FAIL;
        }

        return sendHttpRequest(registryUrl, HttpMethod.POST, ENDPOINT_REGISTER, serviceJson);
    }

    public RequestResult deregister(String serviceName) {
        return sendHttpRequest(registryUrl, HttpMethod.GET, ENDPOINT_DEREGISTER + "/" + serviceName);
    }

    public Service retrieve(String serviceName) {
        return queryHttpRequest(registryUrl, ENDPOINT_RETRIEVE + "/" + serviceName, Service.class);
    }

    public HashMap<String, Service> retrieveAll() {
        var hashMapType = TypeFactory.defaultInstance().constructParametricType(HashMap.class, String.class, Service.class);
        return queryHttpRequest(registryUrl, ENDPOINT_RETRIEVE_ALL, hashMapType); // why not simply Map instead of HashMap?
    }

    public RequestResult reset() {
        return sendHttpRequest(registryUrl, HttpMethod.GET, ENDPOINT_RESET);
    }
}
