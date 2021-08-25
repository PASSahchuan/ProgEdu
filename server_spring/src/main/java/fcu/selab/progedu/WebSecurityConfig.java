package fcu.selab.progedu;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Bean
  PasswordEncoder passwordEncoder() { // Todo 一定要這一個 bean , 我也不知道為什麼
    return NoOpPasswordEncoder.getInstance();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(new MyUserDetailsService());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());

    http.authorizeRequests()
            .antMatchers(HttpMethod.POST, "/publicApi/**").permitAll()
            .antMatchers(HttpMethod.GET, "/assignment/getAssignmentFile").permitAll()
            .antMatchers(HttpMethod.GET, "/assignment/getMvnAssignmentFile").permitAll()
            .antMatchers(HttpMethod.GET, "/assignment/getJavaAssignmentFile").permitAll()
            .antMatchers(HttpMethod.GET, "/assignment/getAndroidAssignmentFile").permitAll()
            .antMatchers(HttpMethod.GET, "/assignment/getWebAssignmentFile").permitAll()
            .antMatchers(HttpMethod.POST, "/login").permitAll()

            .anyRequest().authenticated()
            .and()
            .addFilterBefore(new JwtLoginFilter("/login",authenticationManager()), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtFilter(),UsernamePasswordAuthenticationFilter.class)
            .csrf().disable();
  }

}
