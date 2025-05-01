package com.togedy.togedy_server_v2.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"success", "response"})
public class ErrorResponse {

    @JsonProperty(value = "isSuccess")
    private final boolean success = false;

    @JsonProperty(value = "status")
    private final int status;

    @JsonProperty(value = "code")
    private final String code;

    @JsonProperty(value = "message")
    private final String message;

}
