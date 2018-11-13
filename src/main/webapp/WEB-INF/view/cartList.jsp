<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/11/10
  Time: 15:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>首页</title>
    <link rel="shortcut icon" type="image/icon" href="images/jd.ico">
    <link rel="stylesheet" type="text/css" href="css/css.css">
    <link rel="stylesheet" href="css/style.css">
    <script type="text/javascript" src='js/jquery-1.11.3.min.js'></script>
    <script>
        //flag是否选中
        function changeShfxz(skuId , flag){
            var shfxz = 0;
            if(flag){
                shfxz = 1;
            }
            $.post("changeShfxz.do",{"skuId":skuId,"shfxz":shfxz},function(data){
                $("#cartListInnerDiv").html(data);
            })
        }

    </script>
</head>
<style type="text/css">
    td{vertical-align: middle !important;}
    .form-group{padding: 5px 0;}
</style>
<body>

    <jsp:include page="top.jsp"></jsp:include>

    <jsp:include page="search.jsp"></jsp:include>

    <div id="cartListInnerDiv">
        <jsp:include page="cartListInner.jsp"></jsp:include>
    </div>

    <jsp:include page="list-footer.jsp"></jsp:include>

</body>
</html>