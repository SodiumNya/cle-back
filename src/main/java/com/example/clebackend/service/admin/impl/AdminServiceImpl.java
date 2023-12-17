package com.example.clebackend.service.admin.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.common.UserRole;
import com.example.clebackend.entity.admin.Carousel;
import com.example.clebackend.entity.admin.Admin;
import com.example.clebackend.entity.admin.InvitationCode;
import com.example.clebackend.entity.shop.Shop;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.exception.ServiceException;
import com.example.clebackend.mapper.admin.AdminMapper;
import com.example.clebackend.mapper.admin.CarouselMapper;
import com.example.clebackend.mapper.admin.InvitationCodeMapper;
import com.example.clebackend.mapper.user.ShopMapper;
import com.example.clebackend.mapper.user.UserMapper;
import com.example.clebackend.service.admin.AdminService;
import com.example.clebackend.util.FileUploadUtils;
import com.example.clebackend.util.InvitationCodeUtils;
import com.example.clebackend.util.TimeFormatUtils;
import com.example.clebackend.vo.InvitationCodeVo;
import com.example.clebackend.vo.ShopVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;


@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    AdminMapper adminMapper;

    @Resource
    CarouselMapper carouselMapper;

    @Resource
    FileUploadUtils uploadUtils;

    @Resource
    InvitationCodeMapper invitationCodeMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    ShopMapper shopMapper;

    @Override
    public Admin getAdmin(Admin admin) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("account", admin.getAccount());
        queryWrapper.eq("password", admin.getPassword());

        return adminMapper.selectOne(queryWrapper);
    }

    @Override
    public Admin getAdminByAccount(String account) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("account", account);
        return adminMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCarousel(List<MultipartFile> files) {
        List<String> urlList = uploadUtils.uploadFileList(files);
        urlList.forEach(item->{
            int res = carouselMapper.insert(createCarousel(item));
            if(res <= 0) throw new ServiceException(ResponseCode.FAIL.getCode(), "上传失败");
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createInvitationCode(Integer day, Long count ) {
        for(int i = 1; i <= count; ++i){
            InvitationCode invitationCode = generateInvitationCode(day);
            int res = invitationCodeMapper.insert(invitationCode);
            if(res <= 0) throw new ServiceException(ResponseCode.FAIL.getCode(), "邀请码生成失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInvitationCodeTime(InvitationCode invitationCode, Integer day) {
        UpdateWrapper<InvitationCode> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("code", invitationCode.getCode());
        Timestamp newTime = Timestamp.valueOf(invitationCode.getExpirationDate().toLocalDateTime().plusMinutes(day * 24 * 60));
        updateWrapper.set("expiration_date", newTime);
        int update = invitationCodeMapper.update(updateWrapper);
        if(update <= 0) throw new ServiceException(ResponseCode.FAIL.getCode(), "邀请码更新失败");

    }

    @Override
    public IPage<User> getUserInfo(Integer pageNum, Integer pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);

        // 构造查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("account", keyword)
                    .or().like("role", keyword);
        }

        // 执行分页查询

        // 返回分页结果
        return userMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sealNo(User user, Integer status) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", user.getAccount())
                .eq("id", user.getId());
        updateWrapper.set("state", status);
        int res = userMapper.update(updateWrapper);
        if(res <= 0) throw new ServiceException(ResponseCode.FAIL.getCode(), "封号失败");
        if(UserRole.SHOP_OWNER.getRoleName().equals(user.getRole())){
            UpdateWrapper<Shop> shopUpdateWrapper = new UpdateWrapper<>();
            shopUpdateWrapper.eq("shop_owner", user.getAccount());
            if(status == 0) shopUpdateWrapper.set("state", 0);
            else shopUpdateWrapper.set("state", 2);
            res = shopMapper.update(shopUpdateWrapper);
            if(res <= 0) throw new ServiceException(ResponseCode.FAIL.getCode(), "封号失败");

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShopInvitationCode(ShopVo shopVo, Integer day) {
        Page<InvitationCode> page = new Page<>(1, 1);
        QueryWrapper<InvitationCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("active_status", 0);
        Page<InvitationCode> page1 = invitationCodeMapper.selectPage(page, queryWrapper);
        InvitationCode code;
        if(page1.getSize() == 0){
            InvitationCode invitationCode = generateInvitationCode(day);
            int res = invitationCodeMapper.insert(invitationCode);
            if(res <= 0)
                throw new ServiceException(ResponseCode.FAIL.getCode(), "生成新邀请码失败");
            code = invitationCode;
        }else code = page1.getRecords().get(0);

        UpdateWrapper<Shop> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", shopVo.getId());
        updateWrapper.set("invitation_code", code.getCode());
        int update = shopMapper.update(updateWrapper);
        if(update <= 0) throw new ServiceException(ResponseCode.FAIL.getCode(), "更新邀请码失败");

        UpdateWrapper<InvitationCode> updateWrapper1 = new UpdateWrapper<>();
        updateWrapper1.eq("code", code.getCode())
                .set("active_status", 1);
        update = invitationCodeMapper.update(updateWrapper1);
        if(update <= 0)
            throw new ServiceException(ResponseCode.FAIL.getCode(), "更新邀请码失败");

    }

    @Override
    public IPage<InvitationCode> getInvitationCodeAll(Integer pageNum, Integer pageSize, Integer status) {
        Page<InvitationCode> page = new Page<>(pageNum, pageSize);
        QueryWrapper<InvitationCode> queryWrapper = new QueryWrapper<>();
        if (status != 2) queryWrapper.eq("active_status", status);


        return invitationCodeMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCarousel(Carousel calendar) {
        UpdateWrapper<Carousel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", calendar.getId())
                .set("status", calendar.getStatus());
        int update = carouselMapper.update(updateWrapper);
        if(update <= 0) throw new ServiceException(ResponseCode.FAIL.getCode(), "更新失败");
    }

    @Override
    public void deleteCarousel(Carousel calendar) {
        UpdateWrapper<Carousel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", calendar.getId())
                .set("status", 2);
        carouselMapper.update(updateWrapper);
        int update = carouselMapper.update(updateWrapper);
        if(update <= 0) throw new ServiceException(ResponseCode.FAIL.getCode(), "删除失败");
    }

    @Override
    public InvitationCode getInvitationCodeByCode(InvitationCode invitationCode) {
        QueryWrapper<InvitationCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", invitationCode.getCode());
        return invitationCodeMapper.selectOne(queryWrapper);

    }

    private static Carousel createCarousel(String url){
        Carousel carousel = new Carousel();
        carousel.setCreateTime(TimeFormatUtils.getTimeStamp());
        carousel.setUrl(url);
        return carousel;
    }


    private InvitationCode generateInvitationCode(Integer day){
        InvitationCodeUtils.InvitiationCodeInfo codeInfo = InvitationCodeUtils.generateInvitationCode(8);
        InvitationCode invitationCode = new InvitationCode();
        invitationCode.setCode(codeInfo.getCode());
        invitationCode.setCreateTime(codeInfo.getCreationTime());
        LocalDateTime newTime = LocalDateTime.now().plusMinutes(day * 24 * 60);
        invitationCode.setExpirationDate(Timestamp.valueOf(newTime));
        return invitationCode;
    }


}
