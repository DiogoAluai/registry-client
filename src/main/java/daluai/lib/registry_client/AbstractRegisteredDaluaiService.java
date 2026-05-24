package daluai.lib.registry_client;

import daluai.lib.network_utils.LocalIpProbe;
import daluai.lib.registry_api.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Abstract class that registers itself as a service to the registry.
 */
public abstract class AbstractRegisteredDaluaiService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRegisteredDaluaiService.class);

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private String privateIp;

    public AbstractRegisteredDaluaiService(String name) {
        this.privateIp = LocalIpProbe.firstActiveIPv4Address();
        registerService(new Service(
                name,
                privateIp,
                true
        ));
    }

    /**
     * Send register command and set deregister hook for app shutdown
     */
    protected void registerService(Service service) {
        LOG.info("Registering service in registry: {}", service);
        RegistryClient.get().register(service);
        setUnregisterShutdownHook(service);
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            // ~ private ip may change, so let's check now and then and keep registry updated
            String newPrivateIp = LocalIpProbe.firstActiveIPv4Address();
            if (!privateIp.equals(newPrivateIp)) {
                LOG.info("IP address has changed from {} to {}", privateIp, newPrivateIp);
                privateIp = newPrivateIp;
                registerService(new Service(
                        service.name(),
                        newPrivateIp,
                        true
                ));
            } else {
                LOG.info("Private ip unchanged: {}", privateIp);
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    /**
     * Set deregister hook to be called on shutdown.
     * You might want to override this for services with cold start, since they'll deregister when going dark.
     */
    protected void setUnregisterShutdownHook(Service service) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
                LOG.info("Unregistering service in registry: {}", service);
                RegistryClient.get().deregister(service.name());
                LOG.info("Done unregistering");
            } catch (Exception e) {
                LOG.error("Failed unregistering service in registry: {}", service);
			}
		}));
    }


}
