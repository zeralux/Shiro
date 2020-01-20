package person.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@Controller
public class UserController {
	@RequestMapping(value="/user", method = RequestMethod.GET)
	public String getUser() {
		return "user";
	}
	
	@RequestMapping(value="/admin", method = RequestMethod.GET)
	public String getAdmin() {
		return "admin";
	}
}
