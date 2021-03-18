package edu.oauth.first.vo;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class OAuthToken {
  private String access_token;

  private String token_type;

  private long expires_in;

  private String refresh_token;
}
