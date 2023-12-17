package com.example.clebackend.controller.shop;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.common.RestBean;
import com.example.clebackend.common.UserRole;
import com.example.clebackend.dto.*;
import com.example.clebackend.entity.product.Goods;
import com.example.clebackend.entity.shop.Shop;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.service.good.ProductService;
import com.example.clebackend.service.user.ShopService;
import com.example.clebackend.service.user.UserService;
import com.example.clebackend.util.EncryptUtils;
import com.example.clebackend.util.FileUploadUtils;
import com.example.clebackend.util.TimeFormatUtils;
import com.example.clebackend.util.TokenUtils;
import com.example.clebackend.vo.GoodsVo;
import com.example.clebackend.vo.ShopGoodsVo;
import com.example.clebackend.vo.ShopUserVo;
import com.example.clebackend.vo.ShopVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    ShopService shopService;
    @Resource
    ProductService productService;


    @Resource
    UserService userService;

    @Resource
    FileUploadUtils uploadUtils;


    @GetMapping("{Id}")
    public RestBean<ShopVo> getBasicInfo(@PathVariable Integer Id){
        if(Id == null) return RestBean.error(ResponseCode.FAIL.getCode(), "获取信息失败");

        Shop shop = shopService.getInfo(Id);
        if(shop == null) return RestBean.error(ResponseCode.FAIL.getCode(), "未查询到信息");

        ShopVo shopVo = new ShopVo();

        BeanUtil.copyProperties(shop, shopVo);

        return RestBean.success(shopVo);
    }

    @GetMapping("/get/{Id}")
    public RestBean<Shop> getProdInfo(@PathVariable Integer Id){
        if(Id == null) return RestBean.error(ResponseCode.FAIL.getCode(), "获取信息失败");

        Shop shop = shopService.getInfo(Id);
        if(shop == null) return RestBean.error(ResponseCode.FAIL.getCode(), "未查询到信息");


        return RestBean.success(shop);
    }


    @PostMapping("/login")
    public RestBean<ShopUserVo> login(@RequestBody @Valid LoginDto loginUser){
        User user = new User();
        BeanUtil.copyProperties(loginUser, user);
        user.setPassword(EncryptUtils.shopPwdEncrypt(user.getPassword()));

        User respUser = userService.getUser(user);

        if(respUser == null){
            return RestBean.error(ResponseCode.FAIL.getCode(), "账户或密码错误");
        }

        if(!Objects.equals(respUser.getRole(), "商家"))
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "非商家不可登录");

        int shopId = shopService.getShopId(user.getAccount());
        Shop shop = shopService.getInfo(shopId);


        Timestamp expirationTimestamp = shopService.expirationDateByCode(shop.getInvitationCode());
        LocalDate expirationDate = TimeFormatUtils.CoverToLocalDate(expirationTimestamp);
        LocalDate now = LocalDate.now();
        if(now.isAfter(expirationDate))
            return RestBean.error(ResponseCode.FAIL.getCode(), "邀请码已过期");

        String jwt = TokenUtils.generateToken(user.getAccount(), user.getPassword());

        respUser.setToken(jwt);

        ShopUserVo shopUserVo = new ShopUserVo();
        BeanUtil.copyProperties(respUser, shopUserVo);

        shopUserVo.setShopId(shopId);
        shopUserVo.setInvitation_code(shop.getInvitationCode());
        shopUserVo.setExpirationDate(TimeFormatUtils.coverToDate(expirationTimestamp));

        return RestBean.success(shopUserVo);

    }

    @PostMapping("/register")
    public RestBean<String> Register(@Valid @RequestBody ShopRegisterDto registerUser){

        if(!Objects.equals(registerUser.getPassword(), registerUser.getVerifyPassword())){
            return RestBean.error(ResponseCode.FAIL.getCode(), "两次密码不一致");

        }

        if(userService.getUserByAccount(registerUser.getAccount()) != null){
            return RestBean.error(ResponseCode.FAIL.getCode(), "账户已存在");
        }



        User user = new User();
        user.setPassword(EncryptUtils.shopPwdEncrypt(registerUser.getPassword()));

        user.setRole("商家");
        user.setState(0);
        user.setAccount(registerUser.getAccount());
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));

        userService.addShopUser(user, registerUser.getInvitationCode());

        return RestBean.success("创建成功");
    }

    @GetMapping("sale/{shopId}")
    public RestBean<List<GoodsVo>> getSaleGoods(@PathVariable @Valid Integer shopId){
        List<Goods> goodsList = productService.getGoodsByShopId(shopId);

        if (goodsList.isEmpty()) {
            return RestBean.error(ResponseCode.FAIL.getCode(), "未查询到信息");
        }

        List<GoodsVo> goodsVos = goodsList.stream()
                .map(item -> {
                    GoodsVo goodsVo = new GoodsVo();
                    BeanUtil.copyProperties(item, goodsVo);
                    return goodsVo;
                })
                .collect(Collectors.toList());

        return RestBean.success(goodsVos);
    }


    @GetMapping("/location")
    public RestBean<List<String>>getLocation(){

        List<String> locationList = shopService.getLocation();
        if(locationList == null) return RestBean.error(ResponseCode.FAIL.getCode(), "获取信息失败");

        return RestBean.success(locationList);
    }

    @PutMapping("update/location")
    public RestBean<String> updateLocation(@RequestBody @Valid StringDto canteenName){

        User user = TokenUtils.getCurrentUser();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        Shop shop = shopService.getInfoByShopOwner(user.getAccount());
        if(shop == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        shop.setCanteenName(canteenName.toString());
        Integer res = shopService.updateCanteenName(shop);

        if(res <= 0)
            return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");

        return RestBean.success("更新成功");
    }

    @PutMapping("update/shopName")
    public RestBean<String> updateShopName(@RequestBody @Valid StringDto ShopName){

        User user = TokenUtils.getCurrentUser();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        Shop shop = shopService.getInfoByShopOwner(user.getAccount());
        if(shop == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        shop.setName(ShopName.toString());
        Integer res = shopService.updateShopName(shop);

        if(res <= 0)
            return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");

        return RestBean.success("更新成功");
    }

    @PutMapping("update/ShopLogo")
    public RestBean<String> updateShopLogo(MultipartFile file){

        User user = TokenUtils.getCurrentUser();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        String url = uploadUtils.uploadFile(file);

        if(StrUtil.isBlank(url))
            return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");
        Shop shop = shopService.getInfoByShopOwner(user.getAccount());

        if(shop == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        shop.setShopLogo(url);


        int res = shopService.updateShopLogo(shop);

        if (res > 0){
            return RestBean.success(url);
        }
        return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");
    }

    @PutMapping("update/phone")
    public RestBean<String> updatePhone(@RequestBody @Valid String phone){

        User user = TokenUtils.getCurrentUser();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        Shop shop = shopService.getInfoByShopOwner(user.getAccount());
        if(shop == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        shop.setPhone(phone);
        Integer res = shopService.updatePhone(shop);

        if(res <= 0)
            return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");

        return RestBean.success("更新成功");
    }

    @PutMapping("update/status")
    public RestBean<String> updateStatus(@RequestBody @Valid Shop respShop){

        User user = TokenUtils.getCurrentUser();
        if(user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        Shop shop = shopService.getInfoByShopOwner(user.getAccount());
        if(shop == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");


        shop.setState(respShop.getState());
        Integer res = shopService.updateStatus(shop);

        if(res <= 0)
            return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");

        return RestBean.success("更新成功");
    }


    @PostMapping("get/goods")
    public RestBean<List<ShopGoodsVo>> getGoodsInfoForShop(@RequestBody @Valid Shop shop){

        List<ShopGoodsDto> res = productService.getGoodsInfoForShop(shop.getId());

        List<ShopGoodsVo> data = res.stream()
                .map(this::mapToShopGoodsVo)
                .collect(Collectors.toList());

        return RestBean.success(data);
    }

    @PostMapping("get/goods/sale")
    public RestBean<List<ShopGoodsVo>> getSaleGoodsInfoForShop(@RequestBody @Valid Shop shop){

        List<ShopGoodsDto> res = productService.getSaleGoodsInfoForShop(shop.getId(), 1);

        List<ShopGoodsVo> data = res.stream()
                .map(this::mapToShopGoodsVo)
                .collect(Collectors.toList());


        return RestBean.success(data);
    }

    @PostMapping("get/goods/unSale")
    public RestBean<List<ShopGoodsVo>> getUnSaleGoodsInfoForShop(@RequestBody @Valid Shop shop){
        List<ShopGoodsDto> res = productService.getSaleGoodsInfoForShop(shop.getId(), 0);

        List<ShopGoodsVo> data = res.stream()
                .map(this::mapToShopGoodsVo)
                .collect(Collectors.toList());

        return RestBean.success(data);

    }

    @PostMapping("get/businessStatus")
    public RestBean<Map<String, Integer>> getBusinessStatus(@RequestBody @Valid Shop shop){
        Integer status = shopService.getBusinessStatus(shop.getId());

        Map<String, Integer> data = new HashMap<>();
        data.put("businessStatus", status);
        return RestBean.success(data);
    }


    @PostMapping(value = "add/goods", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestBean<String> addGoods(AddGoodsDto goodsDto,
                                     @RequestParam("mainImage") MultipartFile mainImage,
                                     @RequestParam("pictureList") List<MultipartFile> pictureList){

        User user = TokenUtils.getCurrentUser();
//        user = null;

        if(user == null || !UserRole.SHOP_OWNER.getRoleName().equals(user.getRole()))
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        ObjectMapper objectMapper = new ObjectMapper();
        List<AddGoodsSpecsDto> res;
        try {
            res = objectMapper.readValue(goodsDto.getSpecsListJson(), new TypeReference<>() {});
            goodsDto.setSpecsList(res);
        } catch (JsonProcessingException e) {
            return RestBean.error(ResponseCode.FAIL.getCode(), "提交失败");
        }

        shopService.addGoods(goodsDto, mainImage, pictureList);

        return RestBean.success("添加成功");
    }

    @GetMapping("search/{keyword}")
    public RestBean<List<ShopGoodsVo>> search(@PathVariable @Valid String keyword){
        User user = TokenUtils.getCurrentUser();

        if (user == null || UserRole.SHOP_OWNER.getRoleName().equals(user.getRole())) {
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权访问");
        }


        int status = -1;
        if (keyword.equalsIgnoreCase("已上架")) {
            status = 1;
        } else if (keyword.equalsIgnoreCase("已下架")) {
            status = 0;
        }

        List<ShopGoodsDto> res = productService.searchGoods(keyword, status);

        List<ShopGoodsVo> data = res.stream()
                .map(this::mapToShopGoodsVo)
                .collect(Collectors.toList());

        return RestBean.success(data);
    }


    private ShopGoodsVo mapToShopGoodsVo(ShopGoodsDto goods) {
        ShopGoodsVo shopGoodsVo = new ShopGoodsVo();
        BeanUtil.copyProperties(goods, shopGoodsVo);

        String priceRange = goods.getMinPrice().equals(goods.getMaxPrice()) ?
                goods.getMinPrice().toString() :
                goods.getMinPrice() + "~" + goods.getMaxPrice();

        shopGoodsVo.setPrice(priceRange);
        return shopGoodsVo;
    }
}


