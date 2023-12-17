package com.example.clebackend.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.dto.AddGoodsDto;
import com.example.clebackend.entity.admin.InvitationCode;
import com.example.clebackend.entity.product.*;
import com.example.clebackend.entity.shop.Shop;
import com.example.clebackend.exception.ServiceException;
import com.example.clebackend.mapper.admin.InvitationCodeMapper;
import com.example.clebackend.mapper.goods.*;
import com.example.clebackend.mapper.user.ProductMapper;
import com.example.clebackend.mapper.user.ShopMapper;
import com.example.clebackend.service.admin.AdminService;
import com.example.clebackend.service.user.ShopService;
import com.example.clebackend.util.FileUploadUtils;
import com.example.clebackend.util.JsonParseUtils;
import com.example.clebackend.util.TimeFormatUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class ShopServiceImpl implements ShopService {

    @Resource
    ShopMapper shopMapper;

    @Resource
    InvitationCodeMapper invitationCodeMapper;
    @Resource
    AttributeKeyMapper attributeKeyMapper;

    @Resource
    AttributeValueMapper attributeValueMapper;

    @Resource
    ProductMapper productMapper;

    @Resource
    GoodsPictureMapper goodsPictureMapper;

    @Resource
    GoodsSpecsMapper goodsSpecsMapper;



    @Resource
    FileUploadUtils fileUtil;



    @Override
    @Transactional
    public Integer addShop(String account, String invitationCode) {
        Shop shop = new Shop();
        shop.setShopOwner(account);
        shop.setInvitationCode(invitationCode);
        shop.setCreated(TimeFormatUtils.getTimeStamp());

        UpdateWrapper<InvitationCode> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("code", invitationCode)
                .set("active_status", 1);

        int update = invitationCodeMapper.update(updateWrapper);
        if(update <= 0) throw new ServiceException(ResponseCode.SUCCESS.getCode(), "邀请码更新失败");
        return shopMapper.insert(shop);
    }

    @Override
    public Shop getInfo(Integer id) {
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return shopMapper.selectOne(queryWrapper);
    }

    @Override
    public Integer getShopId(String account) {
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_owner", account);
        Optional<Shop> optionalShop = Optional.ofNullable(shopMapper.selectOne(queryWrapper));
        return optionalShop.map(Shop::getId).orElse(null);
    }

    @Override
    public List<String> getLocation() {
        return shopMapper.getLocationList();

    }

    @Override
    public Shop getInfoByShopOwner(String shopOwner) {
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_owner", shopOwner);
        return shopMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public Integer updateCanteenName(Shop shop) {
        UpdateWrapper<Shop> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", shop.getId())
                .eq("shop_owner", shop.getShopOwner());
        updateWrapper.set("canteen_name", shop.getCanteenName());
        return  shopMapper.update(updateWrapper);
    }

    @Override
    @Transactional
    public Integer updateShopName(Shop shop) {
        UpdateWrapper<Shop> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", shop.getId())
                .eq("shop_owner", shop.getShopOwner());
        updateWrapper.set("name", shop.getName());

        return  shopMapper.update(updateWrapper);
    }

    @Override
    @Transactional
    public Integer updateShopLogo(Shop shop) {
        UpdateWrapper<Shop> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", shop.getId())
                .eq("shop_owner", shop.getShopOwner());
        updateWrapper.set("shop_logo", shop.getShopLogo());

        return  shopMapper.update(updateWrapper);
    }

    @Override
    @Transactional
    public Integer updatePhone(Shop shop) {
        UpdateWrapper<Shop> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", shop.getId())
                .eq("shop_owner", shop.getShopOwner());
        updateWrapper.set("phone", shop.getPhone());

        return  shopMapper.update(updateWrapper);
    }

    @Override
    @Transactional
    public Integer updateStatus(Shop shop) {
        UpdateWrapper<Shop> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", shop.getId())
                .eq("shop_owner", shop.getShopOwner());
        updateWrapper.set("state", shop.getState());
        if(Objects.equals(shop.getState(), 0)){
            UpdateWrapper<Goods> goodsUpdateWrapper = new UpdateWrapper<>();
            goodsUpdateWrapper.eq("shop_id", shop.getId());
            goodsUpdateWrapper.set("sale_state", 0);
            productMapper.update(goodsUpdateWrapper);
        }
        return shopMapper.update(updateWrapper);
    }

    @Override
    public Integer getBusinessStatus(Integer shopId) {
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", shopId);
        queryWrapper.select("state");
        Shop shop = shopMapper.selectOne(queryWrapper);
        return shop.getState();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addGoods(AddGoodsDto goodsDto, MultipartFile mainImage, List<MultipartFile> pictureList) {
        try {
            // 插入参数属性和值
            insertAttributeKeyAndValues(goodsDto);

            // 转换规格参数JSON为Map
            List<Map<String, Object>> list = JsonParseUtils.convertJsonArrayToList(goodsDto.getSpecsListJson());
            Map<String, List<String>> resMap = JsonParseUtils.convertListToMap(list);

            // 上传商品主图
            String url = fileUtil.uploadFile(mainImage);

            // 插入商品
            Goods goods = createGoodsObject(goodsDto, resMap, url);
            AtomicInteger res = new AtomicInteger(productMapper.insert(goods));
            if (res.get() <= 0) throw new ServiceException("productMapper.insert fail");

            // 上传商品图片
            uploadGoodsPictures(goods, pictureList);

            // 插入商品参数
            insertGoodsSpecs(goods, goodsDto);

            return 1;
        } catch (Exception e) {
            // 捕获异常并抛出自定义异常
            throw new ServiceException("添加商品失败");
        }
    }

    @Override
    public Timestamp expirationDateByCode(String code) {
        QueryWrapper<InvitationCode> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("code", code)
                .select("expiration_date");

        InvitationCode res = invitationCodeMapper.selectOne(queryWrapper);
        if(res == null) throw new ServiceException(ResponseCode.FAIL.getCode(), "未查询到邀请码");

        return res.getExpirationDate();


    }

    private void insertAttributeKeyAndValues(AddGoodsDto goodsDto) {
        goodsDto.getSpecsList().forEach(item -> {
            AttributeKey key = new AttributeKey();
            AttributeValue value = new AttributeValue();

            key.setCategoryID(goodsDto.getCategoryId());
            key.setName(item.getSpecs());
            int res = attributeKeyMapper.insert(key);
            if (res <= 0) throw new ServiceException("attributeKeyMapper.insert fail");

            value.setAttributeKeyId(key.getId());
            value.setName(item.getValue());
            res = attributeValueMapper.insert(value);
            if (res <= 0) throw new ServiceException("attributeValueMapper.insert fail");
        });
    }

    private Goods createGoodsObject(AddGoodsDto goodsDto, Map<String, List<String>> resMap, String url) {
        Goods goods = new Goods();
        goods.setAttributeList(JsonParseUtils.convertMapToJson(resMap));
        goods.setCategoryId(goodsDto.getCategoryId());
        goods.setShopId(goodsDto.getShopId());
        goods.setName(goodsDto.getName());
        goods.setMainImage(url);
        goods.setDetail(goodsDto.getDetail());

        if (!Objects.equals(goodsDto.getAllDaySale(), 1)) {
            setSalesTime(goodsDto, goods);
        }

        goods.setAllDaySale(goodsDto.getAllDaySale());
        goods.setSaleState(0);
        return goods;
    }

    private void setSalesTime(AddGoodsDto goodsDto, Goods goods) {
        // 获取当前日期
        LocalDateTime currentDate = LocalDateTime.now().toLocalDate().atStartOfDay();

        // 将时间字符串解析为LocalTime对象
        LocalTime startTime = LocalTime.parse(goodsDto.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime = LocalTime.parse(goodsDto.getEndTime(), DateTimeFormatter.ofPattern("HH:mm"));

        // 创建带有当前日期的LocalDateTime对象
        LocalDateTime localStartTime = currentDate.with(startTime);
        LocalDateTime localEndTime = currentDate.with(endTime);

        // 将LocalDateTime对象转换为Timestamp
        Timestamp start = Timestamp.valueOf(localStartTime);
        Timestamp end = Timestamp.valueOf(localEndTime);

        // 设置到goods对象中
        goods.setStartTime(start);
        goods.setEndTime(end);
    }

    private void uploadGoodsPictures(Goods goods, List<MultipartFile> pictureList) {
        GoodsPicture goodsPicture = new GoodsPicture();

        pictureList.forEach(file -> {
            String pictureUrl = fileUtil.uploadFile(file);

            goodsPicture.setId(null);
            goodsPicture.setGoodsId(goods.getId());
            goodsPicture.setUrl(pictureUrl);
            goodsPicture.setCreated(new Timestamp(System.currentTimeMillis()));
            goodsPicture.setDeleteState(0);
            int r = goodsPictureMapper.insert(goodsPicture);
            if (r <= 0) throw new ServiceException("goodsPictureMapper.insert fail");
        });
    }

    private void insertGoodsSpecs(Goods goods, AddGoodsDto goodsDto) {
        goodsDto.getSpecsList().forEach(item -> {
            String s = String.format("{\"%s\":\"%s\"}", item.getSpecs(), item.getValue());
            GoodsSpecs goodsSpecs = new GoodsSpecs();
            goodsSpecs.setGoodsId(goods.getId());
            goodsSpecs.setSpecs(s);
            Double price = Double.valueOf(item.getPrice());
            goodsSpecs.setPrice(price);

            AtomicInteger res = new AtomicInteger(goodsSpecsMapper.insert(goodsSpecs));
            if (res.get() <= 0) throw new ServiceException("goodsSpecsMapper.insert fail");
        });
    }


}

