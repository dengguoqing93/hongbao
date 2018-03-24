package com.hongbao.service;

import com.hongbao.pojo.RedPacket;

/**
 * @author guoqing
 * @since ： 2018/3/18 20:47
 * description:
 */
public interface RedPacketService {
    /**
     * @Author:guoqing
     * @Description:获取红包信息
     * @Date:2018/3/18 19:53
     * @param:id 红包id
     * @return: 红包的详细信息
     */
    public RedPacket getRedPacket(Long id);

    /**
     * @Author:guoqing
     * @Description:扣减红包数
     * @Date:2018/3/18 19:55
     * @param: id -- 红包id
     * @return: 更新记录条数
     */
    public int decreaseRedPacket(Long id);


}
