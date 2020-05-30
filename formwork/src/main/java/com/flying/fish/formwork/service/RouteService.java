package com.flying.fish.formwork.service;

import com.flying.fish.formwork.base.BaseService;
import com.flying.fish.formwork.dao.RegServerDao;
import com.flying.fish.formwork.dao.RouteDao;
import com.flying.fish.formwork.entity.RegServer;
import com.flying.fish.formwork.entity.Route;
import com.flying.fish.formwork.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @Description 路由管理业务类
 * @Author jianglong
 * @Date 2020/05/14
 * @Version V1.0
 */
@Service
public class RouteService extends BaseService<Route,Long,RouteDao> {

//    @Autowired
//    private RouteDao routeDao;

//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
//    public void save(Route route){
//        routeDao.save(route);
//    }
//
//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
//    public void update(Route route){
//        routeDao.save(route);
//    }
//
//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
//    public void deleteById(Long id){
//        routeDao.deleteById(id);
//    }

//    public Route findById(Long id){
//        Optional<Route> optional = routeDao.findById(id);
//        if (optional.isPresent()){
//            return optional.get();
//        }
//        return null;
//    }

//    public List<Route> list(Route route){
//        return routeDao.findAll(Example.of(route,ExampleMatcher.matching()),new Sort(Sort.Direction.ASC,"createTime"));
//    }

//    public PageResult<Route> pageList(Route route, int currentPage, int pageSize){
//        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
//        Pageable pageable = PageRequest.of(currentPage-1,pageSize, sort);
//        Page<Route> pageData  =  routeDao.findAll(Example.of(route), pageable);
//        PageResult<Route> pageResult = new PageResult<>();
//        pageResult.setCurrentPage(currentPage);
//        pageResult.setPageSize(pageSize);
//        pageResult.setTotalNum(pageData.getTotalElements());
//        pageResult.setLists(pageData.getContent());
//        return pageResult;
//    }

}
