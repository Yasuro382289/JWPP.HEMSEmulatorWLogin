package com.example.app.mapper;

import com.example.app.domain.Admin;

public interface AdminMapper {
	
	Admin selectByLoginId(String loginId) throws Exception;

}
