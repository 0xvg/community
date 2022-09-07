package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/a")
public class A {
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String say() {
        return "Hello spring Boot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest requst, HttpServletResponse response){
        //获取请求数据
        System.out.println(requst.getMethod());
        System.out.println(requst.getServletPath());
        Enumeration<String> enumeration=requst.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = requst.getHeader(name);
            System.out.println(name+":"+value);
        }
        System.out.println(requst.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(PrintWriter writer=response.getWriter()){
            writer.write("<h1>牛a</h1>");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //get 请求,获得参数

    //第一种方式：/students?current=1&limit=20
        @RequestMapping(path = "/students",method = RequestMethod.GET)
        @ResponseBody
        public String getStudents(
                @RequestParam(name = "current", required = false, defaultValue = "1")int current,
                @RequestParam(name = "limit", required = false, defaultValue = "10")int limit){
            System.out.println(current);
            System.out.println(limit);
            return "some students";
        }

    //第二种：/student/编号或者学号（查找一个学生）
        @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
        @ResponseBody
        public String getStudent(@PathVariable("id") int id ){//  @PathVariable 路径变量
            System.out.println(id);
            return "a student";
        }

    //post请求
        @RequestMapping(path = "/student",method = RequestMethod.POST)
        @ResponseBody//返回字符串
        public String saveStudent(String name,int age){
            System.out.println(name);
            System.out.println(age);
            return "success";
        }

    //响应HTML数据
      //（1）
        @RequestMapping(path = "/teacher",method = RequestMethod.GET)
        //不加@ResponseBody默认返回html
        public ModelAndView getTeacher(){
            ModelAndView mav = new ModelAndView();
            mav.addObject("name","夏");
            mav.addObject("age",20);
            mav.setViewName("/demo/view");
            return mav;
        }

      //（2）简单一些
        @RequestMapping(path = "/school",method = RequestMethod.GET)
        public String getSchool(Model model){
            model.addAttribute("name","...");
            model.addAttribute("age","...");
            return "/demo/view";
        }

    //响应JSON数据（异步请求：当前网页不刷新，访问了服务器数据）
    //浏览器解析对象是js对象，json（特定格式的字符串）它可以让java对象与js对象兼容
    //java对象 -> JSON字符串 -> js对象（可以让Java对象转化为任意一种对象）
        @RequestMapping(path = "/emp",method = RequestMethod.GET)
        @ResponseBody
        public Map<String,Object> getEmp(){
            Map<String,Object> emp = new HashMap<>();
            emp.put("name","张三");
            emp.put("age","30");
            emp.put("salary","6000.00");
            return emp;
        }//{"name":"张三","salary":"6000.00","age":"30"}输出这样的样式。json字符串

    //返回多个
        @RequestMapping(path = "/emps",method = RequestMethod.GET)
        @ResponseBody
        public List<Map<String,Object>> getEmps(){//多个，就用集合
            List<Map<String,Object>> list=new ArrayList<>();//创建集合
            Map<String,Object> emp = new HashMap<>();
            emp.put("name","张三");
            emp.put("age","30");
            emp.put("salary","6000.00");
            list.add(emp);

            emp = new HashMap<>();
            emp.put("name","许");
            emp.put("age","20");
            emp.put("salary","8000.00");
            list.add(emp);

            return list;
        }

    //cookie示例
        @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
        @ResponseBody
        public String setCookie(HttpServletResponse response){
            //创建cookie
            Cookie cookie=new Cookie("code", CommunityUtil.generateUUID());
            //cookie生效范围
            cookie.setPath("/community/a");
            //cookie生存时间
            cookie.setMaxAge(60*10);
            //发送cookie
            response.addCookie(cookie);
            return "set cookie";
        }

        @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
        @ResponseBody
        public String getCookie(@CookieValue("code") String code){
            System.out.println(code);
            return "get nima";
        }

        //cookie只能存字符串，session什么都行
        //session存放更加安全，但是增加内存压力。cookie保存小。
    //项目提问：
        /**
         *多服务器时，session会有问题，分布式储存，负载均衡。粘性session不完美，浏览器访问多个服务器时，按照最优先访问，
         * 并不是每次同一访问会是同一个服务器。
         * 解决：1.同步session，浏览器访问后，不论哪个服务器都同步给所有服务器。但是会影响性能，不理想。
         *      2.共享session，单独列一个服务器，专门存session，浏览器访问时，服务器向这个服务器申请session。
         *但是非常危险，单体服务器，如果这个服务器出现问题，那全部会出现问题
         *      3.把一些数据，能存在cookie里就放在cookie里。一些敏感数据，可以存放在数据库里（可以做集群备份），
         *服务器访问数据库来得到session。但是也会有一些问题，最好放在Redis里（nosql）
         */


    //session示例

        @RequestMapping(path = "/session/set",method = RequestMethod.GET)
        @ResponseBody
        public String setSession(HttpSession session){
            session.setAttribute("id",1);
            session.setAttribute("time","Test");
            return "set seses";
        }

        @RequestMapping(path = "/session/get",method = RequestMethod.GET)
        @ResponseBody
        public String getSession(HttpSession session){
            System.out.println(session.getAttribute("id"));
            System.out.println(session.getAttribute("time"));
            return "get";
        }

         public static void main(String[] args) {
            System.out.println(1);
        }
}
