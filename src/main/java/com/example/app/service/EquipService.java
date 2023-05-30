package com.example.app.service;

import com.example.app.domain.HomeEquip;

public interface EquipService {
	
	// 呼ばれた時点の宅内状態を返す
	public HomeEquip homeEquipGet();
	
	// 宅内の全ての機器の状態を取得、下のエアコン状態取得と電力消費状態取得のメソッドを順次呼び出す
	public HomeEquip allEquipStateGet(HomeEquip homeEquip);
	
	// idで指定されたエアコンの状態を取得, 
	// この時各機器への問い合わせの模擬として
	// あわせて指定エアコンの動作状態による室温変化を模擬する
	public HomeEquip airConditionerStateGet(Integer id, HomeEquip homeEquip);
    
	// idで指定されたエアコンの動作状態を引数のものに変更
	public HomeEquip airConditionerStateChange(Integer id, HomeEquip homeEquip, Integer opMode, Integer opState, Double targetTemp);
    
	// 電力消費の状態を取得、
	// この時各機器への問い合わせの模擬として
	// 合わせてエアコンの動作状態による電力消費量変化を模擬する
	public HomeEquip smartMeterStateGet(HomeEquip homeEquip);
	
	// 室温の上限・下限設定を引数のものに変更
	public HomeEquip roomTempChange(HomeEquip homeEquip, Double max, Double min);
    
}
