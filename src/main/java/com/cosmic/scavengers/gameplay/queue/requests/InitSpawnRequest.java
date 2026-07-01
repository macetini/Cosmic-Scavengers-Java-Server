package com.cosmic.scavengers.gameplay.queue.requests;

import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequest;

import io.netty.channel.ChannelId;

public record InitSpawnRequest(ChannelId channelId, Long playerId) implements IGameplayRequest {
}
