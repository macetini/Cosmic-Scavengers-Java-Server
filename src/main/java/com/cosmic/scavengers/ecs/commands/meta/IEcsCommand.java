package com.cosmic.scavengers.ecs.commands.meta;

import dev.dominion.ecs.api.Dominion;

public interface IEcsCommand {
	void execute(Dominion dominion);
}