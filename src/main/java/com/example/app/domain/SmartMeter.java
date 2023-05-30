package com.example.app.domain;

import lombok.Data;

@Data
public class SmartMeter {
	
	// 消費電力 (W)
	private Integer powerConsumption;
	// 積算消費電力量 (Wh)
	private Double energyConsumption;

}
