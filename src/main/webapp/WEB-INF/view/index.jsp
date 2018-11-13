<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>首页</title>
    <link rel="shortcut icon" type="image/icon" href="images/jd.ico">
    <link rel="stylesheet" type="text/css" href="css/css.css">

    <script type="text/javascript" src='js/jquery-1.11.3.min.js'></script>
    <script type="text/javascript">
        $(function(){
            $('.nav_mini ul li').hover(function(){
                $(this).find('.two_nav').show(100);
            },function(){
                $(this).find('.two_nav').hide(100);
            })
        })
    </script>
</head>
<body>

    <jsp:include page="top.jsp"></jsp:include>

    <jsp:include page="search.jsp"></jsp:include>

    <jsp:include page="menu.jsp"></jsp:include>

    <jsp:include page="banner.jsp"></jsp:include>

</body>
</html>