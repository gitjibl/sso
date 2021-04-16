$(function () {

})

function testClick(type) {
    var url = "";
    if (type == 1) {
        url = "http://192.168.0.244:8010/user/getUserInfos";
    }else{
        url = "http://192.168.0.244:8010/user/getTestInfos";
    }
    $.ajax({
        //请求方式
        type : "GET",
        //请求的媒体类型
        contentType: "application/json;charset=UTF-8",
        //请求地址
        url : url,
        //数据，json字符串
        // data : JSON.stringify(list),
        //请求成功
        success : function(result) {
            alert(result);
        },
        //请求失败，包含具体的错误信息
        error : function(e){
            console.log(e.status);
            console.log(e.responseText);
        }
    });
}