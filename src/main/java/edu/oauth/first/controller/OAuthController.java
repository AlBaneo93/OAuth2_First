package edu.oauth.first.controller;

import edu.oauth.first.service.OAuthService;
import edu.oauth.first.vo.GoogleUser;
import edu.oauth.first.vo.OAuthToken;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/oauth")
@Slf4j
@RequiredArgsConstructor
public class OAuthController {

  @NonNull
  private OAuthService service;

  @Value("${oauth.google.client_id}")
  String client_id;

  @Value("${oauth.google.secret}")
  String secret;

  @Value("${oauth.google.scope}")
  String scope;

  @Value("${oauth.google.redirect_uri}")
  String redirect_uri;

  /*
   *
   * */
  @GetMapping("/callback")
  public ResponseEntity<Map<String, Object>> callBack(HttpServletRequest request, HttpServletResponse httpServletResponse) {
    log.info("Query: " + request.getQueryString());

    String code = request.getParameter("code");

    if (code != null) {
      log.info("code: " + code);

      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("grant_type", "authorization_code");
      body.add("code", code);
      body.add("client_id", client_id);
      body.add("client_secret", secret);
      body.add("redirect_uri", redirect_uri);

      HttpHeaders headers = new HttpHeaders();

      HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

      // 어떤 객체는 CallBack함수까지 등록이 가능하다
      ResponseEntity<OAuthToken> response = new RestTemplate().exchange(
          "https://accounts.google.com/o/oauth2/token",
          HttpMethod.POST,
          entity,
          OAuthToken.class  // vo 클래스에 setter 등록을 해주어야 매핑이 된다
      );
      OAuthToken tokenVO = response.getBody();
      log.info("Token Object: {}", tokenVO.toString());

      if (tokenVO != null) {
        // get user info
        ResponseEntity<GoogleUser> responseEntity = new RestTemplate().exchange(
            "https://www.googleapis.com/oauth2/v3/userinfo?" +
                "access_token=" + tokenVO.getAccess_token() +
                "&token_type=" + tokenVO.getToken_type(),
            HttpMethod.GET,
            null,
            GoogleUser.class
        );
        //        log.info(responseEntity.getBody().toString());
        GoogleUser googleUser = responseEntity.getBody();
        service.googleOAuthUserLogin(googleUser);
        httpServletResponse.addCookie(new Cookie("access_token", tokenVO.getAccess_token()));
        Map<String, Object> map = new HashMap<>();
        map.put("result", googleUser);
        map.put("access_token", tokenVO.getAccess_token());
        return ResponseEntity.ok(map);
      }
    }

    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @GetMapping("/token")
  public void callBackToken(HttpServletRequest request) {
    log.info("GET: " + request.getQueryString());
  }

  @PostMapping("/token")
  public void callBackToken2(HttpServletRequest request) {
    log.info("POST: " + request.getQueryString());
  }


  @GetMapping("/google")
  public void googleLogin(HttpServletResponse response) {
    String loginAsGoogleAddr = "https://accounts.google.com/o/oauth2/auth?" +
        "client_id=" + client_id +
        "&redirect_uri=" + redirect_uri +
        "&response_type=code" +
        "&scope=" + scope;
    try {
      response.sendRedirect(loginAsGoogleAddr);
    } catch (IOException e) {
      log.info(e.getMessage());
    }
  }
}
