package com.wanhutong.backend.modules.sys.web;

import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.office.SysOfficeAddress;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.office.SysOfficeAddressService;
import org.activiti.engine.repository.Model;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/sys/viewMap")
public class ViewMapController extends BaseController {
    @Autowired
    private SysOfficeAddressService sysOfficeAddressService;
    @Autowired
    private OfficeService officeService;

    @RequiresPermissions("sys:viewMap:view")
    @RequestMapping(value = "purchaserList")
    public String purchaserList() {

        return "modules/sys/purchasersMap";
    }

    @RequiresPermissions("sys:viewMap:view")
    @RequestMapping(value = "supplierList")
    public String supplierList() {

        return "modules/sys/supplierMap";
    }

    @RequiresPermissions("sys:viewMap:view")
    @RequestMapping(value = "centerList")
    public String centerList() {

        return "modules/sys/centerMap";
    }

    @ResponseBody
    @RequiresPermissions("sys:viewMap:view")
    @RequestMapping(value = "findOfficeAddress")
    public Map<String,Object> findOfficeAddress(String type){
        Map<String,Object> map =new HashMap<>();
        SysOfficeAddress sysOfficeAddress=new SysOfficeAddress();
        Office office=new Office();
        sysOfficeAddress.setType(2);
        sysOfficeAddress.setOffice(office);
        if("8".equals(type)){
            List<SysOfficeAddress> list= sysOfficeAddressService.findListByTypes(sysOfficeAddress);
            map.put("list",list);
            return map;
         }
        office.setType(type);
        List<Office> officeList=officeService.queryList(office);
        map.put("ofCount",officeList.size());
       List<SysOfficeAddress> list= sysOfficeAddressService.findList(sysOfficeAddress);
        map.put("list",list);
        return map;
    }


}
