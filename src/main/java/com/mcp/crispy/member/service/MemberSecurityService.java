//package com.mcp.crispy.member.service;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import com.example.test.member.dto.MemberDto;
//import com.example.test.member.mapper.MemberMapper;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class MemberSecurityService implements UserDetailsService{
//	
//	private final MemberMapper memberMapper;
//	private final PasswordEncoder passwordEncoder;
//
//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		MemberDto byUserEmail = Optional.ofNullable(memberMapper.findByMemberEmail(username))
//				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
//		
//		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + byUserEmail.getRole().getDisplayName());
//		List<GrantedAuthority> authorities = Collections.singletonList(authority);
//		log.info("Email: {}, PW: {}, ROLE: {}", byUserEmail.getUserEmail(), byUserEmail.getUserPw(), authorities);
//		return new User(byUserEmail.getUserEmail(), byUserEmail.getUserPw(), authorities);
//	}
//	
//	
//	
//
//}
