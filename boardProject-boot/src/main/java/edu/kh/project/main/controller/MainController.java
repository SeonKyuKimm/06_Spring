package edu.kh.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MainController {

	@RequestMapping("/") //  " / " 요청 매핑 (method 가라지 않음)
	public String mainPage() {
		
		// 접두사 / 접미사 제외하고 
		return "common/main";
	}
	
	// LoginFilter가 동작하면서 -> loginError 쪽으로 Redirect
	// -> message 만들어서 메인페이지로 Redirect가 끗~
	@GetMapping("loginError")
	public String loginError(RedirectAttributes ra) {
		
		ra.addFlashAttribute("message", "로그인 후 이용해주세요 ");
		return "redirect:/";
		
	}
	
	
}
