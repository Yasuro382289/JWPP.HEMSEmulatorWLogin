package com.example.app.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.app.domain.ACState;
import com.example.app.domain.HomeEquip;
import com.example.app.domain.RoomTemp;
import com.example.app.domain.Season;
import com.example.app.domain.SmartMeter;
import com.example.app.service.EquipService;
import com.example.app.service.VirtualDateTimeService;

@Controller
@EnableScheduling

public class HemsController {
	
	@Autowired
	private EquipService equipService;
	
	@Autowired
	private VirtualDateTimeService virtualDateTimeService;
	
	private HomeEquip homeEquip;
	
	private String time;
	
	@Scheduled(fixedRate = 2000)
    public void updateHEMS() {
		Date d = virtualDateTimeService.virtualDateTimeUpdate();
		SimpleDateFormat fmt = new SimpleDateFormat("y/MM/d HH:mm");
        time = fmt.format(d);
        // System.out.println("Virtual Time = "+time);
        
        // 各機器の状態をupdateしてEquipment Equipにセット
        // この時各機器の動作状態による室温更新・消費電力更新を実施
        homeEquip = equipService.allEquipStateGet(equipService.homeEquipGet());
		
     	// ダッシュボード表示へのデータ渡しはHTML側でのポーリングで実施        
        
    }
	
	@RequestMapping("/start_page")
	public String showOpenGet(Model model) {
		// プログラム起動で、企画説明のページを表示
		// 企画説明のページからダッシュボードを表示
		
		return "projectPlan";		
	}
	
	@RequestMapping("/dash")  
    public String showHEMSDashboard(Model model) {
		// HTML側からのポーリングにより呼ばれるメソッド
		// サーバー側の最新データをHTMLに渡す
		HomeEquip homeEquip;
		SmartMeter smartMeter;
		RoomTemp roomTemp;		
		List<ACState> airCondList;
		ACState airCond0;
		ACState airCond1;
		ACState airCond2;
		Integer sea;
		
		homeEquip = equipService.homeEquipGet();
		smartMeter = homeEquip.getSmartMeter();
		roomTemp = homeEquip.getRoomTemp();
		airCondList = homeEquip.getAirConds();
		airCond0 = airCondList.get(0);
		airCond1 = airCondList.get(1);
		airCond2 = airCondList.get(2);
		sea = homeEquip.getSeasonVal();
		
		
        model.addAttribute("time", time);
        model.addAttribute("season", sea);
        // 宅内機器全体の状態をModel modelにセット
        model.addAttribute("SmartMeter", smartMeter);
        model.addAttribute("RoomTemp", roomTemp);
        model.addAttribute("AC0", airCond0);
        model.addAttribute("AC1", airCond1);
        model.addAttribute("AC2", airCond2);
        
        return "showDashboard";
    }
	
	@GetMapping("/airconditioner_set/{id}")
	public String airConditionerStateChangeGet(
			@PathVariable("id") Integer id,			
			Model model) {
		
		List<ACState> airConds;
		ACState targetAC, formACState;
    	
		// 指定されたエアコンへの参照を取得
		homeEquip = equipService.homeEquipGet();
		airConds = homeEquip.getAirConds();
		targetAC = airConds.get(id);
		
		//thymeleafのformを使う準備
		formACState = new ACState();
		
		formACState.setId(id);
		formACState.setOperationMode (targetAC.getOperationMode());
		formACState.setOperationState (targetAC.getOperationState());
		formACState.setOperationTargetTemp (targetAC.getOperationTargetTemp());
		formACState.setTemp (targetAC.getTemp());
		
		 model.addAttribute("AirCondId", formACState.getId());
		 model.addAttribute("ACState", formACState);
		 		
        return "changeAirConState"; // エアコン状態変更画面 -> HTMLでのポーリング停止
    }
	
	@PostMapping("/airconditioner_set/{id}")
	public String airConditionerStateChangePost(
			@PathVariable("id") Integer id,
			@ModelAttribute("ACState") ACState returnedACState,
			Model model) {
		
		// エアコン状態変更画面での指定にあわせて状態を変更
		homeEquip = equipService.homeEquipGet();
		Integer opMode = returnedACState.getOperationMode();
		Integer opState = returnedACState.getOperationState();
		Double targetTemp = returnedACState.getOperationTargetTemp();
		homeEquip = equipService.airConditionerStateChange(id, homeEquip, opMode, opState, targetTemp);
    	
        return "redirect:/dash"; // HEMS dashboardを再表示
    }
	
	@GetMapping("/roomtemp_set")
	public String roomTempSetGet(Model model) {
    	
		RoomTemp roomTemp, formRoomTemp;
		
		// ユーザー設定の室温上下限への参照を取得
		homeEquip = equipService.homeEquipGet();
		roomTemp = homeEquip.getRoomTemp();
		
		//thymeleafのformを使う準備
		formRoomTemp = new RoomTemp();
		
		formRoomTemp.setMax(roomTemp.getMax());
		formRoomTemp.setMin(roomTemp.getMin());
		
		 model.addAttribute("RoomTemp", formRoomTemp);
		
		// 変更画面に渡す		
		
        return "changeRoomTemp"; // 室温変更画面
    }
	
	@PostMapping("/roomtemp_set")
	public String roomTempSetPost(
			@ModelAttribute("RoomTemp") RoomTemp returnedRoomTemp,
			Model model) {
		
		// 室温変更画面での指定に合わせて状態を変更
		
		homeEquip = equipService.roomTempChange(homeEquip, returnedRoomTemp.getMax(), returnedRoomTemp.getMin());
    	
		return "redirect:/dash"; // HEMS dashboardを再表示
    }
	
	@GetMapping("/season_set")
	public String seasonSetGet(Model model) {
    	
		Season season; 
		Integer seasonVal;
		
		// homeEquip内の季節情報をlocalのseasonクラスに代入
		homeEquip = equipService.homeEquipGet();
		seasonVal = homeEquip.getSeasonVal();
		
		//thymeleafのformを使う準備
		season = new Season();
		
		season.setSeasonVal(seasonVal);		
		
		model.addAttribute("season", season);
		
		// 変更画面に渡す		
		
        return "changeSeason"; // 季節変更画面
    }
	
	@PostMapping("/season_set")
	public String seasonSetPost(
			@ModelAttribute("season") Season season,
			Model model) {
		
		// 季節変更画面での指定に合わせて状態を変更
		
		homeEquip.setSeasonVal(season.getSeasonVal());
    	
		return "redirect:/dash"; // HEMS dashboardを再表示
    }
	
	// 以下説明資料表示用
	/*
	@GetMapping("/presen/intro")
	public String presenIntroGet(Model model) {
    	
		// プレゼン画面：プロジェクト企画に移る		
		
        return "projectPlan"; // プロジェクト企画画面
    }
	
	@PostMapping("/presen/intro")
	public String presenIntroPost(Model model) {
		
		return "redirect:/dash"; // HEMS dashboardを再表示
    }
    */
	
	@GetMapping("/presen/design")
	public String presenDesignGet(Model model) {
    	
		// プレゼン画面：設計説明に移る		
		
        return "projectDesign"; // 設計説明画面
    }
	
	@PostMapping("/presen/design")
	public String presenDesignPost(Model model) {
		
		return "redirect:/dash"; // HEMS dashboardを再表示
    }
	
	@GetMapping("/presen/screen")
	public String presenScreenGet(Model model) {
    	
		// プレゼン画面：画面遷移に移る		
		
        return "projectScreen"; //画面遷移説明画面
    }
	
	@PostMapping("/presen/screen")
	public String presenScreendPost(Model model) {
		
		return "redirect:/dash"; // HEMS dashboardを再表示
    }
	
	
	@GetMapping("/presen/dashboard")
	public String presenDashboardGet(Model model) {
    	
		// プレゼン画面：ダッシュボード説明に移る		
		
        return "projectDashboard"; //ダッシュボード説明画面
    }
	
	@PostMapping("/presen/dashboard")
	public String presenDashboardPost(Model model) {
		
		return "redirect:/dash"; // HEMS dashboardを再表示
    }
	
	@GetMapping("/presen/further")
	public String presenFurtherGet(Model model) {
    	
		// プレゼン画面：今後の課題に移る		
		
        return "projectFurther"; // 今後の課題画面
    }
	
	@PostMapping("/presen/further")
	public String presenFurtherPost(Model model) {
		
		return "redirect:/dash"; // HEMS dashboardを再表示
    }

}
