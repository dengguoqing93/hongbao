package com.hongbao.dao;

import com.hongbao.pojo.UserRedPacket;
import org.springframework.stereotype.Repository;

/**
 * @author guoqing
 * @since ： 2018/3/18 20:41
 * description:
 */
@Repository
public interface UserRedPacketDao {
    /**
     * @Author:guoqing
     * @Description:插入抢红包信息
     * @Date:2018/3/18 20:42
     * @param: userRedPacket 抢红包信息
     * @return: 影响记录数
     */
    public int grapRedPacket(UserRedPacket userRedPacket);
}
