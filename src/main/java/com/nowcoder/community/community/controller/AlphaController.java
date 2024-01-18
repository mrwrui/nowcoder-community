package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.service.AlphaService;
import com.nowcoder.community.community.util.CommunityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    //处理浏览器的请求和响应的原始底层方式，后面会介绍简单的方式
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        // 获取请求数据
        // 请求方法
        System.out.println(request.getMethod());
        // 请求路径
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code"));

        // response 是用来向浏览器做出相应的对象，返回响应数据的对象
        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try (
                PrintWriter writer = response.getWriter();
                ) {
            writer.write("<h1>牛客网<h1>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // GET 请求怎么处理，我们希望向服务器获取某些数据，通常使用的请求就是GET请求
    //请求查询所有的学生 /students?current=1&limit=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)     //明确只处理GET请求
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    //如果不是查询所有的学生，只是查询一个学生： /student/123 这样的话参数就成为了路径的一部分
    @RequestMapping(path="/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    //POST请求 浏览器要向服务器提交数据 其实如果使用GET的话也能把参数传过来，但是GET的参数是使用?para=value这种一大串，并且路径带参数路径是有有长度限制的，传输的数据量有限
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //上面说的是GET和POST两个请求，响应的都是一个简单的字符串，接下来我们相应一个动态的HTML
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
         ModelAndView mav = new ModelAndView();
         mav.addObject("name","张三");
         mav.addObject("age","30");
         //模板的路径和名字,这里使用的路径是/demo由于thymeleaf使用的模板直接就是html文件，我们在文件名后面不需要加上.html，直接写名字就行
        mav.setViewName("/demo/view");
        return mav;
    }

    //向浏览器响应动态HTML的另一种方法
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name", "Peking University");
        model.addAttribute("age","80");
        return"/demo/view";
    }

    //向浏览器响应JSON数据，一般是在异步请求当中：当前网页不刷新，悄悄的访问了一次服务器，例如注册账号中判断你的账号是否可用
    //将Java对象返回浏览器 浏览器希望得到JS对象，无法直接将JAVA对象转化为JS对象，JSON可以实现两者的兼容 Java对象 -> JSON字符串 —> JS对象
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody //加上这个才能够返回一个JSON文件，如果不加上这个默认返回的是一个HTML
    public Map<String, Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","wurui");
        emp.put("age",23);
        emp.put("salary",5000);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody //加上这个才能够返回一个JSON文件，如果不加上这个默认返回的是一个HTML
    public List<Map<String, Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","wurui");
        emp.put("age",23);
        emp.put("salary",20000);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","里斯");
        emp.put("age",24);
        emp.put("salary",20000);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","王五");
        emp.put("age",24);
        emp.put("salary",20000);
        list.add(emp);

        return list;
    }

    // Cookie实例 服务端代码
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public  String setCookie(HttpServletResponse response){
        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置Cookie生效的范围
        cookie.setPath("/community/alpha");
        // 设置Cookie的生存周期
        cookie.setMaxAge(60 * 10);
        // 发送cookie
        response.addCookie(cookie);

        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    // 使用@CookieValue注解能够从众多的cookie中根据key选取我们想要的那个cookie
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }

}

