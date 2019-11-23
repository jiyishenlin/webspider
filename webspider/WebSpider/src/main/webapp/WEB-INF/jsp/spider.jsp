<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>webspider</title>
<script type="text/javascript" src="tool/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript">
$(function(){
	$("#getResource").click(function(){
		$.ajax({
			url:"/getResource/index",
			success:function(date){
				var table = $("table");
				var content="";
				for(var i=0;i<date.length;i++){
					table.append("<tr><td class='td'>").append(date[i]).append("</td></tr>");
				}
			}
		});
	})
	
	
})
function test(p){
	alert(p);
}
</script>
<style type="text/css">
.td{
width:20%;
}
</style>
</head>
<body>
webspider
<hr>
<a href="javascript:void(0);" id="getResource">获取小说</a>
<a href="javascript:test('p');">测试</a>
<div style="width:80%;margin:0 auto;background-color: #ccc;">
<table style="width:60%;margin:0 auto;"border="0" cellspacing="0" >
</table>
</div>
</body>
</html>