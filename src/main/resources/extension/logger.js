let domain;
let lastTimestamp;
let timeArray = [];
let index = 0;
let resultFileName = "";

function logger() {
    let stayTime = (new Date()).getTime() - lastTimestamp;
    if (stayTime >= 500) {
        timeArray[index++] = stayTime;
    }
    lastTimestamp = (new Date()).getTime();
}

$(document).ready(function () {
    domain = document.domain;
    lastTimestamp = (new Date()).getTime();
    console.log("domain为" + domain);
    console.log("href为" + window.location.href);
    if (location.search.substring(1, 14) === "PassPageUser=") {
        console.log("重新设置cookie为" + location.search.substring(14));
        Cookies.set("PassPageUser", location.search.substring(14));
    }
    console.log("cookie为" + Cookies.get("PassPageUser"));
    if (Cookies.get("PassPageUser") !== undefined && window.location.href !== "https://" + domain && window.location.href.substring(0, domain.length + 10) !== "https://" + domain + "/?") {
        console.log("准备检测用户登录信息");
        $.get("https://www.passpage.xyz:8443/checkUserSession/" + domain + "/" + Cookies.get("PassPageUser"), function (msg) {
            console.log("检测完毕，结果为" + msg);
            if (msg !== 0) {
                Cookies.remove("PassPageUser");
                alert("登录信息无效！");
            } else {
                console.log("准备上传此页面的URL");
                let request = {
                    "domain": domain,
                    "username": Cookies.get("PassPageUser").split("-")[0],
                    "url": window.location.href
                };
                $.ajax({
                    url: "https://www.passpage.xyz:8443/uploadPageUrl",
                    type: "POST",
                    data: JSON.stringify(request),
                    contentType: "application/json",
                    dataType: "text",
                    success: function (msg) {
                        if (msg.length > 1) {
                            resultFileName = msg;
                            console.log(resultFileName);
                        }
                    }
                });
                $(document).scroll(function () {
                    logger();
                });
                $(document).on('visibilitychange', function (e) {
                    if (document.visibilityState === "visible") {
                        lastTimestamp = (new Date()).getTime();
                    } else if (document.visibilityState === "hidden") {
                        logger();
                    }
                });
                window.addEventListener("beforeunload", function (event) {
                    logger();
                    let request = {
                        "domain": domain,
                        "fileName": resultFileName,
                        "timeArray": timeArray
                    };
                    $.ajax({
                        url: "https://www.passpage.xyz:8443/uploadPageLog",
                        type: "POST",
                        data: JSON.stringify(request),
                        contentType: "application/json",
                        dataType: "text",
                        success: function (msg) {
                        }
                    });
                });
            }
        });
    }
});