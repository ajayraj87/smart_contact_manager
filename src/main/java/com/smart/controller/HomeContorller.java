package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repository.UserRepository;
import com.smart.uploadFile.UploadFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeContorller {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UploadFile uploadFile;
	
	@GetMapping("/")
	public String home(Model m) {
		m.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model m) {
		m.addAttribute("title", "Signup - Smart Contact Manager");
		m.addAttribute("user", new User());
		return "signup";
	}

	// Your registration logic here

	@PostMapping("/do_ragister")
	public String register(@Valid @ModelAttribute User user, BindingResult request, @RequestParam("image") String image,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {
			if (!agreement) {
				throw new Exception("You have not agreed term and condition");
			}

			if (request.hasErrors()) {
				return "signup";
			}

			user.setPassword(this.encoder.encode(user.getPassword()));
			user.setRole("USER");
			user.setEnebled(true);
			
//			Get image but new image upload and old image
			
			if (image == null || image.isEmpty()) {
				user.setImage("default.png");				
			}else {
				user.setImage(image);
				this.uploadFile.uploadProfile(image);
			}
			

			User result = this.userRepository.save(user);
			model.addAttribute("user", result);
			System.out.println(agreement);
			System.out.println(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Ragister !!", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Sumething went wrong !! " + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}

//	Your sing up here 

	@GetMapping("/signin")
	public String signin(Model m) {
		m.addAttribute("title", "Signin - Smart Contact Manager");
		return "signin";
	}
}
