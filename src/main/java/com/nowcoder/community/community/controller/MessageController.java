package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.entity.Message;
import com.nowcoder.community.community.entity.Page;
import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.service.MessageService;
import com.nowcoder.community.community.service.UserService;
import com.nowcoder.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // 私信列表
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        // 查询会话列表
        List<Message> conversationList = messageService.findConversation(
                user.getId(), page.getOffset(), page.getLimit());
        // List<Map<String,Object>> conversations;
        List<Map<String,Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("message",message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unReadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询未读消息数量 这里仍然使用查询每个会话的未读私信数量来查询用户的所有会话未读私信的数量，只要把会话ID传入null即可
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        return "/site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for(Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                User fromUser = userService.findUserById(message.getFromId());
                map.put("fromUser", fromUser);
                letters.add(map);
            }
            model.addAttribute("letters",letters);
            User targetUser = getLetterTarget(conversationId, user);
            model.addAttribute("targetUser", targetUser);
        }
        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId, User user) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        int targetId = id0==user.getId() ? id1 : id0;
        return userService.findUserById(targetId);
    }



}
