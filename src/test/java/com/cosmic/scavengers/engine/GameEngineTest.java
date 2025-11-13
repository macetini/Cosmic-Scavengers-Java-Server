package com.cosmic.scavengers.engine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

class GameEngineTest {

	// Use @Spy to test the actual GameEngine while being able to mock its internal
	// calls if needed.
	@Spy
	private GameEngine engine;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testEngineLoopRunsAndIsInterruptable() throws InterruptedException {
		// Arrange: Start the engine thread
		Thread engineThread = new Thread(engine);
		engineThread.start();

		// Act: Give the engine a moment to run its loop (e.g., 2 ticks)
		Thread.sleep(250);

		// Assert 1: The thread is alive (it ran)
		assertTrue(engineThread.isAlive(), "GameEngine thread should be running.");

		// Act 2: Interrupt the thread to simulate a shutdown
		engineThread.interrupt();

		// Assert 2: Wait for the thread to finish its shutdown process
		engineThread.join(500); // Wait up to 500ms for it to finish

		// Assert 3: The thread should no longer be alive after interruption
		assertFalse(engineThread.isAlive(), "GameEngine thread should have shut down gracefully after interrupt.");
	}

	// (Optional but good for future logic): Test the GameEngine's interaction with
	// the Broadcaster
	// Once GameEngine takes an IMessageBroadcaster dependency, you would test:
	/*
	 * @Mock private IMessageBroadcaster mockBroadcaster;
	 * 
	 * @Test void testEngineCallsBroadcasterOnTick() throws InterruptedException {
	 * // Arrange // (Modify GameEngine to use mockBroadcaster here)
	 * 
	 * // Act: Let it run for one tick // (You would need a more elaborate test to
	 * wait for the first tick)
	 * 
	 * // Assert // verify(mockBroadcaster, atLeastOnce()).broadcast(anyString(),
	 * any()); }
	 */
}