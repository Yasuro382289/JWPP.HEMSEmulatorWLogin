package com.example.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app.domain.ACState;
import com.example.app.domain.HomeEquip;
import com.example.app.domain.RoomTemp;
import com.example.app.domain.SmartMeter;

@Service
public class EquipServiceImpl implements EquipService {
	
	private HomeEquip homeEquip;
		
	public EquipServiceImpl () { // コンストラクタ 
		
		homeEquip = new HomeEquip();
		homeEquip.setSmartMeter(new SmartMeter());
		homeEquip.setRoomTemp(new RoomTemp());
		homeEquip.setAirConds(new ArrayList<>());
		
		homeEquip.getSmartMeter().setPowerConsumption(0); // (Integer) 消費電力 W
		homeEquip.getSmartMeter().setEnergyConsumption(0.0); // (Double) 積算消費電力量 Wh
		
		homeEquip.getRoomTemp().setMax(32.0); // (double) 室温上限 ℃
		homeEquip.getRoomTemp().setMin(18.0); // (double) 室温下限 ℃
		
		homeEquip.getAirConds().add(new ACState(0, 0, 1, 26.0, 30.0)); // エアコン1台目：冷房, 送風中, 設定温度26.0℃, 室温30.0℃
		homeEquip.getAirConds().add(new ACState(0, 0, 1, 27.0, 31.0)); // エアコン2台目：冷房, 送風中, 設定温度27.0℃, 室温31.0℃
		homeEquip.getAirConds().add(new ACState(0, 0, 1, 28.0, 32.0)); // エアコン3台目：冷房, 送風中, 設定温度28.0℃, 室温32.0℃
		
		homeEquip.setSeasonVal(1); // 季節の初期値は夏
		
	}
	
	@Override
	// 呼ばれた時点の宅内状態を返す
	public HomeEquip homeEquipGet() {
		
		return homeEquip;
	};

	@Override
	public HomeEquip allEquipStateGet(HomeEquip homeEquip) {

		// System.out.println("called allEquipStateGet");
		
		// 存在するエアコンの状態update; 
		homeEquip = airConditionerStateGet(0, homeEquip);
		homeEquip = airConditionerStateGet(1, homeEquip);
		homeEquip = airConditionerStateGet(2, homeEquip);
		
		// 宅内消費電力のupdate
		homeEquip = smartMeterStateGet(homeEquip);
		
		return homeEquip;
	}

	@Override
	public HomeEquip airConditionerStateGet(Integer id, HomeEquip homeEquip) {
		
		// System.out.println("called airConditionerStateGet with id="+id);
		
		// 実機に問い合わせを行う場合はここで実施
		
		// 指定されたエアコン動作による室温変化の模擬
		// それぞれのエアコンの動作状況による室温変化を計算し、homeEquipにセット
		List<ACState> airConds;
		ACState targetAC;
		RoomTemp tempMinMax;
		double tempTemp;
		Integer sea; // 季節, 夏=1, 冬=-1; エアコンが送風中/停止の時の室温変化に反映
		
		// エアコンへの参照を取得
		airConds = homeEquip.getAirConds();
		targetAC = airConds.get(id);
		// 室温上下限への参照を取得
		tempMinMax = homeEquip.getRoomTemp();
		// 季節を取得
		sea = homeEquip.getSeasonVal();
		
		// 冷房運転の時：運転中は1分に0.2℃室温が低下、送風・停止中は1分に0.1℃室温が、夏は上昇、冬は下降
		if(targetAC.getOperationMode()==0) { // 冷房運転
			if(targetAC.getOperationState()==2) { //動作中
				tempTemp = targetAC.getTemp();
				tempTemp -= 0.2;
				targetAC.setTemp(tempTemp);
			}
			else { // 送風・停止中
				tempTemp = targetAC.getTemp();
				tempTemp += 0.1 * sea; 
				targetAC.setTemp(tempTemp);
			}
			// 室温が設定目標温度＋2度になり、かつ送風中であれば、動作状態を動作中にする
			if(targetAC.getTemp()>targetAC.getOperationTargetTemp()+2.0 && targetAC.getOperationState() == 1) targetAC.setOperationState(2);
			// 室温が設定目標温度以下になったら動作状態を送風中にする
			if(targetAC.getTemp()<targetAC.getOperationTargetTemp()) targetAC.setOperationState(1);				
		} else if(targetAC.getOperationMode()==1) {
			// 暖房運転の時：運転中は1分に0.2℃室温が上昇、送風・停止中は1分に0.1℃室温が、夏は上昇、冬は下降
			if(targetAC.getOperationState()==2) { //動作中
				tempTemp = targetAC.getTemp();
				tempTemp += 0.2;
				targetAC.setTemp(tempTemp);
			}
			else { // 送風・停止中
				tempTemp = targetAC.getTemp();
				tempTemp += 0.1 * sea;
				targetAC.setTemp(tempTemp);
			}
			// 室温が設定目標温度-2度になり、かつ送風中であれば、動作状態を動作中にする
			if(targetAC.getTemp()<targetAC.getOperationTargetTemp()-2.0 && targetAC.getOperationState() == 1) targetAC.setOperationState(2);
			// 室温が設定目標温度以上になったら動作状態を送風中にする
			if(targetAC.getTemp()>targetAC.getOperationTargetTemp()) targetAC.setOperationState(1);				
		}
		
		// 停止状態が長く続き、室温が上限温度を超えたら、強制的に動作モードを冷房に、動作状態を動作中にする			
		if(tempMinMax.getMax() < targetAC.getTemp()) {
			targetAC.setOperationMode(0);
			targetAC.setOperationState(2);
		}
		// 停止状態が長く続き、室温が下限温度を下回ったら、強制的に動作モードを暖房に、動作状態を動作中にする
		if(tempMinMax.getMin() > targetAC.getTemp()) {
			targetAC.setOperationMode(1);
			targetAC.setOperationState(2);
		}
		
		
		return homeEquip;

	}

	@Override
	public HomeEquip airConditionerStateChange(Integer id, HomeEquip homeEquip, Integer opMode, Integer opState, Double targetTemp) {
		
		List<ACState> airConds;
		ACState targetAC;
		
		// System.out.println("called airConditionerStateChange with id="+id);
		
		// 実機に変更依頼を行う場合はここで実施		
		
		// 指定されたエアコンへの参照を取得
		airConds = homeEquip.getAirConds();
		targetAC = airConds.get(id);
		
		// 指定されたエアコンの動作モード、動作状態、設定室温を指定された値に変更
		targetAC.setOperationMode(opMode);
		targetAC.setOperationState(opState);
		targetAC.setOperationTargetTemp(targetTemp);		
		
		return homeEquip;
	}

	@Override
	public HomeEquip smartMeterStateGet(HomeEquip homeEquip) {
		
		// System.out.println("called smartMeterStateGet()");
		
		// 実機に問い合わせを行う場合はここで実施
		
		// エアコン3台の動作状況による消費電力・累積消費電力量を計算し、homeEquipにセット
		List<ACState> airConds;
		SmartMeter smartMeter;
		ACState targetAC;
		Integer tempPower;
		Double tempEnergy;

		// エアコンリスト、スマートメーターへの参照を取得
		airConds = homeEquip.getAirConds();
		smartMeter = homeEquip.getSmartMeter();
		
		smartMeter.setPowerConsumption(0);
		
		for (int i=0; i<3; i++) {
			// エアコンiへの参照を取得
			targetAC = airConds.get(i);
		
			// 指定されたエアコンの動作状態を確認、動作中であれば消費電力に1200Wを足しこむ
			// 1分で消費電力量20Wh、これも足しこむ
			if(targetAC.getOperationState()==2) { //動作中
				tempPower = smartMeter.getPowerConsumption();
				tempPower += 1200;
				smartMeter.setPowerConsumption(tempPower);
				tempEnergy = smartMeter.getEnergyConsumption();
				tempEnergy +=20.0;
				smartMeter.setEnergyConsumption(tempEnergy);
			} else if (targetAC.getOperationState()==1) { //送風中
				// 送風中の消費電力は12Wとする、1分間での消費電力量は0.2Wh、これも足しこむ
				tempPower = smartMeter.getPowerConsumption();
				tempPower += 12;
				smartMeter.setPowerConsumption(tempPower);
				tempEnergy = smartMeter.getEnergyConsumption();
				tempEnergy += 0.2;
				smartMeter.setEnergyConsumption(tempEnergy);
			}
			
		}
		
		return homeEquip;
		
	}
	
	@Override
	public HomeEquip roomTempChange(HomeEquip homeEquip, Double max, Double min) {
		
		// System.out.println("called roomTempChange()");
		
		// 室温の上限・下限設定を引数のものに変更
		
		RoomTemp roomTemp;
		
		roomTemp = homeEquip.getRoomTemp();
		
		roomTemp.setMax(max);
		roomTemp.setMin(min);
		
		return homeEquip;
	}

}
