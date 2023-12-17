package com.example.clebackend.controller.product;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.common.RestBean;
import com.example.clebackend.dto.*;
import com.example.clebackend.entity.product.Category;
import com.example.clebackend.entity.product.Comments;
import com.example.clebackend.entity.product.Goods;
import com.example.clebackend.entity.product.GoodsSpecs;
import com.example.clebackend.entity.shop.Shop;
import com.example.clebackend.entity.user.User;
import com.example.clebackend.request.OrderItemRequest;
import com.example.clebackend.request.OrderRequest;
import com.example.clebackend.response.OrderResponse;
import com.example.clebackend.response.PayOrderItem;
import com.example.clebackend.service.good.ProductService;
import com.example.clebackend.service.user.ShopService;
import com.example.clebackend.util.CalculateThePrice;
import com.example.clebackend.util.JsonParseUtils;
import com.example.clebackend.util.TimeFormatUtils;
import com.example.clebackend.util.TokenUtils;
import com.example.clebackend.vo.GoodsVo;
import com.example.clebackend.vo.SearchGoodsVo;
import com.example.clebackend.vo.ShopGoodsVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Resource
    ProductService productService;

    @Resource
    ShopService shopService;

    @GetMapping("/get/sale/{Id}")
    public RestBean<GoodsVo> getSaleProdInfo(@PathVariable Integer Id) {
        if (Id == null) return RestBean.error(ResponseCode.FAIL.getCode(), "获取信息失败");

        GoodsVo goods = productService.getProductsById(Id, 1);
        if (goods == null) return RestBean.error(ResponseCode.FAIL.getCode(), "获取信息失败");


        return RestBean.success(goods);
    }

    @GetMapping("/get/{Id}")
    public RestBean<GoodsVo> getAnyProdInfo(@PathVariable Integer Id) {

        GoodsVo goods = productService.getProductsById(Id, null);
        if (goods == null) return RestBean.error(ResponseCode.FAIL.getCode(), "获取信息失败");

        return RestBean.success(goods);
    }


    @GetMapping("/picture/list/{Id}")
    public RestBean<List<Map<Integer, String>>> getPictureList(@PathVariable Integer Id) {
        if (Id == null) return RestBean.error(ResponseCode.FAIL.getCode(), "获取信息失败");

        List<Map<Integer, String>> pictureList = productService.getProductPictureById(Id);
        if (pictureList == null) return RestBean.error(ResponseCode.FAIL.getCode(), "获取信息失败");

        return RestBean.success(pictureList);
    }

    @PostMapping("get/price")
    public RestBean<Double> getPrice(@RequestBody GoodsSpecs goodsSpecs) {
        if (goodsSpecs == null)
            return RestBean.error(ResponseCode.FAIL.getCode(), "获取信息失败");

        if (StrUtil.isBlank(goodsSpecs.getSpecs()) || goodsSpecs.getGoodsId() == null)
            return RestBean.error(ResponseCode.FAIL.getCode(), "获取信息失败");

        GoodsSpecs data = productService.getSpecsBySpec(goodsSpecs.getGoodsId(), goodsSpecs.getSpecs());

        if (data == null)
            return RestBean.error(ResponseCode.FAIL.getCode(), "未查询到信息");

        return RestBean.success(data.getPrice());
    }

    @GetMapping("/comments/{Id}")
    public RestBean<List<ProductCommentsDto>> geComments(@PathVariable Integer Id) {

        List<ProductCommentsDto> data = productService.getComments(Id);
        if (data == null)
            return RestBean.error(ResponseCode.FAIL.getCode(), "未查询到信息");

        data.forEach(item -> {
            String time = TimeFormatUtils.coverToDate(item.getCreated());
            item.setCreatedLocal(time);
        });

        return RestBean.success(data);
    }


    /**
     * 查询销量前30的商品
     *
     * @return 30条商品记录
     */
    @GetMapping("/recommend/{location}")
    public RestBean<List<GoodsVo>> recommend(@PathVariable String location) {
        if (StrUtil.isBlank(location)) {
            return RestBean.error(ResponseCode.FAIL.getCode(), "位置信息为空，获取信息失败");
        }

        // 在这里可以记录日志，提供更详细的错误信息
        List<Goods> goodsList = Optional.ofNullable(productService.getTop30Products(location))
                .orElseGet(Collections::emptyList);

        List<GoodsVo> goodsVos = goodsList.stream()
                .map(item -> {
                    GoodsVo goodsVo = new GoodsVo();
                    BeanUtil.copyProperties(item, goodsVo);
                    return goodsVo;
                })
                .collect(Collectors.toList());

        return RestBean.success(goodsVos);

    }


    @PostMapping("payment/order")
    public RestBean<OrderResponse> getPayOrder(@RequestBody @Valid OrderRequest orderRequest) {
        // 判断订单请求是否为空
        if (orderRequest == null) {
            return RestBean.error(ResponseCode.FAIL.getCode(), "获取信息失败");
        }

        OrderResponse orderResponse = productService.preGeneratedOrders(orderRequest);

        return RestBean.success(orderResponse);


    }


    @PostMapping("update/list/saleStatus")
    public RestBean<String> updateSaleStatus(@RequestBody @Nonnull ListShopGoodsDto list) {
        User user = TokenUtils.getCurrentUser();
        if (user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        if (!Objects.equals(user.getRole(), "商家"))
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        Integer res = productService.updateSaleStatus(list.getShopGoodsList());

        return RestBean.success("更新成功");
    }

    @PostMapping("update/saleStatus")
    public RestBean<String> updateSaleStatusByGoodsId(@RequestBody @Nonnull Goods goods) {
        User user = TokenUtils.getCurrentUser();
        if (user == null) return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");
        if (!Objects.equals(user.getRole(), "商家"))
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        Shop shop = shopService.getInfoByShopOwner(user.getAccount());
        if (Objects.equals(shop.getState(), 0))
            return RestBean.error(ResponseCode.FAIL.getCode(), "未营业");

        Integer res = productService.updateSaleStatusById(goods);

        return RestBean.success("更新成功");
    }

    @GetMapping("get/category")
    public RestBean<List<Category>> getCateGory() {

        List<Category> categoryList = productService.getAllCateGory();

        return RestBean.success(categoryList);
    }

    @GetMapping("get/specs/{goodId}")
    public RestBean<List<GoodsSpecs>> getGoodsSpecs(@PathVariable Integer goodId) {
        List<GoodsSpecs> list = productService.getGoodsSpecsByGoodId(goodId);
        return RestBean.success(list);
    }


    @PostMapping(value = "update/goods", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestBean<String> updateGoods(UpdateGoodsDto goodsDto,
                                        @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
                                        @RequestParam(value = "pictureList", required = false) List<MultipartFile> pictureList) {

        User user = TokenUtils.getCurrentUser();
//        user = null;

        if (user == null || !Objects.equals(user.getRole(), "商家"))
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        ObjectMapper objectMapper = new ObjectMapper();
        List<UpdateGoodsSpecsDto> res;
        try {
            res = objectMapper.readValue(goodsDto.getSpecsListJson(), new TypeReference<>() {
            });
            goodsDto.setSpecsList(res);
        } catch (JsonProcessingException e) {
            return RestBean.error(ResponseCode.FAIL.getCode(), "提交失败");
        }

        productService.updateGoods(goodsDto, mainImage, pictureList);

        return RestBean.success("添加成功");
    }


    @PutMapping("delete")
    public RestBean<String> deleteGoods(@RequestBody Goods goods) {
        User user = TokenUtils.getCurrentUser();
        if (Objects.isNull(user) || !Objects.equals(user.getRole(), "商家"))
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");

        Integer res = productService.deleteById(goods.getId());
        if (res <= 0) return RestBean.error(ResponseCode.FAIL.getCode(), "删除失败");
        return RestBean.success("删除成功");
    }

    @GetMapping("search/{keyword}")
    public RestBean<List<SearchGoodsVo>> userSearchGood(@PathVariable @Nonnull String keyword) {
        List<SearchGoodsDto> searchGoodsDtos = productService.searchGoodsForUser(keyword);

        List<SearchGoodsVo> data = searchGoodsDtos
                .stream()
                .map(goods -> {
                    SearchGoodsVo searchGoodsVo = new SearchGoodsVo();
                    searchGoodsVo.setCanteenName(goods.getCanteenName());
                    List<ShopGoodsDto> list = goods.getGoodsList();
                    List<ShopGoodsVo> voList = list
                            .stream()
                            .map(g -> {
                                ShopGoodsVo shopGoodsVo = new ShopGoodsVo();
                                BeanUtil.copyProperties(g, shopGoodsVo);

                                String priceRange = g.getMinPrice().equals(g.getMaxPrice()) ?
                                        g.getMinPrice().toString() :
                                        g.getMinPrice() + "~" + g.getMaxPrice();

                                shopGoodsVo.setPrice(priceRange);
                                return shopGoodsVo;
                            })
                            .collect(Collectors.toList());
                    list.forEach(g -> {
                    });
                    searchGoodsVo.setGoodsList(voList);
                    return searchGoodsVo;
                })
                .collect(Collectors.toList());
        return RestBean.success(data);
    }

    @PostMapping("rate")
    public RestBean<String> Reviews(@RequestBody UserReviews reviews) {

        User user = TokenUtils.getCurrentUser();
        if (Objects.isNull(user))
            return RestBean.error(ResponseCode.UNAUTHORIZED.getCode(), "无权限");


        Comments comments = new Comments();
        comments.setUserId(user.getId());
        comments.setGoodsId(reviews.getId());
        comments.setScore(reviews.getScore());
        comments.setContent(reviews.getMessage());

        Timestamp dateTime = new Timestamp(System.currentTimeMillis());
        comments.setCreated(dateTime);


        Integer res = productService.addComments(comments, reviews.getOrderSn());
        if (res <= 0) return RestBean.error(ResponseCode.FAIL.getCode(), "更新失败");

        return RestBean.success("已评价");

    }
}
