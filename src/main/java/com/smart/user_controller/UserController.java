package com.smart.user_controller;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;
import com.smart.uploadFile.UploadFile;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UploadFile uploadFile;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

//	Method for adding common data to response

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String name = principal.getName();

//		System.out.println("user name => "+name);

//		Get the user by using username(String name)	

		User user = this.userRepository.findByName(name).orElse(null);
//		System.out.println("this is all vlaue => "+user);
		model.addAttribute("user", user);
	}

//	this is a Dashbord handler

	@GetMapping("/index")
	public String dashbord(Model model) {
		model.addAttribute("title", "User Dashbord page");
		return "normal_user/dashbord";
	}

//	this is add contact handler
	
	@GetMapping("/add-contact")
	public String addUserCotactForm(Model model) {
		model.addAttribute("title", "Add Contact Page");
		model.addAttribute("contact", new Contact());
		return "normal_user/add_contact_form";
	}

//	this is process-contact handler 

	@PostMapping("/process_contact")
	public String addContactForm(@ModelAttribute Contact contact,
			@RequestParam String image,
			Principal principal,
			HttpSession session) {		
		
		try {			
//			if (request.hasErrors()) {
//				System.out.println(request);
//				return "normal_user/add_contact_form";
//			}
			
			if (image.isEmpty()) {
				image = "download_photo.png";				
			}
			
			String name = principal.getName();
//		    System.out.println("User name : "+name);
			User user = this.userRepository.findByName(name).orElse(null);
			contact.setImage(image);
			this.uploadFile.uploadProfile(image);
			contact.setUser(user);
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Successfull add contact !! Added more !!", "alert-success"));
			return "normal_user/add_contact_form";
			
		} catch (Exception e) {
			session.setAttribute("massage", new Message("Some thing went wrong !! try again !!", "alert-success"));
			return "normal_user/add_contact_form";
		}	
	}

	
//	This is View-Contacts Handler to user
	
	@GetMapping("/view_contact/{page}")
	public String addContactForm(@PathVariable Integer page , Model model , Principal principal) {		
		model.addAttribute("title" , "View Contact Page");
		String name = principal.getName();
		User user = this.userRepository.findByName(name).orElse(null);	
//		Current Page - page
//		Contact per - page
		Pageable pageable = PageRequest.of(page, 3);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
		
//		System.out.println(contacts);
		model.addAttribute("contacts" , contacts);
		model.addAttribute("currentPage" , page);
		model.addAttribute("totalPages" , contacts.getTotalPages());
		return "normal_user/view_contact";
	}	
	
//	This is Contact Details Handler
	
	@GetMapping("/contact_detail/{cid}")
	public String contactDetail(@PathVariable Integer cid, Model model,Principal principal) {
	    model.addAttribute("title", "Contact Detail");

	    Contact contact = this.contactRepository.findById(cid)
	                         .orElseThrow(() -> new RuntimeException("Contact with ID " + cid + " not found!"));
	    System.out.println(contact.getName() + " "+ contact.getEmail());
	    
	    String userName = principal.getName();
	    User user = this.userRepository.findByName(userName).orElse(null);
	    if (user.getId() == contact.getUser().getId()) {
	    	model.addAttribute("c", contact);			
		}
	    return "normal_user/contact_detail";
	}

	
//	This is Delete Contact Handler
	
	@GetMapping("/delete_contact/{cId}")
	public String deleteContact(@PathVariable Integer cId,HttpSession session , Principal principal) {
//		Get User
		String name = principal.getName();
		User user = this.userRepository.findByName(name).orElse(null);
//	    Get Contact
		Optional<Contact> contactObject = this.contactRepository.findById(cId);
		Contact contact = contactObject.get();
//		Bug Security
		if (user.getId()==contact.getUser().getId()) {
			contact.setUser(null);
//			Delete file the upload directory
			this.uploadFile.deleteProfile(contact.getImage());
//			Delete contact to the user
			this.contactRepository.delete(contact);	
			session.setAttribute("message", new Message("Contact Deleted Successfully....", "alert-success"));			
		}
		return "redirect:/user/view_contact/0"; // Standard Spring MVC redirect
	}
	
	
//	This is showing  Update Contact Handler page
	
	@GetMapping("/update_contact/{cId}")
	public String updateContact(@PathVariable Integer cId , Model model,Principal principal) {
		model.addAttribute("title" , "Contact Your Update");
//		Get User when do not delete any contact
	    String name = principal.getName();
	    User user = this.userRepository.findByName(name).get();
//		Get Contact and send this contact to update_contact page
		Contact contact = this.contactRepository.findById(cId).get();
		if (user.getId()==contact.getUser().getId()) {			
			model.addAttribute("c" , contact);
		}
		return "normal_user/update_contact";
	}
	
//	This is Update Processing
	@PostMapping("/update_processing/{cId}")
	public String updateProcessing(@PathVariable Integer cId, @ModelAttribute Contact contact, Model model, Principal principal,@RequestParam String image) {

	    // Get the authenticated user
	    String name = principal.getName();
	    User user = this.userRepository.findByName(name)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    // Fetch the existing Contact entity from the database
	    Contact existingContact = this.contactRepository.findById(cId)
	            .orElseThrow(() -> new RuntimeException("Contact not found with id: " + cId));
	    
	    // Update fields of the existing managed Contact entity
	    existingContact.setName(contact.getName());
	    existingContact.setSecondName(contact.getSecondName());
	    existingContact.setWork(contact.getWork());
	    existingContact.setEmail(contact.getEmail());	    
//	     file is empty Same image update but you are choose file to change your image
	    
//	    Get the Old Image
	    
	    String oldImage = existingContact.getImage();
	    if (image.isEmpty() || image == null) {
//	    	old image upload
	    	existingContact.setImage(oldImage);
	    	this.uploadFile.uploadProfile(oldImage);

		}else {
//			new image upload And Old Image remove
			String newImage = contact.getImage();
			existingContact.setImage(newImage);
			this.uploadFile.uploadProfile(newImage);
			this.uploadFile.deleteProfile(oldImage);
			
		}
	    existingContact.setPhone(contact.getPhone());
	    existingContact.setDiscription(contact.getDiscription());
 
//	    Get Old Contact 	    
	    
	    // Reassociate the user with the contact
	    existingContact.setUser(user);
        
//      Save the updated contact
	    this.contactRepository.save(existingContact);

//	    System.out.println("Updated Contact: " + updatedContact);

	    return "redirect:/user/contact_detail/"+cId; // Replace with your appropriate return URL
	}

	
//	This is Your Profile Handler  
	
	@GetMapping("/your_profile")
	public String showYourProfile(Model model){
		model.addAttribute("title" , "Your Profile");
		return "normal_user/your_profile";
	}
	
	
//	This is Delete User Handler
	
	@GetMapping("/delete_user")
	public String deleteUser(Principal principal, HttpSession session) {
		String name = principal.getName();
		User user = this.userRepository.findByName(name).get();
		
		String image = user.getImage();
		if (image == null || image.isEmpty()) {
			return null;
		} else {
			this.uploadFile.deleteProfile(image);
		}
//		System.out.println(user);
		this.userRepository.delete(user);
		session.setAttribute("message", new Message("This project has been deleted. Please signup in to continue.!!", "alert-warning"));
		return "redirect:/signin";
	}

	
//	This is Update User Handler page
	
	@GetMapping("/update_user")
	public String updateUser(Model model) {
		model.addAttribute("title" , "Update User Page");
		return "normal_user/update_user";
	}
	
//	This is Update User Content Handler
	
    @PostMapping("/update_contect")
	public String updateContent(@ModelAttribute User user , @RequestParam String image , HttpSession session) {
	
    	String oldImage = user.getImage();
    	System.out.println("old image is "+oldImage);
    	
    	if (image == null || image.isEmpty()) {
			this.uploadFile.uploadProfile(oldImage);
		}else {
			this.uploadFile.uploadProfile(image);
			this.uploadFile.deleteProfile(oldImage);
		}
    	
		this.userRepository.save(user);
		
		session.setAttribute("message", new Message("Your update was completed successfully!! /n🔐 Please sign in to continue.", "alert-success"));
		
		return "redirect:/signin";
	}
	
}
