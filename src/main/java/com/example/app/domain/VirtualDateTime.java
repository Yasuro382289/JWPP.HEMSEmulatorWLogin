package com.example.app.domain;

import java.util.Date;

import lombok.Data;

@Data
public class VirtualDateTime {
	
	// シュミレーション開始日時
	Date startDateTime;
	
	// 現時点のシュミレーションサイクル数
	int simulateCycle;
	
	// シュミレーション開始時刻とサイクル数から計算される現時点の仮想時刻
	Date currentDateTime;

}
