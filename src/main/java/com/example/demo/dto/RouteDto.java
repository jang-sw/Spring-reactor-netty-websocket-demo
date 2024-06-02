package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RouteDto {
	String room;
	String method;
	String data;
}
