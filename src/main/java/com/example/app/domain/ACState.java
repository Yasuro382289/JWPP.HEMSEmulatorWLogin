package com.example.app.domain;

import lombok.Data;

@Data
public class ACState {	
	
	private Integer id; // エアコン番号

	private Integer operationMode; // 0: 冷房 1: 暖房; その他のモードは今後の課題
	
	private Integer operationState; // 0: 停止中 1: 送風中 2: 動作中
	
	private double operationTargetTemp; // 目標温度の設定
	
	private double temp; // エアコン測定の室温
	
	public ACState() {
		// 空のコンストラクタ
	}
	
	public ACState(Integer id, Integer opMode, Integer opState, double targetTemp, double roomTemp) {
		this.id = id;
		this.operationMode = opMode;
		this.operationState = opState;
		this.operationTargetTemp = targetTemp;
		this.temp = roomTemp;				
	}	

}
