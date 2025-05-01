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
public class ApiResponse<T> {

    @JsonProperty(value = "isSuccess")
    private final boolean success = true;

    @JsonProperty(value = "response")
    private final T response;

}
