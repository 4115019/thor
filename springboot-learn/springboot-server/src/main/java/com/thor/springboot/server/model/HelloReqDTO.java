package com.thor.springboot.server.model;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * @author huangpin
 * @date 2018-12-10
 */
@Data
public class HelloReqDTO {

    @NotEmpty(message = "name不能为空")
    private String nameHold;

    @NotEmpty(message = "email不能为空")
    @Email(message = "邮箱格式错误")
    private String email;

    @AssertTrue(message = "请求参数有误")
    public boolean isValid(){
        return true;
    }

    @AssertTrue(message = "请求参数有误2222")
    public boolean isValid2(){
        return false;
    }
}
