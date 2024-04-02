package com.nowcoder.community.community.controller;
import com.nowcoder.community.community.annotation.LoginRequired;
import com.nowcoder.community.community.dao.UserMapper;
import com.nowcoder.community.community.entity.Page;
import com.nowcoder.community.community.entity.User;

import com.nowcoder.community.community.service.FollowService;
import com.nowcoder.community.community.service.LikeService;
import com.nowcoder.community.community.service.UserService;
import com.nowcoder.community.community.util.CommunityConstant;
import com.nowcoder.community.community.util.CommunityUtil;
import com.nowcoder.community.community.util.HostHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;


@Controller
@RequestMapping(path = "/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if(headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)) {
            model.addAttribute("error","文件的格式不正确！");
            return "/site/setting";
        }
        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + fileName;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }

        // 更新当前用户的头像路径（Web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放的路径
        fileName = uploadPath + "/" + fileName;
        // 文件的后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                // java7的语法，写在这里最后会关闭，前提是有close方法
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
                ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1) {
                os.write(buffer,0, b);
            }
        } catch (IOException e) {
            logger.error("读取图片失败！", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 个人主页
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user==null) {
            throw new RuntimeException("该用户不存在");
        }
        // 用户
        model.addAttribute("user", user);
        // 点赞
        int userLikeCount = likeService.userLikeCount(userId);
        model.addAttribute("likeCount",userLikeCount);
        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser()!=null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),userId,ENTITY_TYPE_USER);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

    @LoginRequired
    @RequestMapping(path = "/change",method = RequestMethod.POST)
    public String changePassword(String oldPassword, String newPassword, String confirmPassword1, Model model) {
        if (oldPassword == null) {
            model.addAttribute("error", "请输入原密码！");
            return "/site/setting";
        }
        if (newPassword == null) {
            model.addAttribute("error", "请输入新密码！");
            return "/site/setting";
        }
        if (confirmPassword1 == null || !confirmPassword1.equals(newPassword)) {
            model.addAttribute("error2", "两次密码不一致！");
            return "/site/setting";
        }

        if (newPassword.length() < 8) {
            model.addAttribute("error1", "新密码的长度不能少于8位！");
            return "/site/setting";
        }

        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            model.addAttribute("codeMsg", "原密码不正确！");
            return "/site/setting";
        }
        newPassword = (CommunityUtil.md5(newPassword + user.getSalt()));
        userMapper.updatePassword(user.getId(),newPassword);
        return "redirect:/login";
    }

}
