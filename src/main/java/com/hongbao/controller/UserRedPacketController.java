package com.hongbao.controller;

import com.hongbao.service.UserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author guoqing
 * @since ： 2018/3/19 20:05
 * description:
 */
@Controller
@RequestMapping("/userRedPacket")
public class UserRedPacketController {

    @Autowired
    private UserRedPacketService userRedPacketService;

    @RequestMapping(value = "/grapRedPacket")
    @ResponseBody
    public Map<String, Object> grapRedPacket(Long redPacketId, Long userId) {

        //抢红包
        int result = userRedPacketService.grapRedPacket(redPacketId, userId);
        Map<String, Object> resultMap = new HashMap<>();
        boolean flag = result > 0;
        return getStringObjectMap(resultMap, (long) result);
    }

    @RequestMapping(value = "/grapRedPacketForVersion")
    @ResponseBody
    public Map<String, Object> grapRedPacketForVersion(Long redPacketId, Long userId) {
        int result = userRedPacketService.grapRedPacketForVersion(redPacketId, userId);
        Map<String, Object> resultMap = new HashMap<>();
        return getStringObjectMap(resultMap, (long) result);
    }

    @RequestMapping(value = "/graRedPacketByRedis")
    @ResponseBody
    public Map<String, Object> graRedPacketByRedis(Long redPacketId, Long userId) {
        Map<String, Object> resultMap = new HashMap<>();
        Long result = userRedPacketService.grapRedPacketByRedis(redPacketId, userId);
        return getStringObjectMap(resultMap, result);
    }

    private Map<String, Object> getStringObjectMap(Map<String, Object> resultMap, Long
            result) {
        boolean flag = result > 0;
        resultMap.put("success", flag);
        resultMap.put("message", flag ? "抢红包成功" : "抢红包失败");
        return resultMap;
    }
}
