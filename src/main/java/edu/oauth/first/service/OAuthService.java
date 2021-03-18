package edu.oauth.first.service;


import edu.oauth.first.vo.GoogleUser;

public interface OAuthService {
  void googleOAuthUserLogin(GoogleUser googleUser);
}
