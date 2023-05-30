package com.example.app.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.example.app.domain.VirtualDateTime;

@Service
public class VirtualDateTimeServiceImpl implements VirtualDateTimeService {
	
	VirtualDateTime vDT = new VirtualDateTime();	
	
	public VirtualDateTimeServiceImpl() { // コンストラクタ	
		
		// シュミレーション開始日時; アプリ起動日時をセット
		vDT.setStartDateTime(new Date());
		
		// シュミレートサイクル数初期化
		vDT.setSimulateCycle(0);
		
		// 現時点の仮想時刻はシミュレーション開始日時と同じ
		vDT.setCurrentDateTime(vDT.getStartDateTime());		
	}
	
	// シミュレートサイクル数をインクリメントし、それから仮想時刻を計算、Date型で渡す
	// 1回のシュミレートサイクルで1分時刻が進むものとする
	public Date virtualDateTimeUpdate() {
		
		// シミュレートサイクル更新
		int i=vDT.getSimulateCycle();
		i++;
		vDT.setSimulateCycle(i);
		// System.out.println("Simulate Cycle ="+vDT.getSimulateCycle());
		
		// 開始時刻＋"i"分の時刻計算
		Calendar c = Calendar.getInstance();
		c.setTime(vDT.getStartDateTime());
		c.add(Calendar.MINUTE, i);
		
		// 現時点の仮想時刻をセット
		vDT.setCurrentDateTime(c.getTime());
		
		Date d=vDT.getCurrentDateTime();
		
		return d;
		
	}
			
	// 現在の仮想時刻を(更新せずに)返す
	public Date virtualDateTimeCurrent() {
		Date d = vDT.getCurrentDateTime();
		return d;
	}

}
