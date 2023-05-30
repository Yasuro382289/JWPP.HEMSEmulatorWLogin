package com.example.app.domain;

import java.util.List;

import lombok.Data;

@Data
public class HomeEquip {
	
	// スマートメーター
	private SmartMeter smartMeter;	
	
	// 室温上限・下限
	private RoomTemp roomTemp;	

	// エアコンのリスト
	private List<ACState> airConds;
	
	// 季節
	private Integer seasonVal; // 1 = 夏, -1 = 冬
}
