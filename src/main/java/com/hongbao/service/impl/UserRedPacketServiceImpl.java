package com.hongbao.service.impl;

import com.hongbao.dao.RedPacketDao;
import com.hongbao.dao.UserRedPacketDao;
import com.hongbao.pojo.RedPacket;
import com.hongbao.pojo.UserRedPacket;
import com.hongbao.service.RedisRedPacketService;
import com.hongbao.service.UserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

/**
 * @author guoqing
 * @since ： 2018/3/18 21:55
 * description:
 */

@Service
public class UserRedPacketServiceImpl implements UserRedPacketService {


    @Autowired
    private UserRedPacketDao userRedPacketDao;
    @Autowired
    private RedPacketDao redPacketDao;
    //失败
    private static final int FAILED = 0;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation
            .REQUIRED)
    public int grapRedPacket(Long redPacketId, Long userId) {
        //获取红包信息
        RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
        //当前红包库存大于0
        if (redPacket.getStock() > 0) {
            redPacketDao.decreaseRedPacket(redPacketId);
            //生成抢红包信息
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setAmount(redPacket.getUnitAmount());
            userRedPacket.setNote("抢红包" + redPacketId);
            int result = userRedPacketDao.grapRedPacket(userRedPacket);
            return result;
        }

        return FAILED;
    }

    //乐观锁解决超发现象
    /*@Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation
            .REQUIRED)
    public int grapRedPacketForVersion(Long redPacketId, Long userId) {
        //获取红包信息
        RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
        //当前红包库存大于0
        if (redPacket.getStock() > 0) {
            //再次传入线程保存的version旧值给Sql判断，是否有其他线程修改过数据
            int update = redPacketDao.decreaseRedPacketForVersion(redPacketId, redPacket
                    .getVersion());
            //如果没有数据更新，则说明其他线程已经修改过数据本次抢红包失败
            if (update == 0) {
                return FAILED;
            }
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setAmount(redPacket.getUnitAmount());
            userRedPacket.setNote("抢红包" + redPacketId);
            int result = userRedPacketDao.grapRedPacket(userRedPacket);
            return result;
        }

        return FAILED;
    }*/

    //乐观锁重入机制--解决成功率问题(通过时间戳)
   /* @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation
            .REQUIRED)
    public int grapRedPacketForVersion(Long redPacketId, Long userId) {
        long start = System.currentTimeMillis();
        while (true) {
            long end = System.currentTimeMillis();
            if (end - start > 100) {
                return FAILED;
            }
            //获取红包信息
            RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
            //当前红包库存大于0
            if (redPacket.getStock() > 0) {
                //再次传入线程保存的version旧值给Sql判断，是否有其他线程修改过数据
                int update = redPacketDao.decreaseRedPacketForVersion(redPacketId,
                        redPacket

                        .getVersion());
                //如果没有数据更新，则说明其他线程已经修改过数据本次抢红包失败
                if (update == 0) {
                    continue;
                }
                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setUserId(userId);
                userRedPacket.setAmount(redPacket.getUnitAmount());
                userRedPacket.setNote("抢红包" + redPacketId);
                int result = userRedPacketDao.grapRedPacket(userRedPacket);
                return result;
            }else{
                return FAILED
            }

            return FAILED;
        }
    }*/
    //乐观锁重入机制--解决成功率问题(通过重试次数)
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation
            .REQUIRED)
    public int grapRedPacketForVersion(Long redPacketId, Long userId) {
        for (int i = 0; i < 3; i++) {
            //获取红包信息
            RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
            //当前红包库存大于0
            if (redPacket.getStock() > 0) {
                //再次传入线程保存的version旧值给Sql判断，是否有其他线程修改过数据
                int update = redPacketDao.decreaseRedPacketForVersion(redPacketId,
                        redPacket.getVersion());
                //如果没有数据更新，则说明其他线程已经修改过数据本次抢红包失败
                if (update == 0) {
                    continue;
                }
                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setUserId(userId);
                userRedPacket.setAmount(redPacket.getUnitAmount());
                userRedPacket.setNote("抢红包" + redPacketId);
                int result = userRedPacketDao.grapRedPacket(userRedPacket);
                return result;
            } else {
                return FAILED;
            }
        }
        return FAILED;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisRedPacketService redisRedPacketService;

    //Lua脚本
    String script = "local listKey = 'red_packet_list_'..KEYS[1] \n" +
            "local redPacket = 'red_packet_'..KEYS[1] \n" +
            "local stock = tonumber(redis.call('hget',redPacket,'stock')) \n" +
            "if stock <= 0 then return 0 end \n" +
            "stock = stock-1 \n" +
            "redis.call('hset',redPacket,'stock',tostring(stock)) \n" +
            "redis.call('rpush',listKey,ARGV[1]) \n" +
            "if stock == 0 then return 2 end \n" +
            "return 1 \n";
    String shal = null;

    @Override
    public Long grapRedPacketByRedis(Long redPacketId, Long userId) {
        String args = userId + "-" + System.currentTimeMillis();
        Long result;
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        RedisConnection connection = connectionFactory.getConnection();
        Jedis jedis = (Jedis) connection.getNativeConnection();
        try {
            if (shal == null) {
                shal = jedis.scriptLoad(script);
            }
            Object res = jedis.evalsha(shal, 1, redPacketId + "", args);
            result = (Long) res;
            if (result == 2) {
                //获取单个小红包金额
                String unitAmountStr = jedis.hget("red_packet_" + redPacketId,
                        "unit_amount");
                Double unitAmount = Double.parseDouble(unitAmountStr);
                System.err.println("thread_name = " + Thread.currentThread().getName());
                redisRedPacketService.saveUserRedPacketByRedis(redPacketId, unitAmount);
            }
        } finally {
            if (jedis != null && jedis.isConnected()) {
                jedis.close();

            }
        }
        return result;
    }
}
