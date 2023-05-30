package com.example.app.service;

public interface AdminService {
	
	boolean isCorrectIdAndPassword(String loginId, String loginPass) throws Exception;

}
