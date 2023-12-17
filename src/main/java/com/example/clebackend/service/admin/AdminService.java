package com.example.clebackend.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.clebackend.entity.admin.Admin;
import com.example.clebackend.entity.admin.Carousel;
import com.example.clebackend.entity.admin.InvitationCode;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.vo.ShopVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

    Admin getAdmin(Admin admin);
    Admin getAdminByAccount(String account);

    void addCarousel(List<MultipartFile> file);

    void createInvitationCode(Integer day, Long count);

    InvitationCode getInvitationCodeByCode(InvitationCode invitationCode);

    void updateInvitationCodeTime(InvitationCode invitationCode, Integer day);

    IPage<User> getUserInfo(Integer pageNum, Integer pageSize, String keyword);

    void sealNo(User user, Integer status);

    void updateShopInvitationCode(ShopVo shopVo, Integer day);

    IPage<InvitationCode> getInvitationCodeAll(Integer pageNum, Integer pageSize, Integer status);

    void updateCarousel(Carousel carousel);

    void deleteCarousel(Carousel carousel);

}
