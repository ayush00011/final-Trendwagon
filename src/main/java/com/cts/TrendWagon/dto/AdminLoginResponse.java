package com.cts.TrendWagon.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginResponse {
	@JsonProperty("message")
    private String message;
	
	@JsonProperty("name")
    private String name;

}