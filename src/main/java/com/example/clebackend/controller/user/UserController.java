package com.example.clebackend.controller.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.common.RestBean;
import com.example.clebackend.dto.RestPwdDto;
import com.example.clebackend.dto.StringDto;
import com.example.clebackend.dto.LoginDto;
import com.example.clebackend.dto.UseRegisterDto;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.service.user.UserService;
import com.example.clebackend.util.EmailValidator;
import com.example.clebackend.util.EncryptUtils;
import com.example.clebackend.util.FileUploadUtils;
import com.example.clebackend.util.TokenUtils;
import com.example.clebackend.vo.LoginUserVo;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.Objects;


@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/test")
    public String test(){
        return RestBean.success("hello").asJsonString() ;
    }
    @Resource
    UserService userService;

    @Resource
    FileUploadUtils fileUploadUtils;
    private final String salt = "orz_orz_orz_114514";


    /**
     *
     * @param loginUser 用户实体
     * @return 登录成功信息
     */
    @PostMapping("/login")
    public RestBean<LoginUserVo> login(@RequestBody @Valid LoginDto loginUser){

        User user = new User();
        BeanUtil.copyProperties(loginUser, user);

        user.setPassword(EncryptUtils.userPwdEncrypt(user.getPassword()));

        User respUser = userService.getUser(user);

        if(respUser == null)
            return RestBean.error(ResponseCode.FAIL.getCode(), "账户或密码错误");

        if(!Objects.equals(respUser.getRole(), "用户"))
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "非用户不可登录");

        String jwt = TokenUtils.generateToken(user.getAccount(), user.getPassword());

        respUser.setToken(jwt);

        LoginUserVo userVo = new LoginUserVo();
        BeanUtil.copyProperties(respUser, userVo);

        return RestBean.success(userVo);
    }

    @PostMapping("/register")
    public RestBean<String> Register(@RequestBody UseRegisterDto registerUser){

        if(!Objects.equals(registerUser.getPassword(), registerUser.getVerifyPassword()))
            return RestBean.error(ResponseCode.FAIL.getCode(), "两次密码不相等");

        if(userService.getUserByAccount(registerUser.getAccount()) != null){
            return RestBean.error(ResponseCode.FAIL.getCode(), "账户已存在");
        }

        registerUser.setPassword(EncryptUtils.userPwdEncrypt(registerUser.getPassword()));

        User user = new User();

        BeanUtil.copyProperties(registerUser, user);

        user.setRole("用户");
        user.setState(0);
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));

        userService.addUser(user);

        return RestBean.success("创建成功");
    }

    /**
     * 获取用户信息
     * @return 用户信息
     */
    @GetMapping("/info")
    public RestBean<User> info(){
        User user = TokenUtils.getCurrentUser();
        return RestBean.success(user);
    }

    /**
     *
     * @param file 头像
     * @return 新头像url
     */
    @PutMapping("update/avatar")
    public RestBean<String> updateAvatar(MultipartFile file){

        User user = TokenUtils.getCurrentUser();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        String url = fileUploadUtils.uploadFile(file);

        if(StrUtil.isBlank(url))
            return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");
        user.setAvatar(url);
        int res = userService.update(user);

        if (res > 0){
            return RestBean.success(url);
        }
        return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");
    }

    @PutMapping("update/phone")
    public RestBean<String> updatePhone(@RequestBody @NotBlank(message = "电话号码不能为空") String phone){

        User user = TokenUtils.getCurrentUser();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        user.setPhone(phone);
        int res = userService.update(user);

        if (res > 0){
            return RestBean.success("更新成功");
        }
        return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");
    }

    @PutMapping("update/email")
    public RestBean<String> updateEmail(@RequestBody @Valid StringDto email){
        User user = TokenUtils.getCurrentUser();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        if(!EmailValidator.validate(email.getData().toString())){
            return RestBean.error(ResponseCode.FAIL.getCode(), "邮箱格式不合法");
        }

        user.setEmail(email.getData().toString());

        int res = userService.update(user);

        if (res > 0){
            return RestBean.success("更新成功");
        }
        return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");
    }

    @PutMapping("update/nickname")
    public RestBean<String> updateNickname(@RequestBody @Valid StringDto nickname){

        User user = TokenUtils.getCurrentUser();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        if(StrUtil.isBlank(nickname.getData().toString()))
            return RestBean.error(ResponseCode.FAIL.getCode(), "更新信息不能为空");


        user.setNickname(nickname.getData().toString());
        int res = userService.update(user);
        if (res > 0){
            return RestBean.success("更新成功");
        }
        return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");
    }

    @PutMapping("/rest/password")
    public RestBean<String> reSetPassword(@RequestBody @Valid RestPwdDto restPwdDto){
        User dataUser = userService.getUserByAccount(restPwdDto.getAccount());
        if (dataUser == null) {
            return RestBean.error(ResponseCode.FAIL.getCode(), "未查询到用户");
        }

        String oldEncryptedPassword = EncryptUtils.userPwdEncrypt(restPwdDto.getOldPassword());
        if (!oldEncryptedPassword.equals(dataUser.getPassword())) {
            // 避免暴露细节，直接返回密码错误
            return RestBean.error(ResponseCode.FAIL.getCode(), "密码更新失败，请检查输入并重试");
        }

        String newEncryptedPassword = EncryptUtils.userPwdEncrypt(restPwdDto.getNewPassword());
        int result = userService.reSetPassword(restPwdDto.getAccount(), newEncryptedPassword);

        if (result <= 0) {
            // 提供更详细的失败信息
            return RestBean.error(ResponseCode.FAIL.getCode(), "密码更新失败，请检查输入并重试");
        }

        return RestBean.success("密码更新成功");

    }
}
