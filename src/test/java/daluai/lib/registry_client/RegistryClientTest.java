package daluai.lib.registry_client;

import daluai.lib.registry_api.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Assumes registry service is deployed locally
 */
public class RegistryClientTest {

	public static final Service TEST_SERVICE = new Service(
			"asd", "bsd", true);
	public static final Service TEST_RESCIVE = new Service(
			"dsa", "dsb", false);

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
        List<Service> serviceList = LOCAL_REGISTRY.retrieveAll();
		assertTrue(serviceList.contains(TEST_SERVICE));
		assertTrue(serviceList.contains(TEST_RESCIVE));
	}
}
