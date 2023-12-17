package com.example.clebackend.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clebackend.dto.*;
import com.example.clebackend.entity.product.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface ProductMapper extends BaseMapper<Goods> {

    List<Map<Integer, String>> getProductPicture(@Param("id") Integer id);

    List<Goods> getTop30Products(String canteenName);

    List<ProductCommentsDto> getComments(Integer goodsId);

    List<Goods> getGoodsByShopId(Integer shopId);

    OrderDto getOrder(Integer goodId, String goodSpecs);

    List<ShopGoodsDto> getGoodsInfoForShop(Integer ShopId);
    List<ShopGoodsDto> getSaleGoodsInfoForShop(@Param("shopId") Integer shopId, @Param("saleStatue") Integer saleStatue);

    AddGoodsDto getUpdateGoodsByGoodsId(Integer goodId);

    Integer updateGoods(Goods goods);

    List<ShopGoodsDto> searchGoods(String keyword, Integer status);

    List<SearchGoodsDto> searchGoodsForUser(String keyword);

    //sumScore, avgScore, comments.getId()
    Integer updateScore(@Param("sumScore") Double sumScore,
                        @Param("score") Double score,
                        @Param("goodsId") Integer goodsId);
}
