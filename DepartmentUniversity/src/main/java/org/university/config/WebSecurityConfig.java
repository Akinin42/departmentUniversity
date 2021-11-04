package org.university.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    private static final String ADMIN = "ADMIN";
    private static final String TEACHER = "TEACHER";    
    private static final String USER = "USER";
    private static final String STUDENT = "STUDENT";
    
    private UserDetailsService userDetailService;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new HiddenHttpMethodFilter(),BasicAuthenticationFilter.class)
        .csrf().disable()
        .authorizeRequests()       
        .antMatchers(HttpMethod.POST, "/courses/**").hasAuthority(ADMIN)
        .antMatchers(HttpMethod.POST, "/classrooms/**").hasAuthority(ADMIN)
        .antMatchers("/groups/student").hasAnyAuthority(TEACHER,ADMIN)
        .antMatchers(HttpMethod.POST, "/groups").hasAuthority(ADMIN)
        .antMatchers("/groups/edit").hasAuthority(ADMIN)
        .antMatchers(HttpMethod.POST, "/groups/**").permitAll()
        .antMatchers("/students/course").hasAnyAuthority(TEACHER, ADMIN)
        .antMatchers("/teachers/new").hasAuthority(ADMIN)
        .antMatchers("/teachers/edit").hasAnyAuthority(TEACHER, ADMIN)
        .antMatchers("/timetables/new").hasAnyAuthority(TEACHER, ADMIN)
        .antMatchers("/timetables/edit").hasAnyAuthority(TEACHER, ADMIN)
        .antMatchers(HttpMethod.DELETE, "/**").hasAuthority(ADMIN)
        .antMatchers(HttpMethod.POST, "/students").permitAll()
        .antMatchers(HttpMethod.GET,"/").permitAll()
        .antMatchers(HttpMethod.GET,"/temporary/new").permitAll()
        .antMatchers(HttpMethod.GET,"/temporary").hasAuthority(ADMIN)
        .antMatchers(HttpMethod.POST,"/temporary/login").hasAuthority(USER)
        .antMatchers(HttpMethod.POST,"/temporary/edit").hasAuthority(USER)
        .antMatchers(HttpMethod.POST,"/temporary/update").hasAuthority(USER)
        .antMatchers(HttpMethod.POST,"/temporary").permitAll()        
        .antMatchers(HttpMethod.GET,"/static/**").permitAll()
        .antMatchers(HttpMethod.GET,"/teachers").hasAnyAuthority(TEACHER, ADMIN)
        .antMatchers(HttpMethod.GET).hasAnyAuthority(TEACHER, ADMIN, STUDENT)
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/login").usernameParameter("email").permitAll()
        .and()
        .logout().permitAll()
        .logoutSuccessUrl("/")
        .and()
        .headers()
        .contentSecurityPolicy("script-src 'self' https://use.fontawesome.com");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider(userDetailsService(), encoder()));
    }
    
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {        
        return userDetailService;
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder encoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);         
        return authProvider;
    }
}
