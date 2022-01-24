package hello.controller;

import edu.emory.mathcs.backport.java.util.Collections;
import hello.anno.ReadRoleIdInSession;
import hello.anno.ReadUserIdInSession;
import hello.entity.Route;
import hello.entity.RouteResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RouteController {
    @ReadRoleIdInSession
    @GetMapping("/user/menu")
    public RouteResult getUserMenu(Integer roleId){
        // 根据用户的权限返回相应的路由
        if(roleId == 1){
            List<Route> routes = new ArrayList<>();
            Route route = new Route();
            route.setId(1);
            route.setPid(0);
            route.setName("我的任务");
            route.setPath("/");
            route.setRedirect("/display/task");
            route.setComponent("BasicLayout");
            route.setIcon("AppleOutlined");
            route.setKey("layout");

            Route route2 = new Route();
            List<Route> routeChildren = new ArrayList<>();
            List<Route> route2children = new ArrayList<>();
            route2.setId(2);
            route2.setPid(1);
            route2.setName("我的任务");
            route2.setPath("/display/task");
            route2.setRedirect("/display/task");
            route2.setIcon("ChromeOutlined");
            route2.setKey("element");
            route2.setComponent("RouteView");

            Route route3 = new Route();
            route3.setId(10);
            route3.setPid(2);
            route3.setName("进行中的任务");
            route3.setPath("/display/task");
            route3.setRedirect("");
            route3.setComponent("/display/task");
            route3.setIcon("");
            route3.setKey("el_icon");


            Route route4 = new Route();
            route4.setId(11);
            route4.setPid(2);
            route4.setName("已有产值的任务");
            route4.setPath("/display/doneTask");
            route4.setRedirect("");
            route4.setComponent("/display/doneTask");
            route4.setIcon("");
            route4.setKey("el_done");


            Route route5 = new Route();
            List<Route> route5children = new ArrayList<>();
            route5.setId(3);
            route5.setPid(1);
            route5.setName("排行榜");
            route5.setPath("/rank/peopleRank");
            route5.setRedirect("/rank/peopleRank");
            route5.setIcon("ChromeOutlined");
            route5.setKey("rank");
            route5.setComponent("RouteView");



            Route route6 = new Route();
            route6.setId(4);
            route6.setPid(3);
            route6.setName("个人排行榜");
            route6.setPath("/rank/peopleRank");
            route6.setRedirect("");
            route6.setIcon("ChromeOutlined");
            route6.setKey("peopleRank");
            route6.setComponent("/rank/peopleRank");

            route5children.add(route6);
            route5.setChildren(route5children);



            route2children.add(route3);
            route2children.add(route4);
            route2.setChildren(route2children);
            routeChildren.add(route2);
            routeChildren.add(route5);

            route.setChildren(routeChildren);


            routes.add(route);
            return RouteResult.success(routes);
        }
        return RouteResult.failure("路由异常");
    }
}
