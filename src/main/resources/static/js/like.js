function like(btn,entityType,entityId,entityUserId){
    $.post(
        CONTEXT_PATH+"/like",
        {"entityType":entityType, "entityId":entityId,"entityUserId":entityUserId},
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