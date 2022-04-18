package com.ali.service;

import com.ali.pojo.ProductInfo;
import com.github.pagehelper.PageInfo;

import java.util.List;
public interface ProductInfoService {
    //显示全部商品（不分页）
    public List<ProductInfo> getAll();

    //分页功能实现
    PageInfo splitPage(int pageNum, int pageSize);

    //增加商品
    int save(ProductInfo info);

    //按主键id查询商品
    ProductInfo getById(int pid);

    //更新商品
    int update(ProductInfo info);

    //单个商品删除
    int delete(int pid);
}
