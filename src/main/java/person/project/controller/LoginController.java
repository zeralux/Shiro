package person.project.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import person.project.service.IShiroService;

@RestController
public class LoginController {
	
	@Autowired
	IShiroService iShiroService;
	
	@GetMapping("/doLogin")
	public String doLogin(String username, String password) {
		// 取得當前用戶
		Subject user = SecurityUtils.getSubject();
		// 測試是否認證過
		if (!user.isAuthenticated()) {
			// 將登陸帳密封裝為UsernamePasswordToken對象
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			// 記憶功能
			token.setRememberMe(true);
			try {
				// 交由shiro登入 呼叫自定義realm進行認證
				user.login(token);
			// 所有登錄異常的父類 , 尚有許多分支	
			} catch (AuthenticationException e) {
				return "登录失败! error="+e;
			}
		}
		
		// 測試session
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		session.setAttribute("testseesion","hello admin");
		
		return "登录成功!";
	}

	@GetMapping("/hello")
	public String hello() {
		// 測試session
		String msg = iShiroService.testSession();
		System.out.println(msg);
		
		return "hello";
	}

	@GetMapping("/login")
	public String login() {
		return "please login!";
	}
	
	@GetMapping("/unauthorizedurl")
	public String unauthorizedurl() {
		return "unauthorizedurl!";
	}
	
	@GetMapping("/notRememberMe")
	public String notRememberMe() {
		return "notRememberMe";
	}
	
	@GetMapping("/rememberMe")
	public String rememberMe() {
		return "rememberMe";
	}
	
	
}
