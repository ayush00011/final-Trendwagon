package com.cts.TrendWagon.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
	
	@JsonProperty("message")
    private String message;
	
	@JsonProperty("name")
    private String name;
	
	@JsonProperty("joiningDate")
    private LocalDate joiningDate;
	
	
}