package com.central.common.config;

import com.central.common.resolver.ClientArgumentResolver;
import com.central.common.resolver.TokenArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 默认SpringMVC拦截器
 */
public class DefaultWebMvcConfig implements WebMvcConfigurer {
//	@Lazy
//	@Autowired
//	private UserService userService;

	/**
	 * Token参数解析
	 *
	 * @param argumentResolvers 解析类
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		//注入用户信息
		argumentResolvers.add(new TokenArgumentResolver()); // TokenArgumentResolver(userService));
		//注入应用信息
		argumentResolvers.add(new ClientArgumentResolver());
	}
}
