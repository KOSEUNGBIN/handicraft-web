package com.handicraft.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ModelController {

	@RequestMapping("/model")
	public String getModel()
	{
		return "model";
	}
}