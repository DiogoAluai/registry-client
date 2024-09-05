package daluai.lib.registry_client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import daluai.lib.network_utils.ApiKeyInterceptor;
import daluai.lib.network_utils.HttpMethod;
import daluai.lib.network_utils.RequestResult;
import daluai.lib.registry_api.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

import static daluai.lib.network_utils.HttpRequestUtils.queryHttpRequest;
import static daluai.lib.network_utils.HttpRequestUtils.sendHttpRequest;
import static daluai.lib.registry_api.Coms.ENDPOINT_DEREGISTER;
import static daluai.lib.registry_api.Coms.ENDPOINT_REGISTER;
import static daluai.lib.registry_api.Coms.ENDPOINT_RESET;
import static daluai.lib.registry_api.Coms.ENDPOINT_RETRIEVE;
import static daluai.lib.registry_api.Coms.ENDPOINT_RETRIEVE_ALL;
import static daluai.lib.registry_api.Coms.REGISTRY_CLOUD_URL;
import static daluai.lib.registry_api.Coms.REGISTRY_LOCAL_URL;

/**
 * Client for accessing registry through the network.
 */
public class RegistryClient {

    private static final Logger LOG = LoggerFactory.getLogger(RegistryClient.class);

    private static final ApiKeyInterceptor API_KEY_INTERCEPTOR = new ApiKeyInterceptor();

    public static final RegistryClient PUBLIC_INSTANCE = new RegistryClient();
    public static final RegistryClient LOCAL_INSTANCE = new RegistryClient(true);

    private final String registryUrl;

    RegistryClient() {
        this(false);
    }

    RegistryClient(boolean isLocal) {
        registryUrl = isLocal ? REGISTRY_LOCAL_URL : REGISTRY_CLOUD_URL;
    }

    public static RegistryClient get() {
        return PUBLIC_INSTANCE;
    }

    public static RegistryClient get(boolean local) {
        return local ? LOCAL_INSTANCE : PUBLIC_INSTANCE;
    }

    public RequestResult register(Service service) {
        String serviceJson;
        try {
            serviceJson = new ObjectMapper().writeValueAsString(service);
        } catch (JsonProcessingException e) {
            LOG.error("Error during service json creation.", e);
            return RequestResult.FAIL;
        }

        return sendHttpRequest(registryUrl, HttpMethod.POST, ENDPOINT_REGISTER, serviceJson, API_KEY_INTERCEPTOR);
    }

    public RequestResult deregister(String serviceName) {
        return sendHttpRequest(registryUrl, HttpMethod.GET, ENDPOINT_DEREGISTER + "/" + serviceName, API_KEY_INTERCEPTOR);
    }

    public Service retrieve(String serviceName) {
        return queryHttpRequest(registryUrl, ENDPOINT_RETRIEVE + "/" + serviceName, Service.class, null, List.of(API_KEY_INTERCEPTOR));
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, Service> retrieveAll() {
        var hashMapType = TypeFactory.defaultInstance().constructParametricType(HashMap.class, String.class, Service.class);
        return queryHttpRequest(registryUrl, ENDPOINT_RETRIEVE_ALL, hashMapType, null, List.of(API_KEY_INTERCEPTOR));
    }

    public RequestResult reset() {
        return sendHttpRequest(registryUrl, HttpMethod.GET, ENDPOINT_RESET, API_KEY_INTERCEPTOR);
    }
}
