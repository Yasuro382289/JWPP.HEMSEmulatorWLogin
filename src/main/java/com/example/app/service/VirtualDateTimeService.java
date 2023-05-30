package com.example.app.service;

import java.util.Date;

public interface VirtualDateTimeService {
	
	// 初期設定はVirtualDateTimeServiceImpl.javaのコンストラクタ；開始時刻はアプリ起動時
	
	// シミュレートサイクル数をインクリメントし、それから仮想時刻を計算、Date型で渡す
	// 1回のシュミレートサイクルで1分時刻が進むものとする
	public Date virtualDateTimeUpdate();
	
	// 現在の仮想時刻を(更新せずに)返す
	public Date virtualDateTimeCurrent();
	

}
