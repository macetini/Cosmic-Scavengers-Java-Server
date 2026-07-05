package com.cosmic.scavengers.core.init;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.core.engine.GameEngine;
import com.cosmic.scavengers.core.netty.NettyServer;
import com.cosmic.scavengers.db.ingestion.BlueprintsIngestionService;
import com.cosmic.scavengers.db.ingestion.TraitsIngestionService;
import com.cosmic.scavengers.db.ingestion.WorldsIngestionService;
import com.cosmic.scavengers.gameplay.registries.BlueprintRegistry;
import com.cosmic.scavengers.gameplay.registries.TraitRegistry;

@Component
public class DataInitializer implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	private final TraitsIngestionService traitsIngester;
	private final BlueprintsIngestionService blueprintIngester;
	private final WorldsIngestionService worldsIngester;
	
	private final TraitRegistry traitRegistry;
	private final BlueprintRegistry blueprintRegistry;
	
	private final NettyServer nettyServer;
	private final GameEngine gameEngine;

	private final ExecutorService executorService = Executors.newFixedThreadPool(2);

	public DataInitializer(
			TraitsIngestionService traitsIngester, 
			BlueprintsIngestionService blueprintIngester,
			WorldsIngestionService worldsIngester,
			
			TraitRegistry traitRegistry,
			BlueprintRegistry blueprintRegistry, 
			
			NettyServer nettyServer, 
			GameEngine gameEngine) {

		// Ingesters
		this.traitsIngester = traitsIngester;
		this.blueprintIngester = blueprintIngester;
		this.worldsIngester = worldsIngester;

		// Registries
		this.traitRegistry = traitRegistry;
		this.blueprintRegistry = blueprintRegistry;

		// Threads
		this.nettyServer = nettyServer;
		this.gameEngine = gameEngine;
	}

	@Override
	public void run(String... args) {
		try {
			log.info("--- STARTING BOOTSTRAP SEQUENCE ---");

			// Phase 1: File to DB
			log.info("Phase [1/3] Persistence Sync (YAML -> DB)");

			log.debug("[1/3] Synchronizing TRAITS definitions from filesystem.");
			traitsIngester.sync();
			log.debug("[2/3] Synchronizing BLUEPRINT definitions from filesystem.");
			blueprintIngester.sync();
			log.debug("[3/3] Synchronizing WORL definitions from filesystem.");
			worldsIngester.sync();

			log.debug("Persistence Sync (YAML -> DB) COMPLETE.");
			//

			// Phase 2: DB to RAM
			log.info("Phase [2/3] Registry Sync (DB -> RAM)");

			log.debug("[1/2] Loading TRAITS from Database into Registries.");
			traitRegistry.load();
			log.debug("[2/2] Loading BLUEPRINTS from Database into Registries.");
			blueprintRegistry.load();

			log.debug("Phase [2/3] Registry Sync (DB -> RAM) COMPLETE.");
			//

			// Phase 3: Launch Threads
			log.info("Phase [3/3] Launching Game Core");

			executorService.submit(gameEngine);
			log.debug("[1/2] GameEngine tick loop started.");
			executorService.submit(nettyServer);
			log.debug("[2/2] Submitted NettyServer listening for players.");

			log.debug("Phase [3/3] Launching Game Core COMPLETE.");
			//

			log.info("--- BOOTSTRAP COMPLETE: SERVER LIVE ---");

		} catch (Exception e) {
			log.error("--- BOOTSTRAP FAILED! ---", e);
			System.exit(1);
		}
	}
}
