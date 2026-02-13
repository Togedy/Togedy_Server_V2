package com.togedy.togedy_server_v2.domain.chat.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NerKeyword {

    private List<String> uni;

    private List<String> type;

    private List<String> keyword;

}
