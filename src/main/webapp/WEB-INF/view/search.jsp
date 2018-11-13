<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/11/5
  Time: 15:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src='js/jquery-1.11.3.min.js'></script>
    <script>
        //鼠标移上事件 执行查询方法 并展示div框
        function findMiniCart() {
            $.post("findMiniCart.do",function (data) {
                $("#miniCartInnerDiv").html(data);
            })
            $("#miniCartInnerDiv").show();
        }

        //点击事件 跳转到主页面//
        function toIndexPage() {
            location.href="toMainPage.do";
        }

        //移出隐藏事件
        function outMiniCart() {
            $("#miniCartInnerDiv").hide();
        }

    </script>
</head>
<body>
    <div class="search">
        <div class="logo"><img onclick="toIndexPage()" src="./images/logo.jpg" alt=""></div>
        <div class="search_on">
            <div class="se">
                <input type="text" name="search" class="lf">
                <input type="submit" class="clik" value="搜索">
            </div>
            <div class="se">
                <a href="">取暖神奇</a>
                <a href="">1元秒杀</a>
                <a href="">吹风机</a>
                <a href="">玉兰油</a>
            </div>
        </div>
        <div class="card" onmouseover="findMiniCart()" onmouseout="outMiniCart()">
            <a href="toCartListPage.do">购物车<div class="num">0</div></a>

            <!--内嵌的迷你购物车页面-->
            <div id="miniCartInnerDiv" style="display: none">

            </div>
        </div>
    </div>
</body>
</html>
