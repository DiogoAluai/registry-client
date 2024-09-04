package daluai.lib.registry_client;

import daluai.lib.registry_api.Service;
import daluai.lib.registry_api.ServiceType;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

/**
 * Assumes registry service is deployed locally
 */
public class RegistryClientTest {

	public static final Service TEST_SERVICE = new Service(
			"asd", "bsd", "csd", "dsd", ServiceType.PRIVATE);
	public static final Service TEST_RESCIVE = new Service(
			"dsa", "dsb", "dsc", "dsd", ServiceType.CLOUD);

	private static final RegistryClient client = RegistryClient.LOCAL_INSTANCE;

	@Test
	public void checkSanity() {
		assertEquals(2 + 2, 4);
		assertNotEquals(10 + 9, 21);
	}

	/**
	 * Clear registry after each test
	 */
	@Before
	public void clearRegistry() {
		client.reset();
	}

	@Test
	public void testRegisterAndRetrieve() {
		client.register(TEST_SERVICE);
		Service retrievedTestService = client.retrieve(TEST_SERVICE.name());
		assertEquals(TEST_SERVICE, retrievedTestService);
	}

	@Test
	public void testDeregister() {
		client.register(TEST_SERVICE);
		Service retrievedTestService = client.retrieve(TEST_SERVICE.name());
		assertEquals(retrievedTestService, TEST_SERVICE);

		client.deregister(TEST_SERVICE.name());
		assertNull(client.retrieve(TEST_SERVICE.name()));
	}

	@Test
	public void testRetrieveAll() {
		client.register(TEST_SERVICE);
		client.register(TEST_RESCIVE);
        HashMap<String, Service> serviceHashMap = client.retrieveAll();
		assertEquals(TEST_SERVICE, serviceHashMap.get(TEST_SERVICE.name()));
		assertEquals(TEST_RESCIVE, serviceHashMap.get(TEST_RESCIVE.name()));
	}

	/**
	 * To be safe, let's not test this.
	 */
	public void testReset() {
		var registry = RegistryClient.LOCAL_INSTANCE; // careful with that, don't want to reset cloud registry!
		registry.register(TEST_SERVICE);

		var resultService = registry.retrieve(TEST_SERVICE.name());
		assertEquals(resultService, TEST_SERVICE);

		registry.deregister(TEST_SERVICE.name());
	}
}
