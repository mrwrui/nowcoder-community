package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.service.LikeService;
import com.nowcoder.community.community.util.CommunityUtil;
import com.nowcoder.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId) {
        User user = hostHolder.getUser();
        // 点赞
        likeService.like(user.getId(), entityType,entityId,entityUserId);
        // 数量
        Long likeCount = likeService.findEntityLikeCount(entityType,entityId);
        // 状态
        int status = likeService.findEntityLikeStatus(user.getId(), entityType,entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", status);
        return CommunityUtil.getJSONString(0, null, map);
    }
}
