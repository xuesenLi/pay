<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>title</title>

</head>
<body>

<div id="myQrcode">

    <div id="orderId" hidden>${orderId}</div>
    <div id="returnUrl" hidden>${returnUrl}</div>

</div>

<script src="https://cdn.bootcss.com/jquery/1.5.1/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/jquery.qrcode/1.0/jquery.qrcode.min.js"></script>
<script>
    /*二维码转换*/
    jQuery('#myQrcode').qrcode({
        text:"${codeUrl}"
    });

    $(function (){
        //定时器
        setInterval(function(){
            console.log('开始查询支付状态......')
            $.ajax({
                url: 'pay/queryByOrderId',
                data:{
                    'orderId' : $('#orderId').text()
                },
                success: function (result) {
                    console.log(result)
                    if(result.platformStatus != null
                    && result.platformStatus == 'SUCCESS'){
                        location.href = $('#returnUrl').text();
                    }
                },
                error: function (result) {
                    alert(result)
                }
            })
        }, 2000)
    })

</script>
</body>
</html>
