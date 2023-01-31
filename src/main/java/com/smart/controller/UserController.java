package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Autowired
	private ContactRepository contactRepository;
	
	// Method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model,Principal principal)
	{
		String username=principal.getName();
		System.out.println("Username :"+username);
		User user=userRepository.getUserByUserName(username);
		System.out.println("User :"+user);
		 model.addAttribute("user",user);
	}
	
	
	//dash board home
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
		return "normal/user_dashboard";
	}
	
	//open add form handler
	@GetMapping("/add_contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		
		
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file, Principal principal,HttpSession session)
	{
		try
		{
			
		String name=principal.getName();
		User user=this.userRepository.getUserByUserName(name);
		contact.setUser(user);
		user.getContacts().add(contact);
		//processing and uploading file .......
		
		if(file.isEmpty())
		{
			 // If file is empty then try our message
			System.out.println("File not available");
			contact.setImage("contact.jpg");
		}
		else
		{
			//file to folder and update name of contact
			contact.setImage(file.getOriginalFilename());
			File f=new ClassPathResource("static/img").getFile();
			Path path=Paths.get(f.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image is uploaded");
		}
		
		
		this.userRepository.save(user);
		System.out.println(contact);
		System.out.println("contact added");
		
		//message success ....
		session.setAttribute("message",new Message("Your contact is added !! Add more","success"));
		}catch(Exception e)
		{
			System.out.println("Exception"+e.getMessage());
			e.printStackTrace();
			//message error
			session.setAttribute("message",new Message("Something went wrong!! try again","danger"));
		}
		
		return "normal/add_contact_form";
	}
	
	//show contacts handler
	
	// Per page =5(n)
	//Current page=0(currentPage)
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,  Model model,Principal principal)
	{
		model.addAttribute("title","Show User contacts");
		//Contact List
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		Pageable pageable=PageRequest.of(page,5);
		Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(),pageable);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());
		model.addAttribute("contacts",contacts);
		return "normal/show_contacts";
	}
	
	// showing particular contact details
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId")Integer cId,Model model)
	{
		System.out.println("CID"+cId);
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		Contact contact=contactOptional.get();
		model.addAttribute("contact",contact);
		
		
		return "normal/contact_details";
		
	}
	

}
