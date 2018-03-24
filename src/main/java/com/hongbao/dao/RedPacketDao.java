package com.hongbao.dao;

import com.hongbao.pojo.RedPacket;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author guoqing
 * @since ： 2018/3/18 19:53
 * description:
 */
@Repository
public interface RedPacketDao {
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

    /**
     * @Author:guoqing
     * @Description:通过乐观锁实现抢红包业务
     * @Date:2018/3/21 20:54
     * @param: id -- 红包id, Integer version --版本号
     * @return:int 影响的行数
     */
    public int decreaseRedPacketForVersion(@Param("id") Long id, @Param("version")
            Integer version);
}
