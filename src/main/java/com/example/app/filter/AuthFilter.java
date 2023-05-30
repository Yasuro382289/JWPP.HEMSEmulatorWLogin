package com.example.app.filter;

// Springbootの場合、eclipse現バージョンのfilterでは自動生成できない
// Java classとし、javax.servlet.Filterを継承、「継承された抽象メソッド」にチェックを入れることで、
// doFilterの定義は作れるが、chain等はは自動的に入らないので手入力

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		
		if (session.getAttribute("loginId") == null) {
			res.sendRedirect("/");
			return;
		}
		
		chain.doFilter(request, response);

	}

}
