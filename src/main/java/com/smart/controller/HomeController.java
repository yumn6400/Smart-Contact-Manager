package com.smart.controller;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
  
	@Autowired
	private UserRepository userRepository;
	@GetMapping("/")
	public String home(Model model)
	{	
		model.addAttribute("title","My Smart Contact Manager Application");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model)
	{	
		model.addAttribute("about","about-My Smart Contact Manager Application");
		return "about";
	}
	@GetMapping("/signup")
	public String signup(Model model)
	{	
		model.addAttribute("title","Register-My Smart Contact Manager Application");
		model.addAttribute("user",new User());
		
		return "signup";
	}
	@GetMapping("/login")
	public String login(Model model)
	{	
		model.addAttribute("title","Register-My Smart Contact Manager Application");
		return "login";
	}
	
	
	
	// handler for register user
	@RequestMapping(value="/do_register",method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user")User user,BindingResult result,@RequestParam(value="agreed",defaultValue="false")boolean agreed,Model model,HttpSession session)
	{
		System.out.println(user.isAgreed());
		if(result.hasErrors())
		{
			return "signup";
			//model.addAttribute("user",user);
			
		}
		try
		{
			if(!agreed)
			{
				
				throw new Exception("You have not agreed the terms and conditions");
			}
			user.setRole("ROLE_USER");
			user.setAgreed(agreed);
			user.setImageUrl("banner.jpg");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println(user);
			User res=userRepository.save(user);
			System.out.println("User"+res);
			model.addAttribute("user",new User());
			session.setAttribute("message",new Message("Successfully registered","alert-success"));
		}catch(Exception exception)
		{
			System.out.println(exception);
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong :"+exception.getMessage(),"alert-error"));
		}
		
		return "signup";
	}

   // handler for custom login
	
	@GetMapping("/signin")
	public String customLogin(Model model)
	{
	model.addAttribute("title","Login Page");
	return "login";
	}
	






}
