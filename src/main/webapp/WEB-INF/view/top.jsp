<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/11/5
  Time: 15:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src='js/jquery-1.11.3.min.js'></script>
    <script>
        $(function () {
           var yhMch = getCookByKey("yhMch");
           $("#yhMch_span").html(yhMch);
        })

        //通过key获取到value
        function getCookByKey(key) {

            var val = "";
            var ck = document.cookie;
            ck = ck.replace(" ","");
            var ckArr = ck.split(";");
            for (var i = 0; i < ckArr.length; i++){
                    <!-- ckArr[i]————yhMch=zs -->
               var arr = ckArr[i].split("=");
                if(arr[0]==key){
                    val=arr[1];
                }
            }
            return val;
        }
    </script>
</head>
<body>
    <div class="top">
        <!-- session中没有user 说明用户没有登录 -->
        <c:if test="${empty user}">
            <div class="top_text">
                <a href="">用户注册</a>
                <a href="toLoginPage.do">用户登录:<span id="yhMch_span"></span></a>
            </div>
        </c:if>
        <!-- session中有user 说明用户已登录 -->
        <c:if test="${!empty user}">
            <div class="top_text">
                <a href="logOut.do">注销</a>
                <a href="toLoginPage.do">用户名:${user.yhMch}</a>
            </div>
        </c:if>
    </div>
    <div class="top_img">
        <img src="./images/top_img.jpg" alt="">
    </div>
</body>
</html>
