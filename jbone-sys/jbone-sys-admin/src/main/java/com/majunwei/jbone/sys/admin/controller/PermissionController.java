package com.majunwei.jbone.sys.admin.controller;

import com.majunwei.jbone.common.ui.result.Result;
import com.majunwei.jbone.common.utils.ResultUtils;
import com.majunwei.jbone.sys.dao.domain.RbacPermissionEntity;
import com.majunwei.jbone.sys.dao.domain.RbacSystemEntity;
import com.majunwei.jbone.sys.service.MenuService;
import com.majunwei.jbone.sys.service.PermissionService;
import com.majunwei.jbone.sys.service.SystemService;
import com.majunwei.jbone.sys.service.model.ListModel;
import com.majunwei.jbone.sys.service.model.menu.TreeMenuModel;
import com.majunwei.jbone.sys.service.model.permission.PermissionBaseInfoModel;
import com.majunwei.jbone.sys.service.model.permission.PermissionCreateModel;
import com.majunwei.jbone.sys.service.model.permission.PermissionUpdateModel;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private MenuService menuService;

    @Description("权限管理首页")
    @RequiresRoles("admin")
    @RequestMapping("/index")
    public String index(){
        return "pages/permission/index";
    }

    @Description("权限管理分页查询")
    @RequiresRoles("admin")
    @RequestMapping("/list")
    @ResponseBody
    public Result list(ListModel listModel){
        PageRequest pageRequest = new PageRequest(listModel.getPageNumber()-1,listModel.getPageSize(), Sort.Direction.fromString(listModel.getSortOrder()),listModel.getSortName());
        //分页查找
        Page<RbacPermissionEntity> page = permissionService.findPage(listModel.getSearchText(),pageRequest);
        List<PermissionBaseInfoModel> list = permissionService.getBaseInfos(page.getContent());
        return ResultUtils.wrapSuccess(page.getTotalElements(),list);
    }

    @Description("跳转至新增权限页面")
    @RequestMapping("/toCreate")
    public String toCreate(ModelMap modelMap){
        List<RbacSystemEntity> systemEntities = systemService.findAll();
        modelMap.put("systemList",systemEntities);
        return "pages/permission/create";
    }

    @Description("执行新增权限")
    @RequestMapping("/create")
    @ResponseBody
    public Result create(@Validated PermissionCreateModel createModel, BindingResult bindingResult){
        permissionService.save(createModel);
        return ResultUtils.wrapSuccess();
    }

    @Description("跳转至更新权限页面")
    @RequestMapping("/toUpdate/{id}")
    public String toUpdate(@PathVariable("id")int id, ModelMap model){
        PermissionBaseInfoModel permissionBaseInfoModel = permissionService.getBaseInfo(id);
        List<RbacSystemEntity> systemEntities = systemService.findAll();
        model.put("systemList",systemEntities);
        model.put("permission",permissionBaseInfoModel);
        if(permissionBaseInfoModel.getMenuId() != 0){
            TreeMenuModel menuModel = menuService.get(permissionBaseInfoModel.getMenuId());
            model.put("menuModel",menuModel);
        }
        return "pages/permission/update";
    }

    @Description("执行更新权限")
    @RequestMapping("/update")
    @ResponseBody
    public Result update(@Validated PermissionUpdateModel updateModel, BindingResult bindingResult){
        permissionService.update(updateModel);
        return ResultUtils.wrapSuccess();
    }

    @Description("批量删除权限")
    @RequestMapping("/delete/{ids}")
    @ResponseBody
    public Result delete(@PathVariable("ids")String ids){
        permissionService.delete(ids);
        return ResultUtils.wrapSuccess();
    }

    @Description("获取权限详情")
    @RequestMapping("/get/{id}")
    @ResponseBody
    public Result get(@PathVariable("id")int id){
        return ResultUtils.wrapSuccess(permissionService.getBaseInfo(id));
    }

    @Description("获取权限")
    @RequestMapping("/getPermissions/{systemId}/{menuId}")
    @ResponseBody
    public Result getPermissions(@PathVariable("systemId")int systemId,@PathVariable("menuId")int menuId){
        return ResultUtils.wrapSuccess(permissionService.getPermissions(systemId,menuId));
    }
}
