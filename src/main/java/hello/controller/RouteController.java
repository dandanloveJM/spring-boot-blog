package hello.controller;

import hello.anno.ReadRoleIdInSession;
import hello.entity.Route;
import hello.entity.RouteResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RouteController {

    private List<Route> getRoutes(String undoneTaskURL, String doneTaskURL, String teamRankURL) {
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
        route3.setComponent(undoneTaskURL);
        route3.setIcon("");
        route3.setKey("el_icon");


        Route route4 = new Route();
        route4.setId(11);
        route4.setPid(2);
        route4.setName("已有产值的任务");
        route4.setPath("/display/doneTask");
        route4.setRedirect("");
        route4.setComponent(doneTaskURL);
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


        Route route7 = new Route();
        route7.setId(5);
        route7.setPid(3);
        route7.setName("团队排行榜");
        route7.setPath("/rank/teamRank");
        route7.setRedirect("");
        route7.setIcon("ChromeOutlined");
        route7.setKey("teamRank");
        route7.setComponent("/rank/teamRank");
        route5children.add(route7);


        Route route10 = new Route();
        route10.setId(10);
        route10.setPid(1);
        route10.setName("修改密码");
        route10.setPath("/userInfo/resetPassword");
        route10.setRedirect("");
        route10.setIcon("ChromeOutlined");
        route10.setKey("resetPassword");
        route10.setComponent("/userInfo/resetPassword");



        routeChildren.add(route2);
        routeChildren.add(route5);

        if (teamRankURL != null) {
            Route route8 = new Route();
            route8.setId(6);
            route8.setPid(3);
            route8.setName("30%产值待分配");
            route8.setPath("/rank/teamBonus");
            route8.setRedirect("");
            route8.setIcon("ChromeOutlined");
            route8.setKey("teamBonus");
            route8.setComponent("/rank/teamBonus");

            routeChildren.add(route8);

            Route route9 = new Route();
            route9.setId(6);
            route9.setPid(3);
            route9.setName("数据分析");
            route9.setPath("/rank/chart");
            route9.setRedirect("");
            route9.setIcon("ChromeOutlined");
            route9.setKey("chart");
            route9.setComponent("/rank/chart");

            routeChildren.add(route9);


        }


        route5.setChildren(route5children);


        route2children.add(route3);
        route2children.add(route4);
        route2.setChildren(route2children);
        routeChildren.add(route10);

        route.setChildren(routeChildren);
        routes.add(route);
        return routes;
    }


    private List<Route> getR5Routes() {
        List<Route> routes = new ArrayList<>();
        Route route = new Route();
        route.setId(1);
        route.setPid(0);
        route.setName("排行榜");
        route.setPath("/");
        route.setRedirect("/rank/peopleRank");
        route.setComponent("BasicLayout");
        route.setIcon("AppleOutlined");
        route.setKey("layout");

        Route route2 = new Route();
        List<Route> routeChildren = new ArrayList<>();
        List<Route> route2Children = new ArrayList<>();
        route2.setId(2);
        route2.setPid(1);
        route2.setName("排行榜");
        route2.setPath("/rank/peopleRank");
        route2.setRedirect("/rank/peopleRank");
        route2.setIcon("ChromeOutlined");
        route2.setKey("element");
        route2.setComponent("RouteView");



        Route route6 = new Route();
        route6.setId(4);
        route6.setPid(1);
        route6.setName("个人排行榜");
        route6.setPath("/rank/peopleRank");
        route6.setRedirect("");
        route6.setIcon("ChromeOutlined");
        route6.setKey("peopleRank");
        route6.setComponent("/rank/peopleRank");

        route2Children.add(route6);


        Route route7 = new Route();
        route7.setId(5);
        route7.setPid(1);
        route7.setName("团队排行榜");
        route7.setPath("/rank/teamRank");
        route7.setRedirect("");
        route7.setIcon("ChromeOutlined");
        route7.setKey("teamRank");
        route7.setComponent("/rank/teamRank");
        route2Children.add(route7);


        Route route8 = new Route();
        route8.setId(6);
        route8.setPid(3);
        route8.setName("30%产值待分配");
        route8.setPath("/rank/teamBonus");
        route8.setRedirect("");
        route8.setIcon("ChromeOutlined");
        route8.setKey("teamBonus");
        route8.setComponent("/rank/teamBonus");
        route2Children.add(route8);


        Route route9 = new Route();
        route9.setId(6);
        route9.setPid(3);
        route9.setName("数据分析");
        route9.setPath("/rank/chart");
        route9.setRedirect("");
        route9.setIcon("ChromeOutlined");
        route9.setKey("chart");
        route9.setComponent("/rank/chart");
        route2Children.add(route9);

        route2.setChildren(route2Children);

        routeChildren.add(route2);
        route.setChildren(routeChildren);
        routes.add(route);

        return routes;


    }


    private List<Route> getAdminRoutes(){
        List<Route> routes = new ArrayList<>();
        Route route = new Route();
        route.setId(1);
        route.setPid(0);
        route.setName("任务管理");
        route.setPath("/");
        route.setRedirect("/admin/undoneTask");
        route.setComponent("BasicLayout");
        route.setIcon("AppleOutlined");
        route.setKey("layout");

        Route route2 = new Route();
        List<Route> routeChildren = new ArrayList<>();
        List<Route> route2children = new ArrayList<>();
        route2.setId(2);
        route2.setPid(1);
        route2.setName("任务管理");
        route2.setPath("/admin/undoneTask");
        route2.setRedirect("/admin/undoneTask");
        route2.setIcon("ChromeOutlined");
        route2.setKey("element");
        route2.setComponent("RouteView");

        Route route3 = new Route();
        route3.setId(10);
        route3.setPid(2);
        route3.setName("进行中的任务");
        route3.setPath("/admin/undoneTask");
        route3.setRedirect("");
        route3.setComponent("/admin/undoneTask");
        route3.setIcon("");
        route3.setKey("el_icon");


        Route route4 = new Route();
        route4.setId(11);
        route4.setPid(2);
        route4.setName("已有产值的任务");
        route4.setPath("/admin/doneTask");
        route4.setRedirect("");
        route4.setComponent("/admin/doneTask");
        route4.setIcon("");
        route4.setKey("doneTask");


        Route route8 = new Route();
        route8.setId(6);
        route8.setPid(1);
        route8.setName("30%产值待分配");
        route8.setPath("/admin/allocate");
        route8.setRedirect("");
        route8.setIcon("ChromeOutlined");
        route8.setKey("allocate");
        route8.setComponent("/admin/allocate");


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


        Route route7 = new Route();
        route7.setId(5);
        route7.setPid(3);
        route7.setName("团队排行榜");
        route7.setPath("/rank/teamRank");
        route7.setRedirect("");
        route7.setIcon("ChromeOutlined");
        route7.setKey("teamRank");
        route7.setComponent("/rank/teamRank");
        route5children.add(route7);

        route5.setChildren(route5children);


        Route route9 = new Route();
        route9.setId(13);
        route9.setPid(2);
        route9.setName("分管领导的任务类型管理");
        route9.setPath("/admin/types");
        route9.setRedirect("");
        route9.setComponent("/admin/r4types");
        route9.setIcon("");
        route9.setKey("el_done");

        Route route10 = new Route();
        route10.setId(14);
        route10.setPid(2);
        route10.setName("用户管理");
        route10.setPath("/admin/userInfo");
        route10.setRedirect("");
        route10.setComponent("/admin/userInfos");
        route10.setIcon("");
        route10.setKey("el_userinfo2");


        route2children.add(route3);
        route2children.add(route4);
        route2.setChildren(route2children);
        routeChildren.add(route2);
        routeChildren.add(route5);
        routeChildren.add(route8);
        routeChildren.add(route9);
        routeChildren.add(route10);
        route.setChildren(routeChildren);
        routes.add(route);
        return routes;


    }


    @ReadRoleIdInSession
    @GetMapping("/user/menu")
    public RouteResult getUserMenu(Integer roleId) {
        // 根据用户的权限返回相应的路由
        if (roleId == 1) {
            List<Route> routes = getRoutes("/display/task", "/display/doneTask", null);
            return RouteResult.success(routes);
        } else if (roleId == 2) {
            List<Route> routes = getRoutes("/display/r2task", "/display/r2DoneTask", null);
            return RouteResult.success(routes);
        } else if (roleId == 3) {
            List<Route> routes = getRoutes("/display/r3task", "/display/r3DoneTask", null);
            return RouteResult.success(routes);
        } else if (roleId == 4) {
            List<Route> routes = getRoutes("/display/r4task", "/display/r4DoneTask", "/rank/teamRank");
            return RouteResult.success(routes);
        } else if (roleId == 5) {
            List<Route> routes = getR5Routes();
            return RouteResult.success(routes);
        } else if (roleId == 6) {// 财务
            List<Route> routes = getRoutes("/display/a1task", "/display/a1DoneTask", null);
            return RouteResult.success(routes);
        } else if (roleId == 7) {
            List<Route> routes = getAdminRoutes();
            return RouteResult.success(routes);
        }
        return RouteResult.failure("路由异常");
    }
}
