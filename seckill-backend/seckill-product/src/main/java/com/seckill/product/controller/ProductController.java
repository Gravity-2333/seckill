package com.seckill.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.seckill.common.constant.RoleEnum;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.vo.ResultVO;
import com.seckill.product.entity.Product;
import com.seckill.product.service.ProductService;
import com.seckill.product.vo.ProductVO;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品 Controller
 */
@RestController
@RequestMapping("/product")
@Api(tags = "商品接口")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ApiOperation("新增商品（仅管理员）")
    @ApiOperationSupport(author = "乙")
    @ApiImplicitParam(
            name = "Authorization",
            value = "JWT Token（格式：Bearer + 空格 + token）",
            required = true,
            paramType = "header",
            dataType = "String",
            example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
    )
    public ResultVO<Long> addProduct(@RequestBody Product product, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (!RoleEnum.ADMIN.getCode().equals(role)) {
            throw BusinessException.forbidden();
        }

        boolean ok = productService.addProduct(product);
        if (!ok) {
            throw BusinessException.badRequest("新增商品失败");
        }
        return ResultVO.success(product.getId());
    }

    @PutMapping
    @ApiOperation("修改商品（仅管理员）")
    @ApiOperationSupport(author = "乙")
    @ApiImplicitParam(
            name = "Authorization",
            value = "JWT Token（格式：Bearer + 空格 + token）",
            required = true,
            paramType = "header",
            dataType = "String",
            example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
    )
    public ResultVO<Void> updateProduct(@RequestBody Product product, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (!RoleEnum.ADMIN.getCode().equals(role)) {
            throw BusinessException.forbidden();
        }

        boolean ok = productService.updateProduct(product);
        if (!ok) {
            throw BusinessException.badRequest("修改商品失败");
        }
        return ResultVO.success();
    }

    @PutMapping("/status/{id}")
    @ApiOperation("商品上下架（仅管理员）")
    @ApiOperationSupport(author = "乙")
    @ApiImplicitParam(
            name = "Authorization",
            value = "JWT Token（格式：Bearer + 空格 + token）",
            required = true,
            paramType = "header",
            dataType = "String",
            example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
    )
    public ResultVO<Void> updateProductStatus(
            @ApiParam("商品ID") @PathVariable Long id,
            @ApiParam(value = "状态：1-上架/0-下架", required = true) @RequestParam Integer status,
            HttpServletRequest request
    ) {
        Integer role = (Integer) request.getAttribute("role");
        if (!RoleEnum.ADMIN.getCode().equals(role)) {
            throw BusinessException.forbidden();
        }

        if (id == null) {
            throw BusinessException.badRequest("商品ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw BusinessException.badRequest("状态参数不合法");
        }

        Product exist = productService.getById(id);
        if (exist == null) {
            throw BusinessException.badRequest("商品不存在");
        }

        Product update = new Product();
        update.setId(id);
        update.setStatus(status);

        boolean ok = productService.updateById(update);
        if (!ok) {
            throw BusinessException.badRequest("上下架失败");
        }
        return ResultVO.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除商品（仅管理员）")
    @ApiOperationSupport(author = "乙")
    @ApiImplicitParam(
            name = "Authorization",
            value = "JWT Token（格式：Bearer + 空格 + token）",
            required = true,
            paramType = "header",
            dataType = "String",
            example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
    )
    public ResultVO<Void> deleteProduct(@ApiParam("商品ID") @PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (!RoleEnum.ADMIN.getCode().equals(role)) {
            throw BusinessException.forbidden();
        }

        boolean ok = productService.deleteProduct(id);
        if (!ok) {
            throw BusinessException.badRequest("删除商品失败");
        }
        return ResultVO.success();
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("商品详情（匿名访问）")
    @ApiOperationSupport(author = "乙")
    public ResultVO<ProductVO> getProductDetail(@ApiParam("商品ID") @PathVariable Long id) {
        if (id == null) {
            throw BusinessException.badRequest("商品ID不能为空");
        }

        Product p = productService.getById(id);
        if (p == null) {
            throw BusinessException.badRequest("商品不存在");
        }
        // 只允许查看上架商品
        if (p.getStatus() == null || p.getStatus() != 1) {
            throw BusinessException.badRequest("商品已下架");
        }

        ProductVO vo = new ProductVO();
        vo.setId(p.getId());
        vo.setProductName(p.getProductName());
        vo.setProductDesc(p.getProductDesc());
        vo.setPrice(p.getPrice());
        vo.setStock(p.getStock());
        vo.setCategoryId(p.getCategoryId());
        vo.setImgUrl(p.getImgUrl());

        return ResultVO.success(vo);
    }

    @GetMapping("/list")
    @ApiOperation("商品分页列表（匿名访问；管理员可查看全部/按状态筛选）")
    @ApiOperationSupport(author = "乙")
    public ResultVO<IPage<ProductVO>> listProducts(
            @ApiParam(value = "页码（从1开始）", required = true) @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页条数", required = true) @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("分类ID（可选）") @RequestParam(required = false) Long categoryId,
            @ApiParam("状态：1-上架/0-下架（仅管理员可用，不传表示查看全部）") @RequestParam(required = false) Integer status,
            HttpServletRequest request
    ) {
        if (page == null || page < 1 || size == null || size < 1) {
            throw BusinessException.badRequest("分页参数不合法");
        }

        Integer role = (Integer) request.getAttribute("role");
        boolean isAdmin = RoleEnum.ADMIN.getCode().equals(role);

        IPage<Product> productPage;

        if (!isAdmin) {
            productPage = productService.listProducts(page, size, categoryId);
        } else {
            Page<Product> pageParam = new Page<>(page, size);
            LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<>();
            if (categoryId != null) {
                qw.eq(Product::getCategoryId, categoryId);
            }
            if (status != null) {
                qw.eq(Product::getStatus, status);
            }
            qw.orderByDesc(Product::getId);
            productPage = productService.page(pageParam, qw);
        }

        List<ProductVO> voList = productPage.getRecords().stream().map(p -> {
            ProductVO vo = new ProductVO();
            vo.setId(p.getId());
            vo.setProductName(p.getProductName());
            vo.setProductDesc(p.getProductDesc());
            vo.setPrice(p.getPrice());
            vo.setStock(p.getStock());
            vo.setCategoryId(p.getCategoryId());
            vo.setImgUrl(p.getImgUrl());
            return vo;
        }).collect(Collectors.toList());

        Page<ProductVO> voPage = new Page<>();
        voPage.setCurrent(productPage.getCurrent());
        voPage.setSize(productPage.getSize());
        voPage.setTotal(productPage.getTotal());
        voPage.setRecords(voList);

        return ResultVO.success(voPage);
    }
}
