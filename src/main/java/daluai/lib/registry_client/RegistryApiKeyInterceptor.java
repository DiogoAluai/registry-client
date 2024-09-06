package daluai.lib.registry_client;

import daluai.lib.network_utils.EncryptedApiKeyInterceptor;
import daluai.lib.network_utils.property.PropertyKeys;

import static daluai.lib.registry_api.Coms.REGISTRY_PROPERTY_MANAGER;

public class RegistryApiKeyInterceptor extends EncryptedApiKeyInterceptor {
    /**
     * Fetch api key and aes secret from properties and use them.
     */
    public RegistryApiKeyInterceptor() {
        super(
                REGISTRY_PROPERTY_MANAGER,
                PropertyKeys.REGISTRY_API_KEY,
                PropertyKeys.REGISTRY_API_KEY_AES_SECRET);
    }
}
