$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	var title=$("#title").val()
	var content=$("#content").val()
	//"msg":"提示信息","code":"0/403"为0成功
	$.post(
		CONTEXT_PATH+"/addDiscussPost",
		{"title":title,"content":content},
		function (data){
			alert(data.msg)
			$("#hintModal").modal("show");
			$("#hintBody").val(data.msg)

			setTimeout(function(){
				$("#hintModal").modal("hide");
				if(data.code==0){
					window.location.reload()
				}
			}, 2000);
		},
		"json"
	)

}