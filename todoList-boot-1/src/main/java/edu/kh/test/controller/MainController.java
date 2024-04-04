package edu.kh.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.test.model.dto.Member;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("member")
public class MainController {

	@PostMapping("select")
	public String selectStudent(HttpServletRequest req, @ModelAttribute Member member) { 

		req.setAttribute("memberName", member.getMemberName());

		req.setAttribute("memberAge", member.getMemberAge());

		req.setAttribute("memberAddress", member.getMemberAddress());

		
		return "member/select";
	}
	
}
