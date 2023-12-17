package com.example.clebackend.service.good;

import com.example.clebackend.dto.*;
import com.example.clebackend.entity.order.OrderItem;
import com.example.clebackend.entity.product.Category;
import com.example.clebackend.entity.product.Comments;
import com.example.clebackend.entity.product.Goods;
import com.example.clebackend.entity.product.GoodsSpecs;
import com.example.clebackend.request.OrderRequest;
import com.example.clebackend.response.OrderResponse;
import com.example.clebackend.vo.GoodsVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductService {
    GoodsVo getProductsById(Integer id, Integer status);

    List<Goods> getGoodsByShopId(Integer shopId);


    List<Map<Integer, String>> getProductPictureById(Integer id);

    List<Goods> getTop30Products(String location);

    GoodsSpecs getSpecsBySpec(Integer id, String spec);

    List<ProductCommentsDto> getComments(Integer goodsId);

    OrderDto getOrder(Integer goodId, String goodSpecs);

    Integer updateGoodsSaleByList(List<OrderItem> orderItems);


    List<ShopGoodsDto> getGoodsInfoForShop(Integer shopId);

    List<ShopGoodsDto> getSaleGoodsInfoForShop(Integer shopId, Integer saleStatue);

    Integer updateSaleStatus(List<ShopGoodsDto> shopGoodsList);

    List<Category> getAllCateGory();

    AddGoodsDto getUpdateGoodsByGoodsId(Integer goodsId);

    List<GoodsSpecs> getGoodsSpecsByGoodId(Integer goodsId);

    Integer updateGoods(UpdateGoodsDto goodsDto, MultipartFile mainImage, List<MultipartFile> pictureList);

    List<ShopGoodsDto> searchGoods(String keyword, Integer status);

    Integer updateSaleStatusById(Goods goods);

    Integer deleteById(Integer id);

    List<SearchGoodsDto> searchGoodsForUser(String keyword);

    Integer addComments(Comments comments, String orderSn);

    Double calculateScore(Double SumScore, Long sale);

    Goods getSumScoreAndSale(Integer goodsId);

    OrderResponse preGeneratedOrders(OrderRequest orderRequest);

    Integer getGoodsStatus(Integer goodsId);
}
