package edu.oauth.first.service;

import edu.oauth.first.repository.OAuthRepository;
import edu.oauth.first.vo.GoogleUser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

  @NonNull
  private OAuthRepository repository;

  @Override
  public void googleOAuthUserLogin(GoogleUser googleUser) {
    repository.save(googleUser);
  }
}
