package daluai.lib.registry_client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import daluai.lib.network_utils.HttpMethod;
import daluai.lib.network_utils.RequestResult;
import daluai.lib.registry_api.Service;
import okhttp3.Interceptor;
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
 * Default api key interceptor requires api key defined in properties file
 */
public class RegistryClient {

    private static final Logger LOG = LoggerFactory.getLogger(RegistryClient.class);

    // Default api key interceptor requires that both api key and api key encr secret are defined in properties file
    public static final RegistryClient PUBLIC_INSTANCE = new RegistryClient();
    public static final RegistryClient LOCAL_INSTANCE = new RegistryClient(true);

    private final Interceptor interceptor;
    private final String registryUrl;

    RegistryClient() {
        this(new RegistryApiKeyInterceptor());
    }

    RegistryClient(boolean isLocal) {
        this(new RegistryApiKeyInterceptor(), isLocal);
    }

    RegistryClient(Interceptor interceptor) {
        this(interceptor, false);
    }

    RegistryClient(Interceptor interceptor, boolean isLocal) {
        this.interceptor = interceptor;
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

        return sendHttpRequest(registryUrl, HttpMethod.POST, ENDPOINT_REGISTER, serviceJson, interceptor);
    }

    public RequestResult deregister(String serviceName) {
        return sendHttpRequest(registryUrl, HttpMethod.GET, ENDPOINT_DEREGISTER + "/" + serviceName, interceptor);
    }

    public Service retrieve(String serviceName) {
        return queryHttpRequest(registryUrl, ENDPOINT_RETRIEVE + "/" + serviceName, Service.class, null, List.of(interceptor));
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, Service> retrieveAll() {
        var hashMapType = TypeFactory.defaultInstance().constructParametricType(HashMap.class, String.class, Service.class);
        return queryHttpRequest(registryUrl, ENDPOINT_RETRIEVE_ALL, hashMapType, null, List.of(interceptor));
    }

    public RequestResult reset() {
        return sendHttpRequest(registryUrl, HttpMethod.GET, ENDPOINT_RESET, interceptor);
    }
}
