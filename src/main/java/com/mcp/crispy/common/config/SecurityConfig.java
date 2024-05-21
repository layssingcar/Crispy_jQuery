package com.mcp.crispy.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;

	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new Argon2PasswordEncoder(16, 32, 4, 512, 5);
	}

	@Bean
	RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
		TokenBasedRememberMeServices.RememberMeTokenAlgorithm encodingAlgorithm = TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256;
		TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices("remember-me", userDetailsService, encodingAlgorithm);
		rememberMe.setTokenValiditySeconds(604800);
		rememberMe.setMatchingAlgorithm(TokenBasedRememberMeServices.RememberMeTokenAlgorithm.MD5);
		return rememberMe;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, RememberMeServices rememberMeServices) throws Exception {
		http
			.cors(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable) // _csrf
			.headers(headersConfigurer -> headersConfigurer
							.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.authorizeHttpRequests(config -> config
					.requestMatchers("/resources/**","/css/**", "/js/**", "/img/**").permitAll()
					.requestMatchers("/api/v1/email/**").permitAll()
					.requestMatchers("/api/v1/employee/**").permitAll()
					.requestMatchers("/crispy/login", "/crispy/logout", "/crispy/signup",
									 "/crispy/employee/find/username","/crispy/employee/find/username/result",
									 "/crispy/employee/find/password", "/crispy/employee/change/password").permitAll()
					.anyRequest().permitAll()) // 2024.05.20 편의성을 위해 모든 접근 허용
//			.exceptionHandling(exception -> exception
//						.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/crispy/login")))
			.formLogin(login -> login
					.loginPage("/crispy/login")
					.defaultSuccessUrl("/", true))
			.logout(logout -> logout
					.logoutRequestMatcher(new AntPathRequestMatcher("/crispy/logout"))
					.logoutSuccessUrl("/crispy/login")
					.invalidateHttpSession(true))
			.rememberMe(rememberMe -> rememberMe.rememberMeParameter("remember-me")
					.rememberMeServices(rememberMeServices)
					.useSecureCookie(false));
		return http.build();
	}
}