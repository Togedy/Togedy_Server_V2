package com.togedy.togedy_server_v2.domain.chat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NerKeyword {

    @JsonProperty("UNI")
    private List<String> uni;

    @JsonProperty("TYPE")
    private List<String> type;

    @JsonProperty("KEYWORD")
    private List<String> keyword;

}
