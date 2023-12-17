package com.example.clebackend.service.user;

import com.example.clebackend.dto.AddGoodsDto;
import com.example.clebackend.entity.shop.Shop;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

public interface ShopService {


    Integer addShop(String account, String invitationCode);

    Shop getInfo(Integer id);

    Integer getShopId(String account);
    List<String> getLocation();

    Shop getInfoByShopOwner(String shopOwner);

    Integer updateCanteenName(Shop shop);

    Integer updateShopName(Shop shop);
    Integer updateShopLogo(Shop shop);

    Integer updatePhone(Shop shop);

    Integer updateStatus(Shop shop);

    Integer getBusinessStatus(Integer shopId);

    //AddGoodsDto goodsDto,
    //                                     @RequestParam("file") MultipartFile mainImage,
    //                                     @RequestParam("pictureList") List<MultipartFile> pictureList
    Integer addGoods(AddGoodsDto goodsDto, MultipartFile mainImage, List<MultipartFile> pictureList);

    Timestamp expirationDateByCode(String code);
}
