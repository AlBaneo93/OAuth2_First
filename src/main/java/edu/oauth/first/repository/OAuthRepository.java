package edu.oauth.first.repository;


import edu.oauth.first.vo.GoogleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface OAuthRepository extends JpaRepository<GoogleUser, Long> {
}
