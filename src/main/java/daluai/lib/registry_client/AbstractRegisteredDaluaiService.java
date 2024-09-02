package daluai.lib.registry_client;

import daluai.lib.network_utils.LocalIpProbe;
import daluai.lib.registry_api.Service;
import daluai.lib.registry_api.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Abstract class that registers itself as a service to the registry.
 * Todo:
 */
public abstract class AbstractRegisteredDaluaiService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRegisteredDaluaiService.class);

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private String privateIp;

    public AbstractRegisteredDaluaiService(String name, String port, ServiceType type) {
        this(name, "", port, type);
    }

    public AbstractRegisteredDaluaiService(String name, String port, ServiceType type, String protocol) {
        this(name, "", port, type, protocol);
    }

    public AbstractRegisteredDaluaiService(String name, String publicUrl, String port, ServiceType type) {
        registerService(new Service(
                name,
                LocalIpProbe.firstActiveIPv4Address(),
                publicUrl,
                port,
                type
        ));
    }
    public AbstractRegisteredDaluaiService(String name, String publicUrl, String port, ServiceType type, String protocol) {
        registerService(new Service(
                name,
                LocalIpProbe.firstActiveIPv4Address(),
                publicUrl,
                port,
                type,
                protocol
        ));
    }

    /**
     * Send register command and set derigister hook for app shutdown
     */
    protected void registerService(Service service) {
        LOG.info("Registering service in registry: " + service);
        RegistryClient.PUBLIC_INSTANCE.register(service);
        privateIp = service.privateIp();
        setUnregisterShutdownHook(service);
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            // ~ private ip may change, so let's check now and then and keep registry updated
            String newPrivateIp = LocalIpProbe.firstActiveIPv4Address();
            if (!privateIp.equals(newPrivateIp)) {
                LOG.info("IP address has changed from " + privateIp + " to " + newPrivateIp);
                privateIp = newPrivateIp;
                registerService(new Service(
                        service.name(),
                        newPrivateIp,
                        service.publicIp(),
                        service.port(),
                        service.type())
                );
            } else {
                LOG.info("Private ip unchanged: " + privateIp);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Set deregister hook to be called on shutdown.
     * You might want to override this for services with cold start, since they'll deregister when going dark.
     */
    protected void setUnregisterShutdownHook(Service service) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
                RegistryClient.PUBLIC_INSTANCE.deregister(service.name());
                LOG.info("Unregistering service in registry: " + service);
            } catch (Exception e) {
                LOG.error("Failed unregistering service in registry: " + service);
			}
		}));
    }


}
