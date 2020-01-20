package person.project.shiro;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AllSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfiguration {
	
	
	// 第一個realm
	@Bean
	AuthRealm authRealm() {
		AuthRealm authRealm = new AuthRealm();
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		// MD5加密
		hashedCredentialsMatcher.setHashAlgorithmName("MD5");
		// 加密次數
		hashedCredentialsMatcher.setHashIterations(1024);
		// 前台密碼加密
		authRealm.setCredentialsMatcher(hashedCredentialsMatcher);
		// 註冊/啟用緩存
		authRealm.setCacheManager(cacheManager());
		
		return authRealm;
	}
	
	// 第二個realm
	@Bean
	GroupRealm groupRealm() {
		GroupRealm groupRealm = new GroupRealm();
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		// MD5加密
		hashedCredentialsMatcher.setHashAlgorithmName("MD5");
		// 加密次數
		hashedCredentialsMatcher.setHashIterations(1024);
		// 前台密碼加密
		groupRealm.setCredentialsMatcher(hashedCredentialsMatcher);

		// 註冊/啟用緩存
		groupRealm.setCacheManager(cacheManager());
		
		return groupRealm;
	}
	
	//  處理多個realm
	@Bean
	ModularRealmAuthenticator modularRealmAuthenticator() {
		ModularRealmAuthenticator modularRealmAuthenticator = new ModularRealmAuthenticator();
		List realms = new ArrayList();
		realms.add(authRealm());
		realms.add(groupRealm());
		modularRealmAuthenticator.setRealms(realms);
		
		// 切換認證策略
		AllSuccessfulStrategy allSuccessfulStrategy = new AllSuccessfulStrategy();
		modularRealmAuthenticator.setAuthenticationStrategy(allSuccessfulStrategy);
		
		return modularRealmAuthenticator;
	}
	
	//  處理多個realm
	@Bean
	ModularRealmAuthorizer modularRealmAuthorizer() {
		ModularRealmAuthorizer modularRealmAuthorizer = new ModularRealmAuthorizer();
		List realms = new ArrayList();
		realms.add(authRealm());
		realms.add(groupRealm());
		modularRealmAuthorizer.setRealms(realms);
		return modularRealmAuthorizer;
	}
	
	// 緩存器
	@Bean
	public CacheManager cacheManager() {
		//JVM緩存
	    return new MemoryConstrainedCacheManager();
	}
	
	
	@Bean
	org.apache.shiro.mgt.SessionsSecurityManager securityManager() {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		// 註冊驗證器
		manager.setAuthenticator(modularRealmAuthenticator());
//		manager.setRealm(authRealm());
		
		// 註冊授權器
		manager.setAuthorizer(modularRealmAuthorizer());
		
		// 註冊緩存器
		manager.setCacheManager(cacheManager());
		
		// 設定remember me
		CookieRememberMeManager cookieRememberMeManager = (CookieRememberMeManager) manager.getRememberMeManager();
		// 設定AES密鑰
		cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
		// 設定max age
		SimpleCookie cookie = (SimpleCookie) cookieRememberMeManager.getCookie();
		cookie.setMaxAge(10); // 10 秒
		cookieRememberMeManager.setCookie(cookie);
		manager.setRememberMeManager(cookieRememberMeManager);
		
		return manager;
	}

	
	@Bean
	ShiroFilterFactoryBean shiroFilterFactoryBean() {
		ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
		bean.setSecurityManager(securityManager());
		bean.setLoginUrl("/login");
		bean.setSuccessUrl("/index");
		bean.setUnauthorizedUrl("/unauthorizedurl");
		Map<String, String> map = new LinkedHashMap<>();
		// 可匿名訪問
		map.put("/doLogin", "anon");
		// 登出
		map.put("/doLogout", "logout");
		// 角色user可訪問/user
		map.put("/user", "roles[user]");
		// 角色admin可訪問/admin
		map.put("/admin", "roles[admin]");
		
		// not remember me 訪問
		map.put("/rememberMe", "user");
		// remember me 訪問
		map.put("/notRememberMe", "authc");
		
		map.put("/**", "authc");
		bean.setFilterChainDefinitionMap(map);
		return bean;
	}

}
