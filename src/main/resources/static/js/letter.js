$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	alert($("#toName").val())
	$.post(
		CONTEXT_PATH+"/message/add",
		{"toName":$("#toName").val(),"content":$("#content").val()},
		function (data){
			if(data.code==0){
				$("#hintBody").val(data.msg)
			}
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
			}, 2000);
			window.location.reload()
		},
		"json"
	)

}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}