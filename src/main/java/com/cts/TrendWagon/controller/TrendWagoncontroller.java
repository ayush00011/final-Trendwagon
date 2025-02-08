package com.cts.TrendWagon.controller;
import org.springframework.web.bind.annotation.*;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping
public class TrendWagoncontroller {
	
	@GetMapping("/")
	public String showHome() {
		return "index";
	}
	
	@GetMapping("/account")
	public String showaccount() {
		return "account";
	}
	
	@GetMapping("/admin_signin")
	public String showAdmin() {
		return "admin_signin";
	}
	@GetMapping("/cart")
	public String showCart() {
		return "cart";
	}
	@GetMapping("/signin")
	public String showSignin() {
		return "signin";
	}
	@GetMapping("/settings")
	public String showSettings() {
		return "settings";
	}
	
	@GetMapping("/signup")
	public String showSignup() {
		return "signup";
	}
	@GetMapping("/admin_dashboard")
	public String showAdminDashboard() {
		return "admin_dashboard";
	}	
	@GetMapping("/orders")
	public String showOrders() {
		return "orders";
	}	
	
}



