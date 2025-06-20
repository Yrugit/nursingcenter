package com.neusoft.nursingcenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.nursingcenter.entity.*;
import com.neusoft.nursingcenter.mapper.FoodMapper;
import com.neusoft.nursingcenter.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/food")
public class FoodController {
    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private FoodService foodService;

    @PostMapping("/page")
    public PageResponseBean<List<Food>> page (@RequestBody Map<String, Object> request){
        int current = (int)request.get("current");
        int size = (int)request.get("size");
        String name = (String)request.get("name");
        String type = (String)request.get("type");

        QueryWrapper<Food> qw = new QueryWrapper<>();
        qw.like("name", name);
        qw.eq("type",type);

        IPage<Food> page = new Page<>(current,size);
        IPage<Food> result = foodMapper.selectPage(page,qw);
//        System.out.println(result);
        List<Food> list = result.getRecords();
//        System.out.println("size: "+list.size());
        long total = result.getTotal();
        System.out.println(total);

        PageResponseBean<List<Food>> prb = null;
        if(total > 0){
            prb = new PageResponseBean<>(list);
            prb.setTotal(total);
        }else {
            prb = new PageResponseBean<>(500, "No data");
        }
        return prb;
    }

    @PostMapping("/listByType")
    public ResponseBean<List<Food>> listByType(@RequestBody Map<String, Object> request) {
        String type = (String) request.get("type");
        List<Food> foodList = foodMapper.listByType(type);
        ResponseBean<List<Food>> rb = null;
        if (foodList.size() > 0) {
            rb = new ResponseBean<>(foodList);
        } else {
            rb = new ResponseBean<>(500, "No data");
        }
        return rb;
    }

    // 要先检查重名
    @PostMapping("/add")
    public ResponseBean<Integer> add(@RequestBody Food food) {
        ResponseBean<Integer> rb = null;
        Food check = foodMapper.getByName(food.getName());
        if (check != null) {
            rb = new ResponseBean<>(500, "不能添加重名的食品");
            return rb;
        }

        int result = foodMapper.insert(food);
        if(result > 0) {
            rb = new ResponseBean<>(result);
        }else {
            rb = new ResponseBean<>(500,"Fail to add");
        }
        return rb;
    }

    // 需要级联更新
    @PostMapping("/update")
    public ResponseBean<Integer> update(@RequestBody Food data) {
        ResponseBean<Integer> rb = null;
        try {
            int result = foodService.updateFood(data);
            if(result > 0) {
                rb = new ResponseBean<>(result);
            }else {
                rb = new ResponseBean<>(500,"Fail to update");
            }
        } catch (Exception e) {
            rb = new ResponseBean<>(500, e.getMessage());
        }
        return rb;
    }

    // 需要级联删除
    @PostMapping("/delete")
    public ResponseBean<Integer> delete(@RequestBody Map<String, Object> request) {
        int id = (int) request.get("id");
        ResponseBean<Integer> rb = null;
        try {
            int result = foodService.deleteFoodById(id);
            if(result > 0) {
                rb = new ResponseBean<>(result);
            }else {
                rb = new ResponseBean<>(500,"Fail to update");
            }
        } catch (Exception e) {
            rb = new ResponseBean<>(500, e.getMessage());
        }
        return rb;
    }
}
