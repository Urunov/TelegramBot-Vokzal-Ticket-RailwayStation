package com.urunov.telgbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Car {

    @JsonProperty(value = "type")
    private String catType;

    @JsonProperty(value = "freeSeats")
    private int freeSeats;

    @JsonProperty(value = "tariff")
    private int minimalPrice;
}
