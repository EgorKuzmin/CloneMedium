package ru.javamentor.controller.rest;

import com.github.scribejava.apis.vk.VKOAuth2AccessToken;
import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.javamentor.config.FacebookConfig;
import ru.javamentor.config.GoogleConfig;
import ru.javamentor.config.VKontakteConfig;
import ru.javamentor.model.User;
import ru.javamentor.service.user.UserService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/**
 * Rest контроллер для авторизации с помощью соцсетей
 *
 * @version 1.0
 * @author Java Mentor
 */
@RestController
@RequestMapping("/authorization")
public class RegistrationThroughSocialNetworks {

    private final VKontakteConfig vKontakteConfig;
    private final FacebookConfig facebookConfig;
    private final GoogleConfig googleConfig;

    public UserService userService;

    @Autowired
    public RegistrationThroughSocialNetworks(VKontakteConfig vKontakteConfig, FacebookConfig facebookConfig, UserService userService, GoogleConfig googleConfig) {
        this.vKontakteConfig = vKontakteConfig;
        this.facebookConfig = facebookConfig;
        this.googleConfig = googleConfig;
        this.userService = userService;
    }

    /**
     * метод для ВК-авторизации
     *
     * @param code - параметр запроса
     * @return ResponseEntity, который перенаправляет на страницу Home
     */
    @GetMapping("/returnCodeVK")
    public ResponseEntity<Object> getCodeThird(@RequestParam String code) throws InterruptedException, ExecutionException, IOException, URISyntaxException {
        OAuth2AccessToken token = vKontakteConfig.toGetTokenVK(code);
        String email = ((VKOAuth2AccessToken) token).getEmail();
        if  (email == null){
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(new URI("/login?errorEmail"));
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        }
        User currentUser = vKontakteConfig.toCreateUser(token, email);
        if (userService.getUserByEmail(email) == null) {
            userService.addUserThroughSocialNetworks(currentUser);
        }
        userService.login(currentUser.getUsername(), currentUser.getPassword(), currentUser.getAuthorities());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(new URI("/home"));
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    /**
     * метод для FB-авторизации
     *
     * @param code - параметр запроса
     * @return ResponseEntity, который перенаправляет на страницу Home
     */
    @GetMapping("/returnCodeFacebook")
    public ResponseEntity<Object> getCodeSecond(@RequestParam String code) throws InterruptedException, ExecutionException, IOException, URISyntaxException {
        OAuth2AccessToken token = facebookConfig.toGetTokenFacebook(code);
        User currentUser = facebookConfig.toCreateUser(token);
        if (userService.getUserByEmail(currentUser.getUsername()) == null) {
            userService.addUserThroughSocialNetworks(currentUser);
        }
        userService.login(currentUser.getUsername(), currentUser.getPassword(), currentUser.getAuthorities());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(new URI("/home"));
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @GetMapping("/returnCodeGoogle")
    public ResponseEntity<Object> getCode (@RequestParam String code) throws InterruptedException, ExecutionException, IOException, URISyntaxException {
        OAuth2AccessToken token = googleConfig.toGetTokenGoogle(code);
        User currentUser = googleConfig.toCreateUser(token);
        System.out.println(token.getRawResponse());
        if (userService.getUserByEmail(currentUser.getUsername()) == null) {
            userService.addUserThroughSocialNetworks(currentUser);
        }
        userService.login(currentUser.getUsername(), currentUser.getPassword(), currentUser.getAuthorities());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(new URI("/home"));
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }
}