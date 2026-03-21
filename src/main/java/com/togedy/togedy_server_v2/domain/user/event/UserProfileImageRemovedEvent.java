package com.togedy.togedy_server_v2.domain.user.event;

import com.togedy.togedy_server_v2.global.event.ImageRemovedEvent;

public record UserProfileImageRemovedEvent(String imageUrl) implements ImageRemovedEvent {
}
