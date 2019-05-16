package com.project.picktoon.security;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class PicktoonSecurityUser extends User {
    private Long id;
    private String nickName;
    private String name;

    public PicktoonSecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public String getEmail(){
        return super.getUsername();
    }
}


