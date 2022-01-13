function like(btn,entityType,entityId,entityUserId,postId){
    $.post(
        CONTEXT_PATH+"/like",
        {"entityType":entityType, "entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function (data){
            if(data.code==0){
                $(btn).children("b").text(data.map.likeStatus==1?'已赞':'赞')
                $(btn).children("i").text(data.map.likeCount)
            }else{
                alert(data.msg)
            }
        },
        "json"
    )
}
$(function (){
    var id=$("#postId").val()
    $("#top").click(setTop(id))
    $("#wonderful").click(setWonderful(id))
    $("#delete").click(setDelete(id))
})
function setTop(id){
    $.post(
        CONTEXT_PATH+"/setTop",
        {"id":id},
        function (data){
            if(data.code==0){
                $("#top").attr("disabled","disabled")
            }else{
                alert(data.msg)
            }
        },
        "json"
    )
}
function setWonderful(id){
    $.post(
        CONTEXT_PATH+"/setWonderful",
        {"id":id},
        function (data){
            if(data.code==0){
                $("#top").attr("disabled","disabled")
            }else{
                alert(data.msg)
            }
        },
        "json"
    )
}
function setDelete(id){
    $.post(
        CONTEXT_PATH+"/setDelete",
        {"id":id},
        function (data){
            if(data.code==0){
                window.location.href=CONTEXT_PATH+"/index"
            }else{
                alert(data.msg)
            }
        },
        "json"
    )
}