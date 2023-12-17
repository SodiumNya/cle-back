package com.example.clebackend.service.user.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.dto.*;
import com.example.clebackend.entity.order.OrderItem;
import com.example.clebackend.entity.product.*;
import com.example.clebackend.exception.ServiceException;
import com.example.clebackend.mapper.goods.CategoryMapper;
import com.example.clebackend.mapper.goods.CommentsMapper;
import com.example.clebackend.mapper.goods.GoodsPictureMapper;
import com.example.clebackend.mapper.goods.GoodsSpecsMapper;
import com.example.clebackend.mapper.user.ProductMapper;
import com.example.clebackend.request.OrderItemRequest;
import com.example.clebackend.request.OrderRequest;
import com.example.clebackend.response.OrderResponse;
import com.example.clebackend.response.PayOrderItem;
import com.example.clebackend.service.good.ProductService;
import com.example.clebackend.service.order.OrderItemService;
import com.example.clebackend.util.CalculateThePrice;
import com.example.clebackend.util.FileUploadUtils;
import com.example.clebackend.util.JsonParseUtils;
import com.example.clebackend.util.TimeFormatUtils;
import com.example.clebackend.vo.GoodsVo;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Valid
@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    ProductMapper productMapper;

    @Resource
    CommentsMapper commentsMapper;

    @Resource
    OrderItemService orderItemService;

    @Resource
    GoodsSpecsMapper goodsSpecsMapper;
    @Resource
    CategoryMapper categoryMapper;
    @Resource
    GoodsPictureMapper goodsPictureMapper;

    @Resource
    FileUploadUtils fileUtil;


    @Override
    public GoodsVo getProductsById(Integer id, Integer status) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).eq("delete_status", 0);

        if (status != null) {
            queryWrapper.eq("sale_state", status);
        }

        Goods goods = productMapper.selectOne(queryWrapper);

        if (goods == null) {
            return null;
        }

        return createGoodsVoFromGoods(goods);

    }

    @Override
    public List<Goods> getGoodsByShopId(Integer shopId) {
        return productMapper.getGoodsByShopId(shopId);
    }

    @Override
    public List<Map<Integer, String>> getProductPictureById(Integer id) {
        return productMapper.getProductPicture(id);
    }

    @Override
    public List<Goods> getTop30Products(String location) {

        return productMapper.getTop30Products(location);

    }

    @Override
    public GoodsSpecs getSpecsBySpec(Integer id, String spec) {
        QueryWrapper<GoodsSpecs> productQueryWrapper = new QueryWrapper<>();

//        productQueryWrapper.select("price");
        productQueryWrapper.eq("goods_id", id)
                .eq("specs", spec);

        return goodsSpecsMapper.selectOne(productQueryWrapper);
    }

    @Override
    public List<ProductCommentsDto> getComments(Integer goodsId) {
        return productMapper.getComments(goodsId);
    }

    @Override
    public OrderDto getOrder(@Param("goodId") Integer goodId, @Param("goodSpecs") String goodSpecs) {
        return productMapper.getOrder(goodId, goodSpecs);
    }

    @Override
    @Transactional
    public Integer updateGoodsSaleByList(List<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> {
            UpdateWrapper<Goods> updateWrapper = new UpdateWrapper<>();

            updateWrapper.eq("id", orderItem.getGoodId());
            updateWrapper.setSql("sale = sale + 1");
            int update = productMapper.update(updateWrapper);
            if (update <= 0) throw new ServiceException("更新销量失败");

        });
        return 1;
    }

    @Override
    public List<ShopGoodsDto> getGoodsInfoForShop(Integer shopId) {
        return productMapper.getGoodsInfoForShop(shopId);
    }

    @Override
    public List<ShopGoodsDto> getSaleGoodsInfoForShop(Integer shopId, Integer saleStatue) {
        return productMapper.getSaleGoodsInfoForShop(shopId, saleStatue);
    }

    @Override
    @Transactional
    public Integer updateSaleStatus(List<ShopGoodsDto> shopGoodsList) {
        shopGoodsList.forEach(goods -> {
            UpdateWrapper<Goods> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", goods.getId());
            updateWrapper.set("sale_state", goods.getSaleState());
            int res = productMapper.update(updateWrapper);
            if (res <= 0) throw new ServiceException("更新失败");
        });
        return 1;
    }

    @Override
    public List<Category> getAllCateGory() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        return categoryMapper.selectList(queryWrapper);
    }

    @Override
    public AddGoodsDto getUpdateGoodsByGoodsId(Integer goodsId) {
        return productMapper.getUpdateGoodsByGoodsId(goodsId);
    }

    @Override
    public List<GoodsSpecs> getGoodsSpecsByGoodId(Integer goodsId) {
        QueryWrapper<GoodsSpecs> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_id", goodsId);
        return goodsSpecsMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateGoods(UpdateGoodsDto goodsDto, MultipartFile mainImage, List<MultipartFile> pictureList) {
        try {
            // TODO:校验用户信息是否完整

            // 解析规格参数JSON字符串
            List<Map<String, Object>> specList = JsonParseUtils.convertJsonArrayToList(goodsDto.getSpecsListJson());

            // 分离出新增规格参数
            List<Map<String, Object>> insertSpecsList = specList.stream()
                    .filter(map -> map.get("id") == null)
                    .collect(Collectors.toList());

            // 转换为规格参数Map
            Map<String, List<String>> specMap = JsonParseUtils.convertListToMap(specList);

            // 分离出已存在的规格参数
            specList = specList.stream()
                    .filter(map -> map.get("id") != null)
                    .collect(Collectors.toList());

            // 上传主图
            String mainImageUrl = null;
            if (!Objects.isNull(mainImage)) {
                mainImageUrl = fileUtil.uploadFile(mainImage);
            }

            // 创建商品对象
            Goods goods = createGoodsEntity(goodsDto, mainImageUrl, specMap);

            // 更新商品信息
            int updateResult = productMapper.updateGoods(goods);
            if (updateResult <= 0) {
                throw new ServiceException("商品更新失败");
            }

            // 处理商品图片
            handleGoodsPictures(goods.getId(), pictureList);

            // 处理删除的商品图片
            handleDeletedGoodsPictures(goodsDto.getDeletePicture());

            // 更新已存在的规格参数
            updateSpecs(specList);

            // 插入新增的规格参数
            insertSpecs(goods.getId(), insertSpecsList);

            return 1;
        } catch (Exception e) {
            // 捕获异常时回滚事务
            throw new ServiceException("更新商品失败");
        }
    }


    @Override
    public List<ShopGoodsDto> searchGoods(String keyword, Integer status) {
        return productMapper.searchGoods(keyword, status);

    }

    @Override
    @Transactional
    public Integer updateSaleStatusById(Goods goods) {
        UpdateWrapper<Goods> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", goods.getId());
        updateWrapper.set("sale_state", goods.getSaleState());
        return productMapper.update(updateWrapper);

    }

    @Override
    public Integer deleteById(Integer id) {
        UpdateWrapper<Goods> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("delete_status", 1);
        updateWrapper.set("sale_state", 0);
        return productMapper.update(updateWrapper);
    }

    @Override
    public List<SearchGoodsDto> searchGoodsForUser(String keyword) {
        return productMapper.searchGoodsForUser(keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addComments(Comments comments, String orderSn) {
        comments.setDeleteState(0);

        int res = commentsMapper.insert(comments);

        if (res <= 0) return -1;

        Goods goods = this.getSumScoreAndSale(comments.getGoodsId());
        Double sumScore = goods.getSumScore() + comments.getScore();
        Long sale = goods.getSale();

        Double avgScore = this.calculateScore(sumScore, sale);

        res = orderItemService.updateReviewStatus(orderSn, comments.getGoodsId(), 1);

        return productMapper.updateScore(sumScore, avgScore, comments.getGoodsId());
    }

    @Override
    public Double calculateScore(Double sumScore, Long sale) {
        if (sale == 0) return 0.00;
        return (sumScore / sale);
    }

    @Override
    public Goods getSumScoreAndSale(Integer goodsId) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", goodsId);
        queryWrapper.select("sum_score", "sale");
        return productMapper.selectOne(queryWrapper);
    }

    public Integer getGoodsStatus(Integer goodsId) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", goodsId);
        queryWrapper.select("sale_state");
        return Optional.ofNullable(productMapper.selectOne(queryWrapper))
                .map(Goods::getSaleState)
                .orElse(-1);
    }

    @Override
    public OrderResponse preGeneratedOrders(OrderRequest orderRequest) {
        // 创建订单响应对象
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrder(new ArrayList<>());

        // 使用BigDecimal代替AtomicReference<Double>来避免精度问题
        final BigDecimal[] payPrice = {BigDecimal.ZERO};

        // 遍历订单请求中的每个订单项
        for (OrderItemRequest orderItemRequest : orderRequest.getOrder()) {
            // 创建支付订单项
            PayOrderItem payOrderItem = new PayOrderItem();
            payOrderItem.setShopId(-1);
            payOrderItem.setGoodList(new ArrayList<>());
            payOrderItem.setPayPrice(BigDecimal.ZERO.doubleValue());

            List<GoodDto> goodDtoList = new ArrayList<>();

            // 遍历每个订单项中的商品列表
            orderItemRequest.getGoodList().forEach(item -> {
                Integer res = this.getGoodsStatus(item.getGoodId());
                if(res == -1) throw new ServiceException("数据库查询错误");

                // 先查询商品是否下架
                if(res == 0) throw new ServiceException(ResponseCode.FAIL.getCode(), "商品已经下架");
                // 获取商品详细信息
                OrderDto orderDto = this.getOrder(item.getGoodId(), item.getSpecs());

                // 设置支付订单项的店铺信息
                if (payOrderItem.getShopId() == -1) {
                    payOrderItem.setShopId(orderDto.getShopId());
                    payOrderItem.setShopName(orderDto.getShopName());
                }

                // 创建GoodDto对象，并计算价格
                GoodDto g = new GoodDto();
                BeanUtil.copyProperties(orderDto, g);
                g.setQuantity(item.getQuantity());
                CalculateThePrice.calc(g);
                payPrice[0] = payPrice[0].add(BigDecimal.valueOf(g.getTotalPrice()));

                // 将规格转换为Map
                Map<String, String> map = JsonParseUtils.parse(g.getSpecs());
                g.setGoodSpecsJSon(map);

                // 更新支付订单项的价格和添加到商品列表中
                payOrderItem.setPayPrice(payOrderItem.getPayPrice() + g.getTotalPrice());
                goodDtoList.add(g);
            });

            // 设置支付订单项的商品列表
            payOrderItem.setGoodList(goodDtoList);

            // 将支付订单项添加到订单响应中
            orderResponse.getOrder().add(payOrderItem);
        }

        // 设置订单响应的支付总额
        orderResponse.setPayPrice(payPrice[0].doubleValue());

        return orderResponse;

    }

    // 创建商品对象
    private Goods createGoodsEntity(UpdateGoodsDto goodsDto, String mainImageUrl, Map<String, List<String>> specMap) {
        Goods goods = new Goods();
        goods.setId(goodsDto.getId());
        goods.setAttributeList(JsonParseUtils.convertMapToJson(specMap));
        goods.setCategoryId(goodsDto.getCategoryId());
        goods.setShopId(goodsDto.getShopId());
        goods.setName(goodsDto.getName());
        goods.setMainImage(mainImageUrl);
        goods.setDetail(goodsDto.getDetail());

        // 设置销售时间
        setSalesTime(goodsDto, goods);

        goods.setAllDaySale(goodsDto.getAllDaySale());

        return goods;
    }

    // 处理商品图片
    private void handleGoodsPictures(Integer goodsId, List<MultipartFile> pictureList) {
        if (!Objects.isNull(pictureList)) {
            List<String> urlList = fileUtil.uploadFileList(pictureList);

            // 插入商品图片
            urlList.forEach(url -> {
                GoodsPicture goodsPicture = new GoodsPicture();
                goodsPicture.setGoodsId(goodsId);
                goodsPicture.setUrl(url);
                goodsPicture.setCreated(new Timestamp(System.currentTimeMillis()));
                goodsPicture.setDeleteState(0);

                int insertResult = goodsPictureMapper.insert(goodsPicture);
                if (insertResult <= 0) {
                    throw new ServiceException("商品图片插入失败");
                }
            });
        }
    }

    // 处理删除的商品图片
    private void handleDeletedGoodsPictures(List<Integer> deletePictureIds) {
        if (Objects.nonNull(deletePictureIds) && !deletePictureIds.isEmpty()) {
            deletePictureIds.forEach(id -> {
                UpdateWrapper<GoodsPicture> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", id);
                updateWrapper.set("delete_state", 1);

                int updateResult = goodsPictureMapper.update(updateWrapper);
                if (updateResult <= 0) {
                    throw new ServiceException("删除商品图片失败");
                }
            });
        }
    }

    // 更新已存在的规格参数
    private void updateSpecs(List<Map<String, Object>> specList) {
        specList.forEach(item -> {
            String specs = String.format("{\"%s\":\"%s\"}", item.get("specs"), item.get("value"));
            GoodsSpecs goodsSpecs = new GoodsSpecs();
            goodsSpecs.setId(Integer.valueOf(item.get("id").toString()));
            goodsSpecs.setSpecs(specs);
            Double price = Double.valueOf(item.get("price").toString());
            goodsSpecs.setPrice(price);

            int updateResult = goodsSpecsMapper.updateGoodsSpecs(goodsSpecs);
            if (updateResult <= 0) {
                throw new ServiceException("更新商品规格参数失败");
            }
        });
    }

    // 插入新增的规格参数
    private void insertSpecs(Integer goodsId, List<Map<String, Object>> insertSpecsList) {
        insertSpecsList.forEach(item -> {
            String specs = String.format("{\"%s\":\"%s\"}", item.get("specs"), item.get("value"));
            GoodsSpecs goodsSpecs = new GoodsSpecs();
            goodsSpecs.setGoodsId(goodsId);
            goodsSpecs.setSpecs(specs);
            Double price = Double.valueOf(item.get("price").toString());
            goodsSpecs.setPrice(price);

            int insertResult = goodsSpecsMapper.insert(goodsSpecs);
            if (insertResult <= 0) {
                throw new ServiceException("插入商品规格参数失败");
            }
        });

    }

    private void setSalesTime(UpdateGoodsDto goodsDto, Goods goods) {
        if (!Objects.equals(goodsDto.getAllDaySale(), 1)) {

            Timestamp startTime = TimeFormatUtils.getTimestampFromTimeString(goodsDto.getStartTime());
            Timestamp endTime = TimeFormatUtils.getTimestampFromTimeString(goodsDto.getEndTime());

            // 设置到goods对象中
            goods.setStartTime(startTime);
            goods.setEndTime(endTime);
        }
    }


    private GoodsVo createGoodsVoFromGoods(Goods goods) {
        GoodsVo goodsVo = new GoodsVo();
        BeanUtil.copyProperties(goods, goodsVo);

        Map<String, List<String>> attributeMap = JsonParseUtils.parseList(goods.getAttributeList());
        goodsVo.setSpecs(attributeMap);

        if (Objects.nonNull(goods.getStartTime())) {
            String start = TimeFormatUtils.coverToTime(goods.getStartTime());
            String end = TimeFormatUtils.coverToTime(goods.getEndTime());
            goodsVo.setStartSale(start);
            goodsVo.setEndSale(end);
        }

        return goodsVo;
    }
}
