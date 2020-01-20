package person.project.shiro;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class AuthRealm extends AuthorizingRealm {

	// 授權
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 1.獲取登陸用戶訊息
		Object principal = principals.getPrimaryPrincipal();

		// 2.獲取登陸用戶角色
		Set<String> roles = new HashSet<String>();
		if ("admin".equals(principal)) {
			roles.add("user");
			roles.add("admin");
		}
		if ("user".equals(principal)) {
			roles.add("user");
		}

		return new SimpleAuthorizationInfo(roles);
	}

	// 認證
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 1.把AuthenticationToken轉換為UsernamePasswordToken
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;

		// 2.提取username
		String username = upToken.getUsername();

		// 3.從數據庫查詢username,password
		// ...

		// 4.若用戶不存在,則可以拋出異常
		if ("unknown".equals(username)) {
			throw new UnknownAccountException("账户不存在!");
		}

		// 5.依據用戶狀態決定拋出其他異常
		if ("monster".equals(username)) {
			throw new LockedAccountException("账户被鎖定!");
		}

		// 6.將資料庫密碼回傳給Shiro比對 "123"應來自資料庫或其他
		// 1). principal : 認證訊息,可以是username 也可以是數據表對應欄位
		Object principal = username;
		// 2). credentials : 認證密碼 ,由數據庫取得
//				Object credentials = "123";
		// 2.1) 模擬已加密的密碼
		String algorithmName = "MD5";
		String source = "123";
		// 鹽質加密
		Object salt = null;
		// 可用唯一值增加鹽值
//				Object salt = ByteSource.Util.bytes(username); 
		int hashIterations = 1024;
		SimpleHash credentials = new SimpleHash(algorithmName, source, salt, hashIterations);
		// 3). realmName : 當前realmName name
		String realmName = getName();
		return new SimpleAuthenticationInfo(username, credentials, realmName);

	}

}
