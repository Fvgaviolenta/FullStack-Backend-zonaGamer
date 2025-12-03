package com.zonagamer.zonagamer_backend.security;

import com.zonagamer.zonagamer_backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private String id;
    private String email;
    private String password;
    private boolean isAdmin;
    private boolean active;
	
	public static UserPrincipal create(User user){
        return new UserPrincipal(
            user.getId(),
            user.getEmail(),
            user.getPassword(),
            user.isAdmin(),
            user.isActive()
        );
    }
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (isAdmin) {
            return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ADMIN")
            );
        }
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getUsername() {
		return email;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return active;
	}
}
