package com.togedy.togedy_server_v2.domain.planner.event;

import com.togedy.togedy_server_v2.global.event.ImageRemovedEvent;

public record PlannerImageRemovedEvent(String imageUrl) implements ImageRemovedEvent {
}
