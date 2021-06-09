package com.vihackerframework.auth.exception;

import com.vihackerframework.core.api.ViHackerResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * @author Ranger
 * @email wilton.icp@gmail.com
 * @since 2021/6/5
 */
@Slf4j
@Component
public class ViHackerAuthWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    @Override
    public ResponseEntity<?> translate(Exception e) {
        ResponseEntity.BodyBuilder status = ResponseEntity.status(HttpStatus.OK);
        String message = "认证失败";
        log.error(message, e);
        if (e instanceof UnsupportedGrantTypeException) {
            message = "不支持该认证类型";
            return status.body(ViHackerResult.failed(message));
        }
        if (e instanceof InvalidTokenException
                && StringUtils.containsIgnoreCase(e.getMessage(), "Invalid refresh token (expired)")) {
            message = "刷新令牌已过期，请重新登录";
            return status.body(ViHackerResult.failed(message));
        }
        if (e instanceof InvalidScopeException) {
            message = "不是有效的scope值";
            return status.body(ViHackerResult.failed(message));
        }
        if (e instanceof RedirectMismatchException) {
            message = "redirect_uri值不正确";
            return status.body(ViHackerResult.failed(message));
        }
        if (e instanceof BadClientCredentialsException) {
            message = "client值不合法";
            return status.body(ViHackerResult.failed(message));
        }
        if (e instanceof UnsupportedResponseTypeException) {
            String code = StringUtils.substringBetween(e.getMessage(), "[", "]");
            message = code + "不是合法的response_type值";
            return status.body(ViHackerResult.failed(message));
        }
        if (e instanceof InvalidGrantException) {
            if (StringUtils.containsIgnoreCase(e.getMessage(), "Invalid refresh token")) {
                message = "refresh token无效";
                return status.body(ViHackerResult.failed(message));
            }
            if (StringUtils.containsIgnoreCase(e.getMessage(), "Invalid authorization code")) {
                String code = StringUtils.substringAfterLast(e.getMessage(), ": ");
                message = "授权码" + code + "不合法";
                return status.body(ViHackerResult.failed(message));
            }
            if (StringUtils.containsIgnoreCase(e.getMessage(), "locked")) {
                message = "用户已被锁定，请联系管理员";
                return status.body(ViHackerResult.failed(message));
            }
            message = "用户名或密码错误";
            return status.body(ViHackerResult.failed(message));
        }
        return status.body(ViHackerResult.failed(message));
    }
}
