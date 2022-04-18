package com.ali.controller;

import com.ali.pojo.ProductInfo;
import com.ali.service.ProductInfoService;
import com.ali.utils.FileNameUtil;
import com.github.pagehelper.PageInfo;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RequestMapping("/prod")
@Controller
public class ProductInfoAction {
    //每页显示的记录数
    public static final int PAGE_SIZE = 5;

    //异步上传的文件名称
    String saveFileName = "";
    @Autowired
    private ProductInfoService productInfoService;

    @RequestMapping("/getAll")
    public String getAll(HttpServletRequest request){
        List<ProductInfo> list = productInfoService.getAll();
        request.setAttribute("list",list);
        return "product";
    }

    //显示第1页的5条记录
    @RequestMapping("/split")
    public String split(HttpServletRequest request){
        //得到第1页的数据
        PageInfo info = productInfoService.splitPage(1, PAGE_SIZE);
        request.setAttribute("info", info);
        return "product";
    }

    //ajax分页翻页处理
    @ResponseBody
    @RequestMapping("/ajaxsplit")
    public void ajaxSplit(int page, HttpSession session){
        //取得当前page参数的页面数据
        PageInfo info = productInfoService.splitPage(page, PAGE_SIZE);
        session.setAttribute("info", info);
    }

    @ResponseBody
    @RequestMapping("/ajaxImg")
    public Object ajaxImg(MultipartFile pimage, HttpServletRequest request){
        //进行文件上传操作
        //取文件名
        saveFileName  = FileNameUtil.getUUIDFileName() + FileNameUtil.getFileType(pimage.getOriginalFilename());
        String path = request.getServletContext().getRealPath("/image_big");
        //转存
        try {
            pimage.transferTo(new File(path + File.separator + saveFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //为了能够回显图片=》手工处理JSON
        JSONObject object = new JSONObject();
        object.put("imgurl", saveFileName);
        //JSON对象toString()回到客户端
        return object.toString();
    }

    @RequestMapping("/save")
    public String save(ProductInfo info, HttpServletRequest request){
        info.setpImage(saveFileName);
        info.setpDate(new Date());
        //info对象中有表单提交上来的5个数据，有异步ajax上来的图片名称，上架的时间
        int num = -1;
        try {
            num = productInfoService.save(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(num > 0){
            request.setAttribute("msg","增加成功！");
        }else{
            request.setAttribute("msg","增加失败！");
        }
        //清空saveFileName变量中的内容，为了下次增加或修改的异步ajax的上传处理
        saveFileName = "";
        //增加成功后，重新访问数据库，所以跳转到分页显示的action上
        return "forward:/prod/split.action";
    }

    @RequestMapping("/one")
    public String one(int pid, Model model){
        ProductInfo info = productInfoService.getById(pid);
        model.addAttribute("prod", info);
        return "update";
    }

    @RequestMapping("/update")
    public String update(ProductInfo info, HttpServletRequest request){
        //因为ajax的异步图片上传，如果有上传过，
        //则saveFileName里有上传上来的图片的名称
        //如果没有使用异步ajax上传过图片，则saveFileName="";
        //实体类info使用隐藏表单域提供上来的pImage原始图片的名称
        if(!saveFileName.equals("")){
            info.setpImage(saveFileName);
        }
        //完成更新处理
        int num = -1;
        try {
            num = productInfoService.update(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(num > 0){
            //更新成功
            request.setAttribute("msg","更新成功！");
        }else{
            //更新失败
            request.setAttribute("msg","更新失败！");
        }
        //处理完后，saveFileName里面可能有数据
        //而下一次更新时要使用这个变量作为判断的依据，就会出错，所以必须清空saveFileName
        saveFileName = "";
        return "forward:/prod/split.action";
    }

    @RequestMapping("/delete")
    public String delete(int pid, HttpServletRequest request){
        int num = -1;
        try {
            num = productInfoService.delete(pid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(num > 0){
            request.setAttribute("msg", "删除成功！");
        }else{
            request.setAttribute("msg","删除失败！");
        }
        //删除结束后跳到分页显示
        return "forward:/prod/deleteAjaxSplit.action";
    }

    @ResponseBody
    @RequestMapping(value = "/deleteAjaxSplit", produces = "text/html;;charset=UTF-8")
    public Object deleteAjaxSplit(HttpServletRequest request){
        //取得第1页的数据
        PageInfo info = productInfoService.splitPage(1, PAGE_SIZE);
        request.getSession().setAttribute("info", info);
        return request.getAttribute("msg");
    }
}
