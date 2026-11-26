package com.togedy.togedy_server_v2.domain.badge.event;

import com.togedy.togedy_server_v2.global.event.ImageRemovedEvent;

public record BadgeImageRemovedEvent(String imageUrl) implements ImageRemovedEvent {
}
