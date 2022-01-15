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
    $("#top").click(setTop)
    $("#wonderful").click(setWonderful)
    $("#delete").click(setDelete)
})
function setTop(){
    var id=$("#postId").val()
    alert(1)
    $.post(
        CONTEXT_PATH+"/setTop",
        {"id":id},
        function (data){
            if(data.code==0){
                $("#top").attr("disabled","disabled")
                window.location.reload()
            }else{
                alert(data.msg)
            }
        },
        "json"
    )
}
function setWonderful(){
    var id=$("#postId").val()
    $.post(
        CONTEXT_PATH+"/setWonderful",
        {"id":id},
        function (data){
            if(data.code==0){
                $("#top").attr("disabled","disabled")
                window.location.reload()
            }else{
                alert(data.msg)
            }
        },
        "json"
    )
}
function setDelete(){
    var id=$("#postId").val()
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