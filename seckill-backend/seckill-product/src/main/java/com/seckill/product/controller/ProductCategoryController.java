package com.seckill.product.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.seckill.common.constant.RoleEnum;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.utils.MinioUtil;
import com.seckill.common.vo.ResultVO;
import com.seckill.product.entity.ProductCategory;
import com.seckill.product.service.ProductCategoryService;
import com.seckill.product.vo.ProductCategoryVO;
import io.minio.MinioClient;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品分类 Controller
 */
@RestController
@RequestMapping("/product/category")
@Api(tags = "商品分类接口")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @PostMapping
    @ApiOperation("新增分类（仅管理员）")
    @ApiOperationSupport(author = "乙")
    @ApiImplicitParam(
            name = "Authorization",
            value = "JWT Token（格式：Bearer + 空格 + token）",
            required = true,
            paramType = "header",
            dataType = "String",
            example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
    )
    public ResultVO<Long> addCategory(@RequestBody ProductCategory category, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (!RoleEnum.ADMIN.getCode().equals(role)) {
            throw BusinessException.forbidden();
        }



        boolean ok = productCategoryService.addCategory(category);
        if (!ok) {
            throw BusinessException.badRequest("新增分类失败");
        }
        return ResultVO.success(category.getId());
    }

    @GetMapping("/list")
    @ApiOperation("查询分类列表（匿名访问；不传parentId默认查一级分类）")
    @ApiOperationSupport(author = "乙")
    public ResultVO<List<ProductCategoryVO>> listCategories(
            @ApiParam("父分类ID（不传默认查一级分类：parentId=0）")
            @RequestParam(required = false) Long parentId
    ) {
        List<ProductCategory> list = productCategoryService.listCategories(parentId);

        List<ProductCategoryVO> voList = list.stream().map(c -> {
            ProductCategoryVO vo = new ProductCategoryVO();
            vo.setId(c.getId());
            vo.setCategoryName(c.getCategoryName());
            vo.setParentId(c.getParentId());
            return vo;
        }).collect(Collectors.toList());

        return ResultVO.success(voList);
    }
}
