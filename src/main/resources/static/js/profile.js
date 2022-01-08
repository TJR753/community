$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	if($(btn).hasClass("btn-info")) {
		// 关注TA
		$.post(
			CONTEXT_PATH+"/unfollow",
			{"entityType":3,"entityId":$("#entityId").val(),},
			function (data){
				if(data.code==0){
					window.location.reload()
				}else{
					alert("关注失败")
				}
			},
			"json"
		)
		// $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
	} else {
		// 取消关注
		$.post(
			CONTEXT_PATH+"/follow",
			{"entityType":3,"entityId":$("#entityId").val(),},
			function (data){
				console.log(data.code)
				if(data.code==0){
					window.location.reload()
				}else{
					alert("关注失败")
				}
			},
			"json"
		)
		// $(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
	}
}