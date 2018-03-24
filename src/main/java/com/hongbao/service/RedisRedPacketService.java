package com.hongbao.service;

/**
 * @author guoqing
 * @since ： 2018/3/22 20:36
 * description:
 */
public interface RedisRedPacketService {
    /**
     * @param unitAmount 红包金额
     * @author guoqing
     * @description 保存redis抢红包列表
     * @date 2018/3/22 20:37
     * @param redPacketId 抢红包编号
     * @return:
     */
    public void saveUserRedPacketByRedis(Long redPacketId, Double unitAmount);
}
