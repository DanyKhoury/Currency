package com.eddress.Currency.IServices;

import org.springframework.security.core.userdetails.UserDetails;

public interface IUserService {
    UserDetails  loadUserByUsername(String username);
}
