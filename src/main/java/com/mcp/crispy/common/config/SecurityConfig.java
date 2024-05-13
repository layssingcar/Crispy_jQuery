//package com.mcp.crispy;
//
//import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.web.authentication.RememberMeServices;
//import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//@EnableWebSecurity
//@EnableMethodSecurity
//@Configuration
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//	private final AuthenticationConfiguration authenticationConfiguration;
//
//	@Bean
//	public AuthenticationManager authenticationManager() throws Exception {
//		return authenticationConfiguration.getAuthenticationManager();
//	}
//
//	@Bean
//	PasswordEncoder passwordEncoder() {
//		return new Argon2PasswordEncoder(16, 32, 4, 512, 5);
//	}
//
//	@Bean
//	RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
//		TokenBasedRememberMeServices.RememberMeTokenAlgorithm encodingAlgorithm = TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256;
//		TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices("remember-me", userDetailsService, encodingAlgorithm);
//		rememberMe.setTokenValiditySeconds(604800);
//		rememberMe.setMatchingAlgorithm(TokenBasedRememberMeServices.RememberMeTokenAlgorithm.MD5);
//		return rememberMe;
//	}
//
//	@Bean
//	SecurityFilterChain filterChain(HttpSecurity http, RememberMeServices rememberMeServices) throws Exception {
//		http
//			.cors(AbstractHttpConfigurer::disable)
//			.headers(headersConfigurer -> headersConfigurer
//							.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
//			.authorizeHttpRequests(config -> config
//					.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//					.anyRequest().permitAll())
//			.formLogin(login -> login
//					.loginPage("/crispy/member/logout"))
//			.logout(logout -> logout
//					.logoutRequestMatcher(new AntPathRequestMatcher("/crispy/member/logout"))
//					.logoutSuccessUrl("/")
//					.invalidateHttpSession(true))
//			.rememberMe(rememberMe -> rememberMe.rememberMeParameter("remember-me")
//					.rememberMeServices(rememberMeServices)
//					.useSecureCookie(false));
//		return http.build();
//	}
//}