package com.hongbao.service.impl;

import com.hongbao.pojo.UserRedPacket;
import com.hongbao.service.RedisRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guoqing
 * @since ： 2018/3/22 20:43
 * description:
 */
@Service
public class RedisRedPacketServiceImpl implements RedisRedPacketService {
    private static final String PREFIX = "red_packet_list_";
    private static final int TIME_SIZE = 1000;

    @Autowired
    private RedisTemplate redisTemplate;

    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;

    @Override
    @Async
    public void saveUserRedPacketByRedis(Long redPacketId, Double unitAmount) {
        System.out.println("开始保存数据");
        Long start = System.currentTimeMillis();
        //获取列表操作对象
        BoundListOperations ops = redisTemplate.boundListOps(PREFIX + redPacketId);
        Long size = ops.size();
        Long times = size % TIME_SIZE == 0 ? size / TIME_SIZE : size / TIME_SIZE + 1;
        int count = 0;
        List<UserRedPacket> userRedPackets = new ArrayList<>(TIME_SIZE);
        for (int i = 0; i < times; i++) {
            List userIdList;
            if (i == 0) {
                userIdList = ops.range(i * TIME_SIZE, (i + 1) * TIME_SIZE);
            } else {
                userIdList = ops.range(i * TIME_SIZE + 1, (i + 1) * TIME_SIZE);
            }
            userRedPackets.clear();
            for (int j = 0; j < userIdList.size(); j++) {
                String args = userIdList.get(j).toString();
                String[] arr = args.split("-");
                String userIdStr = arr[0];
                String timeStr = arr[1];
                Long userId = Long.parseLong(userIdStr);
                Long time = Long.parseLong(timeStr);
                //生成抢红包信息
                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setUserId(userId);
                userRedPacket.setAmount(unitAmount);
                userRedPacket.setGrabTime(new Timestamp(time));
                userRedPacket.setNote("抢红包" + redPacketId);
                userRedPackets.add(userRedPacket);
            }
            count += executeBatch(userRedPackets);
        }
        //删除Redis列表
        redisTemplate.delete(PREFIX + redPacketId);
        Long end = System.currentTimeMillis();
        System.err.println("保存数据结束，耗时" + (end - start) + "ms,共" + count + "条记录被保存。");
    }

    private int executeBatch(List<UserRedPacket> userRedPackets) {

        int[] count = null;

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            for (UserRedPacket userRedPacket : userRedPackets) {
                String sql1 = "UPDATE T_RED_PACKET set stock = stock-1 WHERE " +
                        "id = " + userRedPacket.getRedPacketId();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String sql2 = "INSERT into T_USER_RED_PACKET(red_packet_id, user_id, " +
                        "amount, grab_time, note) VALUES (" + userRedPacket
                        .getRedPacketId() + "," + userRedPacket.getUserId() + "," +
                        userRedPacket.getAmount() + ",'" + df.format(userRedPacket
                        .getGrabTime()) + "'," + "'" + userRedPacket.getNote() + "')";
                statement.addBatch(sql1);
                statement.addBatch(sql2);
            }
            count = statement.executeBatch();
            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return count.length / 2;
    }
}
