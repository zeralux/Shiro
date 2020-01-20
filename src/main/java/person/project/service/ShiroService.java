package person.project.service;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

@Service
public class ShiroService implements IShiroService{
	
	//限制授權角色
	@RequiresRoles({"admin"})
	@Override
	public String testSession() {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		// 取得session不受controller限制
		return (String) session.getAttribute("testseesion");
	}
}
