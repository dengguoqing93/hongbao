package com.hongbao.service;

/**
 * @author guoqing
 * @since ： 2018/3/18 20:47
 * description:
 */
public interface UserRedPacketService {
    /**
     * @Author:guoqing
     * @Description:插入抢红包信息
     * @Date:2018/3/18 20:42
     * @param: userRedPacket 抢红包信息
     * @return: 影响记录数
     */
    public int grapRedPacket(Long redPacketId, Long userId);

    /**
     * @Author:guoqing
     * @Description:通过乐观锁实现抢红包业务
     * @Date:2018/3/21 20:54
     * @param: id -- 红包id, Integer version --版本号
     * @return:int 影响的行数
     */
    public int grapRedPacketForVersion(Long redPacketId, Long userId);

    /**
      *@author guoqing
      *@description 通过Redis实现抢红包
      *@date 2018/3/22 21:28
      *@param redPacketId 红包编号
      *@param userId 用户编号
      *@return: 0-没有库存，失败
      *          1--成功且不是最后一个红包，
      *          2--成功，且是最有一个红包
      */
    public Long grapRedPacketByRedis(Long redPacketId, Long userId);
}
