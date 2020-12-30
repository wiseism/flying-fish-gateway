package com.flying.fish.formwork.service;

import com.flying.fish.formwork.base.BaseService;
import com.flying.fish.formwork.dao.LoadServerDao;
import com.flying.fish.formwork.entity.LoadServer;
import com.flying.fish.formwork.util.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description 负载服务业务实现类
 * @Author jianglong
 * @Date 2020/06/28
 * @Version V1.0
 */
@Service
public class LoadServerService extends BaseService<LoadServer, Long, LoadServerDao> {

    @Resource
    private LoadServerDao loadServerDao;

    /**
     * 查询当前负载网关已加配置路由服务
     * @param balancedId
     * @return
     */
    @Transactional(readOnly = true)
    public List loadServerList(String balancedId){
        //String sql ="SELECT r.id,r.name,r.routeId,r.groupCode,r.uri,r.path,r.method FROM route r WHERE r.status='0' AND r.routeId IN (SELECT l.routeId FROM loadserver l WHERE l.balancedId=?)";
        //return pageNativeQuery(sql, Arrays.asList(balancedId), currentPage, pageSize);
        return loadServerDao.queryLoadServerList(balancedId);
    }

    /**
     * 查询所有路由服务
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult notLoadServerPageList(int currentPage, int pageSize){
        String sql ="SELECT r.id,r.name,r.groupCode,r.uri,r.path,r.method FROM route r WHERE r.status='0' ";
//        if (balancedId != null && balancedId > 0){
//            sql += " AND r.id NOT IN (SELECT l.routePrimaryId FROM loadserver l WHERE l.balancedId=?)";
//            return pageNativeQuery(sql, Arrays.asList(balancedId), currentPage, pageSize);
//        }
        return pageNativeQuery(sql, null, currentPage, pageSize);
    }

    /**
     *  删除指定负载下所有的路由服务
     * @param balancedId
     */
    public void deleteAllByBalancedId(String balancedId){
        loadServerDao.deleteAllByBalancedId(balancedId);
    }

    /**
     * 查询指定负载下所有路由服务
     * @param balancedId
     * @return
     */
    public List<LoadServer> queryByBalancedId(String balancedId){
        return loadServerDao.queryByBalancedId(balancedId);
    }

    /**
     * 保存指定负载下的所有路由服务
     * @param serverList
     */
    public void updates(String balancedId, List<LoadServer> serverList){
        List<LoadServer> dbServerList = this.queryByBalancedId(balancedId);
        Map<Long, Integer> dbIdMap = new HashMap<>();
        for (LoadServer loadServer : serverList){
            //新增
            if (loadServer.getId() == null || loadServer.getId() <= 0){
                loadServer.setBalancedId(balancedId);
                loadServer.setCreateTime(new Date());
                this.save(loadServer);
            }else {
                //记录未变更的ID
                dbIdMap.put(loadServer.getId(), loadServer.getWeight());
            }
        }

        if (!CollectionUtils.isEmpty(dbServerList)){
            //idList中不存在，则需要删除数据库中，已经被前端取消绑定的服务
            for (LoadServer loadServer : dbServerList){
                if (dbIdMap.get(loadServer.getId()) == null){
                    this.deleteById(loadServer.getId());
                }else {
                    //比较weight值是否有改变
                    Integer weight = dbIdMap.get(loadServer.getId());
                    if (loadServer.getWeight().intValue() != weight.intValue()){
                        loadServer.setWeight(weight.intValue());
                        this.update(loadServer);
                    }
                }
            }
        }
    }

}
