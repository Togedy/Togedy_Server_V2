package com.togedy.togedy_server_v2.domain.study.event;

import com.togedy.togedy_server_v2.global.event.ImageRemovedEvent;

public record StudyImageRemovedEvent(String imageUrl) implements ImageRemovedEvent {
}
