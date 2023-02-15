package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import java.util.Optional;

import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
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
	public String showContactDetail(@PathVariable("cId")Integer cId,Model model,Principal principal)
	{
		System.out.println("CID"+cId);
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		Contact contact=contactOptional.get();
		//security checking
		String username=principal.getName();
		User user=this.userRepository.getUserByUserName(username);
		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("contact",contact);
			model.addAttribute("title",contact.getName());
		}
	
		
		
		return "normal/contact_details";
		
	}
	//delete contact handler
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId")Integer cId,Model model,Principal principal,HttpSession session)
	{
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		Contact contact=contactOptional.get();
		
		//security check for delete
		User user=this.userRepository.getUserByUserName(principal.getName());
		
			//delete not performed due to cascadeType
			
			//remove photo
		   
			
			//this.contactRepository.delete(contact);
			user.getContacts().remove(contact);
			
		    System.out.println("Contact deleted successfully");
		    try
			   {
				File deleteFile=new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile,contact.getImage());
				file1.delete();
			   }catch(Exception e)
			   {
				   System.out.println(e.getMessage());
			   }
		    this.userRepository.save(user);
		    
		    
			session.setAttribute("message", new Message("contact deleted successfully...","success"));
		
		
		return "redirect:/user/show-contacts/0";
	}
	
	// edit contact controller
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId")Integer cId,Model model)
	{
		model.addAttribute("title","Update Contact");
		Contact contact=this.contactRepository.findById(cId).get();
		model.addAttribute("contact",contact);
		
		
		return "normal/update_form";
	}
	
	// update contact handler
	@RequestMapping(value="/process-update" ,method=RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file,Model model,HttpSession session,Principal principal)
	{
		try
		{
			//old contact details
			Contact oldContactDetails=this.contactRepository.findById(contact.getcId()).get();
			if(!file.isEmpty())
			{
			
				//file work...
				//rewrite
				//delete old photo and add new photo
				File deleteFile=new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile,oldContactDetails.getImage());
				file1.delete();
				
				File f=new ClassPathResource("static/img").getFile();
				Path path=Paths.get(f.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
				contact.setImage(file.getOriginalFilename());	
			}
			else
			{
				if(oldContactDetails.getImage()==null)
				{
					contact.setImage("contact.jpg");
				}
				else
				{
					contact.setImage(oldContactDetails.getImage());					
				}		
			}
			User user=this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message",new Message("Your contact is updated","success"));
		}catch(Exception e)
		{
			contact.setImage("contact.jpg");
			System.out.println(e.getMessage());
		}
		System.out.println(contact.getName());
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	
	// profile handler
	@GetMapping("/profile")
	public String profile(Model model,Principal principal)
	{
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		model.addAttribute("title","Profile Page");
		model.addAttribute("user",user);
	return "normal/profile";	
	}

	
	//open setting handler
	@GetMapping("/settings")
	public String openSettings()
	{
	return "normal/settings";	
	
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword")String oldPassword,@RequestParam("newPassword")String newPassword,Principal principal,HttpSession session)
	{
	   System.out.println("oldPassword"+oldPassword);
	   System.out.println("newPassword"+newPassword);
	   
	  String username= principal.getName();
	  User user=this.userRepository.getUserByUserName(username);
	  if(bCryptPasswordEncoder.matches(oldPassword, user.getPassword()))
	  {
		  //change the password
		  user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		  this.userRepository.save(user);
		  session.setAttribute("message", new Message("Your password successfully changed","success"));
      }
	  else
	  {
		  //error
		  session.setAttribute("message",new Message("Enter correct old password", "danger"));
		  return "redirect:/user/settings";
	  }
			 
	   
	   
	   
	   return "redirect:/user/index";
	}
	
	
}
