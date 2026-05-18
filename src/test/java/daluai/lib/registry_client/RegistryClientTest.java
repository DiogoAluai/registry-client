package daluai.lib.registry_client;

import daluai.lib.registry_api.Service;
import daluai.lib.registry_api.ServiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Assumes registry service is deployed locally
 */
public class RegistryClientTest {

	public static final Service TEST_SERVICE = new Service(
			"asd", "bsd", "csd", "dsd", ServiceType.PRIVATE);
	public static final Service TEST_RESCIVE = new Service(
			"dsa", "dsb", "dsc", "dsd", ServiceType.CLOUD);

	private static final RegistryClient LOCAL_REGISTRY = new RegistryClient("http://localhost:8080");

	@Test
	public void checkSanity() {
		assertEquals(2 + 2, 4);
		assertNotEquals(10 + 9, 21);
	}

	/**
	 * Clear registry before each test
	 */
	@BeforeEach
	public void clearRegistry() {
		LOCAL_REGISTRY.reset();
	}

	@Test
	public void testRegisterAndRetrieve() {
		LOCAL_REGISTRY.register(TEST_SERVICE);
		Service retrievedTestService = LOCAL_REGISTRY.retrieve(TEST_SERVICE.name());
		assertEquals(TEST_SERVICE, retrievedTestService);
	}

	@Test
	public void testDeregister() {
		LOCAL_REGISTRY.register(TEST_SERVICE);
		LOCAL_REGISTRY.deregister(TEST_SERVICE.name());
		assertNull(LOCAL_REGISTRY.retrieve(TEST_SERVICE.name()));
	}

	@Test
	public void testRetrieveAll() {
		LOCAL_REGISTRY.register(TEST_SERVICE);
		LOCAL_REGISTRY.register(TEST_RESCIVE);
        HashMap<String, Service> serviceHashMap = LOCAL_REGISTRY.retrieveAll();
		assertEquals(TEST_SERVICE, serviceHashMap.get(TEST_SERVICE.name()));
		assertEquals(TEST_RESCIVE, serviceHashMap.get(TEST_RESCIVE.name()));
	}
}
