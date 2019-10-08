package com.edilson.justiniano.kalah.unit.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GameResponse {

    private String id;
    private String url;
    private Map<Integer, Integer> status;
}
