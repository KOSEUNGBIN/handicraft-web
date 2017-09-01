package com.handicraft.api.controller;


import com.handicraft.api.utils.EncrypttionUtil;
import com.handicraft.core.dto.User;
import com.handicraft.core.service.UserService;
import com.handicraft.core.utils.enums.Gender;
import io.swagger.annotations.ApiImplicitParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    UserService userService;

    @PostMapping("/auth/signin")
    @Transactional
    public  ResponseEntity signin(@RequestParam("access_token") String access_token)
    {


        ResponseEntity naverAuthentication = authenticateNaver(access_token);

        JsonParser jsonParser = JsonParserFactory.getJsonParser();
        Map<String , Object> result = jsonParser.parseMap(naverAuthentication.getBody().toString());

        Map<String , Object> responseMap = (HashMap<String,Object>) result.get("response");

        User user = userService.findByUser(Integer.parseInt(responseMap.get("id").toString()));


        if(user == null)
        {
//            SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentDate = LocalDateTime.now();
            user = new User();
            user.setUid(Integer.parseInt(responseMap.get("id").toString()));
            user.setGender(responseMap.get("gender").toString().equals("M") ? Gender.MALE : Gender.FEMALE);
            user.setName(responseMap.get("name").toString());
            user.setUpdateAt(currentDate);
            user.setJoinAt(currentDate);

            logger.info("test");

            User userForInfo = userService.insertToUser(user);

            logger.info("User Temp Insert : " + userForInfo);
        }

        MultiValueMap<String ,String> headers = new HttpHeaders();
        try {
            headers.add("Authorization" , "craft " + EncrypttionUtil.AES_Encrypt(user));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(headers, HttpStatus.OK );
    }


    @PostMapping("/auth/signup")
    @ApiImplicitParam(name = "access_token", value="access_token", dataType = "string", paramType = "header")
    public ResponseEntity signup(HttpServletRequest httpServletRequest , @ModelAttribute User user)
    {
        return new ResponseEntity(HttpStatus.OK);
    }


    private ResponseEntity authenticateNaver(String access_token)
    {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = URI.create("https://openapi.naver.com/v1/nid/me");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization","Bearer "+access_token);

        RequestEntity requestEntity = new RequestEntity(httpHeaders, HttpMethod.POST, uri);

        ResponseEntity<String> responseEntity = null;


        responseEntity = restTemplate.exchange(requestEntity,String.class);

        return responseEntity;
    }
}
