package lex.shemaleandre.izshema1.util;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public enum Role {
    STUDENT(Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))),
    INSTRUCTOR(Collections.singletonList(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))),
    ADMIN(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

    private final List<SimpleGrantedAuthority> authorities;

    Role(List<SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }
}