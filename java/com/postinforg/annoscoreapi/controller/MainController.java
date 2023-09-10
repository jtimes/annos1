package com.postinforg.annoscoreapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController extends CommonController {

	@GetMapping("/index")
	public String index() throws Exception {
		return "home";
	}

}
