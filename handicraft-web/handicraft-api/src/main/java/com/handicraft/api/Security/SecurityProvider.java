package com.handicraft.api.Security;

import com.handicraft.api.exception.UnAuthorizedException;
import com.handicraft.api.utils.DecryptionUtil;
import com.handicraft.core.dto.Authorities.Authority;
import com.handicraft.core.dto.Users.UserToAuthority;
import com.handicraft.core.service.Users.UserToAuthorityService;
import com.handicraft.core.utils.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component
@Slf4j
public class SecurityProvider implements AuthenticationProvider{

    @Value("${Authentication}")
    private String MASTER_KEY;

//    @Autowired
//    private SecurityUserDetailsService userDetailService;

    @Autowired
    private UserToAuthorityService userToAuthorityService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = (String) authentication.getCredentials();

        // token decryption
        // pk 존재 유무
        // expired time 확인(30분 단위)
        log.info("token : "+ token);
        log.info("master : "+ MASTER_KEY);

        UserToAuthority userToAuthority;
        Authority authority;

        if(!MASTER_KEY.equals(token)) {


            String tokenDecryption = null;
            try {
                tokenDecryption = DecryptionUtil.AES_Decrypt(token);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }

            log.info(tokenDecryption);

            String[] tokenizer = StringUtils.split(tokenDecryption, "/");


            userToAuthority = userToAuthorityService.loadUserByUsername(tokenizer[0]);

            if(userToAuthority == null) throw new UnAuthorizedException();

            log.info("UserToAuthority : " + userToAuthority.toString());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date tokenDate = dateFormat.parse(tokenizer[1]);
                Date curDate = new Date();

                log.info("authentication seconds : "+ (curDate.getTime() - tokenDate.getTime()));
                log.info("authentication now time :"+curDate.getTime());
                log.info("authentication token time :"+tokenDate.getTime());

                if (Math.abs(curDate.getTime() - tokenDate.getTime()) > 1800000)
                {
                    authority = userToAuthority.getAuthority();
                    authority.setCredentialsExpired(true);
                    authority.setEnabled(false);
                    userToAuthority.setAuthority(authority);

                    userToAuthorityService.update(userToAuthority);

                    throw new AuthenticationServiceException("Exprired Exception");
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

//            SecurityUserDetails user = userDetailService.loadUserByUsername(tokenizer[0]);
//
//            return new SecurityAuthentication(user.getUser(), user.getAuthorities());


            return new SecurityAuthentication(userToAuthority);
        }
        else
        {
            authority = new Authority();
            userToAuthority = new UserToAuthority();

            authority.setRole(Role.MASTER);
            authority.setEnabled(true);
            userToAuthority.setAuthority(authority);

            return new SecurityAuthentication(userToAuthority);
        }
    }

    /*
    * authenticate 의 리턴값의 반환형을 확인
    * */
    @Override
    public boolean supports(Class<?> authenticationClass) {
        return SecurityAuthentication.class.isAssignableFrom(authenticationClass);
    }
}
