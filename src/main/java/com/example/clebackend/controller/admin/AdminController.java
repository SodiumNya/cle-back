package com.example.clebackend.controller.admin;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.common.RestBean;
import com.example.clebackend.common.UserRole;
import com.example.clebackend.dto.LoginDto;
import com.example.clebackend.entity.admin.Admin;
import com.example.clebackend.entity.admin.Carousel;
import com.example.clebackend.entity.admin.InvitationCode;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.exception.ServiceException;
import com.example.clebackend.service.admin.AdminService;
import com.example.clebackend.util.EncryptUtils;
import com.example.clebackend.util.TokenUtils;
import com.example.clebackend.vo.LoginUserVo;
import com.example.clebackend.vo.ShopVo;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("admin")
public class AdminController {

    @Resource
    AdminService adminService;

    @PostMapping("login")
    public RestBean<LoginUserVo> login(@RequestBody @Valid LoginDto loginAdmin){
        Admin admin = new Admin();
        BeanUtil.copyProperties(loginAdmin, admin);

        admin.setPassword(EncryptUtils.userPwdEncrypt(loginAdmin.getPassword()));

        Admin respAdmin = adminService.getAdmin(admin);

        if(respAdmin == null)
            return RestBean.error(ResponseCode.FAIL.getCode(), "账户或密码错误");

        String jwt = TokenUtils.generateToken(respAdmin.getAccount(), respAdmin.getPassword());

        respAdmin.setToken(jwt);

        LoginUserVo userVo = new LoginUserVo();
        BeanUtil.copyProperties(respAdmin, userVo);

        return RestBean.success(userVo);
    }

    @GetMapping("info")
    public RestBean<LoginUserVo> info(){
        Admin user = TokenUtils.getCurrentAdmin();
        LoginUserVo loginUserVo = new LoginUserVo();
        BeanUtil.copyProperties(loginUserVo, user);
        return RestBean.success(loginUserVo);
    }


    @PostMapping(value = "add/carousel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestBean<String> addCarousel(@RequestParam(value = "carouselList") List<MultipartFile> carouselList){
        Admin user = TokenUtils.getCurrentAdmin();

        adminService.addCarousel(carouselList);

        return RestBean.success("上传成功");
    }

    @PutMapping("update/carousel")
    public RestBean<String> updateCarouselStatus(@RequestBody @Valid Carousel carousel){
         Admin user = TokenUtils.getCurrentAdmin();
         if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        adminService.updateCarousel(carousel);

        return RestBean.success("更新成功");
    }

    @DeleteMapping("delete/carousel")
    public RestBean<String> deleteCarousel(@RequestBody @Valid Carousel carousel){
        Admin user = TokenUtils.getCurrentAdmin();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        adminService.deleteCarousel(carousel);
        return RestBean.success("已删除");
    }

    @GetMapping("get/info/{pageNum}/{pageSize}/{keyword}")
    public IPage<User> getInfo(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @PathVariable String keyword){
        Admin user = TokenUtils.getCurrentAdmin();
        if(user == null) throw new ServiceException(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        return adminService.getUserInfo(pageNum, pageSize, keyword);
    }


    @PostMapping("generate/invitationCode/{day}/{count}")
    public RestBean<String> generateInvitationCode(@PathVariable @NotNull(message = "有效期不能为空") Integer day,
                                                   @PathVariable Long count){

        if(day <= 0 || count <= 0)
            return RestBean.error(ResponseCode.FAIL.getCode(), "生成信息有误， 请检查重试");

        Admin user = TokenUtils.getCurrentAdmin();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        adminService.createInvitationCode(day, count);

        return RestBean.success("生成成功");
    }

    @PostMapping("invitationCode/get")
    public RestBean<InvitationCode> searchInvitationCode(@RequestBody InvitationCode invitationCode){
        Admin user = TokenUtils.getCurrentAdmin();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        InvitationCode res = adminService.getInvitationCodeByCode(invitationCode);
        if(res == null) return RestBean.error(ResponseCode.FAIL.getCode(), "未查询到信息");
        return RestBean.success(res);

    }


    @PostMapping("update/invitationCode/time/{day}")
    public RestBean<String> updateInvitationCodeTime(@RequestBody InvitationCode invitationCode,  @PathVariable Integer day){
        Admin user = TokenUtils.getCurrentAdmin();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        adminService.updateInvitationCodeTime(invitationCode, day);

        return RestBean.success("已更新");
    }

    @PutMapping("update/sealNo/{status}")
    public RestBean<String> sealNo(@RequestBody User user, @PathVariable Integer status){
        Admin admin = TokenUtils.getCurrentAdmin();
        if(admin == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        adminService.sealNo(user, status);
        return RestBean.success("已封号");
    }

    @PutMapping("update/invitationCode/shop/{day}")
    public RestBean<String> updateShopInvitationCode(@RequestBody @Valid ShopVo shopVo, @PathVariable Integer day){
        Admin admin = TokenUtils.getCurrentAdmin();
        if(admin == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");


        adminService.updateShopInvitationCode(shopVo, day);

        return RestBean.success("更新邀请码成功");
    }

    @GetMapping("invitationCode/all/{pageNum}/{pageSize}/{status}")
    public IPage<InvitationCode> getInvitationCode(@PathVariable Integer pageNum,
                                                   @PathVariable Integer pageSize,
                                                   @PathVariable Integer status){
        Admin admin = TokenUtils.getCurrentAdmin();
        if(admin == null) throw new ServiceException(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        return adminService.getInvitationCodeAll(pageNum, pageSize, status);

    }

}
