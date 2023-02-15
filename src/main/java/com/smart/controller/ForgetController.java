package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

@Controller
public class ForgetController {
	Random random=new Random(10000);
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		
		return "normal/forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email")String email,HttpSession session)
	{
		//Generating OTP of 4 digits
		

		int otp=random.nextInt(999999);
		System.out.println("OTP :"+otp);
		// code to send OTP to email
		String subject="OTP From smart contact manager";
		//String message="OTP ="+otp;
		String message="<div style='border:1px solid #e2e2e2; padding:20px'>"
			+"<h1>"
				+"OTP is "
			+"<b>"
				+otp+"</b></h1></div>";
		
		String to=email;
				
		boolean flag=this.emailService.sendEmail(subject,message,to);
	   
		if(flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			session.setAttribute("message", "We have send otp to your email");
			return "normal/verify-otp";
			
		}
		else {
			session.setAttribute("message", "Check your email address !!");
			return "normal/forgot_email_form";
		}
	}
	
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp")Integer otp,HttpSession session)
	{
		int myOTP=(int)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		System.out.println(myOTP+","+otp);
		if(myOTP==otp)
		{
		User user=this.userRepository.getUserByUserName(email);	
		if(user==null)
		{
			//send error message
			session.setAttribute("message", "User does not exist with this email !!");
			return "normal/forgot_email_form";
			
		}
		else
		{
			//send change password form
			return "normal/password_change_form";
			
		}
			
		}
		else
		{
		session.setAttribute("message","You have entered a wrong OTP");
		return "normal/verify-otp";	
		}
		
		
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword")String newPassword,HttpSession session)
	{
	String email=(String)session.getAttribute("email");
	User user=this.userRepository.getUserByUserName(email);
	user.setPassword(this.bcryptPasswordEncoder.encode(newPassword));
	this.userRepository.save(user);
	return "redirect:/signin?change='password changed successfully...'";
		
	}
	
}
